package com.foxconn.drivers.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class MyServiceLoader<T> {
    private List<T> items = new ArrayList<>();
    private final static String folder = "META-INF/services";

    public Iterator<T> iterator() {
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return !items.isEmpty();
            }

            @Override
            public T next() {
                return items.remove(0);
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T> MyServiceLoader<T> load(Class<T> interfaceClazz) {
        MyServiceLoader<T> loader = new MyServiceLoader<>();
        String resourcePath = new File(folder , interfaceClazz.getName()).toString();
        try {
            Enumeration<URL> systemResources = ClassLoader.getSystemResources(resourcePath);
            while (systemResources.hasMoreElements()) {
                URL nextElement = systemResources.nextElement();
                String filepath = nextElement.toURI().getPath();
                if (filepath.isEmpty() || filepath.trim().startsWith("#"))
                    continue;
                try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
                    String implClassName = null;
                    while ((implClassName = reader.readLine()) != null) {
                        if (implClassName.isEmpty())
                            continue;
                        Class<?> clazz = Class.forName(implClassName);
                        if (interfaceClazz.isAssignableFrom(clazz)) {
                            Class<? extends T> loadedClazz = (Class<? extends T>) clazz;
                            T item = loadedClazz.newInstance();
                            loader.items.add(item);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
        } catch (Exception e) {
        }

        return loader;
    }

}
