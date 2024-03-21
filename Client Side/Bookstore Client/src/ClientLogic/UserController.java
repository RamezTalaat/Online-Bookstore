package ClientLogic;

import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Models.Book;
import BuisnessLogic.Models.User;
import Communication.ICommunicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;

public class UserController {
    private  final User currentUser;
    private static ICommunicator communicator;
    private static BufferedReader reader;
    public UserController(ICommunicator _communicator ,User _user){
        currentUser = _user;
        communicator = _communicator;
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void handleUser(){
        int choice = getUserChoice();
        while (choice != 7){//sign out choice
            switch (choice){
                case 1:{
                    browseBooks();
                    break;
                }
                case 2:{
                    getUserBooks();
                    break;
                }
                case 3:{
                    break;
                }
                case 4:{
                    break;
                }
                case 5:{
                    break;
                }
                case 6:{
                    submitABorrowRequest();
                    break;
                }
                case 7:{
                    break;
                }
                default:{
                    System.out.println("Good Bye!");
                    return;
                }

            }
            choice = getUserChoice();
        }


    }

    public ArrayList<Book> getUserBooks(){
        communicator.sendMessage("get user books");
        Response response = communicator.receiveResponse();
        if(response.status != 200){
            System.out.println("Looks like you have no personal books yet");
            return null;
        }else{
            ArrayList<Book> books= (ArrayList<Book>)response.object;
            for(int i = 0 ; i < books.size() ; i++){
                System.out.println(i+1 + ") " + books.get(i));
            }
            return books;
        }
    }
    public void submitABorrowRequest(){
        // to get books to borrow
        ArrayList<Book> books =  browseBooks();
        if(books == null){
            System.out.println("Sorry , No current books to borrow!");
            return;
        }
        int bookNumber = -1;
        String bookNumberString= "";
        while (bookNumber > books.size() || bookNumber < 1){
            System.out.print("Enter the book number you want to borrow (ex. 1): ");
            try {
                bookNumberString =  reader.readLine();
                bookNumber = Integer.parseInt(bookNumberString);
            }catch (Exception e){
                System.out.println("Sorry , please re-enter a valid book number");
                bookNumber = -1;
            }
        }

        System.out.println("book number = "+bookNumber);
        String choice = "";
        System.out.println("Are you sure you want to submit a borrow request for this book ? (yes or no)");
        int accessBookNumberIndex = bookNumber - 1;
        System.out.println(books.get(accessBookNumberIndex));
        try{
            choice = reader.readLine();
            choice = choice.toLowerCase();
            if(!choice.equals("yes") && !choice.equals("no") ){
                System.out.println("Error : we could not comprehend your input! , please try again");
                return;
            }
            if(choice.equals("no")){
                return;
            }
            System.out.println(currentUser);
            if (books.get(accessBookNumberIndex).ownerid == currentUser.id){
                System.out.println("You already are the owner of this book");
                return;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Error : we could not comprehend your input!");
        }

        communicator.sendMessage("borrow request");
        communicator.sendMessage(String.valueOf( currentUser.id)); // convert to strings
        communicator.sendMessage(String.valueOf( bookNumber));

        Response response = new Response();
        response = communicator.receiveResponse();
        if(response.status != 200){
            System.out.println("Sorry , the request was not submitted , try again");
        }else{
            System.out.println("Congrats , you request has been submitted successfully");
        }

    }
    public ArrayList<Book> browseBooks(){
        communicator.sendMessage("browse");
        Response response = communicator.receiveResponse();
        if(response.status != 200){
            System.out.println("Looks like there is no current books to browse , sorry!");
            return null;
        }else{
            ArrayList<Book> books= (ArrayList<Book>)response.object;
            for(int i = 0 ; i < books.size() ; i++){
                System.out.println(i+1 + ") " + books.get(i));
            }
            return books;
        }


    }
    public int getUserChoice(){
        int choice = -1 ;
        while (choice == -1 ){
            System.out.println("Services: ");
//            System.out.println("1. View Your Books Library");
//            System.out.println("3. Add A Book To Your Library");
//            System.out.println("3. Remove A Book From Your Library");
//            System.out.println("3. Check Incoming Borrow Requests");
//            System.out.println("3. Check Borrow Requests History");
            System.out.println("1. Browse Books Library");
            System.out.println("2. Get User Books");
            //System.out.println("2. Search Books (ex. Search by title , author , genre)");

            System.out.println("4. Remove a book from your inventory");
            System.out.println("5. Check your requests history (ex. Accept/Reject incoming requests & Chat with borrower)");
            System.out.println("6. borrow a book (submit a borrow request)");
            System.out.println("7. Sign Out");
            System.out.print("Choose an option:  ");
            try {
                String userChoice = reader.readLine();

                int choiceNumber = Integer.parseInt(userChoice);
                System.out.println("Choice number = " + choiceNumber);
                if(choiceNumber >=1 && choiceNumber <= 7){
                    choice = choiceNumber;
                    return choiceNumber;
                }
                System.out.println("Error : please enter a valid input");
                //return -1;

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error : please enter a valid input");
            }
            //return -1;
        }
        return choice;
    }


}
