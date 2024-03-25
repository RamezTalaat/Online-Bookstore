package BuisnessLogic.Controllers;

import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Models.Book;
import BuisnessLogic.Models.BorrowRequest;
import BuisnessLogic.Models.User;
import Communication.ICommunicator;
import DbContext.DbConnection;

import java.util.ArrayList;

public class AdminController {
    private ICommunicator communicator;
    public AdminController(ICommunicator communicator_)
    {
        communicator = communicator_;
    }
    public void handleAdmin(){
        String choice;
        while (true){
            choice = communicator.receiveMessage();
            switch (choice){
                case "get available books":{
                    System.out.println("admin in available books function");
                    BookController bookController = new BookController();
                    ArrayList<Book> result = bookController.getAvailableBooksAdmin();
                    Response response = new Response();
                    if(result.isEmpty()){
                        response.status = 400;
                        response.message = "No books available now";

                    }else{
                        response.status = 200;
                        response.message = "available books retrieved successfully";
                        response.object = result;
                    }
                    System.out.println("browse response = " + response);
                    communicator.sendResponse(response);
                    break;
                }
                case "get borrowed books":{
                    System.out.println("admin in borrowed books function");
                    BookController bookController = new BookController();
                    ArrayList<Book> result = bookController.getBorrowedBooksAdmin();
                    Response response = new Response();
                    if(result.isEmpty()){
                        response.status = 400;
                        response.message = "No books borrowed now";

                    }else{
                        response.status = 200;
                        response.message = "borrowed books retrieved successfully";
                        response.object = result;
                    }
                    System.out.println("browse response = " + response);
                    communicator.sendResponse(response);
                    break;
                }
                case "get book by id":{
                    getBookById();
                    break;
                }
                case "get user by id":{
                    getUserById();
                    break;
                }
                case "get borrow requests":{
                    System.out.println("admin in get borrow requests function");
                    RequestController requestController = new RequestController();
                    var requests = requestController.getBorrowRequests();

                    if(requests == null ||requests.isEmpty() ){
                        returnFailureResponse("No borrow requests yet");
                        break;
                    }

                    Response response = new Response();
                    response.status = 200;
                    response.message = "Borrow requests retrieved successfully";
                    response.object = requests;

                    System.out.println("browse response = " + response);
                    communicator.sendResponse(response);
                    break;
                }
                case "sign out":{
                    System.out.println("Admin Signing out");
                    //remove user form active users list in server
                    return;
                    //break;
                }
                default:{
                    Response response  = new Response();
                    response.status = 400;
                    response.message = "Error : Server could not parse request properly , please try again ";
                    communicator.sendResponse(response);
                    break;
                }
            }
        }
        //Sign out code , close current socket connection
    }

    public void returnFailureResponse(String message){
        Response response = new Response();
        response.status = 400;
        response.message = message;
        communicator.sendResponse(response);
    }
    public void returnSuccessResponse(String message){
        Response response = new Response();
        response.status = 200;
        response.message = message;
        communicator.sendResponse(response);
    }
    public void getBookById(){
        System.out.println("admin in get book by id function.");
        BookController bookController = new BookController();
        String stringBookId = communicator.receiveMessage();
        int bookId = Integer.parseInt(stringBookId);
        Book result = bookController.getBookById(bookId);
        Response response = new Response();
        if(result == null){
            response.status = 400;
            response.message = "No book with this id";

        }else{
            response.status = 200;
            response.message = "book retrieved successfully";
            response.object = result;
        }
        communicator.sendResponse(response);
    }
    public void getUserById(){
        String stringUserId = communicator.receiveMessage();
        int userid = Integer.parseInt(stringUserId);
        System.out.println("admin in get user by id function.");
        String query = "select * from users where id = '" + userid + "'";
        DbConnection dbConnection = new DbConnection();
        ArrayList<User> result  = dbConnection.select(User.class , query);
        Response response = new Response();
        if(result == null || result.isEmpty()){
            response.status = 400;
            response.message = "No user with this id";

        }else{
            response.status = 200;
            response.message = "user retrieved successfully";
            response.object = result.get(0); //to return user object not wrapped in list
        }
        communicator.sendResponse(response);
    }
}
