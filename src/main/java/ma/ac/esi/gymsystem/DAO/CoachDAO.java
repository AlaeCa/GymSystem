package ma.ac.esi.gymsystem.DAO;

import ma.ac.esi.gymsystem.model.Coach;
import ma.ac.esi.gymsystem.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LENOVO
 **/
public class CoachDAO {

    Connection conn = DBConnection.getConnection();

    // ---- CREATE ----
    public void ajouterCoach(Coach c) throws SQLException {


        String sql = "INSERT INTO coachs (nom, prenom, email, telephone, specialite) "
                + "VALUES (?, ?, ?, ?, ?)";


        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, c.getNom());
        ps.setString(2, c.getPrenom());
        ps.setString(3, c.getEmail());
        ps.setString(4, c.getTelephone());
        ps.setString(5, c.getSpecialite());
        ps.executeUpdate();
        ps.close();
    }

    // ---- READ ALL ----
    public List<Coach> getTousLesCoachs() throws SQLException {
        List<Coach> liste = new ArrayList<>();
        String sql = "SELECT * FROM coachs";


        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Coach c = new Coach();
            c.setId(rs.getInt("id"));
            c.setNom(rs.getString("nom"));
            c.setPrenom(rs.getString("prenom"));
            c.setEmail(rs.getString("email"));
            c.setTelephone(rs.getString("telephone"));
            c.setSpecialite(rs.getString("specialite"));
            liste.add(c);
        }
        rs.close();
        ps.close();
        return liste;
    }

    // ---- READ ONE ----
    public Coach getCoachParId(int id) throws SQLException {
        String sql = "SELECT * FROM coachs WHERE id = ?";


        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        Coach c = null;
        if (rs.next()) {
            c = new Coach();
            c.setId(rs.getInt("id"));
            c.setNom(rs.getString("nom"));
            c.setPrenom(rs.getString("prenom"));
            c.setEmail(rs.getString("email"));
            c.setTelephone(rs.getString("telephone"));
            c.setSpecialite(rs.getString("specialite"));
        }
        rs.close();
        ps.close();
        return c;
    }

    // ---- UPDATE ----
    public void modifierCoach(Coach c) throws SQLException {
        String sql = "UPDATE coachs SET nom=?, prenom=?, email=?, telephone=?, specialite=? WHERE id=?";


        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, c.getNom());
        ps.setString(2, c.getPrenom());
        ps.setString(3, c.getEmail());
        ps.setString(4, c.getTelephone());
        ps.setString(5, c.getSpecialite());
        ps.setInt(6, c.getId());
        ps.executeUpdate();
        ps.close();
    }

    // ---- DELETE ----
    public void supprimerCoach(int id) throws SQLException {
        String sql = "DELETE FROM coachs WHERE id = ?";


        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }
}
