package Communication;

import BuisnessLogic.Controllers.UserController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerCommunicator {
    private ServerSocket serverSocket;
    private final int portNumber = 5005;

    private static ArrayList<UserController> activeUsers;

    public static  ArrayList<UserController> getActiveUsers(){
        return activeUsers;
    }
    public static  void addActiveUser(UserController user){
        if(user != null)
            activeUsers.add(user);
    }
    public static  void removeActiveUser(UserController user){
        if(activeUsers.contains(user))
            activeUsers.remove(user);
    }

    public ServerCommunicator(){
        if(activeUsers == null)
            activeUsers = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(portNumber);
            while (true) {
                // waiting for connection
                System.out.println("----> Waiting for connections");
                Socket clientSocket = serverSocket.accept();

                //New Client thread instantiation
                Thread newClientThread = new Thread(new ClientHandler(clientSocket));
                newClientThread.start();
                System.out.println("----> New Thread started");

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error : Server could not bind to port number = " + portNumber);
        }
    }



}
