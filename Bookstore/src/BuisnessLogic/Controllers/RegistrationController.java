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



//                case "sign in":{
//                    String userName = communicator.receiveMessage();
//                    String password = communicator.receiveMessage();
//                    IAuthenticator authenticator = new UserAuthenticator();
//                    Response response ;
//                    boolean signInSucc = false;
//                    do{
//                        //System.out.println("iam here1");
//                        response = authenticator.signIn(userName,password);
//                        //System.out.println("iam here2");
//                        communicator.sendResponse(response);
//                        if(response.status == 200){
//                            //System.out.println("true");
//                            signInSucc = true;
//                        }else if(response.status == 404){
//                            //System.out.println("iam here3");
//                            communicator.sendResponse(response);
//                            userName = communicator.receiveMessage();
//                            password = communicator.receiveMessage();
//                            response=authenticator.signIn(userName,password);
//                        }else if(response.status == 401){
//                            communicator.sendResponse(response);
//                            userName = communicator.receiveMessage();
//                            password = communicator.receiveMessage();
//                            response=authenticator.signIn(userName,password);
//                        }else{
//                            signInSucc = true;
//                        }
//                    }while (!signInSucc);
//                    UserController userController = null;
//                    User currentUser = (User) response.object;
//                    //System.out.println("iam here signIN");
//                    if(response.message.equals("Admin signed in successfully")){
//                        AdminController adminController = new AdminController(communicator);
//                        adminController.handleAdmin();
//                    }else{
//                        //System.out.println("iam here signIN");
//                        userController = new UserController(communicator ,currentUser );
//                        //add user to active list
//                        ServerCommunicator.addActiveUser(userController);
//                        userController.handleUser();
//                    }
//                    userChoice = "end";
//                    ServerCommunicator.removeActiveUser(userController);
//                    //communicator.sendResponse(response);
//                    //return;
//                    break;
//                }
//                case "sign out":{
////                    System.out.println("in sign out");
////                    String  Suuid =  communicator.receiveMessage();//not implemented well yet , should receive UUID
////                    UUID uuid = UUID.fromString(Suuid);
////                    IAuthenticator authenticator = new UserAuthenticator();
////                    Response response = authenticator.signOut(uuid); //needs to be improved to take user id not uuid
////                    communicator.sendResponse(response);
////                    if(response.status == 200){
////                        //should close connection socket
////                    }
//                    break;
//                }
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


}
