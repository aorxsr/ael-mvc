package org.ael.orm;

import lombok.Data;
import org.ael.ioc.core.annotation.Bean;

@Data
@Bean
public class DataSource {

    private String username;
    private String password;
    private String url;

}
