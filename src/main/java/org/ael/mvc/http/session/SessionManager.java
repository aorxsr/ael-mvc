package org.ael.mvc.http.session;

import lombok.Getter;
import org.ael.mvc.http.Session;
import sun.plugin2.message.Message;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 17:28
 */
public class SessionManager {

    MessageDigest md = null;

    @Getter
    private Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    public Session putSession(Session session) {
        sessionMap.put(session.getId(), session);
        return session;
    }

    public synchronized String generateId() {
        try {
            md = MessageDigest.getInstance("MD5");
            UUID uuid = UUID.randomUUID();
            String guidStr = uuid.toString();
            md.update(guidStr.getBytes(), 0, guidStr.length());
            String sessionId = new BigInteger(1, md.digest()).toString(16);

            if (sessionMap.containsKey(sessionId)) {
                return generateId();
            } else {
                return sessionId;
            }
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Generate SessionId Fail...");
            return generateId();
        }
    }

    public Session getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }

    public void destructionSession(String id) {
        sessionMap.remove(id);
    }

    public void destructionSession(Session session) {
        sessionMap.remove(session.getId());
    }

}
