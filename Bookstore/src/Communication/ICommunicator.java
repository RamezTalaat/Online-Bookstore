package Communication;

import BuisnessLogic.Authentication.Response;

public interface ICommunicator{
    public String  receiveMessage();
    public void sendMessage(String message);

    public void sendResponse(Response response);
}
