package ma.ac.esi.gymsystem.DAO;

import ma.ac.esi.gymsystem.model.Salle;
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
public class SalleDAO {
    Connection conn = DBConnection.getConnection();

    public void ajouterSalle(Salle s) throws SQLException {
        String sql = "INSERT INTO salles (nom, capacite, description) VALUES (?, ?, ?)";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, s.getNom());
        ps.setInt(2, s.getCapacite());
        ps.setString(3, s.getDescription());
        ps.executeUpdate();
        ps.close();
    }

    public List<Salle> getToutesLesSalles() throws SQLException {
        List<Salle> liste = new ArrayList<>();
        String sql = "SELECT * FROM salles";

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Salle s = new Salle();
            s.setId(rs.getInt("id"));
            s.setNom(rs.getString("nom"));
            s.setCapacite(rs.getInt("capacite"));
            s.setDescription(rs.getString("description"));
            liste.add(s);
        }
        rs.close();
        ps.close();
        return liste;
    }

    public void modifierSalle(Salle s) throws SQLException {
        String sql = "UPDATE salles SET nom=?, capacite=?, description=? WHERE id=?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, s.getNom());
        ps.setInt(2, s.getCapacite());
        ps.setString(3, s.getDescription());
        ps.setInt(4, s.getId());
        ps.executeUpdate();
        ps.close();
    }

    public void supprimerSalle(int id) throws SQLException {
        String sql = "DELETE FROM salles WHERE id = ?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }
}
