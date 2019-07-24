package org.ael.mvc.http;

import io.netty.handler.codec.http.HttpContent;
import io.netty.util.internal.StringUtil;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @Author: aorxsr
 * @Date: 2019/7/18 18:26
 */
public class HttpRequest implements Request {

	private String uri;
	private String method;

	private String remoteAddress;

	private io.netty.handler.codec.http.HttpRequest nettyRequest;

	private Queue<HttpContent> contents = new LinkedList<>();

	public void setNettyRequest(io.netty.handler.codec.http.HttpRequest msg) {
		this.nettyRequest = msg;
	}

	public void appendHttpContent(HttpContent content) {
		contents.add(content.retain());
	}

	/**
	 * 初始化
	 *
	 * @param remoteAddress
	 */
	public void initRequest(String remoteAddress) {
		this.remoteAddress = remoteAddress;

		// Method Type


	}

	@Override
	public String getMethod() {
		if (StringUtil.isNullOrEmpty(method)) {
			this.method = nettyRequest.method().name();
		}
		return method;
	}

	@Override
	public String getUri() {
		if (StringUtil.isNullOrEmpty(uri)) {
			uri = nettyRequest.uri();
		}
		return uri;
	}

}
