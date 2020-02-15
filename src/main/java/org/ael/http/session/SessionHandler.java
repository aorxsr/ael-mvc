package org.ael.http.session;

import io.netty.util.internal.StringUtil;
import lombok.Getter;
import org.ael.http.HttpSession;
import org.ael.http.inter.Session;

import java.time.Instant;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 17:27
 */
public class SessionHandler {

    /**
     * 过期时间
     */
    private final static long EXPIRED_TIME = 180000L;

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
            session.setExpired(now + EXPIRED_TIME);
            if (!StringUtil.isNullOrEmpty(ip)) {
                session.setIp(ip);
            }
            return session;
        } else {
            session = sessionManager.getSession(sessionId);
            if (null == session) {
                session = new HttpSession();
                session.setId(sessionManager.generateId());
                session.setIp(ip);
            } else {
                // 更新时间
                session.setExpired(Instant.now().getEpochSecond() + EXPIRED_TIME);
            }
        }
        sessionManager.putSession(session);

        return session;
    }

}
