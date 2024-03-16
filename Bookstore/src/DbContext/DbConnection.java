package DbContext;

import BuisnessLogic.Models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;


public class DbConnection {
    private final String url = "jdbc:postgresql://localhost:5432/";
    private final String dbName = "BookStore";
    private final String userName = "postgres";
    private final String password = "postgres";

    private final Connection connection ;

    public DbConnection() {
        connection = connectToDb();
    }

    private Connection connectToDb(){
        //System.out.println("in connect to db function");
        Connection connection = null;
        try {
            //Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url+dbName,userName,password);
            if(connection != null){
                System.out.println("Connected to postgresql successfully");
            }else{
                System.out.println("Connection Failed");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }

    public Connection getConnection(){
        return connection;
    }

     public <T> ArrayList<T> select(Class<T> tClass , String query ) {
         ResultSet resultSet;
         ArrayList<T> result = new ArrayList<>();
         try {
             //String query = "select * from users";
             Statement statement = connection.createStatement();
             resultSet = statement.executeQuery(query);

             ObjectMapper mapper = new ObjectMapper();
             while (resultSet.next()) {  //loops on result rows

                 T object = mapper.mapObject(tClass,resultSet); //passes object type and result set
                 result.add(object);
                 //System.out.println(object);
             }
             return result;
         } catch (Exception e) {
             e.printStackTrace();
             System.out.println("Error in selection statement");
             return  null;
         }

     }

     public boolean operate(String  query){
        try{
            Statement statement = connection.createStatement();
            int rowsAffected = statement.executeUpdate(query);
            if(rowsAffected > 0){
                System.out.println("Successful update");
                return true;
            }
            else{
                System.out.println("Unsuccessful update");
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error in create/update/delete operation");

        }
        return false;
     }

}


