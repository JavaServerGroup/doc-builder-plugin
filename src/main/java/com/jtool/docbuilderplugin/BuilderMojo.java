package com.jtool.docbuilderplugin;

import com.jtool.doc.annotation.DocApi;
import com.jtool.docbuilderplugin.model.ApiModel;
import com.jtool.docbuilderplugin.util.MyClassLoader;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @goal build
 * @requiresDependencyResolution runtime
 */
public class BuilderMojo extends AbstractMojo {

    /**
     * @parameter default-value = "${project.basedir}"
     * @required
     * @readonly
     */
    private File baseDir;

    /**
     * @parameter expression = "${project.build.sourceDirectory}"
     * @required
     * @readonly
     */
    private File sourceDirectory;

    /**
     * @parameter expression = "${project.build.resources[0].directory}/docSource/info.html"
     * @required
     * @readonly
     */
    private File resources;

    /**
     * @parameter default-value = "${project.build.directory}/"
     */
    private String outPath;

    /**
     * @parameter default-value = "jtoolDoc.html"
     */
    private String fileName;

    /**
     * @parameter default-value = "${project.build.directory}/classes/"
     * @required
     * @readonly
     */
    private String targetClassPath;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter
     * @required
     */
    private String packageName;

    public void execute() throws MojoExecutionException, MojoFailureException {

        if (packageName == null || "".equals(packageName)) {
            throw new RuntimeException("请设置controller package");
        }

        MyClassLoader.init(project);

        try {
            //在指定目录下找到所有带有@DocApi注解的方法
            List<Method> methodLists = findAllDocApiMethod(new File(targetClassPath + makePackagePath(packageName)));

            //遍历method集合，解析出可用的apiModel对象集合
            List<ApiModel> apiModelList = apiModelList = parseMethodToApiModel(methodLists);

            Generator.generateMainHtml(new File(outPath + fileName), resources, apiModelList);

        } catch (ClassNotFoundException | IllegalAccessException | IOException | InstantiationException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private String makePackagePath(String packageName) {
        String result = "";
        for (String str : packageName.split("\\.")) {
            result += str + "/";
        }
        return result;
    }

    private List<Method> findAllDocApiMethod(File rootDir) throws ClassNotFoundException {
        List<Method> methodLists = new ArrayList<>();

        //遍历目录下的所有源文件
        for (File sourceFile : rootDir.listFiles()) {
            if (sourceFile.isFile()) {
                //遍历源文件下的所有方法
                methodLists.addAll(parseMethodWithDocApi(sourceFile));
            }
        }

        return methodLists;
    }

    private List<Method> parseMethodWithDocApi(File sourceFile) throws ClassNotFoundException {
        List<Method> result = new ArrayList<>();

        String sourceFileName = sourceFile.getName();

        Class<?> clazz = MyClassLoader.loadClass(packageName + "." + sourceFileName.substring(0, sourceFileName.lastIndexOf(".")));

        //遍历源文件的方法
        for (Method method : clazz.getDeclaredMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                // 只保留有@DocApi注解的方法
                if (annotation instanceof DocApi) {
                    result.add(method);
                }
            }
        }

        return result;
    }

    private static List<ApiModel> parseMethodToApiModel(List<Method> methodLists) throws IllegalAccessException, IOException, InstantiationException, NoSuchFieldException, ClassNotFoundException {
        List<ApiModel> apiModelList = new ArrayList<>();
        for (Method method : methodLists) {
            ApiModel apiModel = parseMethod(method);
            if (apiModel != null) {
                apiModelList.add(apiModel);
            }
        }
        return apiModelList;
    }

    private static ApiModel parseMethod(Method method) throws
            ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InstantiationException,
            IOException, NoSuchFieldException, SecurityException {

        if (method.getAnnotation(DocApi.class) != null) {
            ApiModel apiModel = new ApiModel();
            apiModel.setMethodName(MethodParser.paresMethodName(method));
            apiModel.setChapter(MethodParser.paresChapter(method));
            apiModel.setApiName(MethodParser.paresApiName(method));
            apiModel.setHost(MethodParser.paresHost(method));
            apiModel.setInfo(MethodParser.paresInfo(method));
            apiModel.setMethod(MethodParser.paresMethod(method));
            apiModel.setOtherInfo(MethodParser.paresOtherInfo(method));
            apiModel.setUrl(MethodParser.paresUrl(method));
            apiModel.setErrorType(MethodParser.paresErrorType(method));
            apiModel.setRequestType(MethodParser.paresRequestType(method));
            apiModel.setSuccessType(MethodParser.parseSuccessParam(method));
            apiModel.setSuccessReturn(MethodParser.paresSuccessReturnType(method));
            apiModel.setIsDeprecated(MethodParser.parseIsDeprecated(method));
            return apiModel;
        } else {
            throw new RuntimeException("本方法应该有@DocApi注解才对");
        }
    }

}
