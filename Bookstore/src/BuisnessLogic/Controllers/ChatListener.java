package BuisnessLogic.Controllers;

import BuisnessLogic.Models.User;
import Communication.ICommunicator;

public class ChatListener extends Thread{
    private UserController sender ;
    private ICommunicator receiver;
    public ChatListener(UserController _sender, ICommunicator _receiver){
        sender = _sender;
        receiver= _receiver;
    }

    @Override
    public void run() {
        String message = "";
        while (!message.equals("exit chat")){
            if(sender.messageBox.isEmpty()){
                //System.out.println("sender message box is empty");
                continue;
            }

            for (int i = 0 ; i < sender.messageBox.size() ; i++){
                message = sender.messageBox.get(i);
                receiver.sendMessage(message);
                sender.messageBox.remove(message);
                if(message.equals("exit chat"))//to exit loop
                    break;
            }

        }
    }
}
