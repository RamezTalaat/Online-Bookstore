package Communication;

import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Controllers.RegistrationController;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable , ICommunicator {
    private Socket clientSocket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    public ClientHandler(Socket clientSocket) {
        try{
            this.clientSocket = clientSocket;
            inputStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            outputStream = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error : Client handler could not connect to client");
        }

    }

    @Override
    public void run() {
        RegistrationController registrationController = new RegistrationController(this);
        registrationController.handleClientRequest();
        try {
            outputStream.close();
            inputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    @Override
    public String receiveMessage() {
        try{
            String message = inputStream.readUTF();
            return message;
        }catch (Exception e){
            System.out.println("Error : could not receive message properly from client at "+  clientSocket.getPort());
        }
       return null;

    }

    @Override
    public void sendMessage(String  message) {
        try{

            outputStream.writeUTF(message);
            outputStream.flush();

        }catch (Exception e){
            System.out.println("Error : could not send message properly to client at "+  clientSocket.getPort());
        }
    }

    @Override
    public void sendResponse(Response response) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Could not send response : " + response);
        }

    }
}
