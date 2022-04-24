/*
 * Copyright(c) Kingdee Software (China) Co., Ltd. 1993-2020, All rights reserved.
 */

package com.fizz.extension;

import com.fizz.entity.Holder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 功能描述
 *
 * @since 2022-04-24
 */
public class ExtensionFactory<T> {
    // 约定路径
    private static final String EXTENSION_DIRECTORY = "META-INFO/com/fizz/extension/";

    // 接口类: 工厂实例
    private static final ConcurrentHashMap<Class, ExtensionFactory> EXTENSION_FACTORYS = new ConcurrentHashMap<>();

    // 共享类, 实例
    private static final ConcurrentHashMap<Class, Object> CLASS_INS = new ConcurrentHashMap<>();

    // 接口类对象
    private final Class<T> type;

    // 存储实现类 { key: 实现类 }
    // holder 保证加载一次
    private final Holder<Map<String, Class<T>>> clsMapHolder = new Holder<>();

    // 存储最终对象 { key: 实例 }
    // holder 保证本 Factory, newIns 只调用一次
    private final ConcurrentHashMap<String, Holder<T>> cachedIns = new ConcurrentHashMap<>();

    // ???
    private final Set<Class<T>> wrapper = new HashSet<>();

    public ExtensionFactory(Class<T> cls) {
        this.type = cls;
    }

    /**
     * 获取工厂实例
     *
     * @param cls 类对象
     * @param <T> 接口类/抽象类
     * @return 工厂对象
     */
    public static <T> ExtensionFactory<T> getExtensionFactory(Class<T> cls) {
        ExtensionFactory<T> factory = EXTENSION_FACTORYS.get(cls);
        if (factory == null) {
            // 返回的是 oldValue, 可能是 null, 别的线程没有创建
            EXTENSION_FACTORYS.putIfAbsent(cls, new ExtensionFactory(cls));
            factory = EXTENSION_FACTORYS.get(cls);
        }
        return factory;
    }

    /**
     * 获取实例
     *
     * @param name 对应实例key
     */
    public T getExtension(String name) {
        Holder<T> insHolder = cachedIns.get(name);
        if (insHolder == null) {
            cachedIns.putIfAbsent(name, new Holder<>());
            insHolder = cachedIns.get(name);
        }
        // 获取实例, holder 限制 同key 只加载一次
        T ins = insHolder.get(() -> creatExtension(name));
        return ins;
    }

    /**
     * 创建实例
     *
     * @param name 对应实例key
     * @return 新实例
     */
    private T creatExtension(String name) {
        Map<String, Class<T>> clsMap = getExtensionClass();
        Class<T> cls = clsMap.get(name);
        if (cls == null) {
            throw new RuntimeException(String.format("不能找到 [%s] 的 [%s] 类型的实现类", type.getName(), name));
        }
        try {
            T ins = (T) CLASS_INS.get(cls);
            if (ins == null) {
                CLASS_INS.putIfAbsent(cls, cls.newInstance());
                ins = (T) CLASS_INS.get(cls);
            }
            // 新内容, 应该是装饰器
            for (Class<T> wrapperClass : wrapper) {
                ins = wrapperClass.getConstructor(this.type).newInstance(ins);
            }
            return ins;
        } catch (Exception ex) {
            throw new RuntimeException(
                    String.format("扩展实例class:%s, name:%s, 初始化异常:%s", cls.getName(), name, ex.getMessage()), ex);
        }
    }

    /**
     * 获取 所有类
     *
     * @return { 配置key, 类对象 }
     */
    private Map<String, Class<T>> getExtensionClass() {
        // holder 限制只加载一次
        return clsMapHolder.get(() -> loadExtensionClasses());
    }

    private Map<String, Class<T>> loadExtensionClasses() {
        Map<String, Class<T>> res = new HashMap<>();
        loadFile(res, EXTENSION_DIRECTORY);
        return res;
    }

    /**
     * 读取文件 clsName, 并加载类
     *
     * @param resMap 返回结果
     * @param dir    目录
     */
    private void loadFile(Map<String, Class<T>> resMap, String dir) {
        // 获取配置文件名称
        String fileName = dir + this.type.getName();
        // 获取 所有jar 资源
        ClassLoader clsLoader = ExtensionFactory.class.getClassLoader();
        try {
            Enumeration<URL> urls = clsLoader.getResources(fileName);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                loadClass(resMap, url, clsLoader);
            }
        } catch (Throwable ex) {
            throw new RuntimeException(
                    String.format("扩展 [%s] 加载文件 [%s] 异常 [%s]", type.getName(), fileName, ex.getMessage()), ex);
        }
    }

    /**
     * 加载 类
     *
     * @param resMap    返回结果
     * @param url       路径
     * @param clsLoader 加载器
     */
    private void loadClass(Map<String, Class<T>> resMap, URL url, ClassLoader clsLoader) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                // 注释,空行 过滤
                if (line.indexOf("#") >= 0 || line.trim().length() == 0) {
                    continue;
                }
                String[] arr = line.split("=");
                // 获取 key + 实现类
                String key, insClsName;
                if (arr.length == 2) {
                    key = arr[0];
                    insClsName = arr[1];
                } else {
                    key = line.substring(line.lastIndexOf(".") + 1);
                    insClsName = line;
                }
                // 类初始化
                Class<T> cls = (Class<T>) Class.forName(insClsName, true, clsLoader);

                try {
                    // 新内容 这里未知
                    cls.getConstructor(this.type);
                    this.wrapper.add(cls);
                } catch (NoSuchMethodException ex) {
                    // 一个 key 只能一个 cls 实例
                    Class<T> oldCls = resMap.get(cls);
                    if (oldCls == null) {
                        resMap.put(key, cls);
                    } else if (oldCls != cls) {
                        throw new RuntimeException(String.format("扩展 [%s] key [%s] 有多个实例", type.getName(), key));
                    }
                }
            }
        } catch (Throwable ex) {
            throw new RuntimeException(
                    String.format("扩展 [%s] 加载url [%s] 异常 [%s]", type.getName(), url, ex.getMessage()), ex);
        }
    }
}
