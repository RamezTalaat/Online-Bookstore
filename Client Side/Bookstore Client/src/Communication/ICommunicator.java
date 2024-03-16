package Communication;

public interface ICommunicator {
    public void sendMessage(String message);

    public String receiveMessage();
}
