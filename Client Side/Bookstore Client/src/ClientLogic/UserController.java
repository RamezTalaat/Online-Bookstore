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
    private  User currentUser;
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
                    BrowseBooks();
                    break;
                }
                case 2:{
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

    public void BrowseBooks(){
        communicator.sendMessage("browse");
        Response response = communicator.receiveResponse();
        if(response.status != 200){
            System.out.println(response.message);
        }else{
            ArrayList<Book> books= (ArrayList<Book>)response.object;
            for(int i = 0 ; i < books.size() ; i++){
                System.out.println(books.get(i));
            }
        }

    }
    public int getUserChoice(){
        int choice = -1 ;
        while (choice == -1 ){
            System.out.println("Services: ");
            System.out.println("1. Browse Books");
            System.out.println("2. Search Books (ex. Search by title , author , genre)");
            System.out.println("3. Add a book to your inventory");
            System.out.println("4. Remove a book from your inventory");
            System.out.println("5. Check your requests history (ex. Accept/Reject incoming requests & Chat with borrower)");
            System.out.println("6. Submit a borrow request");
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
