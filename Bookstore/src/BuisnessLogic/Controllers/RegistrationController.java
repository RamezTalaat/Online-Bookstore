package BuisnessLogic.Controllers;

import BuisnessLogic.Authentication.IAuthenticator;
import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Authentication.UserAuthenticator;
import BuisnessLogic.Models.User;
import Communication.ICommunicator;

import java.util.UUID;

public class RegistrationController {
    private static ICommunicator communicator;
    public RegistrationController(ICommunicator _communicator){
        communicator = _communicator;
    }
    public void handleClientRequest(){
        String userChoice = "";
        while (!userChoice.equals("end")){

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
                    break;
                }
                case "sign in":{
                    String userName = communicator.receiveMessage();
                    String password = communicator.receiveMessage();
                    IAuthenticator authenticator = new UserAuthenticator();
                    Response response = authenticator.signIn( userName , password);

                    communicator.sendResponse(response);
                    if(response.status == 200){
                        User currentUser = (User)response.object;

                        if(response.message.equals("Admin signed in successfully")){
                            AdminController adminController = new AdminController(); //still not implemented
                        }else{

                            UserController userController = new UserController(communicator ,currentUser );
                            userController.handleUser();
                        }
                    }

                    //communicator.sendResponse(response);
                    break;
                } case "sign out":{
//                    System.out.println("in sign out");
//                    String  Suuid =  communicator.receiveMessage();//not implemented well yet , should receive UUID
//                    UUID uuid = UUID.fromString(Suuid);
//                    IAuthenticator authenticator = new UserAuthenticator();
//                    Response response = authenticator.signOut(uuid); //needs to be improved to take user id not uuid
//                    communicator.sendResponse(response);
//                    if(response.status == 200){
//                        //should close connection socket
//                    }
                    break;
                }default:{
                    Response response = new Response();
                    response.status = 400;

                    communicator.sendResponse(response);

                    break;
                }
            }
        }
    }

//    public void sendResponse(Response response){
//        if(response == null){
//            communicator.sendMessage("response size : 2");
//            communicator.sendMessage("status : 400");
//            communicator.sendMessage("message : Error : Could not understand client choice");
//
//        }
//        communicator.sendMessage("response size = 3");
//        communicator.sendMessage("status : " + String.valueOf(response.status));
//        communicator.sendMessage("message : " + response.message);
//        communicator.sendMessage("object : " + response.object.toString());
//    }
}
