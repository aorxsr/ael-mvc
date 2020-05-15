package org.ael.server.netty.exception;

import lombok.Data;

@Data
public class ExcInfo {
    // 类, 方法
    private String className;
    private String methodName;

    public ExcInfo(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public boolean isEmpty() {
        if (null == className || null == methodName) {
            return false;
        }
        return true;
    }
}
