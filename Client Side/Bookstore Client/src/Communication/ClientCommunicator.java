package Communication;

import java.io.*;
import java.net.Socket;

public class ClientCommunicator implements ICommunicator {
    private static String serverIp = "127.0.0.1";
    private static int serverPort = 5005;
    private static Socket server;
    private static DataOutputStream outputStream;
    private static DataInputStream inputStream;
    public ClientCommunicator(){
       try {
           connectToServer();
           inputStream = new DataInputStream(new BufferedInputStream(server.getInputStream()));
           outputStream = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));
       }catch (Exception e){
           e.printStackTrace();
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

    private void connectToServer(){
        try {
            server = new Socket(serverIp, serverPort);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error : Could not connect to server");
        }
    }

    public void closeServerConnection(){
        try {
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error : could not close server socket connection");
        }
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
    public void sendMessage(String message) {
        server = getServerSocket();
        try {
            outputStream.writeUTF(message);
        } catch (Exception e) {
            //throw new RuntimeException(e);
            System.out.println("Error in sending message (" + message + ") to server ");
        }
    }
}


