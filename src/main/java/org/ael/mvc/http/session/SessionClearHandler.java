package org.ael.mvc.http.session;

import lombok.Getter;

import java.time.Instant;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 18:00
 */
public class SessionClearHandler implements Runnable {

    @Getter
    private SessionManager sessionManager;

    public SessionClearHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        long second = Instant.now().getEpochSecond();
        sessionManager.getSessionMap().values().stream().filter(session -> second < session.getExpired()).forEach(sessionManager::destructionSession);
    }

}
