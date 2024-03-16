import BuisnessLogic.Authentication.ActiveDatabase;
import BuisnessLogic.Models.Book;
import BuisnessLogic.Models.BorrowRequest;
import BuisnessLogic.Models.Review;
import BuisnessLogic.Models.User;
import DbContext.DbConnection;

import java.sql.Array;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
//        DbConnection db = new DbConnection();
//        ArrayList<BorrowRequest> users = db.select(BorrowRequest.class , "select * from borrowRequests ");
//        for (int i =  0; i< users.size() ; i++){
//            System.out.println(users.get(i));
//        }

        ActiveDatabase db = ActiveDatabase.getInstance();
        db.printActiveDatabase();
        UUID uuid =  db.addUser(11);

        System.out.println("User id = "  + db.getUserID(uuid));
        db.printActiveDatabase();
        ActiveDatabase db2 = ActiveDatabase.getInstance();
        System.out.println("New DB");
        db.printActiveDatabase();
        //Connection connection = Db.connectToDb();
       // Db.createTable(connection, "za3bola");
    }
}