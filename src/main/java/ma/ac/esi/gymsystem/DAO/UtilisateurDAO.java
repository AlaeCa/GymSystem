package ma.ac.esi.gymsystem.DAO;

import ma.ac.esi.gymsystem.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author LENOVO
 **/
public class UtilisateurDAO {

    Connection conn = DBConnection.getConnection();


    public String verifierConnexion(String username, String password) throws SQLException {
        String sql = "SELECT role FROM utilisateurs WHERE username = ? AND password = ?";


        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();

        String role = null;
        if (rs.next()) {
            role = rs.getString("role");
        }

        rs.close();
        ps.close();
        return role;   // null = identifiants incorrects
    }
}
