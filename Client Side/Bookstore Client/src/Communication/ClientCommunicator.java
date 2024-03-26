package Communication;


import BuisnessLogic.Authentication.Response;

import java.io.*;
import java.net.Socket;

public class ClientCommunicator implements ICommunicator {
    private static String serverIp = "127.0.0.1";
    private static int serverPort = 5005;
    private static Socket server;
    private static DataOutputStream outputStream;
    private static DataInputStream inputStream;

    private boolean isConnected;
    public ClientCommunicator(){
       try {
           boolean connected = connectToServer();
           if( !connected ||getServerSocket() == null || getServerSocket().isClosed()){
               System.out.println("Could not connect to server");
               isConnected = false;
               return;
           }
           isConnected = true;
           inputStream = new DataInputStream(new BufferedInputStream(server.getInputStream()));
           outputStream = new DataOutputStream(server.getOutputStream());

       }catch (Exception e){
           //e.printStackTrace();
           System.out.println("Error in connecting to server");
       }

    }

    public Socket getServerSocket(){
        if(server.isClosed()){
            System.out.println("Trying to connect to server");
            connectToServer();
            System.out.println("Connected successfully to server");
        }
        return server;
    }

    private boolean connectToServer(){
        boolean connected = false;
        int count = 0;
        while (!connected){
            if(count >= 9)
                return false;
            try {
                server = new Socket(serverIp, serverPort);
                connected = true;
            }catch (Exception e){
                //e.printStackTrace();
                System.out.println("Error : Could not connect to server , Trying again");
                count++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    //throw new RuntimeException(ex);
                }
            }
        }
        return true;
    }


    @Override
    public String receiveMessage() {
        server = getServerSocket();
        try {
            String message = inputStream.readUTF();
            return message;
        } catch (Exception e) {
            //throw new RuntimeException(e);
            System.out.println("Error in reading from server ");
        }
        return null;
    }

    @Override
    public Response receiveResponse() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            Response response = new Response();
            response = (Response) objectInputStream.readObject();
            return response;
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Error : Problem in receiving server response!");
        }
        return null;
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void sendMessage(String message) {
        server = getServerSocket();
        try {
            outputStream.writeUTF(message);
        } catch (Exception e) {
            //throw new RuntimeException(e);
            System.out.println("Error in sending message  to server ");
        }
    }
}


