package ClientLogic;

import Communication.ICommunicator;

import java.io.BufferedReader;
import java.util.UUID;

public class UserController {
    private static UUID uuid;
    private static ICommunicator communicator;
    private static BufferedReader reader;
    public UserController(ICommunicator _communicator ,UUID _uuid){
        uuid = _uuid;
        communicator = _communicator;
    }

    public void handleUser(){

    }
}
