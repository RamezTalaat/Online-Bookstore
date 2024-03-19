import ClientLogic.RegistrationController;
import Communication.ClientCommunicator;
import Communication.ICommunicator;

public class Main {
    public static void main(String[] args) {

        ICommunicator clientCommunicator = new ClientCommunicator(); //communication module
        RegistrationController registrationController = new RegistrationController(clientCommunicator);
        registrationController.registerClient();

    }
}