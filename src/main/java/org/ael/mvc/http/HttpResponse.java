package org.ael.mvc.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: aorxsr
 * @Date: 2019/7/24 20:06
 */
public class HttpResponse implements Response {

	private int status = 200;
	private String contentType;
	private Map<String, String> headers = new ConcurrentHashMap<>();

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public String getHeader(String key) {
		return headers.get(key);
	}

	@Override
	public void addHeader(String key, String value) {
		headers.put(key, value);
	}

	@Override
	public void removeHeader(String key) {
		headers.remove(key);
	}

	@Override
	public String getContentType() {
		return headers.get("");
	}

	@Override
	public void setContentType(String value) {
		contentType = value;
	}

}
