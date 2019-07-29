package org.ael.mvc.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 18:05
 */
public class HttpSession implements Session {

    private String id;
    private long created = -1;
    private long expired = -1;

    private Map<String, Object> attributes = new HashMap<>(8);

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Session setId(String id) {
        this.id = id;
        return this;
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
    public Session setAttribute(String name, Object value) {
        attributes.put(name, attributes);
        return this;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public long getExpired() {
        return expired;
    }

    @Override
    public Session setExpired(long expired) {
        this.expired = expired;
        return this;
    }

    @Override
    public long getCreated() {
        return created;
    }

    @Override
    public Session setCreated(long created) {
        this.created = created;
        return this;
    }

}
