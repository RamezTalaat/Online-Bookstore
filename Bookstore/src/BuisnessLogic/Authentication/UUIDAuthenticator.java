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

        System.out.println("in sing up");
        //Step 1. check userName in database
        Response response  = new Response();
        if(_name == null || _userName == null || _password == null){  //used == not .equals() to compare references not string values
            response.status = 402; //custom error for null values
            response.message = "null arguments are not valid , please try again";
            return response;
        }
        DbConnection dbConnection = new DbConnection();
        String query = "";
        try {

            query = "select * from users where username='" + _userName + "'";
            System.out.println("query = " + query);
//            query = "select * from users where username = ?";
//            PreparedStatement preparedStatement = dbConnection.getConnection().prepareStatement(query);
//            preparedStatement.setString(1,_userName);
//            System.out.println("query = " + query);
//            System.out.println("prepared statement = " + preparedStatement.toString());
            ArrayList<User> result = dbConnection.select(User.class  ,query);
            if(!result.isEmpty()){ //custom error status 400 for reserved userName
                response.status = 400;
                response.message = "userName already reserved , try another one";
                return response;
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("userName already reserved , try another one");
        }


        //Step 2. add user to database
        try {
            query = "insert into users (name , username , password , role) values ('" + _name + "' , '" + _userName + "' , '" +_password + "' , 'user')" ;
            System.out.println("query = " + query);
            dbConnection.create(query);
//            query = "insert into users (name , username , password , role) values (?,?,?,user)";
//            PreparedStatement preparedStatement = dbConnection.getConnection().prepareStatement(query);
//            preparedStatement.setString(1,_name);
//            preparedStatement.setString(2,_userName);
//            preparedStatement.setString(3,_password);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error : problem in adding user to database");
            return errorAddingUser();
        }

        //Step 3. add to Active DB
        int userId = -1;
        try {
            query = "select * from users where username ='" + _userName + "'";
//            PreparedStatement preparedStatement = dbConnection.getConnection().prepareStatement(query);
//            preparedStatement.setString(1,_userName);
            System.out.println("query = " + query);
            ArrayList<User> result = dbConnection.select(User.class ,query);
            System.out.println("result set = " + result.toString());
            if(result.size() != 1){  //we want only 1 user
                System.out.println("result set problem");
                return  errorAddingUser();
            }
            userId = result.get(0).id;


        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error : problem in adding user to database");
            return  errorAddingUser();
        }

        if(userId == -1){
            System.out.println("user id = -1");
            return errorAddingUser();
        }
        ActiveDatabase activeDatabase = ActiveDatabase.getInstance();
        UUID newUuid = activeDatabase.addUser(userId);
        response.status = 200;
        response.message = "User added successfully";
        response.object = newUuid;
        return response;

    }

    //returns uuid if user exists and null otherwise
    public Response signIn( String userName , String password){
        ///Steps
        ///1. get user from DB by userName
        ///2. add to Active DB
        return null;
    }

    private Response errorAddingUser(){
        Response response = new Response();
        response.status = 500; //code for internal server error
        response.message = "Error : problem in adding user to database";
        return response;
    }
}
