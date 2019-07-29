package org.ael.mvc.http;

import org.ael.mvc.constant.HttpConstant;
import org.ael.mvc.http.body.Body;
import org.ael.mvc.http.body.EmptyBody;
import sun.invoke.empty.Empty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: aorxsr
 * @Date: 2019/7/24 20:06
 */
public class HttpResponse implements Response {

	private Body body;

	private int status = 200;
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
		return headers.get(HttpConstant.CONTENTTYPE);
	}

	@Override
	public void setContentType(String value) {
		headers.put(HttpConstant.CONTENTTYPE, value);
	}

	@Override
	public void write(Body body) {
		this.body = body;
	}

	@Override
	public Body getBody() {
		if (null == body) {
			return EmptyBody.of();
		}
		return body;
	}

}
