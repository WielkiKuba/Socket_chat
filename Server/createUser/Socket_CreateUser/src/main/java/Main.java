import javax.management.loading.ClassLoaderRepository;
import java.sql.*;
import java.util.Scanner;

public class Main {
    public static final String url = "jdbc:mysql://jakubdomain.ddns.net:3306/Messenger";
    public static final String user = "WORLD_CLIENT";
    public static final String password = "Brzozowa5";
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int times = 50;
        while(true){
            Clear(times);
            System.out.println("Enter an option");
            System.out.println("=========================");
            System.out.println("1-Show all users");
            System.out.println("2-Create a new user");
            System.out.println("3-Delete a user");
            System.out.println("=========================");
            String option = scanner.nextLine();
            int optionInt = 0;
            try{
                optionInt = Integer.parseInt(option);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
            switch (optionInt){
                case (1)->{
                    try{
                        Statement statement = sqlConnection().createStatement();
                        boolean isEmpty = true;
                        ResultSet resultSet = statement.executeQuery("SELECT name,password FROM users;");
                        System.out.println("=========================");
                        while(resultSet.next()){
                            isEmpty = false;
                            String result = resultSet.getString("name");
                            String result1 = resultSet.getString("password");
                            System.out.println("Login: "+result+"| password: "+result1);
                        }
                        if(isEmpty){
                            System.out.println("List is empty");
                        }
                        System.out.println("=========================");
                        waitThread(5000);
                    }catch (SQLException e){
                        e.printStackTrace();
                    }
                }
                case (2)->{
                    System.out.println("Enter a name");
                    String name = scanner.nextLine();
                    System.out.println("Enter a password");
                    String password = scanner.nextLine();
                    try{
                        PreparedStatement preparedStatement = sqlConnection().prepareStatement("INSERT INTO users(name,password) VALUES (?,?);");
                        preparedStatement.setString(1,name);
                        preparedStatement.setString(2,password);
                        preparedStatement.executeUpdate();
                    }catch (SQLException e){
                        e.printStackTrace();
                    }
                }
                case (3)->{
                    System.out.println("Enter a name");
                    String name = scanner.nextLine();
                    try{
                        PreparedStatement preparedStatement = sqlConnection().prepareStatement("DELETE FROM users WHERE name = ?;");
                        preparedStatement.setString(1,name);
                        preparedStatement.executeUpdate();
                    }catch (SQLException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public static Connection sqlConnection() {
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url,user,password);
//            Statement statement =  connection.createStatement();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }
    public static void Clear(int times){
        for(int i=0;i<times;i++){
            System.out.println(" ");
        }
    }
    public static void waitThread(int time){
        try{
            Thread.sleep(time);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
