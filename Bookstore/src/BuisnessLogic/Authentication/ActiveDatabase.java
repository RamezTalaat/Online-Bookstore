package BuisnessLogic.Authentication;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActiveDatabase {
    private static Map<UUID , Integer> activeDb; //map only operates with Integer not int, I do not know why ;)
    private static ActiveDatabase instance;
    private ActiveDatabase(){ //private constructor to make DB singleton
        activeDb = new HashMap<>();
    }
    public static ActiveDatabase getInstance(){ //to make class singleton
        if(instance == null){
            //System.out.println("Created a new Db");
            instance = new ActiveDatabase();
        }
        //System.out.println("Returned Db instance");
        return instance;
    }
    public int getUserID(UUID uuid){
        Integer id = activeDb.get(uuid);
        if(id == null)
            return -1;
        else
            return id;
    }

    public UUID addUser(int id){
        UUID uuid = UUID.randomUUID();//to generate a random UUID for session
        activeDb.put(uuid , id);
        return uuid;
    }

    public void removeUser (UUID uuid){
        activeDb.remove(uuid);
    }

    public void printActiveDatabase(){
        if(activeDb.isEmpty())
            return;
        for(Map.Entry<UUID , Integer> pair: activeDb.entrySet()){
            System.out.println("uuid = " + pair.getKey() + " , id = " + pair.getValue());
        }
    }
}
