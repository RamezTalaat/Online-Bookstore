package BuisnessLogic.Authentication;

import java.io.Serializable;

public class Response<T> implements Serializable {
    public String message;
    public int status;

    public T object;

    @Override
    public String toString() {
        return "Response => status = " + status + " , message =  " + message + " , object = " + object;
    }

}
