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
import java.util.UUID;

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
        ArrayList<String > optionsArray = new ArrayList<>(Arrays.asList("View Your Books Library" ,
                "Add A Book To Your Library" , "Remove A Book From Your Library" ,
                "Check Incoming Borrow Requests (ex. Accept/Reject incoming requests & Chat with borrower)" ,
                "Check Borrow Your Requests History" , "Browse Books Library" , "Search Books (ex. Search by title , author , genre)",
                "Borrow A Book (submit a borrow request)" , "Sign Out"));
        int choice = inputUserChoice(String.class , optionsArray);
        while (choice != 10) {// sign out choice
            switch (choice) {
                case 1: {
                    getUserBooks();
                    break;
                }
                case 2: {

                    break;
                }
                case 3: {
                    break;
                }
                case 4: {
                    checkRequestsInbox();
                    break;
                }
                case 5: {
                    var requests = getBorrowRequestHistory();
                    if(requests != null || ! requests.isEmpty()){
                        for (int i = 0 ; i < requests.size() ; i++){
                            System.out.print(i+1 + ") ");
                            printBorrowRequest(requests.get(i));
                        }
                    }
                    break;
                }
                case 6: {
                    browseBooks();
                    break;
                }
                case 7: {
                    Search();
                    break;
                }
                case 8: {
                    submitABorrowRequest();
                    break;
                }
                case 9: {
                    communicator.sendMessage("sign out");
                    System.out.println("Good Bye!");
                    return;
                }
                default: {
                    System.out.println("Good Bye!");
                    return;
                }

            }
            choice = inputUserChoice(String.class , optionsArray);
        }

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
            if(requests.get(i).lenderid == currentUser.id){
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

        ArrayList<String > optionsArray = new ArrayList<>(Arrays.asList("Accept / Reject a request" , "Go back to menu"));
        int choice = inputUserChoice( String.class, optionsArray);

        if(choice == 2) //go back to menu
            return;

        choice = inputUserChoice( BorrowRequest.class, pendingRequests);
        System.out.print("You chose request =>" );
        printBorrowRequest(pendingRequests.get(choice-1));

        optionsArray = new ArrayList<>(Arrays.asList("Accept" , "Reject"));
        choice = inputUserChoice(String.class , optionsArray);
        if(choice == 1){
            //accept request in server side

        }
        else{
            //reject request in server side
        }

    }

    public<T> int inputUserChoice( Class<T> tClass ,ArrayList<T> choices  ){ //tclass to get input for borrow requests
        int choice = -1;
        while (choice == -1){
            try {
                System.out.println("Choose an option:");
                T object = tClass.getDeclaredConstructor().newInstance();
                if(object == BorrowRequest.class){
                    for (int i = 0 ; i < choices.size() ; i++){
                        BorrowRequest request = (BorrowRequest) choices.get(i);
                        System.out.print (i+1 + ". " );
                        printBorrowRequest(request);
                    }
                }else{
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
            ArrayList<BorrowRequest> requests = (ArrayList<BorrowRequest>) response.object;
//            for (int i = 0; i < requests.size(); i++) {
//                System.out.print(i+1 + ") ");
//                printBorrowRequest(requests.get(i));
//            }
            return requests;
        }
    }


    void printBorrowRequest(BorrowRequest request){
        Book book = getBookById(request.bookid);
        System.out.println("Request on : " + book);
        int lenderId = request.lenderid;
        if (lenderId == currentUser.id) {
            System.out.println("Book lender : " + currentUser.name + " (You ;)");
        } else {
            User lender = getUserById(lenderId);
            System.out.println("Book lender : " + lender.name);
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

    public void submitABorrowRequest() {
        // to get books to borrow
        ArrayList<Book> books = browseBooks();
        if (books == null) {
            System.out.println("Sorry , No current books to borrow!");
            return;
        }
        int bookNumber = -1;
        String bookNumberString = "";
        while (bookNumber > books.size() || bookNumber < 1) {
            System.out.print("Enter the book number you want to borrow (ex. 1): ");
            try {
                bookNumberString = reader.readLine();
                bookNumber = Integer.parseInt(bookNumberString);
            } catch (Exception e) {
                System.out.println("Sorry , please re-enter a valid book number");
                bookNumber = -1;
            }
        }

        System.out.println("book number = " + bookNumber);
        String choice = "";
        System.out.println("Are you sure you want to submit a borrow request for this book ? (yes or no)");
        int accessBookNumberIndex = bookNumber - 1;
        System.out.println(books.get(accessBookNumberIndex));
        try {
            choice = reader.readLine();
            choice = choice.toLowerCase();
            if (!choice.equals("yes") && !choice.equals("no")) {
                System.out.println("Error : we could not comprehend your input! , please try again");
                return;
            }
            if (choice.equals("no")) {
                return;
            }
            System.out.println(currentUser);
            if (books.get(accessBookNumberIndex).ownerid == currentUser.id) {
                System.out.println("You already are the owner of this book");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error : we could not comprehend your input!");
        }

        communicator.sendMessage("borrow request");
        communicator.sendMessage(String.valueOf(currentUser.id)); // convert to strings
        communicator.sendMessage(String.valueOf(bookNumber));

        Response response = new Response();
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
            ArrayList<Book> books = (ArrayList<Book>) response.object;
            for (int i = 0; i < books.size(); i++) {
                System.out.println(i + 1 + ") " + books.get(i));
            }
            return books;
        }

    }

    public ArrayList<Book> Search() {
        communicator.sendMessage("Search");
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
