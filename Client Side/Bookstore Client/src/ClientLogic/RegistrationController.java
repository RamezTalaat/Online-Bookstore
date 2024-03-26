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
        try {
            boolean RegistrationIsDone  = false, isAdmin = false;
            User currentUser = null;
            while (!RegistrationIsDone){
                int choice = -1 ; //Wrong input handling loop
                while (choice == -1){
                    choice = printRegistrationMenu();
                }
                if(choice == 3) {//exit condition
                    communicator.sendMessage("exit");
                    return;
                }
                if(choice == 1){//sign up
                    communicator.sendMessage("sign up");
                    singUp();
                }
                else{
                    communicator.sendMessage("sign in");
                    signIn();
                }
                Response response = communicator.receiveResponse();
                if(response.status == 200){
                    RegistrationIsDone = true;
                    if(response.message.equals("Admin signed in successfully"))
                        isAdmin = true;
                    currentUser = (User) response.object;
                }else{
                    System.out.println("status = " + response.status); //in SRS
                    System.out.println("PROBLEM:" + response.message);
                }
            }
            if(isAdmin){
                AdminController adminController = new AdminController(communicator ); // not implemented yet
                adminController.handleAdmin();
            }else{
                UserController userController = new UserController(communicator ,currentUser);
                userController.handleUser();
            }
        }
        catch (Exception e){
            System.out.println("Server closed connection suddenly after establishing a connection");
            return;
        }
    }

    public int printRegistrationMenu(){
        System.out.println("User Registration: ");
        System.out.println("1. Sign up");
        System.out.println("2. Sign in");
        System.out.println("3. Exit");
        System.out.print("Choose an option:  ");
        try {
            String choice = reader.readLine();
            switch (choice){
                case "1":{
                    return 1;
                } case "2":{
                    return 2;
                }
                case "3":{
                    return 3;
                }
                default:{
                    System.out.println("Error : please enter a valid input");
                    return -1;
                }
            }

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Error : please enter a valid input");
        }
        return -1;
    }

    public void signIn(){
        try {
            String userName , password;
            System.out.print("Enter you user name: ");
            userName = reader.readLine();
            System.out.print("Enter you password: ");
            password = reader.readLine();
            communicator.sendMessage(userName);
            communicator.sendMessage(password);
        } catch (Exception e) {
            System.out.println("Could not understand your input!");
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
        } catch (Exception e) {
            System.out.println("Could not understand your input!");
        }
    }


}
