package org.ael.mvc.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参考:https://www.cnblogs.com/zhuanzhuanfe/p/8010854.html
 *
 * @Author: aorxsr
 * @Date: 2019/4/19 10:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cookie {

	/**
	 * Cookie名称
	 */
	private String name = null;
	/**
	 * Cookie的值
	 */
	private String value = null;
	/**
	 * Cookie有效期
	 */
	private Long maxAge = null;
	/**
	 * Cookie域属性，控制哪些站点可以看到Cookie
	 */
	private String domain = null;
	/**
	 * Cookie的路径
	 * 例:
	 * www.aorxsr.club/user/ 设置cookie：
	 * Set-cookie：user="fu", domain="www.aorxsr.club"; path=/user/
	 */
	private String path = "/";
	/**
	 * 设置了属性secure，cookie只有在https协议加密情况下才会发送给服务端。但是这并不是最安全的，由于其固有的不安全性，敏感信息也是不应该通过cookie传输的.
	 * Set-Cookie: id=a3fWa; Expires=Wed, 21 Oct 2015 07:28:00 GMT; Secure;
	 */
	private Boolean secure = null;
	/**
	 * 是否禁止JavaScript操作Cookie
	 */
	private Boolean httpOnly = null;

}
