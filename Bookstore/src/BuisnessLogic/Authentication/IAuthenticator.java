package BuisnessLogic.Authentication;

import java.util.UUID;

public interface IAuthenticator {
    public Response signUp(String name , String userName , String password);
    public Response signIn( String userName , String password);

//    public Response signOut( UUID uuid);
}
