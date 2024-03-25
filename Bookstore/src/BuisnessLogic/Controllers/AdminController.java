package BuisnessLogic.Controllers;

import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Models.Book;
import BuisnessLogic.Models.BorrowRequest;
import Communication.ICommunicator;

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
//                case "get borrow requests":{
//                    System.out.println("admin in get borrow requests function");
//                    BookController bookController = new BookController();
//                    ArrayList<BorrowRequest> result = bookController.getBorrowRequestsAdmin();
//                    Response response = new Response();
//                    if(result.isEmpty()){
//                        response.status = 400;
//                        response.message = "No borrow requests";
//
//                    }else{
//                        response.status = 200;
//                        response.message = "Borrow requests retrieved successfully";
//                        response.object = result;
//                    }
//                    System.out.println("browse response = " + response);
//                    communicator.sendResponse(response);
//                    break;
//                }
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
}
