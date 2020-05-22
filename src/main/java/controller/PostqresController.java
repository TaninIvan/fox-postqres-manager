package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class PostqresController {

    static String  url = null;
    static String dbName = null;
    static String user = null;
    static String password = null;
    static Connection connection = null;

    public PostqresController (String dbName, String usr, String pass) {
        url = "jdbc:postgresql://localhost/" + dbName;
        PostqresController.dbName = dbName;
        user = usr;
        password = pass;
    }

    public static Connection createConnection() throws Exception {
        if (connection == null || connection.isClosed()) {
            Properties props = new Properties();
            props.setProperty("escapeSyntaxCallMode", "callIfNoReturn");
            props.setProperty("user", user);
            props.setProperty("password", password);
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, props);
            return connection;
        } else throw new Exception("Соединение уже установлено!");
    }

    public static void setDbName(String newBd) {
        dbName = newBd;
        url = "jdbc:postgresql://localhost/" + newBd;
    }
}
