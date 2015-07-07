package com.jtool.docbuilderplugin;

import com.jtool.doc.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class MethodParser {

    public static String paresMethodName(Method method) {
        return method.getName();
    }

    public static Double paresChapter(Method method) {
        return method.getAnnotation(DocApi.class).chapter();
    }

    public static String paresApiName(Method method) {
        return method.getAnnotation(DocApi.class).name();
    }

    public static String paresHost(Method method) {
        return method.getAnnotation(DocApi.class).host();
    }

    public static String paresInfo(Method method) {
        return method.getAnnotation(DocApi.class).description();
    }

    public static String paresMethod(Method method) {
        String result = "";
        for (RequestMethod requestMethod : method.getAnnotation(RequestMapping.class).method()) {
            if (result.equals("")) {
                result += requestMethod.name();
            } else {
                result += "," + requestMethod.name();
            }
        }
        return result;
    }

    public static String paresOtherInfo(Method method) {
        DocOtherInfo docOtherInfo = method.getAnnotation(DocOtherInfo.class);
        if (docOtherInfo == null) {
            return "暂无";
        } else {
            return docOtherInfo.value();
        }
    }

    public static String paresUrl(Method method) {
        String result = "";
        for (String url : method.getAnnotation(RequestMapping.class).value()) {
            if (result.equals("")) {
                result += url;
            } else {
                result += "," + url;
            }
        }
        return result;
    }

    public static List<Class> paresErrorType(Method method) throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, InstantiationException {

        List<Class> resultException = new ArrayList<>();

        Class<?>[] exceptionTypes = method.getExceptionTypes();

        for(Class exceptionType : exceptionTypes) {
            if(exceptionType.getAnnotation(DocExceptionDefine.class) != null){
                resultException.add(exceptionType);
            }
        }

        DocException docException = method.getAnnotation(DocException.class);
        if (docException != null) {
            // 得到该方法所有异常的Class字节码
            for (Class clazz : docException.value()) {
                resultException.add(clazz);
            }
        }

        return resultException;
    }

    public static boolean parseIsDeprecated(Method method) {
        Deprecated deprecated = method.getAnnotation(Deprecated.class);
        return deprecated != null;
    }

    public static Class parseSuccessParam(Method method) throws ClassNotFoundException {

        DocResponse docResponse = method.getAnnotation(DocResponse.class);
        if (docResponse != null) {
            return docResponse.value();
//            return genParamDoc(Class.forName(returnClassType));
        } else {
//            String returnClassType = getReturnTypeByMethod(method);
//
//            if (returnClassType.equals("")) {
//                return new ArrayList<>();
//            } else {
//                return genParamDoc(Class.forName(returnClassType));
//            }
            return null;
        }
    }

    public static List<Class> paresRequestType(Method method) throws ClassNotFoundException {
        List<Class> result = new ArrayList<>();

        for (Parameter parameter : method.getParameters()) {
            DocParam docParam = parameter.getAnnotation(DocParam.class);
            if (docParam != null) {
                result.add(parameter.getClass());
            }
        }

        Annotation annotation = method.getAnnotation(DocRequest.class);
        if (annotation != null) {
            result.add(((DocRequest) annotation).value());
        }

        return result;
    }

    public static Class paresSuccessReturnType(Method method) {

        DocResponse docResponse = method.getAnnotation(DocResponse.class);

        if (docResponse != null) {
            return docResponse.value();
        } else {
            throw new RuntimeException(method + "需要一个@DocResponse");
        }
    }

    public static String parseForWho(Method method) {
        return method.getAnnotation(DocApi.class).forWho();
    }
}
