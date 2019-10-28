package org.ael.mvc.data.source;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Vector;

@AllArgsConstructor
public class DataSource {

    private String userName;
    private String password;
    private String driverClassName;
    private String url;
    private int max;
    private int initSize;
    private int timeout;

    private List<SqlSession> freeSessions = new Vector<>();
    private List<SqlSession> useSession = new Vector<>();

    public DataSource(String userName, String password, String driverClassName, String url, int max, int initSize, int timeout) {
        this.userName = userName;
        this.password = password;
        this.driverClassName = driverClassName;
        this.url = url;
        this.max = max;
        this.initSize = initSize;
        this.timeout = timeout;
    }

    public void init() {
        for (int i = 0; i < initSize; i++) {
            createSqlSession();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            freeSessions.forEach(this::sqlSessionClose);
            useSession.forEach(this::sqlSessionClose);
        }));
    }

    private void sqlSessionClose(SqlSession session) {
        try {
            session.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createSqlSession() {
        if (max > (freeSessions.size() + useSession.size())) {
            try {
                Class.forName(driverClassName);
                Connection connection = DriverManager.getConnection(url, userName, password);
                SqlSession sqlSession = new SqlSession();
                sqlSession.setConnection(connection);
                freeSessions.add(sqlSession);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized SqlSession getSqlSession() {
        long epochSecond = Instant.now().getEpochSecond();
        for (;;) {
            long c = Instant.now().getEpochSecond() - epochSecond;
            if (timeout <= c) {
                throw new RuntimeException("get connection timeout...");
            }
            if (freeSessions.isEmpty()) {
                createSqlSession();
            } else {
                SqlSession sqlSession = freeSessions.get(0);
                freeSessions.remove(0);
                useSession.add(sqlSession);
                return sqlSession;
            }
        }
    }

}
