package org.ael.mvc.http;

import org.ael.mvc.constant.HttpConstant;
import org.ael.mvc.http.body.Body;
import org.ael.mvc.http.body.StringBody;
import org.ael.mvc.http.body.ViewBody;

import java.util.Map;

/**
 * @Author: aorxsr
 * @Date: 2019/7/24 19:54
 */
public interface Response {

	int getStatus();
	void setStatus(int status);

	Map<String, String> getHeaders();
	String getHeader(String key);
	void addHeader(String key, String value);
	void removeHeader(String key);

	String getContentType();
	void setContentType(String value);

	/*  default impl */

	default void text(String text) {
		setContentType(HttpConstant.TEXT_PLAIN);
		write(StringBody.of(text));
	}

	default void json(String json) {
		setContentType(HttpConstant.APPLICATION_JSON);
		write(StringBody.of(json));
	}

	default void html(String htmlUrl) {
		setContentType(HttpConstant.APPLICATION_JSON);
		write(ViewBody.of(htmlUrl));
	}

	void write(Body body);
	Body getBody();

}
