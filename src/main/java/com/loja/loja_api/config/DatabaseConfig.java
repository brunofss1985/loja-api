//package com.loja.loja_api.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//
//import javax.sql.DataSource;
//import java.net.URI;
//
//@Configuration
//public class DatabaseConfig {
//
//    @Value("${DATABASE_URL}")
//    private String databaseUrl;
//
//    @Bean
//    public DataSource dataSource() throws URISyntaxException {
//        String dbUrlEnv = System.getenv("DATABASE_URL");
//
//        if (dbUrlEnv == null) {
//            throw new IllegalStateException("DATABASE_URL não encontrada");
//        }
//
//        if (dbUrlEnv.startsWith("jdbc:")) {
//            // Já está em formato JDBC (local)
//            return DataSourceBuilder.create()
//                    .url(dbUrlEnv)
//                    .username(System.getenv("DB_USER"))
//                    .password(System.getenv("DB_PASSWORD"))
//                    .build();
//        }
//
//        // Heroku format: postgres://user:pass@host:port/db
//        URI dbUri = new URI(dbUrlEnv);
//        String[] userInfo = dbUri.getUserInfo().split(":");
//        String username = userInfo[0];
//        String password = userInfo[1];
//
//        String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
//
//        return DataSourceBuilder.create()
//                .url(jdbcUrl)
//                .username(username)
//                .password(password)
//                .build();
//    }
//
//}
