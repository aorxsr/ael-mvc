package org.ael.mvc.http.session;

import lombok.Getter;
import org.ael.mvc.http.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 17:28
 */
public class SessionManager {

	@Getter
	private Map<String, Session> sessionMap = new ConcurrentHashMap<>();

	public Session getSession(String sessionId) {
		return sessionMap.get(sessionId);
	}

}
