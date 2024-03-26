package BuisnessLogic.Controllers;

import BuisnessLogic.Authentication.IAuthenticator;
import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Authentication.UserAuthenticator;
import BuisnessLogic.Models.User;
import Communication.ICommunicator;
import Communication.ServerCommunicator;

import java.util.ArrayList;
import java.util.UUID;

public class RegistrationController {
    private ICommunicator communicator;
    public RegistrationController(ICommunicator _communicator){
        communicator = _communicator;
    }
    public void handleClientRequest(){
        try {
            String userChoice = "";
            boolean userRegistered = false , isAdmin = false;
            User currentUser = null;

            while (!userRegistered && !userChoice.equals("exit") ){

                userChoice = communicator.receiveMessage();
                if(userChoice == null)
                    continue;
                switch (userChoice){
                    case "sign up":{
                        System.out.println("in sign up");
                        String name = communicator.receiveMessage();
                        String userName = communicator.receiveMessage();
                        String password = communicator.receiveMessage();
                        IAuthenticator authenticator = new UserAuthenticator();
                        Response response = authenticator.signUp(name , userName , password);
                        communicator.sendResponse(response);
                        if(response.status == 200){
                            userRegistered = true;
                            currentUser = (User) response.object;
                        }


                        break;
                    }
                    case "sign in":{
                        String userName = communicator.receiveMessage();
                        String password = communicator.receiveMessage();
                        IAuthenticator authenticator = new UserAuthenticator();
                        Response response = authenticator.signIn(userName,password);
                        communicator.sendResponse(response);
                        if(response.status == 200){
                            userRegistered = true;
                            if(response.message.equals("Admin signed in successfully")){
                                isAdmin = true;
                            }
                            currentUser = (User) response.object;
                        }
                        break;
                    }
                    case "exit":{
                        return;
                    }
                    default:{
                        Response response = new Response();
                        response.status = 400;
                        response.message = "Could not understand registration option";

                        communicator.sendResponse(response);

                        break;
                    }
                }
            }
            if(userRegistered && isAdmin){
                AdminController adminController = new AdminController(communicator);
                adminController.handleAdmin();
            }else{
                //System.out.println("iam here signIN");
                UserController userController = new UserController(communicator ,currentUser );
                //add user to active list
                ServerCommunicator.addActiveUser(userController);
                userController.handleUser();
            }
        }
        catch (Exception e){
            System.out.println("Client closed connection suddenly");
            return;
        }

    }


}
