import BuisnessLogic.Authentication.ActiveDatabase;
import BuisnessLogic.Authentication.IAuthenticator;
import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Authentication.UUIDAuthenticator;
import BuisnessLogic.Models.Book;
import BuisnessLogic.Models.BorrowRequest;
import BuisnessLogic.Models.Review;
import BuisnessLogic.Models.User;
import DbContext.DbConnection;

import java.net.Authenticator;
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

//        ActiveDatabase db = ActiveDatabase.getInstance();
//        db.printActiveDatabase();
//        UUID uuid =  db.addUser(11);


        //Connection connection = Db.connectToDb();
       // Db.createTable(connection, "za3bola");

        IAuthenticator authenticator = new UUIDAuthenticator();
        Response response = authenticator.signUp("tarek" , "tarek" , "tarek123");
        System.out.println(response);
        ActiveDatabase db = ActiveDatabase.getInstance();
        db.printActiveDatabase();
    }
}