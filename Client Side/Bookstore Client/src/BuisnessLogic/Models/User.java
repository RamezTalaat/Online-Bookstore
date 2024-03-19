package BuisnessLogic.Models;

import java.io.Serializable;

public class User implements Serializable {
    public int id;
    public String name;
    public String userName;
    public String password;
    public String role;

    @Override
    public String toString() {
        return "USER=> id = " + id + " , name = " + name + " , userName = " + userName +
                " , password = " + password + " , role = " + role;
    }
}
