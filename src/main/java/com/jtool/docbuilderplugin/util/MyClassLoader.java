package com.jtool.docbuilderplugin.util;

import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * Created by jialechan on 15/6/24.
 */
public class MyClassLoader {

    private static URLClassLoader newLoader;

    private static boolean initialized = false;

    public static void init(MavenProject project) {
        try {
            List runtimeClasspathElements = project.getRuntimeClasspathElements();
            URL[] runtimeUrls = new URL[runtimeClasspathElements.size()];
            for (int i = 0; i < runtimeClasspathElements.size(); i++) {
                String element = (String) runtimeClasspathElements.get(i);
                try {
                    runtimeUrls[i] = new File(element).toURI().toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            newLoader = new URLClassLoader(runtimeUrls,
                    Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {

        }
        initialized = true;
    }

    public static Class<?> loadClass(String name) throws ClassNotFoundException {
        if(!initialized) {
            System.out.println("你应该先初始化ClassLoader");
        }
        return newLoader.loadClass(name);
    }

}
