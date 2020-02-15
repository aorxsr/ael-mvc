package org.ael.http.inter;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 16:32
 */
public interface Session {

	String getId();
	Session setId(String id);

	String getIp();
	Session setIp(String ip);

	Session setAttribute(String name,Object value);
	Object getAttribute(String name);

	long getExpired();
	Session setExpired(long expired);

	long getCreated();
	Session setCreated(long created);

}
