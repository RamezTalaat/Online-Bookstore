package ClientLogic;

import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Models.Book;
import BuisnessLogic.Models.BorrowRequest;
import BuisnessLogic.Models.User;
import Communication.ICommunicator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class AdminController {
    private  User currentAdmin;
    private ICommunicator communicator;
    private BufferedReader reader;
    public AdminController(ICommunicator _communicator , User user){
        currentAdmin = user;
        communicator = _communicator;
        reader = new BufferedReader(new InputStreamReader(System.in));
    }
    public void handleAdmin() {
        ArrayList<String > optionsArray = new ArrayList<>(Arrays.asList("View Current Available Books" ,
                "View Current Borrowed Books" , "View Requests" , "Sign Out"));
        int choice = inputAdminChoice(String.class , optionsArray);
        while (true) {// sign out breaks this loop
            switch (choice) {
                case 1: {
                    getAvailableBooks();
                    break;
                }
                case 2: {
                    getBorrowedBooks();
                    break;
                }
                case 3: {
//                    var requests = getBorrowRequests();
//                    UserController adminUserController = new UserController(communicator, currentAdmin);
//                    if(requests != null || ! requests.isEmpty()){
//                        for (int i = 0 ; i < requests.size() ; i++){
//                            System.out.print(i+1 + ") ");
//                            adminUserController.printBorrowRequest(requests.get(i));
//                        }
//                    }
                    break;
                }
                case 4: {
                    communicator.sendMessage("sign out");
                    System.out.println("Good Bye!");
                    return;
                }
                default: {
                    System.out.println("Good Bye!");
                    return;
                }

            }
            choice = inputAdminChoice(String.class , optionsArray);
        }

    }

    public<T> int inputAdminChoice(Class<T> tClass , ArrayList<T> choices  ){ //tclass to get input for borrow requests
        int choice = -1;
        while (choice == -1){
            try {
                System.out.println("Choose an option:");
                for (int i = 0 ; i < choices.size() ; i++){
                    System.out.println(i+1 + ". "+ choices.get(i));
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
            }
        }
        return choice;
    }
    public ArrayList<Book> getAvailableBooks() {
        communicator.sendMessage("get available books");
        Response response = communicator.receiveResponse();
        if (response.status != 200) {
            System.out.println("Looks there is no available books");
            return null;
        } else {
            ArrayList<Book> books = (ArrayList<Book>) response.object;
            for (int i = 0; i < books.size(); i++) {
                System.out.println(i + 1 + ") " + books.get(i));
            }
            return books;
        }
    }

    public ArrayList<Book> getBorrowedBooks() {
        communicator.sendMessage("get borrowed books");
        Response response = communicator.receiveResponse();
        if (response.status != 200) {
            System.out.println("Looks there is no borrowed books");
            return null;
        } else {
            ArrayList<Book> books = (ArrayList<Book>) response.object;
            for (int i = 0; i < books.size(); i++) {
                System.out.println(i + 1 + ") " + books.get(i));
            }
            return books;
        }
    }
//    public ArrayList<BorrowRequest> getBorrowRequests() {
//        communicator.sendMessage("get borrow requests");
//        Response response = communicator.receiveResponse();
//        if (response.status != 200) {
//            System.out.println("Looks there is no borrow requests");
//            return null;
//        } else {
//            ArrayList<BorrowRequest> borrowRequests = (ArrayList<BorrowRequest>) response.object;
//            for (int i = 0; i < borrowRequests.size(); i++) {
//                System.out.println(i + 1 + ") " + borrowRequests.get(i));
//            }
//            return borrowRequests;
//        }
//    }
}
