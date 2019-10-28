package org.ael.mvc.data.source;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
@Getter
public class DataSourceFactory {

    private Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    public DataSourceFactory createDataSource(String dataSourceName, String userName, String password, String driverClassName, String url,
                                              int max, int initSize, int timeout) {
        if (dataSources.containsKey(dataSourceName)) {
            throw new RuntimeException("dataSourceName repeat");
        }
        DataSource dataSource = new DataSource(userName, password, driverClassName, url, max, initSize, timeout);
        dataSource.init();
        dataSources.put(dataSourceName, dataSource);
        return this;
    }

}
