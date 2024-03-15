import DbContext.DbConnection;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        DbConnection Db = new DbConnection();
        Connection connection = Db.connectToDb("dumy", "postgres", "postgres");
        Db.createTable(connection, "za3bola");
    }
}