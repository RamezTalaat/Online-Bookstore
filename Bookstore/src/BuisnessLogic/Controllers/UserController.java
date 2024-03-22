package BuisnessLogic.Controllers;

import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Models.Book;
import BuisnessLogic.Models.BorrowRequest;
import BuisnessLogic.Models.User;
import Communication.ICommunicator;

import java.util.ArrayList;

public class UserController {
    private static ICommunicator communicator;
    private User currentUser;
    public UserController(ICommunicator _communicator , User _currentUser){
        communicator = _communicator;
        currentUser = _currentUser;
    }

    public void handleUser(){
        String choice = "";
        while (choice != "sign out"){
            choice = communicator.receiveMessage();
            switch (choice){
                case "browse":{
                    System.out.println("user in browse function");
                    BookController bookController = new BookController();
                    ArrayList<Book> result = bookController.browseBooks();
                    Response response = new Response();
                    if(result.isEmpty()){
                        response.status = 400;
                        response.message = "No books to browse now";

                    }else{
                        response.status = 200;
                        response.message = "books retrieved successfully";
                        response.object = result;
                    }
                    System.out.println("browse response = " + response);
                    communicator.sendResponse(response);
                    break;
                }
                case "borrow request":{
                    System.out.println("In borrow request");
                    RequestController requestController = new RequestController();
                    String stringUserID = communicator.receiveMessage();
                    String stringBookID = communicator.receiveMessage();
                    int userId = Integer.parseInt(stringUserID);
                    int bookId = Integer.parseInt(stringBookID);
                    boolean result = requestController.submitBorrowRequest(userId , bookId);
                    Response response = new Response();
                    if(result){
                        response.status = 200;
                        response.message = "book borrow request submitted successfully";
                    }else{
                        response.status = 400;
                        response.message = "could not submit borrow request!";
                    }
                    communicator.sendResponse(response);
                    break;
                }
                case "get user books":{
                    System.out.println("user in browse function");
                    BookController bookController = new BookController();
                    ArrayList<Book> result = bookController.getUserBooks(currentUser.id);
                    Response response = new Response();
                    if(result.isEmpty()){
                        response.status = 400;
                        response.message = "No books to in your library yet";

                    }else{
                        response.status = 200;
                        response.message = "books retrieved successfully";
                        response.object = result;
                    }
                    System.out.println("get my books response = " + response);
                    communicator.sendResponse(response);
                    break;
                }
                case "get book by id":{
                    System.out.println("user in get book by id function");
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
                    break;
                }
                case "get borrow request history":{
                    System.out.println("user in get  borrow request history function");
                    BookController bookController = new BookController();
                    ArrayList<BorrowRequest> result = bookController.getBorrowRequestsHistory(currentUser.id);
                    Response response = new Response();
                    if(result.isEmpty()){
                        response.status = 400;
                        response.message = "No request history yet";

                    }else{
                        response.status = 200;
                        response.message = "requests history retrieved successfully";
                        response.object = result;
                    }
                    System.out.println("borrow requests history response = " + response);
                    communicator.sendResponse(response);
                    break;
                }
                case "sign out":{
                    System.out.println("User Signing out");
                    break;
                }default:{
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
