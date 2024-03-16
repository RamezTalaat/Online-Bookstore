package BuisnessLogic.Authentication;

public class Response<T> {
    public String message;
    public int status;

    public T object;

    @Override
    public String toString() {
        return "Response => status = " + status + " , message =  " + message + " , object = " + object;
    }
}
