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
package org.ael.route.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author aorxsr
 * @Date 2020/1/30
 */
public class ASMUtils {

    /**
     * 存放已经解析好的参数名称,避免浪费资源。
     */
    private static final Map<Method, String[]> METHOD_ARGS = new ConcurrentHashMap<>(16);

    /**
     * 获取方法参数名列表
     *
     * @param clazz
     * @param method
     * @return
     * @throws IOException
     */
    public static String[] getMethodParamNames(Class<?> clazz, Method method) throws IOException {

        if (METHOD_ARGS.containsKey(method)) {
            return METHOD_ARGS.get(method);
        } else {
            try (InputStream in = clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class")) {
                return getMethodParamNames(in, method);
            }
        }
    }

    public static String[] getMethodParamNames(InputStream in, Method m) throws IOException {
        try (InputStream ins = in) {
            return getParamNames(ins,
                    new EnclosingMetadata(m.getName(), Type.getMethodDescriptor(m), m.getParameterTypes().length));
        }

    }

    /**
     * 获取构造器参数名列表
     *
     * @param clazz
     * @param constructor
     * @return
     */
    public static String[] getConstructorParamNames(Class<?> clazz, Constructor<?> constructor) {
        try (InputStream in = clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class")) {
            return getConstructorParamNames(in, constructor);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new String[0];
    }

    public static String[] getConstructorParamNames(InputStream ins, Constructor<?> constructor) {
        try (InputStream in = ins) {
            return getParamNames(in, new EnclosingMetadata(constructor.getName(), Type.getConstructorDescriptor(constructor),
                    constructor.getParameterTypes().length));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return new String[0];
    }

    /**
     * 获取参数名列表辅助方法
     *
     * @param in
     * @param m
     * @return
     * @throws IOException
     */
    private static String[] getParamNames(InputStream in, EnclosingMetadata m) throws IOException {
        ClassReader cr = new ClassReader(in);
        ClassNode cn = new ClassNode();
        // 建议EXPAND_FRAMES
        cr.accept(cn, ClassReader.EXPAND_FRAMES);
        // ASM树接口形式访问
        List<MethodNode> methods = cn.methods;
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < methods.size(); ++i) {
            List<LocalVariable> varNames = new ArrayList<LocalVariable>();
            MethodNode method = methods.get(i);
            // 验证方法签名
            if (method.desc.equals(m.desc) && method.name.equals(m.name)) {
                // System.out.println("desc->"+method.desc+":"+m.desc);
                List<LocalVariableNode> local_variables = method.localVariables;
                for (int l = 0; l < local_variables.size(); l++) {
                    String varName = local_variables.get(l).name;
                    // index-记录了正确的方法本地变量索引。(方法本地变量顺序可能会被打乱。而index记录了原始的顺序)
                    int index = local_variables.get(l).index;
                    // 非静态方法,第一个参数是this
                    if (!"this".equals(varName))
                        varNames.add(new LocalVariable(index, varName));
                }
                LocalVariable[] tmpArr = varNames.toArray(new LocalVariable[varNames.size()]);
                // 根据index来重排序，以确保正确的顺序
                Arrays.sort(tmpArr);
                for (int j = 0; j < m.size; j++) {
                    list.add(tmpArr[j].name);
                }
                break;

            }

        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * 方法本地变量索引和参数名封装
     */
    static class LocalVariable implements Comparable<LocalVariable> {
        public int index;
        public String name;

        public LocalVariable(int index, String name) {
            this.index = index;
            this.name = name;
        }
        @Override
        public int compareTo(LocalVariable o) {
            return this.index - o.index;
        }
    }

}
