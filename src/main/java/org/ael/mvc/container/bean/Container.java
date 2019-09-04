package org.ael.mvc.container.bean;

/**
 * @author aorxsr
 * @date 2019/8/5
 */
public class Container {

	private Object bean;
	private Class<?> type;
	private boolean singleton;
	private boolean fieldHasPrototype;

	public Container(Object bean) {
		this(bean, bean.getClass());
	}

	public Container(Object bean, Class<?> type) {
		this.bean = bean;
		this.type = type;
		this.singleton = true;
	}

	public Container(Object bean, Class<?> type, boolean isSingleton) {
		this.bean = bean;
		this.type = type;
		this.singleton = isSingleton;
	}

	public Object getBean() {
		return bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
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

	public boolean isFieldHasPrototype() {
		return fieldHasPrototype;
	}

	public void setFieldHasPrototype(boolean fieldHasPrototype) {
		this.fieldHasPrototype = fieldHasPrototype;
	}
}
