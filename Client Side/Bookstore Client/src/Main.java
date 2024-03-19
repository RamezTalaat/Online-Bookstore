import ClientLogic.RegistrationController;
import Communication.ClientCommunicator;
import Communication.ICommunicator;

public class Main {
    public static void main(String[] args) {

        ICommunicator clientCommunicator = new ClientCommunicator();
        RegistrationController registrationController = new RegistrationController(clientCommunicator);
        registrationController.registerClient();
//        try {
////            Socket server = new Socket("127.0.0.1", 5005);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
////            DataOutputStream out = new DataOutputStream(server.getOutputStream());
//            String message = "";
//            do {
//
//                System.out.println("Enter a message:");
//                message = reader.readLine();
////                out.writeUTF(message);
//                clientCommunicator.sendMessage(message);
//                System.out.println("----> Message sent to server");
//            } while (!message.equals("end"));
//        }catch (Exception e){
//            e.printStackTrace();
//        }

    }
}