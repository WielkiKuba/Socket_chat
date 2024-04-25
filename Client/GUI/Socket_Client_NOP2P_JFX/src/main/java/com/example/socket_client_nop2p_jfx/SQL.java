package com.example.socket_client_nop2p_jfx;

import java.sql.*;
public class SQL {
    public static final String url = "jdbc:mysql://jakubdomain.ddns.net:3306/Messenger";
    public static final String user = "WORLD_CLIENT";
    public static final String password = "Brzozowa5";

    public static Connection sqlConnection() {
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,user,password);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }
    public static Boolean Login(String name,String password){
        Boolean loginSuccessful = false;
        String result = " ";
        try{
            Statement statement = sqlConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT password FROM users WHERE name = '"+name+"'");
            while(resultSet.next()){
                result = resultSet.getString("password");
            }
            if(result.equals(password)){
                loginSuccessful = true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return loginSuccessful;
    }
}