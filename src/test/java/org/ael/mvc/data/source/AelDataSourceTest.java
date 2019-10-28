package org.ael.mvc.data.source;

import org.ael.mvc.Ael;

public class AelDataSourceTest {

    public static void main(String[] args) {

        Ael ael = new Ael();
        ael.addDataSource("default", "root", "9624344i.",
                "com.mysql.jdbc.Driver", "jdbc:mysql://39.108.13.116:3306/demo?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useSSL=true&serverTimezone=GMT%2b8",
                3, 1, 3000);


    }

}
