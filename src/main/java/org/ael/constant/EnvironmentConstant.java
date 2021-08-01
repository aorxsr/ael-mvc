package org.ael.constant;

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

	/**
	 * 默认页面前缀
	 */
	String TEMPLATE_PRIFIX = "org.ael.template.prefix";

	/**
	 * 默认页面后缀
	 */
	String TEMPLATE_SUFFIX = "org.ael.template.suffix";

	/*******************************************************/

	/**
	 * 默认SESSIONKEY
	 */
	String DEFAULT_SESSION_KEY = "ASESSION";

	/**
	 * 默认页面前缀
	 */
	String DEFAULT_TEMPLATE_PREFIX = "/templates/";

	/**
	 * 默认页面后缀
	 */
	String DEFAULT_TEMPLATE_SUFFIX = ".html";

	/**
	 * 是否打印请求的URL
	 */
	String REQUEST_URL_SHOW = "org.ael.request.url-show";


	/*******************************************************/

	/**
	 * 环境中的IOCList名称
	 */
	String IOCKeyName = "AelIocList";

}