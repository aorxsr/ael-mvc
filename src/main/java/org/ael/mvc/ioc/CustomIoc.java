package org.ael.mvc.ioc;

import org.ael.mvc.Ael;
import org.ael.mvc.ioc.bean.Bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomIoc {

    private Ael ael;

    public void initConfig(Ael ael) {
        this.ael = ael;
    }

    private Map<String, Bean> beans = new ConcurrentHashMap<>(32);


}
