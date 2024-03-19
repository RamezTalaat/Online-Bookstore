package BuisnessLogic.Authentication;

import BuisnessLogic.Models.User;
import DbContext.DbConnection;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.UUID;

public class UUIDAuthenticator implements  IAuthenticator{
    //returns uuid if user was added and null otherwise
    public Response signUp(String _name , String _userName , String _password){
        ///Steps
        ///1. check userName in database
        ///2. add user to database
        ///3. add to Active DB

        //Step 1. check userName in database
        Response response  = new Response();
        if(_name == null || _userName == null || _password == null){  //used == not .equals() to compare references not string values
            response.status = 402; //custom error for null values
            response.message = "null arguments are not valid , please try again";
            return response;
        }
        DbConnection dbConnection = new DbConnection();
        String query = "";

        query = "select * from users where username='" + _userName + "'";
        //System.out.println("query = " + query);

        ArrayList<User> result = dbConnection.select(User.class  ,query);
        if(!result.isEmpty()){ //custom error status 400 for reserved userName
            response.status = 400;
            response.message = "userName already reserved , try another one";
            return response;
        }



        //Step 2. add user to database
        query = "insert into users (name , username , password , role) values ('" + _name + "' , '" + _userName + "' , '" +_password + "' , 'user')" ;//role = user by default
        //System.out.println("query = " + query);
        Boolean queryResult = dbConnection.operate(query);

        if(!queryResult)
            return errorAddingUser();

        //Step 3. add to Active DB
        int userId = -1;
        query = "select * from users where username ='" + _userName + "'";
        System.out.println("query = " + query);
        result = dbConnection.select(User.class ,query);
        System.out.println("result set = " + result.toString());
        if(result.size() != 1){  //we want only 1 user
            System.out.println("result set problem");
            return  errorAddingUser();
        }
        userId = result.get(0).id;

        if(userId == -1){
            System.out.println("user id = -1");
            return errorAddingUser();
        }
        //UUID uuid = addUserToActiveDb(result.get(0).id);
        response.status = 200;
        response.message = "User added successfully";
        response.object = result.get(0);
        return response;

    }

    //returns uuid if user exists and null otherwise
    public Response signIn( String _userName , String _password){
        ///Steps
        ///1. get user from DB by userName
        ///2. add to Active DB

        //Step 1. get user from DB by userName
        Response response  = new Response();
        if( _userName == null || _password == null){  //used == not .equals() to compare references not string values
            response.status = 402; //custom error for null values
            response.message = "null arguments are not valid , please try again";
            return response;
        }
        DbConnection dbConnection = new DbConnection();
        String query = "";

        query = "select * from users where username='" + _userName + "'";


        ArrayList<User> result = dbConnection.select(User.class  ,query);
        if(result.isEmpty()){ //custom error status 400 for reserved userName
            response.status = 404;
            response.message = "Error : userName is not found!";
            return response;
        }


        if(!result.get(0).password.equals(_password)){
            response.status = 401;
            response.message = "Error : Wrong Password!";
            return response;
        }

        //Step 2. add to Active DB
        //UUID uuid = addUserToActiveDb(result.get(0).id);
        response.status = 200;
        if(result.get(0).role.equals("admin")){
            response.message = "Admin signed in successfully";
        }else{
            response.message = "User signed in successfully";
        }
        response.object = result.get(0);
        return response;
    }

    public Response signOut (UUID uuid){
        Response response = new Response();
        if(uuid == null){
            response.status = 402;
            response.message = "Error : UUID ";
        }

        ActiveDatabase activeDatabase = ActiveDatabase.getInstance();
        activeDatabase.removeUser(uuid);
        int id = activeDatabase.getUserID(uuid);
        if(id == -1){
            response.status = 200;
            response.message = "User Removed successfully";
            return response;
        }
        response.status = 500;
        response.message = "Error : Could not remove user";
        return response;
    }

    private Response errorAddingUser(){
        Response response = new Response();
        response.status = 500; //code for internal server error
        response.message = "Error : problem in adding user to database";
        return response;
    }

    private UUID addUserToActiveDb(int id){
        ActiveDatabase activeDatabase = ActiveDatabase.getInstance();
        UUID newUuid = activeDatabase.addUser(id);

        return newUuid;
    }

}
