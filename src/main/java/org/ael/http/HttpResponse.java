package org.ael.http;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import org.ael.constant.HttpConstant;
import org.ael.http.body.Body;
import org.ael.http.body.EmptyBody;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: aorxsr
 * @Date: 2019/7/24 20:06
 */
public class HttpResponse implements Response {

    private Body body;

    private int status = 200;
    private Map<String, String> headers = new ConcurrentHashMap<>();
    private Set<io.netty.handler.codec.http.cookie.Cookie> nettyCookies = new HashSet<>();

    private Set<Cookie> cookies = new HashSet<>();

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
    public void addHeader(String key, int value) {
        headers.put(key, Integer.toString(value));
    }

    @Override
    public void removeHeader(String key) {
        headers.remove(key);
    }

    @Override
    public String getContentType() {
        return headers.get(HttpConstant.CONTENT_TYPE);
    }

    @Override
    public void setContentType(String value) {
        headers.put(HttpConstant.CONTENT_TYPE, value);
    }

    @Override
    public Cookie getCookie(String name) {
        Optional<Cookie> cookie = cookies.stream().filter(coo -> coo.name().equals(name)).findFirst();
        return cookie.isPresent() ? cookie.get() : null;
    }

    @Override
    public Set<Cookie> getCookies() {
        return cookies;
    }

    @Override
    public Cookie setCookie(Cookie cookie) {
        cookies.add(cookie);
        return cookie;
    }

    @Override
    public Cookie setCookie(String name, String value) {
        Cookie cookie = new DefaultCookie(name, value);
        cookies.add(cookie);
        return cookie;
    }

    @Override
    public Set<io.netty.handler.codec.http.cookie.Cookie> getNettyCookies() {
        if (cookies.size() == nettyCookies.size()) {
            return nettyCookies;
        } else {
            nettyCookies.clear();
            cookies.forEach(cookie -> {
                io.netty.handler.codec.http.cookie.Cookie nettyCookie = new DefaultCookie(cookie.name(), cookie.value());
                nettyCookie.setDomain(cookie.domain());
                Boolean httpOnly = cookie.isHttpOnly();
                if (null != httpOnly) {
                    nettyCookie.setHttpOnly(httpOnly);
                }
                Long maxAge = cookie.maxAge();
                if (null != maxAge) {
                    nettyCookie.setMaxAge(maxAge);
                }
                nettyCookie.setPath(cookie.path());
                Boolean secure = cookie.isSecure();
                if (null != secure) {
                    nettyCookie.setSecure(secure);
                }
                nettyCookies.add(nettyCookie);
            });
            return nettyCookies;
        }
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
