package BuisnessLogic.Controllers;

import BuisnessLogic.Authentication.IAuthenticator;
import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Authentication.UUIDAuthenticator;
import Communication.ICommunicator;

public class APIController {
    private static ICommunicator communicator;
    public APIController(ICommunicator _communicator){
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
                    IAuthenticator authenticator = new UUIDAuthenticator();
                    Response response = authenticator.signUp(name , userName , password);
                    communicator.sendResponse(response);
                    break;
                }
                case "sign in":{
                    System.out.println("in sign in");
                    String userName = communicator.receiveMessage();
                    String password = communicator.receiveMessage();
                    IAuthenticator authenticator = new UUIDAuthenticator();
                    Response response = authenticator.signIn( userName , password);
                    communicator.sendResponse(response);
                    break;
                } case "sign out":{

                    break;
                }default:{
                    sendResponse(null);

                    break;
                }
            }
        }
    }

    public void sendResponse(Response response){
        if(response == null){
            communicator.sendMessage("response size : 2");
            communicator.sendMessage("status : 400");
            communicator.sendMessage("message : Error : Could not understand client choice");

        }
        communicator.sendMessage("response size = 3");
        communicator.sendMessage("status : " + String.valueOf(response.status));
        communicator.sendMessage("message : " + response.message);
        communicator.sendMessage("object : " + response.object.toString());
    }
}
