package Controllers;

import java.sql.*;

public class MySQLAccess {


    static Connection connect;

    public static Connection ConnectDB() {

        try {

            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");

            // Setup the connection with the DB
            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestionstore", "root", "ouss112#");

            return connect;

        } catch (Exception e) {
            System.out.println(e);
            return null;

        }
    }

}