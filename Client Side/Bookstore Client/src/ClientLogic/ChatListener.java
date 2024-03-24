package ClientLogic;

import Communication.ICommunicator;

public class ChatListener extends Thread{
    private final ICommunicator communicator;
    public ChatListener(ICommunicator _communicator){
        communicator = _communicator;
    }

    @Override
    public void run() {
        String message = "";
        while (!message.equals("exit chat")){
            message = communicator.receiveMessage();
            if(message.equals("exit chat")){
                System.out.println("The other side exited the chat");
                break;
            }
            System.out.println("CHAT: " + message);
        }
    }
}
