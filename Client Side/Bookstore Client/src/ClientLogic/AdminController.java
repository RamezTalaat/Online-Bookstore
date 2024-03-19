package ClientLogic;

import BuisnessLogic.Models.User;
import Communication.ICommunicator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;

public class AdminController {
    private  User currentAdmin;
    private ICommunicator communicator;
    private BufferedReader reader;
    public AdminController(ICommunicator _communicator , User user){
        currentAdmin = user;
        communicator = _communicator;
        reader = new BufferedReader(new InputStreamReader(System.in));
    }
}
