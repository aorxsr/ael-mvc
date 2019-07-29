package org.ael.mvc.http;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 16:32
 */
public interface Session {

	String getId();
	Session setId(String id);

	String getIp();
	Session setIp(String ip);

	Session setAttribute(String attribute);
	Session getAttribute(String name);

	long getExpired();
	Session setExpired();

	long getCreated();
	Session setCreated();

}
