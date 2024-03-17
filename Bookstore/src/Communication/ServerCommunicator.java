package Communication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerCommunicator {
    private static ServerSocket serverSocket;
    private static final int portNumber = 5005;

    public ServerCommunicator(){
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
