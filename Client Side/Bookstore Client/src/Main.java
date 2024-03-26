import ClientLogic.RegistrationController;
import Communication.ClientCommunicator;
import Communication.ICommunicator;

public class Main {
    public static void main(String[] args) {
        ICommunicator clientCommunicator = new ClientCommunicator(); //communication module
        if(!clientCommunicator.isConnected())
            return;
        System.out.println("Connected successfully to server");
        RegistrationController registrationController = new RegistrationController(clientCommunicator);
        registrationController.registerClient();

    }
}