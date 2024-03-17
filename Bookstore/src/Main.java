import BuisnessLogic.Authentication.ActiveDatabase;
import BuisnessLogic.Authentication.IAuthenticator;
import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Authentication.UUIDAuthenticator;
import BuisnessLogic.Controllers.BookController;
import Communication.ServerCommunicator;

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
        BookController book = new BookController();
        book.removeBook(2);

        //Connection connection = Db.connectToDb();
       // Db.createTable(connection, "za3bola");

//        IAuthenticator authenticator = new UUIDAuthenticator();
//        Response response = authenticator.signIn( "tarekwer" , "tarek123wer");
//        UUID uuid = (UUID) response.object;
//        System.out.println(response);
//        ActiveDatabase db = ActiveDatabase.getInstance();
//        db.printActiveDatabase();
//        authenticator.signOut(uuid);
//        db.printActiveDatabase();

//        ServerCommunicator serverCommunicator = new ServerCommunicator();
    }
}