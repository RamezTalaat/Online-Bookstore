package DbContext;


import java.sql.*;


public class DbConnection {
    public Connection connectToDb(String dbName, String userName, String password){
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbName,userName,password);
            if(connection != null){
                System.out.println("Connected to postgresql successfully");
            }else{
                System.out.println("Connection Failed");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return connection;
    }
    public void createTable(Connection connection, String tableName) {
        PreparedStatement statement = null;

        try {
            String sql = "CREATE TABLE " + tableName + " (id INTEGER, name VARCHAR(200), address VARCHAR(200), PRIMARY KEY (id))";
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();
            System.out.println(tableName + " Table created successfully");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    System.err.println("Error closing statement: " + e.getMessage());
                }
            }
        }
    }

}
