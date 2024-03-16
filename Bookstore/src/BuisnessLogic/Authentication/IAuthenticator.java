package BuisnessLogic.Authentication;

public interface IAuthenticator {
    public Response signUp(String name , String userName , String password);
    public Response signIn( String userName , String password);
}
