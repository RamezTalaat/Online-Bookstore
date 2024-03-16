import BuisnessLogic.Models.Book;
import BuisnessLogic.Models.BorrowRequest;
import BuisnessLogic.Models.Review;
import BuisnessLogic.Models.User;
import DbContext.DbConnection;

import java.sql.Array;
import java.sql.Connection;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        DbConnection db = new DbConnection();
        ArrayList<BorrowRequest> users = db.select(BorrowRequest.class , "select * from borrowRequests ");
        for (int i =  0; i< users.size() ; i++){
            System.out.println(users.get(i));
        }
        //Connection connection = Db.connectToDb();
       // Db.createTable(connection, "za3bola");
    }
}