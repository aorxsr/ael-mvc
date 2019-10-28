package org.ael.mvc.data.source;

import java.util.List;
import java.util.Map;

public class DataSourceTest {

    public static void main(String[] args) {

        DataSourceFactory dataSourceFactory = new DataSourceFactory();

        dataSourceFactory = dataSourceFactory.createDataSource("default", "root", "9624344i.",
                "com.mysql.jdbc.Driver", "jdbc:mysql://39.108.13.116:3306/demo?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useSSL=true&serverTimezone=GMT%2b8",
                3, 1, 3000);

        Map<String, DataSource> dataSources = dataSourceFactory.getDataSources();

        DataSource dataSource = dataSources.get("default");

        SqlSession sqlSession = dataSource.getSqlSession();

        List query = sqlSession.query("SELECT * FROM user", User.class);

        query.forEach(u -> System.out.println(u.toString()));

        List<Map<String, Object>> maps = sqlSession.queryToLM("SELECT * FROM user");

        maps.forEach(m -> System.out.println(m));

    }

}

class User {
    private String name;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
