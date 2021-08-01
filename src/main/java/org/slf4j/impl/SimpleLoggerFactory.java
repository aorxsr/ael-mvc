package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

public class SimpleLoggerFactory implements ILoggerFactory {
    ConcurrentHashMap<String, Logger> logs = null;

    public SimpleLoggerFactory() {
        this.logs = new ConcurrentHashMap();
        SimpleLogger.lazyInit();
    }

    public Logger getLogger(String name) {
        Logger logger = this.logs.get(name);
        if (null != logger) {
            return logger;
        } else {
            Logger newLogger = new SimpleLogger(name);
            Logger old = this.logs.putIfAbsent(name, newLogger);
            return (null == old ? newLogger : old);
        }
    }
}
