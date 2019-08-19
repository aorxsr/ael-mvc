package org.ael.mvc.constant;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 17:05
 */
public interface EnvironmentConstant {

	/**
	 * 是否启用 http 压缩
	 */
	String HTTP_ZIP = "org.ael.mvc.useZip";
	/**
	 * session key
	 */
	String SESSION_KEY = "org.ael.sessionKey";
	/**
	 * 默认SESSIONKEY
	 */
	String DEFAULT_SESSION_KEY = "ASESSION";
	/**
	 * 控制器扫描包
	 */
	String SCAN_PACKAGE = "org.ael.mvc.scanPackage";

	/**
	 * 子文件
	 */
	String ACTIVE_NAME = "org.ael.active";

	/**
	 * 配置文件
	 */
	String ENVIRONMENT_FILE = "org.ael.config.file";

}
