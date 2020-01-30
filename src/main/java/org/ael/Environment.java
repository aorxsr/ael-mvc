/* Copyright (c) 2019, aorxsr (aorxsr@163.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.ael;

import lombok.extern.slf4j.Slf4j;
import org.ael.commons.StreamUtils;
import org.ael.constant.EnvironmentConstant;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @Author: aorxsr
 * @Date: 2019/7/16 18:25
 */
@Slf4j
public class Environment implements Map<String, Object> {

    private Map<String, Object> map = new HashMap<String, Object>(16) {{
        put(EnvironmentConstant.SCAN_PACKAGE, buildList());
    }};

    private List<String> buildList() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("org.ael");
        return strings;
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
        return map.containsKey(key) ? (List) map.get(key) : defaultValue;
    }

    public void initConfig() {
        // 加载配置文件
        InputStream classPathFile = StreamUtils.getClassPathFile("app.properties");
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
                classPathFile = StreamUtils.getClassPathFile(active);
                if (null == classPathFile) {
                    return;
                }
                properties.load(classPathFile);
                properties.forEach((k, v) -> {
                    map.put(k.toString(), v);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
