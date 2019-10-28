package org.ael.mvc;

import lombok.extern.slf4j.Slf4j;
import org.ael.mvc.constant.EnvironmentConstant;
import org.ael.mvc.container.ClassPathFileConstant;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * @Author: aorxsr
 * @Date: 2019/7/16 18:25
 */
@Slf4j
public class Environment implements Map<String, Object> {

    /**
     * 存放环境信息
     */
    private Map<String, Object> map = null;

    public Environment() {
        map = new HashMap<>(16);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return map.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    public Map<String, Object> getMap() {
        return map;
    }

    /**
     * 根据Key获取String值
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        return map.containsKey(key) ? map.get(key).toString() : null;
    }

    /**
     * 根据Key获取String值
     *
     * @param key
     * @return
     */
    public String getString(String key, String defaultValue) {
        return map.containsKey(key) ? map.get(key).toString() : defaultValue;
    }

    public boolean getBoolean(String key, boolean b) {
        return map.containsKey(key) ? Boolean.valueOf(map.get(key).toString()) : b;
    }

    public List getList(String key, List defaultValue) {
        return map.containsKey(key) ? (List)map.get(key)  : defaultValue;
    }

    public void initConfig() {
        // 加载配置文件
        InputStream classPathFile = ClassPathFileConstant.getClassPathFile("app.properties");
        if (null == classPathFile) {
            return;
        }
        Properties properties = new Properties();
        try {
            properties.load(classPathFile);
            properties.forEach((k, v) -> {
                map.put(k.toString(), v);
            });
            classPathFile.close();
            // 判断子配置文件
            if (properties.containsKey(EnvironmentConstant.ACTIVE_NAME)) {
                String active = properties.getProperty(EnvironmentConstant.ACTIVE_NAME);
                classPathFile = ClassPathFileConstant.getClassPathFile(active);
                if (null == classPathFile) {
                    return;
                }
                properties.clear();
                properties.load(classPathFile);
                properties.forEach((k, v) -> {
                    map.put(k.toString(), v);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  List<String> arrayToList(String[] array) {
        List<String> arrayList = new ArrayList<>();
        if (array.length != 0) {
            for (String s : array) {
                arrayList.add(s);
            }
        }
        return arrayList;
    }

}
