package ClientLogic;

import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Models.User;
import Communication.ICommunicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class RegistrationController {
    private static ICommunicator communicator;
    private static BufferedReader reader;
    public RegistrationController(ICommunicator _communicator){
        communicator = _communicator;
        reader = new BufferedReader(new InputStreamReader(System.in));
    }
    public void registerClient(){
        int choice = -1 ; //Wrong input handling loop
        while (choice == -1){
            choice = printRegistrationMenu();
        }

        Response response;
        if(choice == 1){ //Sign Up scenario
            communicator.sendMessage("sign up");
            singUp();
            response = communicator.receiveResponse();
            System.out.println(response);
        }
        else if(choice == 2) { // Sign In scenario
            communicator.sendMessage("sign in");
            singIn();
            response = communicator.receiveResponse();
            System.out.println(response);
        }
        else{
            System.out.println("Error : could not understand user input"); //Extra security
            return;
        }
        if(response.status == 200){
            User user = (User)response.object;

            if(response.message.equals("Admin signed in successfully")){//admin path
                System.out.println("Welcome Admin!");
                AdminController adminController = new AdminController(communicator ,user); // not implemented yet
                adminController.handleAdmin();
            }else{
                System.out.println("User Signed In Successfully");
                UserController userController = new UserController(communicator ,user);
                userController.handleUser();
            }

        }

    }

    public int printRegistrationMenu(){
        System.out.println("User Registration: ");
        System.out.println("1. Sign up");
        System.out.println("2. Sign in");
        System.out.print("Choose an option:  ");
        try {
            String choice = reader.readLine();
            switch (choice){
                case "1":{
                    return 1;
                } case "2":{
                    return 2;
                } default:{
                    System.out.println("Error : please enter a valid input");
                    return -1;
                }
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
