package org.ael.mvc.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import lombok.var;
import org.ael.mvc.Environment;
import org.ael.mvc.constant.EnvironmentConstant;
import org.ael.mvc.constant.HttpConstant;
import org.ael.mvc.http.session.SessionHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static org.ael.mvc.constant.HttpConstant.HOST;

/**
 * @Author: aorxsr
 * @Date: 2019/7/18 18:26
 */
public class HttpRequest implements Request {

	private final static SessionHandler SESSION_HANDLER = new SessionHandler();

	private final static String GZIP = "gzip";
	private final static String WEN = "?";

	private String uri;
	private String url;
	private String host;
	private String method;

	private boolean keepAlive;
	private String remoteAddress;

	private Session session;

	private Map<String, String> headers = new HashMap<>(8);
	private Map<String, Object> parameters = new HashMap<>(8);
	private Set<Cookie> cookies = new HashSet<>(8);

	private io.netty.handler.codec.http.HttpRequest nettyRequest;
	private Queue<HttpContent> contents = new LinkedList<>();

	public void setNettyRequest(io.netty.handler.codec.http.HttpRequest httpRequest) {
		this.nettyRequest = httpRequest;
	}

	public void appendHttpContent(HttpContent content) {
		contents.add(content.retain());
	}

	/**
	 * init request
	 *
	 * @param remoteAddress
	 */
	public void initRequest(String remoteAddress) {
		// url
		url = nettyRequest.uri();
		// uri init
		int wenIndex = this.url.indexOf('?');
		this.uri = wenIndex < 0 ? this.url : this.url.substring(0, wenIndex);
		// remoteAddress
		this.remoteAddress = remoteAddress;
		// headers
		nettyRequest.headers().forEach(header -> headers.put(header.getKey(), header.getValue()));
		// Method Type
		method = nettyRequest.method().name();
		// keepAlive
		keepAlive = HttpUtil.isKeepAlive(nettyRequest);
		// cookie init
		String cookieString = headers.get(HttpConstant.COOKIE);
		if (!StringUtil.isNullOrEmpty(cookieString)) {
			ServerCookieDecoder.LAX.decode(cookieString).forEach(cookie -> cookies.add(new Cookie(cookie.name(), cookie.value(), cookie.maxAge(), cookie.domain(), cookie.path(),
					cookie.isSecure(), cookie.isHttpOnly())));
		}
		// parameter init
		if (url.contains(WEN)) {
			Map<String, List<String>> parameters =
					new QueryStringDecoder(url, CharsetUtil.UTF_8).parameters();
			if (null != parameters) {
				this.parameters.putAll(parameters);
			}
		}

		// session
		session = SESSION_HANDLER.getSession(getCookieValue(HttpConstant.DEFAULT_SESSION_KEY));


		if (HttpMethod.GET.name().equalsIgnoreCase(method)) {
			return;
		}

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

	@Override
	public String getHost() {
		if (StringUtil.isNullOrEmpty(host)) {
			if (headers.containsKey(HOST)) {
				host = headers.get(HOST);
			}
		}
		return host;
	}

	@Override
	public String getRemoteAddress() {
		return remoteAddress;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public Map<String, String> getPathParams() {
		return null;
	}

	@Override
	public Map<String, List<String>> getParameters() {
		return null;
	}

	@Override
	public boolean isUseGZIP() {
		if (WebContent.ael.getEnvironment().getBoolean(EnvironmentConstant.HTTP_ZIP)) {
			return false;
		}
		// 判断是否有 gzip 标识
		String acceptEncoding = headers.get(HttpConstant.ACCEPT_ENCODING);
		if (StringUtil.isNullOrEmpty(acceptEncoding)) {
			return false;
		}
		return acceptEncoding.contains(GZIP);
	}

	@Override
	public Session getSession() {
		return null;
	}

	@Override
	public Map<String, Cookie> cookies() {
		return null;
	}

	@Override
	public Request setCookie(Cookie cookie) {
		return null;
	}

	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public boolean isKeepAlive() {
		return keepAlive;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return null;
	}

	@Override
	public boolean isMultipart() {
		return false;
	}

	@Override
	public ByteBuf body() {
		return null;
	}

}
