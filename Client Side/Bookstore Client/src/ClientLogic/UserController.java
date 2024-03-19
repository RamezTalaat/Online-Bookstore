package ClientLogic;

import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Models.User;
import Communication.ICommunicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        int choice = -1 ;
        while (choice != 7){//sign out choice
            while (choice == -1 ){
                choice = getUserChoice();
            }


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
        }


    }

    public void BrowseBooks(){

    }
    public int getUserChoice(){
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
            String choice = reader.readLine();

            int choiceNumber = Integer.parseInt(choice);
            System.out.println("Choice number = " + choiceNumber);
            if(choiceNumber >=1 && choiceNumber <= 7){
                return choiceNumber;
            }
            System.out.println("Error : please enter a valid input");
            return -1;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error : please enter a valid input");
        }
        return -1;
    }


}
