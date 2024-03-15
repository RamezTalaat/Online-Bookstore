package DbContext;

import BuisnessLogic.Models.User;

import java.sql.ResultSet;

public class ObjectMapper {
    public User mapUser(ResultSet resultSet){
        User user= new User();
        try {
            user.id = resultSet.getInt("id");
            user.name = resultSet.getString("name");
            user.userName = resultSet.getString("userName");
            user.password = resultSet.getString("password");
            user.role = resultSet.getString("role");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error in mapping user");
        }
        return user;
    }
    public <T> T mapObject(Class<T> tClass ,ResultSet resultSet){


        try {
            T object = tClass.getDeclaredConstructor().newInstance(); //to create wanted object

            if(tClass == User.class){
                object =  tClass.cast(mapUser(resultSet));
            }
            return object;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error in mapping user");
            return  null;
        }

    }
}
