package org.ael.mvc.http;

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

}
