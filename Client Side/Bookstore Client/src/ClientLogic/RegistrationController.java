package ClientLogic;

import BuisnessLogic.Authentication.Response;
import Communication.ICommunicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RegistrationController {
    private static ICommunicator communicator;
    private static BufferedReader reader;
    public RegistrationController(ICommunicator _communicator){
        communicator = _communicator;
        reader = new BufferedReader(new InputStreamReader(System.in));
    }
    public void registerClient(){
        int choice = -1 ;
        while (choice == -1){
            choice = printRegistrationMenu();
        }
        if(choice == 1){ //Sign Up scenario
            communicator.sendMessage("sign up");
            singUp();
            Response response = communicator.receiveResponse();
            System.out.println(response);
        }
        else if(choice == 2) { // Sign In scenario
            communicator.sendMessage("sign in");
            singIn();
            Response response = communicator.receiveResponse();
            System.out.println(response);
        }
        else{
            System.out.println("Error : could not understand user input"); //Extra security
            return;
        }

    }

    public int printRegistrationMenu(){
        System.out.println("User Registration: ");
        System.out.println("1. Sign up");
        System.out.println("2. Sign in");
        System.out.print("Choose an option:  ");
        try {
            String choice = reader.readLine();
            if(choice.equals("1")){
                return 1;
            }else if(choice.equals("2")){
                return 2;
            }else{
                System.out.println("Error : please enter a valid input");
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error : please enter a valid input");
        }
        return -1;
    }

    public void singIn(){
        try {
            String userName , password;
            System.out.print("Enter you user name: ");
            userName = reader.readLine();
            System.out.print("Enter you password: ");
            password = reader.readLine();
            communicator.sendMessage(userName);
            communicator.sendMessage(password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void singUp(){
        try {
            String name, userName , password;
            System.out.print("Enter you name: ");
            name = reader.readLine();
            System.out.print("Enter you user name: ");
            userName = reader.readLine();
            System.out.print("Enter you password: ");
            password = reader.readLine();
            communicator.sendMessage(name);
            communicator.sendMessage(userName);
            communicator.sendMessage(password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
