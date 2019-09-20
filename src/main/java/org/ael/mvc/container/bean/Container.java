package org.ael.mvc.container.bean;

/**
 * @author aorxsr
 * @date 2019/8/5
 */
public class Container {

    private Object container;
    private Class<?> type;
    private boolean singleton;

    public Container(Object bean) {
        this(bean, bean.getClass(), false);
    }

    public Container(Object container, Class<?> type, boolean singleton) {
        this.container = container;
        this.type = type;
        this.singleton = singleton;
    }

    public Object getContainer() {
        return container;
    }

    public void setContainer(Object container) {
        this.container = container;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean isSingleton) {
        this.singleton = isSingleton;
    }

}
