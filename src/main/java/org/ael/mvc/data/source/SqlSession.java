package org.ael.mvc.data.source;

import lombok.Data;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SqlSession {

    private Connection connection;

    void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }
    boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }
    void commit() throws SQLException {
        connection.commit();
    }
    void rollback() throws SQLException {
        connection.rollback();
    }

    ResultSet query(String sql) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    List query(String sql, Class clazz) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            Field[] fields = clazz.getDeclaredFields();

            List values = new ArrayList();

            while(resultSet.next()) {//对每一条记录进行操作
                Object instance = clazz.newInstance();//构造业务对象实体
                //将每一个字段取出进行赋值
                for (int i = 1; i <= columnCount; i++) {
                    Object value = resultSet.getObject(i);
                    //寻找该列对应的对象属性
                    for (int j = 0; j < fields.length; j++) {
                        Field f = fields[j];
                        //如果匹配进行赋值
                        if (f.getName().equalsIgnoreCase(metaData.getColumnName(i))) {
                            boolean flag = f.isAccessible();
                            f.setAccessible(true);
                            f.set(instance, value);
                            f.setAccessible(flag);
                        }
                    }
                }
                values.add(instance);
            }

            return values;
        } catch (InstantiationException | SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    List<Map<String, Object>> queryToLM(String sql) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            List<Map<String, Object>> values = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> hm = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String key = metaData.getColumnLabel(i);
                    Object value = resultSet.getObject(i);
                    hm.put(key, value);
                }
                values.add(hm);
            }

            return values;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
