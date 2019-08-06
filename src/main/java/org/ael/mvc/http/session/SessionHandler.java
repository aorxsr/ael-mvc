package org.ael.mvc.http.session;

import io.netty.util.internal.StringUtil;
import lombok.Getter;
import org.ael.mvc.http.HttpSession;
import org.ael.mvc.http.Session;

import java.time.Instant;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 17:27
 */
public class SessionHandler {

	// 过期时间
	private final static long expiredTime = 180000L;

	@Getter
	private SessionManager sessionManager;

	public SessionHandler(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public Session getSession(String sessionId, String ip) {
		Session session;
		if (StringUtil.isNullOrEmpty(sessionId)) {
			// create session
			session = new HttpSession();
			session.setId(sessionManager.generateId());
			long now = Instant.now().getEpochSecond();
			session.setCreated(now);
			session.setExpired(now + expiredTime);
			if (!StringUtil.isNullOrEmpty(ip)) {
				session.setIp(ip);
			}
			return session;
		} else {
			session = sessionManager.getSession(sessionId);
			// 更新时间
			session.setExpired(Instant.now().getEpochSecond() + expiredTime);
			sessionManager.putSession(session);
		}

		return session;
	}

}
