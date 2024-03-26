package ClientLogic;

import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Models.Book;
import BuisnessLogic.Models.BorrowRequest;
import BuisnessLogic.Models.User;
import Communication.ICommunicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class UserController {
    private final User currentUser;
    private static ICommunicator communicator;
    private static BufferedReader reader;

    public UserController(ICommunicator _communicator, User _user) {
        currentUser = _user;
        communicator = _communicator;
        reader = new BufferedReader(new InputStreamReader(System.in));
    }


    public void handleUser() {
        try {
            ArrayList<String > optionsArray = new ArrayList<>(Arrays.asList("View Your Books Library" ,
                    "Add A Book To Your Library" , "Remove A Book From Your Library" ,
                    "Check Incoming Borrow Requests (ex. Accept/Reject incoming requests & Chat with borrower)" ,
                    "Check Borrow Your Requests History" , "Browse Books Library" , "Search Books (ex. Search by title , author , genre)",
                    "Borrow A Book (submit a borrow request)", "Add a review using Book ID" , "Sign Out"));
            int choice = inputUserChoice(String.class , optionsArray);
            while (true) {// sign out breaks this loop
                switch (choice) {
                    case 1: {
                        getUserBooks();
                        break;
                    }
                    case 2: {
                        addBook();
                        break;
                    }
                    case 3: {
                        removeBook();
                        break;
                    }
                    case 4: {
                        checkRequestsInbox();
                        break;
                    }
                    case 5: {
                        var requests = getBorrowRequestHistory();
                        if(requests == null || requests.isEmpty())
                            break;

                        for (int i = 0 ; i < requests.size() ; i++){
                            System.out.print(i+1 + ") ");
                            printBorrowRequest(requests.get(i));
                        }
                        break;
                    }
                    case 6: {
                        browseBooks();
                        break;
                    }
                    case 7: {
                        search();
                        break;
                    }
                    case 8: {
                        submitABorrowRequest();
                        break;
                    }
                    case 9:
                    {
                        addReview();
                        break;
                    }
                    case 10: {
                        communicator.sendMessage("sign out");
                        System.out.println("Good Bye!");
                        return;
                    }
                    default: {
                        System.out.println("Unfortunately , and unexpected operation occurred , working on solving this soon!");
                        return;
                    }

                }
                checkChatInbox(); //to chat with book lenders
                choice = inputUserChoice(String.class , optionsArray);
            }
        }
        catch (Exception e){
            System.out.println("Connection closed suddenly with server");
            System.out.println("Please try connecting again later");
            return;

        }


    }

    public void removeBook(){
        communicator.sendMessage("get user books");
        Response response = communicator.receiveResponse();
        ArrayList<Book> books = (ArrayList<Book>) response.object ;
        if(books == null) //no books to remove
            return;

        int choice = inputUserChoice(Book.class , books);
        Book chosenBook = books.get(choice-1);
        System.out.println("Are you sure you want to remove this book from your library:");
        System.out.println(chosenBook);

        ArrayList<String> choices =  new ArrayList<>(Arrays.asList("YES" , "NO"));
        choice = inputUserChoice(String.class ,choices);
        if(choice == 2)
            return;

        //System.out.println("book id = " + chosenBook.borrowerid);
        if(  chosenBook.borrowerid != -1 && chosenBook.borrowerid != 0){
            System.out.println("Book is already borrowed , can not currently be removed");
            return;
        }

        communicator.sendMessage("remove book");
        communicator.sendMessage(String.valueOf(chosenBook.id));
        response = communicator.receiveResponse();
        System.out.println(response.message); //success or failure

    }

    public void checkChatInbox(){
        communicator.sendMessage("get chat inbox");
        Response response = communicator.receiveResponse();
        ArrayList<Integer> inbox = (ArrayList<Integer>)response.object;
        if(inbox == null || inbox.isEmpty())
            return;


        ///                           you have to make sure that the lender is still online...........
        System.out.println("*******************************************************");
        System.out.println("*NOTIFICATION: You have waiting chatters in you inbox!*");
        System.out.println("*******************************************************");
//        ArrayList<String> choices =  new ArrayList<>(Arrays.asList("Enter a Chat" , "Chat later"));
//        int choice = inputUserChoice(String.class , choices);
//        if(choice == 2)
//            return;
        ArrayList<String> choices = new ArrayList<>();
        System.out.println("Choose a user to chat with:");
        for (int i =0 ; i < inbox.size() ; i++){
            User lender = getUserById(inbox.get(i));
            choices.add(lender.name);
        }
        int choice = inputUserChoice(String.class , choices);

        User chosenBorrower = getUserById(inbox.get(choice-1));
        System.out.println("Entering chat with " + chosenBorrower.name);
        enterChat(chosenBorrower.id);
    }
    public void enterChat (int lenderId){
        communicator.sendMessage("enter chat with lender");
        communicator.sendMessage(String.valueOf(lenderId));
        System.out.println("Note: you are now in the chat room , Enter (exit chat) to exit");
        ChatListener chatListener = new ChatListener(communicator);
        chatListener.start();
        String userInput = "";
        while (!userInput.equals("exit chat")){
            try {
                userInput = reader.readLine();
                communicator.sendMessage(userInput);
            } catch (IOException e) {
                System.out.println("Error : we could not understand/send your input, please try again");
            }
        }

    }
    public boolean startChat (int borrowerId){
        communicator.sendMessage("start chat with borrower");
        communicator.sendMessage(String.valueOf(borrowerId));
        Response response = communicator.receiveResponse();
        //System.out.println("start chat response"+response);
        if(response.status == 200){
            System.out.println(response.message);
        }
        else{
            System.out.println(response.message);
            return false ;
        }
        System.out.println("Note: you are now in the chat room , Enter (exit chat) to exit");


        ChatListener chatListener = new ChatListener(communicator);
        chatListener.start(); //to start listening thread
        String userInput = "";
        while (!userInput.equals("exit chat")){
            try {
                userInput = reader.readLine();
                communicator.sendMessage(userInput);
            } catch (IOException e) {
                System.out.println("Error : we could not understand/send your input, please try again");
            }
        }
        return true;
    }
    public void checkRequestsInbox(){
        //get pending requests
        ArrayList<BorrowRequest> requests = getBorrowRequestHistory();
        if(requests == null || requests.isEmpty()){
            System.out.println("No pending requests");
            return;
        }
        ArrayList<BorrowRequest> pendingRequests = new ArrayList<BorrowRequest>();
        for (int i = 0 ; i < requests.size() ; i++){
            if(requests.get(i).lenderid == currentUser.id && requests.get(i).status.equals("pending")){
                pendingRequests.add(requests.get(i));
            }
        }
        if(pendingRequests.isEmpty()){
            System.out.println("No pending requests");
            return;
        }
        for (int i = 0 ; i < pendingRequests.size() ; i++){
            System.out.print(i+1 + ") ");
            printBorrowRequest(pendingRequests.get(i));
        }

        ArrayList<String > optionsArray = new ArrayList<>(Arrays.asList("Accept (by chat) / Reject a request" , "Go back to menu"));
        int choice = inputUserChoice( String.class, optionsArray);

        if(choice == 2) //go back to menu
            return;

        choice = inputUserChoice( BorrowRequest.class, pendingRequests);
        System.out.print("You chose : " );
        BorrowRequest chosenRequest = pendingRequests.get(choice-1);
        printBorrowRequest(chosenRequest);
        int requestNumber = choice-1;
        optionsArray = new ArrayList<>(Arrays.asList("Chat with borrower" , "Reject"));
        choice = inputUserChoice(String.class , optionsArray);

        //communicator.sendMessage("handle borrow request");
        if(choice == 1){
            //start chat
            boolean isActive = startChat(chosenRequest.borrowerid);
            if(!isActive)
                return;
            //check if user wants to accept or reject
            System.out.println("After chatting with the potential borrower , would you like to :");
            optionsArray = new ArrayList<>(Arrays.asList("Accept" , "Reject"));
            choice = inputUserChoice(String.class , optionsArray);
            if(choice == 1){
                //accept request in server side
                communicator.sendMessage("accept borrow request");
            }
            else{
                //reject request in server side
                communicator.sendMessage("reject borrow request");
            }
            communicator.sendMessage(String.valueOf(pendingRequests.get(requestNumber).id)); //send request id
            Response response = communicator.receiveResponse();
            System.out.println(response.message);
        }
        else{
            //reject request in server side
            communicator.sendMessage("reject borrow request");
            communicator.sendMessage(String.valueOf(pendingRequests.get(requestNumber).id)); //send request id
            Response response = communicator.receiveResponse();
            System.out.println(response.message);

        }

    }

    public<T> int inputUserChoice( Class<T> tClass ,ArrayList<T> choices  ){ //tclass to get input for borrow requests
        int choice = -1;
        while (choice == -1){
            try {
                System.out.println("*---------------------------------------------------------------------------------*");
                System.out.println("Choose an option:");
                if(tClass == BorrowRequest.class){
                    for (int i = 0 ; i < choices.size() ; i++){
                        BorrowRequest request = (BorrowRequest) choices.get(i);
                        System.out.print (i+1 + ". " );
                        printBorrowRequest(request);
                    }
                }else if(tClass == Book.class){ 
                    for(int i = 0 ;i < choices.size() ; i++){
                        Book book = (Book) choices.get(i);
                        System.out.print(i+1+")");
                        printIndetailed(book);
                    }
                }
                else{
                    for (int i = 0 ; i < choices.size() ; i++){
                        System.out.println(i+1 + ". "+ choices.get(i));
                    }
                }
                System.out.print("Choose a number: ");
                String input = reader.readLine();
                choice = Integer.parseInt(input);
            } catch (Exception e) {
                System.out.println("ERROR: Sorry , could not understand your choice , please try again!");
                choice = -1;
                continue;
            }
            if(choice < 1 || choice > choices.size() ){
                System.out.println("WRONG INPUT: please choose a valid number from the menu");
                choice = -1;
                continue;
            }
        }
        return choice;
    }

    public ArrayList<BorrowRequest> getBorrowRequestHistory() {
        communicator.sendMessage("get borrow request history");
        Response response = communicator.receiveResponse();
        if (response.status != 200) {
            System.out.println("Looks like you have no borrow requests history yet");
            return null;
        } else {
            //System.out.println("borrow request history object = " + response.object);
            return (ArrayList<BorrowRequest>) response.object;
        }
    }
    void printIndetailed(Book book){
        System.out.println("(Title): "+book.title+" (Genre): "+book.genre+" (Author):"+book.author);
    }
    void printBorrowRequest(BorrowRequest request){
        Book book = getBookById(request.bookid);
        System.out.println("Request on : " + book);
        int lenderId = request.lenderid;
        int borrowerId = request.borrowerid;
        if (lenderId == currentUser.id) {
            System.out.println("Book lender : " + currentUser.name + " (You ;)");
        } else {
            User lender = getUserById(lenderId);
            System.out.println("Book lender : " + lender.name);
        }
        if (borrowerId == currentUser.id) {
            System.out.println("Book borrower : " + currentUser.name + " (You ;)");
        } else {
            User borrower = getUserById(borrowerId);
            System.out.println("Book borrower : " + borrower.name);
        }
        System.out.println("Request Status : " + request.status);
        System.out.println("-------------------------------------------------");
    }

    public Book getBookById(int bookId) {
        communicator.sendMessage("get book by id");
        String stringBookId = String.valueOf(bookId);
        communicator.sendMessage(stringBookId);
        Response response = communicator.receiveResponse();
        if (response.status != 200) {
            System.out.println(response.message);
            return null;
        } else {
            Book book = (Book) response.object;
            return book;
        }
    }

    public User getUserById(int userid) {
        communicator.sendMessage("get user by id");
        String stringUserId = String.valueOf(userid);
        communicator.sendMessage(stringUserId);
        Response response = communicator.receiveResponse();
        if (response.status != 200) {
            System.out.println(response.message);
            return null;
        } else {
            User user = (User) response.object;
            return user;
        }
    }

    public ArrayList<Book> getUserBooks() {
        communicator.sendMessage("get user books");
        Response response = communicator.receiveResponse();
        if (response.status != 200) {
            System.out.println("Looks like you have no personal books yet");
            return null;
        } else {
            ArrayList<Book> books = (ArrayList<Book>) response.object;
            for (int i = 0; i < books.size(); i++) {
                System.out.println(i + 1 + ") " + books.get(i));
            }
            return books;
        }
    }
    public ArrayList<Book> getUserBooksWithoutPrint() {
        communicator.sendMessage("get user books");
        Response response = communicator.receiveResponse();
        if (response.status != 200) {
            System.out.println("Looks like you have no personal books yet");
            return null;
        } else {
            ArrayList<Book> books = (ArrayList<Book>) response.object;
            return books;
        }
    }

    public void addReview()
    {

        var books = getUserBooksWithoutPrint();
        if(books == null){
            System.out.println("You have no personal/borrowed books to review");
            return;
        }
        int choice = inputUserChoice(Book.class , books);
        int choseBookIndex  = choice -1;
        System.out.println("You want to review this book?");
        System.out.println(books.get(choseBookIndex));
        ArrayList<String> choices = new ArrayList<>(Arrays.asList("yes","no"));
        choice = inputUserChoice(String.class , choices);
        if(choice == 2)
            return;

        String StringBookId, StringRate, comment;

        int bookRate=-1;
        while (bookRate < 0) {
            System.out.print("Enter the rating of the book you want to add to review (ex. 0~10): ");
            try {
                StringRate = reader.readLine();
                bookRate = Integer.parseInt(StringRate);
                if(bookRate<=-1 || bookRate>=11)
                    throw new Exception("Sorry , please re-enter a valid review");
            } catch (Exception e) {
                System.out.println("Sorry , please re-enter a valid review");
                bookRate = -1;
            }
        }
        System.out.println("Enter a comment!");
        try {
            comment = reader.readLine();
        }catch (Exception e){
            System.out.println("ERROR: could not understand your input , please try again!");
            return;
        }

        communicator.sendMessage("add review");
        communicator.sendMessage(String.valueOf(books.get(choseBookIndex).id));
        communicator.sendMessage(String.valueOf(bookRate));
        communicator.sendMessage(comment);
        Response response = new Response();
        response = communicator.receiveResponse();
        if (response.status != 200) {
            System.out.println("Sorry, the review was not added, try again");
        } else {
            System.out.println("Congrats, you review has been added successfully");
        }
    }
    public void addBook()
    {
        String StringPrice, StringQuantity, title = null, author = null, description = null, genre = null;
        int bookQuantity =-1;
        double bookPrice =-1;
        System.out.println("Enter book price");
        while (bookPrice < 0) {
            System.out.print("Enter the book price you want to add to your library (ex. 15.0): ");
            try {
                StringPrice = reader.readLine();
                bookPrice = Double.parseDouble(StringPrice);
                if(bookPrice<0)
                    System.out.println("Sorry , please re-enter a valid price");
            } catch (Exception e) {
                System.out.println("Sorry , please re-enter a valid price");
                bookPrice = -1;
            }
        }
        while (bookQuantity < 0) {
            System.out.print("Enter the quantity of the book you want to add to your library (ex. 5): ");
            try {
                StringQuantity = reader.readLine();
                bookQuantity = Integer.parseInt(StringQuantity);
                if(bookQuantity<0)
                    System.out.println("Sorry , please re-enter a valid quantity");
            } catch (Exception e) {
                System.out.println("Sorry , please re-enter a valid book quantity");
                bookQuantity = -1;
            }
        }
        while(title == null || author == null || genre == null) {
            try {
                System.out.println("Please enter the book title");
                title = reader.readLine();
                System.out.println("Please enter the book genre");
                genre = reader.readLine();
                System.out.println("Please enter the book author");
                author = reader.readLine();
                System.out.println("Please enter the book description");
                description = reader.readLine();
                if(title == null || author == null || genre == null)
                    throw new Exception("Sorry , please re-enter a valid book information.");
            } catch (Exception e) {
                System.out.println("Sorry , please re-enter a valid book information.");
            }
        }
        communicator.sendMessage("add book");
        communicator.sendMessage(String.valueOf(bookPrice));
        communicator.sendMessage(genre);
        communicator.sendMessage(title);
        communicator.sendMessage(author);
        communicator.sendMessage(description);
        communicator.sendMessage(String.valueOf(bookQuantity));
        Response response = new Response();
        response = communicator.receiveResponse();
        if (response.status != 200) {
            System.out.println("Sorry, the book was not added, try again");
        } else {
            System.out.println("Congrats, you book has been added successfully");
        }
    }
    public void submitABorrowRequest() {
        // to get books to borrow
        communicator.sendMessage("get all books");
        Response response = communicator.receiveResponse();
        if(response == null ||response.object == null ){
            System.out.println("Sorry , No current books to borrow!");
            return;
        }
        ArrayList<Book> books = (ArrayList<Book>)response.object;
        System.out.println("Choose a book to borrow:");
        int choice = inputUserChoice(Book.class , books);
        int accessBookNumberIndex = choice-1;

        System.out.println("book number = " + (choice));

        System.out.println("Are you sure you want to submit a borrow request for this book ?");
        ArrayList<String> choices = new ArrayList<>(Arrays.asList("yes","no"));
        choice = inputUserChoice(String.class,choices);


        if (books.get(accessBookNumberIndex).ownerid == currentUser.id) {
            System.out.println(books.get(accessBookNumberIndex));
            System.out.println("You already are the owner of this book");
            return;
        }

        communicator.sendMessage("borrow request");
        communicator.sendMessage(String.valueOf(currentUser.id)); // convert to strings
        communicator.sendMessage(String.valueOf(books.get(accessBookNumberIndex).id));

        response = new Response();
        response = communicator.receiveResponse();
        if (response.status != 200) {
            System.out.println("Sorry , the request was not submitted , try again");
        } else {
            System.out.println("Congrats , you request has been submitted successfully");
        }

    }

    public ArrayList<Book> browseBooks() {
        communicator.sendMessage("browse");
        Response response = communicator.receiveResponse();
        if (response.status != 200) {
            System.out.println("Looks like there is no current books to browse , sorry!");
            return null;
        } else {
            //Book ------
            ArrayList<Book> books = (ArrayList<Book>) response.object;
            System.out.println("choose a book to view its details:");
            int choice = inputUserChoice(Book.class, books);
            Book book = books.get(choice-1);
            communicator.sendMessage("get accumulative rate by id");
            communicator.sendMessage(String.valueOf(book.id));
            response = communicator.receiveResponse();
            System.out.print(book+"\n Rate: "+response.object+"\n");
            return books;
        }

    }

    public ArrayList<Book> search() {
        communicator.sendMessage("search");
        String msg = "";
        String value = "";
        int num = 0;
        try {
            while (num != 4) {
                System.out.println("1.title");
                System.out.println("2.auther");
                System.out.println("3.genre");
                System.out.println("4.exit");
                System.out.print("Please enter number:");
                msg = reader.readLine();
                num = Integer.parseInt(msg);
                if (num == 1) {
                    Response response = new Response<>();
                    communicator.sendMessage("title");
                    System.out.print("Please Enter the title:");
                    value = reader.readLine();
                    communicator.sendMessage(value);
                    response = communicator.receiveResponse();
                    if (response.status != 200) {
                        System.out.println("Looks like there is no book with this title");
                        return null;
                    } else {
                        ArrayList<Book> result = (ArrayList<Book>) response.object;
                        for (Book s : result) {
                            System.out.println(s);
                        }
                        return result;
                    }
                } else if (num == 2) {
                    communicator.sendMessage("author");
                    Response response = new Response<>();
                    System.out.print("Please Enter the Auther Name:");
                    value = reader.readLine();
                    communicator.sendMessage(value);
                    response = communicator.receiveResponse();
                    if (response.status != 200) {
                        System.out.println("Looks like there is no books with this Auther Name");
                        return null;
                    } else {
                        ArrayList<Book> result = (ArrayList<Book>) response.object;
                        for (Book s : result) {
                            System.out.println(s);
                        }
                        return result;
                    }
                } else if (num == 3) {
                    communicator.sendMessage("genre");
                    Response response = new Response<>();
                    System.out.print("Please Enter the genre:");
                    value = reader.readLine();
                    communicator.sendMessage(value);
                    response = communicator.receiveResponse();
                    if (response.status != 200) {
                        System.out.println("Looks like there is no books with this Genre");
                        return null;
                    } else {
                        ArrayList<Book> result = (ArrayList<Book>) response.object;
                        for (Book s : result) {
                            System.out.println(s);
                        }
                        return result;
                    }
                } else {
                    System.out.println("Please Enter valid input");
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
