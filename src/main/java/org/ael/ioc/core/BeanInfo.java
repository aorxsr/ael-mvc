package org.ael.ioc.core;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BeanInfo {

    // 只有在世单例的时候才有,不然其他时候都会创建
    private Object object;
    private Class<?> cls;
    private boolean isSingleton;

}
