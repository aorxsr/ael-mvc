package org.ael.server.netty.exception;

import io.netty.handler.codec.http.FullHttpResponse;
import lombok.SneakyThrows;
import org.ael.http.HttpRequest;
import org.ael.http.inter.Request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author aorsr
 * @date 2020/5/13
 */
public class ExecuteException {

    /**
     * ClassName.toString
     */
    private ConcurrentHashMap exceptions = new ConcurrentHashMap<Class<?>, ExcInfo>(16);

    /**
     * @param exceptionType 必须是异常类型,否则没意义
     * @param excInfo       这个里面是异常之后,调用哪个类和方法
     */
    public ExecuteException addException(Class<?> exceptionType, ExcInfo excInfo) {
        if (null == exceptionType) {
            throw new RuntimeException("异常类型不能为空!");
        }
        if (null == excInfo) {
            throw new RuntimeException("异常执行类不能为空!");
        }
        if (!excInfo.isEmpty()) {
            throw new RuntimeException("异常执行类内容不能为空!");
        }
        exceptions.put(exceptionType, excInfo);
        return this;
    }

    /**
     * 判断是否存在相应的异常类型
     *
     * @param e
     * @return
     */
    public boolean existenceExceptionType(Exception e) {
        for (Object o : exceptions.keySet()) {
            Class<?> tempClazz = (Class<?>) o;
            if (tempClazz.toString().equals(e.getClass().toString())) {
                return true;
            }
        }
        return false;
    }

    public FullHttpResponse executeException(Exception e, HttpRequest request) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        for (Object o : exceptions.keySet()) {
            Class<?> tempClazz = (Class<?>) o;
            if (tempClazz.toString().equals(e.getClass().toString())) {
                return inokeExeInfo((ExcInfo) exceptions.get(o), e, request);
            }
        }
        throw new RuntimeException("未找到相关的异常处理类!");
    }

    static class invokeInfo {
        private Object object;
        private Method method;

        public invokeInfo(Object object, Method method) {
            this.object = object;
            this.method = method;
        }
    }

    private final static ConcurrentHashMap invokes = new ConcurrentHashMap(16);

    private FullHttpResponse inokeExeInfo(ExcInfo excInfo, Exception e, HttpRequest request) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException {
        String className = excInfo.getClassName();
        FullHttpResponse response = null;
        if (invokes.containsKey(className)) {
            // 存在
            invokeInfo invokeInfo = (invokeInfo) invokes.get(className);
            response = (FullHttpResponse) invokeInfo.method.invoke(invokeInfo.object, e, request);
        } else {
            // 不存在,自己创建
            Class<?> aClass = Class.forName(className);
            Method method = aClass.getDeclaredMethod(excInfo.getMethodName(), new Class<?>[]{Exception.class, HttpRequest.class});
            Object instance = aClass.newInstance();
            response = (FullHttpResponse) method.invoke(instance, e, request);
            invokes.put(className, new invokeInfo(instance, method));
        }
        return response;
    }

}

