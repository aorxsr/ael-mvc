package org.ael.mvc.http;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 18:05
 */
public class HttpSession implements Session {

	private String id;

	@Override
	public String getId() {
		return null;
	}

	@Override
	public Session setId(String id) {
		return null;
	}

	@Override
	public String getIp() {
		return null;
	}

	@Override
	public Session setIp(String ip) {
		return null;
	}

	@Override
	public Session setAttribute(String attribute) {
		return null;
	}

	@Override
	public Session getAttribute(String name) {
		return null;
	}

	@Override
	public long getExpired() {
		return 0;
	}

	@Override
	public Session setExpired() {
		return null;
	}

	@Override
	public long getCreated() {
		return 0;
	}

	@Override
	public Session setCreated() {
		return null;
	}
}
