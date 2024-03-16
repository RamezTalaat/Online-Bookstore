import Communication.ClientCommunicator;
import Communication.ICommunicator;

public class Main {
    public static void main(String[] args) {
        ICommunicator ClientCommunicator = new ClientCommunicator();
        ClientCommunicator.sendMessage("hi baby");

    }
}