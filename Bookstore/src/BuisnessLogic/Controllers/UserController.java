package BuisnessLogic.Controllers;

import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Models.Book;
import BuisnessLogic.Models.BorrowRequest;
import BuisnessLogic.Models.User;
import Communication.ICommunicator;
import DbContext.DbConnection;

import java.util.ArrayList;

public class UserController {
    private static ICommunicator communicator;
    private final User currentUser;
    public UserController(ICommunicator _communicator , User _currentUser){
        communicator = _communicator;
        currentUser = _currentUser;
    }

    public void handleUser(){
        String choice;
        while (true){
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
                case"add book":{
                    System.out.println("user in add book function");
                    BookController bookController = new BookController();
                    String price , genre , title , author ,description , quantity, currUserId;
                    price = communicator.receiveMessage();
                    genre = communicator.receiveMessage();
                    title = communicator.receiveMessage();
                    author = communicator.receiveMessage();
                    description = communicator.receiveMessage();
                    quantity = communicator.receiveMessage();
                    currUserId = communicator.receiveMessage();
                    double doublePrice = Double.parseDouble(price);
                    int intQuantity = Integer.parseInt(quantity);
                    int intCurrUserId = Integer.parseInt(currUserId);
                    Response response = new Response();
                    if(!bookController.addBook(doublePrice , genre , title , author , intQuantity , description, intCurrUserId))
                    {
                        response.status=400;
                        response.message="Couldn't add the book";
                        communicator.sendResponse(response);
                    }
                    response.status=200;
                    response.message = "Book added successfully!";
                    communicator.sendResponse(response);
                    break;
                }
                case "add review":{
                    System.out.println("user in add review function");
                    BookController bookController = new BookController();
                    String userId, bookId, rate, comment;
                    userId = communicator.receiveMessage();
                    bookId = communicator.receiveMessage();
                    rate = communicator.receiveMessage();
                    comment = communicator.receiveMessage();
                    int intRate = Integer.parseInt(rate);
                    int intUserId = Integer.parseInt(userId);
                    int intBookId = Integer.parseInt(bookId);
                    Response response = new Response();
                    if(!bookController.addReview(intUserId, comment, intRate,intBookId))
                    {
                        response.status=400;
                        response.message="Couldn't add the review on the book";
                        communicator.sendResponse(response);
                    }
                    response.status=200;
                    response.message = "Review added successfully!";
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
                case "get user by id":{
                    System.out.println("user in get user by id function");
                    String stringUserId = communicator.receiveMessage();
                    int userid = Integer.parseInt(stringUserId);

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
                case "Search":{
                    System.out.println("You are in the search mode");
                    String str = communicator.receiveMessage();
                    String value = communicator.receiveMessage();
                    BookController bookController = new BookController();
                    Response response = new Response();
                    ArrayList<Book> listOfBooks = bookController.searchForBook(str,value);
                    if(!listOfBooks.isEmpty()){
                        response.status = 200;
                        response.message ="list of books returned";
                        response.object=listOfBooks;
                    }else{
                        response.status=400;
                        response.message="there is no books with this "+str;
                    }
                    System.out.println("the returned value is "+response);
                    communicator.sendResponse(response);
                    break;
                }
                case "sign out":{
                    System.out.println("User Signing out");
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
