package ma.ac.esi.gymsystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author LENOVO
 **/
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/gymmanager";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection;

    public static Connection getConnection()  {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return connection;
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
}
