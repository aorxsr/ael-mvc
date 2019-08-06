package org.ael.mvc.ioc;

import cn.hutool.core.util.ClassUtil;
import io.netty.util.internal.StringUtil;
import org.ael.mvc.Ael;
import org.ael.mvc.annotation.Controller;
import org.ael.mvc.annotation.GetMapping;
import org.ael.mvc.annotation.PostMapping;
import org.ael.mvc.constant.EnvironmentConstant;
import org.ael.mvc.ioc.bean.Bean;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CustomIoc {

	private Ael ael;

	public void initConfig(Ael ael) {
		this.ael = ael;
	}

	private Map<String, Bean> beans = new ConcurrentHashMap<>(32);

	public void scanLocalCLass() {
		String scanPackage = ael.getEnvironment().getString(EnvironmentConstant.SCAN_PACKAGE);
		if (StringUtil.isNullOrEmpty(scanPackage)) {
			scanPackage = ael.getStartClass().getPackage().getName();
			if (StringUtil.isNullOrEmpty(scanPackage)) {
				return;
			}
		}

		Set<Class<?>> classes = ClassUtil.scanPackage(scanPackage);
		for (Class<?> clazz : classes) {
			Controller controller = clazz.getAnnotation(Controller.class);
			if (null == controller) {
				continue;
			}

			String controllerUrl = "";

			GetMapping getMapping = clazz.getAnnotation(GetMapping.class);
			PostMapping postMapping = clazz.getAnnotation(PostMapping.class);
			if (null == getMapping || null == postMapping) {
				continue;
			}
			controllerUrl += getMapping.value();



		}
	}



}
