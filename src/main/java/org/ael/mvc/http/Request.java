package org.ael.mvc.http;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import lombok.NonNull;
import org.ael.mvc.constant.HttpConstant;

import java.util.List;
import java.util.Map;

/**
 * @Author: aorxsr
 * @Date: 2019/7/18 18:26
 */
public interface Request {

	/**
	 * get http method
	 * @return
	 */
	String getMethod();

	/**
	 * get uri
	 * @return
	 */
	String getUri();

	/**
	 * Get client host.
	 *
	 * @return Return client request host
	 */
	String getHost();

	/**
	 * Get client remote address. e.g: 102.331.234.11:38227
	 *
	 * @return Return client ip and port
	 */
	String getRemoteAddress();

	/**
	 * Get request url
	 *
	 * @return request url
	 */
	String getUrl();

	/**
	 * Get current request Path params, like /users/:uid
	 *
	 * @return Return parameters on the path Map
	 */
	Map<String, String> getPathParams();

	/**
	 * Get current request query parameters
	 *
	 * @return Return request query Map
	 */
	Map<String, List<String>> getParameters();

	/**
	 * @return whether the current request is a compressed request of GZIP
	 * @since 2.0.9.BETA1
	 */
	boolean isUseGZIP();

	/**
	 * Get current request session, if null then create
	 *
	 * @return Return current session
	 */
	Session getSession();

	/**
	 * Get current request contentType. e.g: "text/html; charset=utf-8"
	 *
	 * @return Return contentType
	 */
	default String contentType() {
		String contentType = header(HttpConstant.CONTENT_TYPE);
		return null != contentType ? contentType : "Unknown";
	}

	/**
	 * Gets the current request is the head of the IE browser
	 *
	 * @return return current request is IE browser
	 */
	default boolean isIE() {
		String ua = userAgent();
		return ua.contains("MSIE") || ua.contains("TRIDENT");
	}

	/**
	 * Get current request is ajax. According to the header "x-requested-with"
	 *
	 * @return Return current request is a AJAX request
	 */
	default boolean isAjax() {
		return "XMLHttpRequest".equals(header("X-Requested-With")) || "XMLHttpRequest".equals(header("x-requested-with"));
	}

	/**
	 * Get request user-agent
	 *
	 * @return return user-agent
	 */
	default String userAgent() {
		return header(HttpConstant.USER_AGENT);
	}

	/**
	 * Get current request cookies
	 *
	 * @return return cookies
	 */
	Map<String, Cookie> cookies();

	/**
	 * Get String Cookie Value
	 *
	 * @param name cookie name
	 * @return Return Cookie Value
	 */
	default String getCookieValue(@NonNull String name) {
		Cookie cookie = cookies().get(name);
		return null == cookie ? null : cookie.getValue();
	}

	default Cookie getCookie(@NonNull String name) {
		return cookies().get(name);
	}

	/**
	 * Add a cookie to the request
	 *
	 * @param cookie cookie raw
	 * @return return Request instance
	 */
	Request setCookie(Cookie cookie);

	/**
	 * Get current request headers.
	 *
	 * @return Return header information Map
	 */
	Map<String, String> getHeaders();

	/**
	 * Get header information
	 *
	 * @param name Parameter name
	 * @return Return header information
	 */
	default String header(@NonNull String name) {
		String header = "";
		if (getHeaders().containsKey(name)) {
			header = getHeaders().get(name);
		} else if (getHeaders().containsKey(name.toLowerCase())) {
			header = getHeaders().get(name.toLowerCase());
		}
		return header;
	}

	/**
	 * Get current request is KeepAlive, HTTP1.1 is true.
	 *
	 * @return return current request connection keepAlive
	 */
	boolean isKeepAlive();

	/**
	 * Get current request attributes
	 *
	 * @return Return all Attribute in Request
	 */
	Map<String, Object> getAttributes();

	/**
	 * Setting Request Attribute
	 *
	 * @param name  attribute name
	 * @param value attribute Value
	 * @return set attribute value and return current request instance
	 */
	default Request getAttribute(@NonNull String name, Object value) {
		this.getAttributes().put(name, value);
		return this;
	}

	/**
	 * Get a Request Attribute
	 *
	 * @param name Parameter name
	 * @return Return parameter value
	 */
	default <T> T getAttribute(String name) {
		if (null == name) {
			return null;
		}
		Object object = getAttributes().get(name);
		return null != object ? (T) object : null;
	}


	boolean isMultipart();

	/**
	 * Get current request body as ByteBuf
	 *
	 * @return Return request body
	 */
	ByteBuf body();

	/**
	 * Get current request body as string
	 *
	 * @return return request body to string
	 */
	default String bodyToString() {
		return this.body().toString(CharsetUtil.UTF_8);
	}

}
