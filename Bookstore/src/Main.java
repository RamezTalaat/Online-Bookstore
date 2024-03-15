import BuisnessLogic.Models.User;
import DbContext.DbConnection;

import java.sql.Array;
import java.sql.Connection;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        DbConnection db = new DbConnection();
        ArrayList<User> users = db.select(User.class , "select * from users");
        for (int i =  0; i< users.size() ; i++){
            System.out.println(users.get(i));
        }
        //Connection connection = Db.connectToDb();
       // Db.createTable(connection, "za3bola");
    }
}