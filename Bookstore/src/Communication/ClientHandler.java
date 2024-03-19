package Communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable , ICommunicator {
    private Socket clientSocket;
    private static DataOutputStream outputStream;
    private static DataInputStream inputStream;
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
        try {
            //
            String message = "";
            while (!message.equals("end")) {
                message = receiveMessage();
                System.out
                        .println("Message from client of port " + clientSocket.getPort() + ": ( " + message + " )");

            }

            clientSocket.close();
            System.out.println("----> Connection closed for client of port " + clientSocket.getPort());
        } catch (Exception e) {
            e.printStackTrace();
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
    public void sendMessage(String message) {
        try{
            outputStream.writeUTF(message);

        }catch (Exception e){
            System.out.println("Error : could not send message properly to client at "+  clientSocket.getPort());
        }
    }
}