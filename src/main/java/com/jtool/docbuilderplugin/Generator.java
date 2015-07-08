package com.jtool.docbuilderplugin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jtool.doc.annotation.DocField;
import com.jtool.docbuilderplugin.model.ApiModel;
import com.jtool.docbuilderplugin.model.Param;
import com.jtool.docbuilderplugin.util.FileUtil;
import com.jtool.docbuilderplugin.util.MyClassLoader;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.validation.constraints.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Generator {

    public static void generateMainHtml(File outputFile, File infoFile, List<ApiModel> apiList) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        String htmlTemple = FileUtil.readFormFile("index.html");
        Collections.sort(apiList, new ApiModel());
        String list = "";
        for (ApiModel api : apiList) {
            list += "<div class=\"apiLinkItem\"><span class=\"lcp\">[" + api.getChapter()
                    + "]</span>&nbsp;<a href='#"
                    + StringEscapeUtils.escapeHtml4(api.getMethodName()) + " ' ";

            if(api.isDeprecated()){
                list += " style='text-decoration:line-through' ";
            }

            list += " >" + StringEscapeUtils.escapeHtml4(api.getApiName()) + "</a></div>";
        }
        htmlTemple = htmlTemple.replaceAll("\\{list\\}", list);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        htmlTemple = htmlTemple.replaceAll("\\{lastModify\\}", sdf.format(new Date()));
        StringBuilder sb = new StringBuilder();

        for (ApiModel api : apiList) {

            String apiEachContentTemple = FileUtil.readFormFile("apiEachContent.htmlp");
            String result = apiEachContentTemple.replace("{apiName}", api.getApiName());
            result = result.replace("{chapter}", api.getChapter() + "");
            result = result.replace("{apiHub}", api.getMethodName());
            result = result.replace("{info}", api.getInfo());
            result = result.replace("{host}", api.getHost());
            result = result.replace("{url}", api.getUrl());
            result = result.replace("{method}", api.getMethod());
            result = result.replace("{otherInfo}", api.getOtherInfo().equals("") ? "暂无" : api.getOtherInfo());

            List<Param> requestParams = genParamDoc(api.getRequestType());
            result = result.replace("{requestParams}", makeParamStr(requestParams));

            List<Param> errorParams = genErrorParamList(api.getErrorType());
            result = result.replace("{errorParams}", makeErrorParamStr(errorParams));

            result = result.replace("{successReturn}", JSON.toJSONString(api.getSuccessReturn(), SerializerFeature.PrettyFormat));

            List<Param> successParams = genParamDoc(api.getSuccessType());
            result = result.replace("{successParams}", makeSuccessParamStr(successParams));

            if(api.isDeprecated()) {
                result = result.replace("{Deprecated}", " style='text-decoration:line-through;' ");
            } else {
                result = result.replace("{Deprecated}", "");
            }

            sb.append(result);
        }
        htmlTemple  = htmlTemple.replace("{content}", sb.toString());

        String docInfo = "";
        if(infoFile.isFile()) {
            docInfo = FileUtil.readFormFile(infoFile);
        }
        htmlTemple = htmlTemple.replace("{docInfo}", docInfo);

        FileUtil.writeToFile(htmlTemple, outputFile);
    }

    private static List<Param> genErrorParamList(List<Class> clazzs) throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, InstantiationException {

        List<Param> paras = new ArrayList<>();
        // 得到该方法所有异常的Class字节码
        for (Class<?> clazz : clazzs) {
            Param param = new Param();
            param.setKey(clazz.getDeclaredField("code").get(null).toString());
            param.setComment(clazz.getDeclaredField("desc").get(null).toString());
            paras.add(param);
        }
        return paras;
    }

    public static List<Param> genParamDoc(Class<?> clazz) throws ClassNotFoundException {

        List<Param> params = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {

            DocField docField = field.getAnnotation(DocField.class);

            if (docField != null) {

                Param param = new Param();
                param.setKey(field.getName());
                param.setComment(makeCommendWithValidateInfo(field));
                param.setOption(makeOptionWithValidateInfo(field));
                params.add(param);

                if(!field.getType().isPrimitive()) {

                    if (field.getType().equals(List.class)) {
                        // 如果是List集合
                        Type fc = field.getGenericType();

                        if (fc == null) {
                            continue;
                        }

                        if (fc instanceof ParameterizedType) {
                            ParameterizedType pt = (ParameterizedType) fc;
                            // List集合中的参数类型的字节码
                            params.addAll(genParamDoc(MyClassLoader.loadClass(pt.getActualTypeArguments()[0].getTypeName())));
                        }
                    } else {
                        // 如果是普通的类
                        params.addAll(genParamDoc(field.getType()));
                    }
                }
            }
        }

        return params;

    }

    public static List<Param> genParamDoc(List<Class> clazzs) throws ClassNotFoundException {
        List<Param> result = new ArrayList<>();
        for(Class clazz : clazzs){
            result.addAll(genParamDoc(clazz));
        }
        return result;
    }

    private static String makeCommendWithValidateInfo(Field field) {
        DocField docField = field.getAnnotation(DocField.class);
        return docField.value();
    }

    // 指定参数是否可选或者范围
    private static String makeOptionWithValidateInfo(Field field) {

        String result = "";

        NotNull notNull = field.getAnnotation(NotNull.class);

        if(notNull == null) {
            result += "可选";
        } else {
            result += "必须";
        }

        Size size = field.getAnnotation(Size.class);

        if(size != null) {
            result += "<br/>长度>=" + size.min();
            result += "<br/>长度<=" + size.max();
        }

        Min min = field.getAnnotation(Min.class);

        if(min != null) {
            result += "<br/>数值>" + min.value();
        }

        Max max = field.getAnnotation(Max.class);

        if(max != null) {
            result += "<br/>数值<" + max.value();
        }

        DecimalMin decimalMin = field.getAnnotation(DecimalMin.class);

        if(decimalMin != null) {
            result += "<br/>数值>=" + decimalMin.value();
        }

        DecimalMax decimalMax = field.getAnnotation(DecimalMax.class);

        if(decimalMin != null) {
            result += "<br/>数值<=" + decimalMax.value();
        }

        return result;
    }

    public static Object genParamJsonObj(Class clazz) throws ClassNotFoundException, IllegalArgumentException,
            IllegalAccessException, InstantiationException, IOException {

        Object obj = clazz.newInstance();

        for (Field field : clazz.getDeclaredFields()) {

            field.setAccessible(true);

            if (field.getType().equals(String.class)) {
                field.set(obj, field.getName());
            } else if (field.getType().equals(List.class)) {

                List<Object> list = new ArrayList<>();

                Type fc = field.getGenericType();

                if (fc == null) {
                    continue;
                }

                if (fc instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) fc;
                    list.add(genParamJsonObj((MyClassLoader.loadClass(pt.getActualTypeArguments()[0].getTypeName()))));
                }

                field.set(obj, list);
            } else if(!field.getType().isPrimitive()) {
                field.set(obj, genParamJsonObj(field.getType()));
            }
        }

        return obj;

    }

    private static String makeErrorParamStr(List<Param> errorParams) {
        String result = "";
        int count = 0;
        for (Param param : errorParams) {
            count++;
            if (count % 2 == 0) {
                result += "<tr class=\"paramTable_hr_hb\">";
            } else {
                result += "<tr>";
            }
            result += "<td>";
            result += param.getKey();
            result += "</td>";
            result += "<td>";
            result += param.getComment();
            result += "</td>";
            result += "</tr>";
        }

        return result;
    }

    private static String makeSuccessParamStr(List<Param> successParams) {
        String result = "";
        int count = 0;
        for (Param param : successParams) {
            count++;
            if (count % 2 == 0) {
                result += "<tr class=\"paramTable_hr_hb\">";
            } else {
                result += "<tr>";
            }
            result += "<td>";
            result += param.getKey();
            result += "</td>";
            result += "<td>";
            result += param.getOption();
            result += "</td>";
            result += "<td>";
            result += param.getComment();
            result += "</td>";
            result += "</tr>";
        }

        return result;
    }

    private static String makeParamStr(List<Param> requestParams) {
        String result = "";
        int count = 0;
        for (Param param : requestParams) {
            count++;
            if (count % 2 == 0) {
                result += "<tr class=\"paramTable_hr_hb\">";
            } else {
                result += "<tr>";
            }
            result += "<td>";
            result += param.getKey();
            result += "</td>";
            result += "<td>";
            result += param.getOption();
            result += "</td>";
            result += "<td>";
            result += param.getComment();
            result += "</td>";
            result += "</tr>";
        }

        return result;
    }
}
