package org.ael.mvc.http.session;

import org.ael.mvc.http.Session;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 17:27
 */
public class SessionHandler {

	private SessionManager sessionManager;

	public Session getSession(String sessionId) {
		Session session = sessionManager.getSession(sessionId);
		if (null == session) {
			// create session


		}

		return session;
	}

}
