package BuisnessLogic.Controllers;

import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Models.User;
import Communication.ICommunicator;

public class UserController {
    private static ICommunicator communicator;
    private User currentUser;
    public UserController(ICommunicator _communicator , User _currentUser){
        communicator = _communicator;
        currentUser = _currentUser;
    }

    public void handleUser(){
        String choice = "";
        while (choice != "sign out"){
            choice = communicator.receiveMessage();
            switch (choice){
                case "browse":{
                    System.out.println("user in browse function");
                    BookController bookController = new BookController();
                    //bookController.browseBooks();
                    break;
                }
                case "sign out":{
                    System.out.println("User Signing out");
                    break;
                }default:{
                    Response response  = new Response();
                    response.status = 400;
                    response.message = "Error : Server could not parse request properly , please try again ";
                    communicator.sendResponse(response);
                    break;
                }
            }
        }
        //Sign out code , close current socket connection
    }
}
