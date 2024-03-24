package BuisnessLogic.Controllers;

import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Models.Book;
import BuisnessLogic.Models.BorrowRequest;
import BuisnessLogic.Models.User;
import Communication.ICommunicator;
import Communication.ServerCommunicator;
import DbContext.DbConnection;

import java.util.ArrayList;
import java.util.Queue;

public class UserController {
    private ICommunicator communicator;
    private final User currentUser;
    public ArrayList<Integer> waitingChats;

    public ArrayList<String> messageBox;
    public ChatListener chatListenerThread;
    public UserController(ICommunicator _communicator , User _currentUser){
        communicator = _communicator;
        currentUser = _currentUser;
        waitingChats = new ArrayList<Integer>();
        messageBox = new ArrayList<String>();
    }

    public void handleUser(){
        String choice;
        while (true){
            choice = communicator.receiveMessage();
            switch (choice){
                case "start chat with borrower":{
                    startChat();
                    break;
                }
                case "enter chat with lender":{
                    String stringLenderId = communicator.receiveMessage();
                    int lenderId = -1;
                    try {
                        lenderId = Integer.parseInt(stringLenderId);
                    }catch (Exception e){
                        returnFailureResponse("Wrong borrower id");
                        //break; //to wait for another instruction
                    }
                    //get lender controller
                    var activeUsers = ServerCommunicator.getActiveUsers();
                    UserController lenderController = null;
                    for (int i = 0 ; i < activeUsers.size() ; i++){
                        if(lenderId == activeUsers.get(i).currentUser.id){
                            lenderController = activeUsers.get(i);
                            break;
                        }
                    }
                    //2. entering in chat room
                    if(lenderController == null){
                        System.out.println("borrower controller is NULLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
                        for (int i = 0 ; i< waitingChats.size() ; i++){
                            System.out.println(currentUser.id + " waiting = " + waitingChats.get(i));
                        }
                    }
                    handleChat(lenderController);
                    break;
                }
                case "reject borrow request":{
                    String stringRequestId = communicator.receiveMessage(); //waiting for request id
                    Response response = new Response();
                    int requestId = -1;
                    try {
                        requestId = Integer.parseInt(stringRequestId);

                    }catch (Exception e){
                        System.out.println("Could not parse request id");
                        returnFailureResponse("Could not parse request id");
                    }

                    RequestController requestController = new RequestController();
                    Boolean check = requestController.rejectBorrowRequest(requestId);
                    if(check){
                        returnSuccessResponse("Request was rejected successfully");
                    }
                    else{
                        returnFailureResponse("Could not reject request properly");
                    }
                    break;
                }
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
                case "add book":{
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
                    //remove user form active users list in server
                    return;
                    //break;
                }
                case "accept chat":{
                    //1. receive borrower id to enter the chat with him and then they both start chatting
                    //2. waiting
                    System.out.println("active users " + ServerCommunicator.getActiveUsers().size());
                    break;
                }
                case "get chat inbox":{
                    // checks current lenders waiting to chat with users
                    System.out.println("in get chat inbox " + currentUser.name);
                    for (int i = 0; i < waitingChats.size(); i++) {
                        System.out.println("waiting chat " + waitingChats.get(i));
                    }
                    Response response = new Response();
                    response.object = waitingChats;
                    communicator.sendResponse(response);
                    break;
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


    public void startChat(){
        //1. get the borrower user controller by borrower id
        System.out.println("In start chat");
        String stringBorrowerId = communicator.receiveMessage();
        int borrowerId = -1;
        try {
            borrowerId = Integer.parseInt(stringBorrowerId);
        }catch (Exception e){
            returnFailureResponse("Wrong borrower id");
            //break; //to wait for another instruction
        }
        //2. check if borrower is active
        Boolean isActive = false;
        var activeUsers = ServerCommunicator.getActiveUsers();
        if(activeUsers == null || activeUsers.isEmpty()){
            returnFailureResponse("No other active users now");
        }
        UserController borrowerController = null;
        for (int i = 0 ; i < activeUsers.size() ; i++){
            if(borrowerId == activeUsers.get(i).currentUser.id){
                isActive = true;
                borrowerController = activeUsers.get(i);
                break;
            }
        }
        if(!isActive){
            returnFailureResponse("Borrower is not currently active");
            return;

        }
        //2. waiting in chat room
        if(borrowerController == null){
            System.out.println("borrower controller is NULLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
            for (int i = 0 ; i< waitingChats.size() ; i++){
                System.out.println(currentUser.id + " waiting = " + waitingChats.get(i));
            }
        }
        borrowerController.waitingChats.add(currentUser.id);
        returnSuccessResponse("lender is currently waiting for borrower");

        handleChat(borrowerController);


        //communicator.receiveMessage();
    }

    public void handleChat(UserController otherUser){
        System.out.println("in handle chat for user" + currentUser.name);
        //1. get the otherUser listener thread
        //communicator.sendMessage("Still waiting for the other chatter!");
        chatListenerThread = new ChatListener(this ,otherUser.communicator);
        chatListenerThread.start();
        //to make sure both users are
        communicator.sendMessage("other chatter joined the room");
        //2. loop on user data and send it to the other thread
        String message = "";
        while (!message.equals("exit chat")){
            message = communicator.receiveMessage();
            System.out.println("message from " + currentUser.name + " = " + message);
            messageBox.add(message);

        }
    }





}
