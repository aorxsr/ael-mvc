package org.ael.mvc.http.session;

import lombok.var;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 18:00
 */
public class SessionClearHandler implements Runnable {

	private SessionManager sessionManager;

	public SessionClearHandler(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	@Override
	public void run() {
		sessionManager.getSessionMap().values().stream().filter(session -> {
			session.
		})
	}

}
