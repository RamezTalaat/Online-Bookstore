package BuisnessLogic.Controllers;

import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Models.Book;
import BuisnessLogic.Models.BorrowRequest;
import BuisnessLogic.Models.Review;
import BuisnessLogic.Models.User;
import Communication.ICommunicator;
import Communication.ServerCommunicator;
import DbContext.DbConnection;

import java.util.ArrayList;

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
        try {
            String choice;
            while (true){
                choice = communicator.receiveMessage();
                switch (choice){
                    case "start chat with borrower":{
                        startChat();
                        break;
                    }
                    case "get recommendations":{
                        getUserRecommendations();
                        break;
                    }
                    case "get book reviews":{
                        getBookReviews();
                        break;
                    }
                    case "enter chat with lender":{
                        enterChatWithLender();
                        break;
                    }
                    case "reject borrow request":{
                        rejectBorrowRequest();
                        break;
                    }
                    case "browse":{
                        System.out.println("user in browse function");
                        BookController bookController = new BookController();
                        ArrayList<Book> result = bookController.browseBooks();
                        if(result == null || result.isEmpty()){
                            returnFailureResponse("No books to browse now");

                        }else{
                            returnSuccessResponse("books retrieved successfully" , result);
                        }

                        break;
                    }
                    case"sort by genre": {
                        BookController bookController = new BookController();
                        ArrayList<Book> result = bookController.browseByGenre();
                        if (result == null || result.isEmpty()) {
                            returnFailureResponse("No books to browse now");
                        }
                        else{
                            returnSuccessResponse("books that are sorted by genre retrieved successfully" , result);
                        }
                        break;
                    }
                    case "get all books":{
                        BookController bookController = new BookController();
                        ArrayList<Book> result = bookController.browseBooks();
                        if(result == null || result.isEmpty()){
                            returnFailureResponse("No books to browse now");
                        }else{
                            returnSuccessResponse("books retrieved successfully" , result);
                        }

                        break;

                    }
                    case "borrow request":{
                        borrowRequest();
                        break;
                    }
                    case "add book":{
                        addBook();
                        break;
                    }
                    case "remove book":{
                        removeBook();
                        break;
                    }
                    case "add review":{
                        addReview();
                        break;
                    }
                    case "get user books":{
                        getUserBooks();
                        break;
                    }
                    case "get user by id":{
                        getUserById();
                        break;
                    }
                    case "get book by id":{
                        getBookById();
                        break;
                    }
                    case "get borrow request history":{
                        getBorrowRequestHistory();
                        break;
                    }
                    case "search":{
                        search();
                        break;
                    }
                    case "accept borrow request":{
                        acceptBorrowRequest();
                        break;
                    }
                    case "get chat inbox":{
                        // checks current lenders waiting to chat with users
                        System.out.println("in get chat inbox " + currentUser.name);
                        for (int i = 0; i < waitingChats.size(); i++) {
                            System.out.println("waiting chats for user " +currentUser.name + " =>"+ waitingChats.get(i));
                        }
                        Response response = new Response();
                        response.object = waitingChats;
                        communicator.sendResponse(response);
                        break;
                    }
                    case "get accumulative rate by id":{
                        String id = communicator.receiveMessage();
                        int ID = Integer.parseInt(id);
                        ReviewController reviewController = new ReviewController();
                        int rate = reviewController.rates(ID);
                        Response response = new Response();
                        response.object= rate;
                        communicator.sendResponse(response);
                        break;
                    }
                    case "sign out":{
                        System.out.println("User Signing out");
                        //remove user form active users list in server
                        return;
                    }
                    default:{
                        returnFailureResponse("Error : Server could not parse request properly , please try again ");
                        break;
                    }
                }
            }
        }
        catch (Exception e){
            System.out.println("Client " + currentUser.name + " closed connection suddenly");
        }
    }

    public void getBookReviews(){
        String stringBookId = communicator.receiveMessage();
        int bookId = Integer.parseInt(stringBookId);
        ReviewController reviewController = new ReviewController();
        ArrayList<Review> reviews =reviewController.getReviewsByBookId(bookId);
        if(reviews == null || reviews.isEmpty()){
            returnFailureResponse("No reviews yet");
            return ;
        }
        returnSuccessResponse("Reviews retrieved successfully" , reviews);
    }
    public void getUserRecommendations(){
        //get genre recommendations
        ArrayList<Book> recommended = new ArrayList<>() , userBooks, allBooks;
        ArrayList<String> genres = new ArrayList<>();
        BookController bookController = new BookController();
        userBooks = bookController.getUserBooks(currentUser.id);
        allBooks = bookController.browseBooks();
        if(allBooks == null){
            returnFailureResponse("No books to recommend");
            return;
        }

        //1. recommend according to user genre preference
        if(userBooks != null){
            for (var userBook : userBooks){
                String genre = userBook.genre;
                if(genres.contains(genre)) //to take only one book from each genre
                    continue;
                for (var book : allBooks){
                    if(book.id != userBook.id && book.genre.equals(genre) && book.ownerid != currentUser.id
                            && book.borrowerid != currentUser.id && !recommended.contains(book)){ //to add a new book to recommendations
                        genres.add(genre);
                        recommended.add(book);
                        break;
                    }
                }
            }
        }

        //2. according to overall genre best rating
        genres = new ArrayList<>();
        for(var book : allBooks){
            if(genres.contains(book.genre))
                continue;
            genres.add(book.genre);
            var highest =  bookController.getHighestRatedBookInGenre(book.genre);
            if(highest == null)
                continue;
            Book highestRate = highest.get(0);
            System.out.println("highest rated in " + highestRate.genre + " = " + highestRate.title);

//            for(var alreadyOwned : userBooks){
//                if(alreadyOwned.id == book.id){
//
//                }
//            }
            boolean isAlreadyRecommendedOrOwned = false;
            for (var temp : userBooks){
                if(temp.id == highestRate.id){
                    isAlreadyRecommendedOrOwned = true;
                    break;
                }
            }
            if(isAlreadyRecommendedOrOwned)
                continue;
            for (var temp : recommended){
                if(temp.id == highestRate.id){
                    isAlreadyRecommendedOrOwned = true;
                    break;
                }
            }
            if(isAlreadyRecommendedOrOwned)
                continue;

            recommended.add(highestRate);

        }
        returnSuccessResponse("These are the recommended books",recommended);
    }
    public void removeBook(){
        String stringBookId = communicator.receiveMessage(); //1. get book id
        int bookId = Integer.parseInt(stringBookId);
        BookController bookController = new BookController();

        boolean check = true;
        Book result = bookController.getBookById(bookId);
        if(result.quantity == 1){
            ReviewController reviewController = new ReviewController();
            if(reviewController.getReviewsByBookId(bookId) != null){
                check = reviewController.removeBookReviews(bookId);
                if(!check){
                    System.out.println("reviews not deleted");
                    returnFailureResponse("could not delete book");
                }
            }

        }

        check = bookController.removeBook(bookId); //delete if 1 or more
        if(!check){
            returnFailureResponse("could not delete book");
        }
        else
            returnSuccessResponse("Book deleted successfully");
    }
    public void getBorrowRequestHistory(){
        System.out.println("user in get  borrow request history function");
        BookController bookController = new BookController();
        ArrayList<BorrowRequest> result = bookController.getBorrowRequestsHistory(currentUser.id);

        if(result == null) {
            returnFailureResponse("No request history yet");
            return;
        }
        System.out.println("borrow requests history response = " + result);

        returnSuccessResponse( "requests history retrieved successfully",result);
    }

    public void search(){
        System.out.println("You are in the search mode");
        String str = communicator.receiveMessage();
        String value = communicator.receiveMessage();
        BookController bookController = new BookController();
        Response response = new Response();
        ArrayList<Book> listOfBooks = bookController.searchForBook(str,value);
        if(!listOfBooks.isEmpty()){
            returnSuccessResponse("list of books returned",listOfBooks);
        }else{
            returnFailureResponse("there is no books with this "+str);
        }
        //System.out.println("the returned value is "+listOfBooks);
    }
    public void getBookById(){
        BookController bookController = new BookController();
        String stringBookId = communicator.receiveMessage();
        int bookId = Integer.parseInt(stringBookId);
        Book result = bookController.getBookById(bookId);
        Response response = new Response();
        if(result == null){
            returnFailureResponse("No book with this id");

        }else{
            returnSuccessResponse("book retrieved successfully" ,result );
        }
    }
    public void getUserById(){
        String stringUserId = communicator.receiveMessage();
        int userid = Integer.parseInt(stringUserId);

        String query = "select * from users where id = '" + userid + "'";
        DbConnection dbConnection = new DbConnection();
        ArrayList<User> result  = dbConnection.select(User.class , query);
        Response response = new Response();
        if(result == null || result.isEmpty()){
            returnFailureResponse("No user with this id");

        }else{
            returnSuccessResponse("user retrieved successfully",result.get(0));//to return user object not wrapped in list
        }

    }
    public void getUserBooks(){
        BookController bookController = new BookController();
        ArrayList<Book> result = bookController.getUserBooks(currentUser.id);
        Response response = new Response();
        if(result==null || result.isEmpty()){
            returnFailureResponse("No books to in your library yet");
        }else{
            returnSuccessResponse("books retrieved successfully" ,result );
        }

    }
    public void addReview(){
        BookController bookController = new BookController();
        String userId, bookId, rate, comment;

        bookId = communicator.receiveMessage();
        rate = communicator.receiveMessage();
        comment = communicator.receiveMessage();
        int intRate = Integer.parseInt(rate);
        int intBookId = Integer.parseInt(bookId);
        if(!bookController.addReview(currentUser.id, comment, intRate,intBookId))
        {
            returnFailureResponse("Couldn't add the review on the book");
            return;
        }
        returnSuccessResponse("Review added successfully!");
    }
    public void addBook(){
        System.out.println("user in add book function");
        BookController bookController = new BookController();
        String price , genre , title , author ,description , quantity, currUserId;
        price = communicator.receiveMessage();
        genre = communicator.receiveMessage();
        title = communicator.receiveMessage();
        author = communicator.receiveMessage();
        description = communicator.receiveMessage();
        quantity = communicator.receiveMessage();
        double doublePrice = Double.parseDouble(price);
        int intQuantity = Integer.parseInt(quantity);

        Response response = new Response();
        boolean check = true;
        check = bookController.addBook(doublePrice , genre , title , author , intQuantity , description, currentUser.id);
        if(!check)
            returnFailureResponse("Book (" + title+ ") Could not be added successfully");
        returnSuccessResponse("Book (" + title+ ") added successfully!");
    }
    public void borrowRequest(){
        System.out.println("In borrow request");
        RequestController requestController = new RequestController();
        String stringUserID = communicator.receiveMessage();
        String stringBookID = communicator.receiveMessage();
        int userId = Integer.parseInt(stringUserID);
        int bookId = Integer.parseInt(stringBookID);
        boolean result = requestController.submitBorrowRequest(userId , bookId);

        if(result){
            returnSuccessResponse("book borrow request submitted successfully");
        }else{
            returnFailureResponse("could not submit borrow request!");
        }

    }

    public void acceptBorrowRequest(){
        System.out.println("in accept request");
        String stringRequestId = communicator.receiveMessage(); //waiting for request id
        int requestId = -1;
        try {
            requestId = Integer.parseInt(stringRequestId);

        }catch (Exception e){
            System.out.println("Could not parse request id");
            returnFailureResponse("Could not parse request id");
            return;
        }

        RequestController requestController = new RequestController();


        ArrayList<BorrowRequest> result = requestController.getBorrowRequestById(requestId);
        if(result == null || result.isEmpty()){
            returnFailureResponse("Could not accept request properly , wrong request id");
            return;
        }

        BookController bookController = new BookController();
        BorrowRequest request = result.get(0);
        Boolean check = bookController.updateBookBorrower(request.bookid,request.borrowerid);
        if(!check){
            returnFailureResponse("Could not accept request properly");
            return;
        }
        check = requestController.acceptBorrowRequest(requestId);
        if(check)
            returnSuccessResponse("Request was accepted successfully");
        else
            returnFailureResponse("Could not accept request properly");

    }
    public void rejectBorrowRequest(){
        String stringRequestId = communicator.receiveMessage(); //waiting for request id
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
    }
    public void enterChatWithLender(){
        String stringLenderId = communicator.receiveMessage();
        int lenderId = -1;
        try {
            lenderId = Integer.parseInt(stringLenderId);
        }catch (Exception e){
            //e.printStackTrace();
            System.out.println("wrong lender id");
            returnFailureResponse("Wrong lender id");
            //break; //to wait for another instruction
        }

        //get lender controller
        var activeUsers = ServerCommunicator.getActiveUsers();
        UserController lenderController = null;
        for (int i = 0 ; i < activeUsers.size() ; i++){
            if(lenderId == activeUsers.get(i).currentUser.id){
                lenderController = activeUsers.get(i);
                System.out.println("got controller for => " +lenderController.currentUser.name);
                break;
            }
        }
        //2. entering in chat room
        if(lenderController == null){
            for (int i = 0 ; i< waitingChats.size() ; i++){
                System.out.println(currentUser.id + " waiting = " + waitingChats.get(i));
            }
        }
        handleChat(lenderController , "borrower");
        //waitingChats.remove(lenderController.currentUser.id);
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
    public void returnSuccessResponse(String message,Object object){
        Response response = new Response();
        response.status = 200;
        response.message = message;
        response.object = object;
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
            System.out.println("borrower controller is NULL");
            for (int i = 0 ; i< waitingChats.size() ; i++){
                System.out.println(currentUser.id + " waiting = " + waitingChats.get(i));
            }
        }
        if(borrowerController.waitingChats == null){
            //System.out.println("making waiting chats");
            borrowerController.waitingChats = new ArrayList<>();
        }
        borrowerController.waitingChats.add(currentUser.id);
        returnSuccessResponse("lender is currently waiting for borrower");

        handleChat(borrowerController , "lender");


        //communicator.receiveMessage();
    }
    public void handleChat(UserController otherUser , String role){
        System.out.println("in handle chat for user " + currentUser.name);
        //1. get the otherUser listener thread

        if(role.equals("lender")){
            chatListenerThread = new ChatListener(this ,otherUser.communicator);
            chatListenerThread.start();
        }


        communicator.sendMessage("NOTIFICATION: Still waiting for the other chatter!");
        while (otherUser.chatListenerThread == null){//to wait until other chatter enters
            try {
                Thread.sleep(100); //to not make request every iteration
            } catch (InterruptedException e) {
                System.out.println("Thread sleep time out error");
                //throw new RuntimeException(e);
            }
        }
        if(role.equals("borrower")){
            chatListenerThread = new ChatListener(this ,otherUser.communicator);
            chatListenerThread.start();
        }
        //to make sure both users are
        communicator.sendMessage("NOTIFICATION: Other chatter joined the room");
        //2. loop on user data and send it to the other thread
        String message = "";
        while (!message.equals("exit chat")){
            message = communicator.receiveMessage();
            System.out.println("message from " + currentUser.name + " = " + message);
            messageBox.add(message);

        }
        //close this user's listening thread
        otherUser.messageBox.add("exit chat");
        if(role.equals("lender")){
            for (int i=0 ; i<otherUser.waitingChats.size() ; i++){
                if(otherUser.waitingChats.get(i) == currentUser.id){
                    otherUser.waitingChats.remove(i);
                }
            }
        }

        System.out.println("user " + currentUser.name + " exited chat");
        chatListenerThread = null;

    }

}
