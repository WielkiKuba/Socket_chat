package com.example.toDoList;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class SQL {
    public static String getPasswordSQL(String clientName) {
        String sqlQuery = "SELECT password FROM users WHERE name = ?";
        String password = sendQuery(sqlQuery,clientName);
        return password;
    }
    public static String sendQuery(String sqlQuery,String variable){
        CorsConfig config = new CorsConfig();
        DriverManagerDataSource dataSource = config.dataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://jakubdomain.ddns.net:3306/Messenger");
        dataSource.setUsername("WORLD_CLIENT");
        dataSource.setPassword("Brzozowa5");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String recived = "";
        try{
            recived = jdbcTemplate.queryForObject(sqlQuery, String.class, variable);
        }catch (EmptyResultDataAccessException e){
            recived = "null";
        }
        return recived;
    }
    public static boolean isExist(String clientName){
        boolean isExist = false;
        String sqlQuery = "SELECT id FROM users WHERE name = ?";
        String id  = sendQuery(sqlQuery,clientName);
        if(!(id.equals("null"))){
            isExist = true;
        }
        return isExist;
    }
}
