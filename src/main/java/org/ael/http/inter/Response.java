package org.ael.http.inter;

import io.netty.handler.codec.http.cookie.Cookie;
import org.ael.constant.ContentType;
import org.ael.constant.HttpConstant;
import org.ael.http.body.Body;
import org.ael.http.body.StreamBody;
import org.ael.http.body.StringBody;
import org.ael.http.body.ViewBody;
import org.ael.exception.NotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

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

	void addHeader(String key, int value);

	void removeHeader(String key);

	String getContentType();

	void setContentType(String value);

	/* cookie */
	Cookie getCookie(String name);

	Set<Cookie> getCookies();

	Cookie setCookie(Cookie cookie);

	Cookie setCookie(String name, String value);

	Set<io.netty.handler.codec.http.cookie.Cookie> getNettyCookies();

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
		setContentType(HttpConstant.TEXT_HTML);
		write(ViewBody.of(htmlUrl));
	}

	default void download(File file, String fileName) throws NotFoundException, UnsupportedEncodingException, FileNotFoundException {
		if (!file.exists() || !file.isFile()) {
			throw new NotFoundException("Not found file: " + file.getPath());
		}
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
		setContentType(ContentType.get(suffix));
		addHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO8859_1"));
		addHeader("Content-Length", String.valueOf(file.length()));
		write(new StreamBody(new FileInputStream(file)));
	}

	void write(Body body);

	Body getBody();

}
