package Communication;

import BuisnessLogic.Authentication.Response;

public interface ICommunicator {
    public void sendMessage(String message);

    public String receiveMessage();
    public Response receiveResponse();
}
