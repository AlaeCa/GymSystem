package ma.ac.esi.gymsystem.DAO;

import ma.ac.esi.gymsystem.model.Cours;
import ma.ac.esi.gymsystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LENOVO
 **/
public class CoursDAO {
    Connection conn = DBConnection.getConnection();


    public void ajouterCours(Cours c) throws SQLException {
        String sql = "INSERT INTO cours (nom, horaire, duree_min, coach_id, salle_id) "
                + "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, c.getNom());
        ps.setTimestamp(2, Timestamp.valueOf(c.getHoraire()));
        ps.setInt(3, c.getDureeMins());
        ps.setInt(4, c.getCoachId());
        ps.setInt(5, c.getSalleId());
        ps.executeUpdate();
        ps.close();
    }

    // Requête avec JOIN pour récupérer les noms du coach et de la salle
    public List<Cours> getTousLesCours() throws SQLException {
        List<Cours> liste = new ArrayList<>();
        String sql = "SELECT c.*, " +
                "CONCAT(co.prenom, ' ', co.nom) AS coach_nom, " +
                "s.nom AS salle_nom " +
                "FROM cours c " +
                "LEFT JOIN coachs co ON c.coach_id = co.id " +
                "LEFT JOIN salles s  ON c.salle_id = s.id " +
                "ORDER BY c.horaire";


        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Cours cours = new Cours();
            cours.setId(rs.getInt("id"));
            cours.setNom(rs.getString("nom"));
            cours.setHoraire(rs.getTimestamp("horaire").toLocalDateTime());
            cours.setDureeMins(rs.getInt("duree_min"));
            cours.setCoachId(rs.getInt("coach_id"));
            cours.setCoachNom(rs.getString("coach_nom"));
            cours.setSalleId(rs.getInt("salle_id"));
            cours.setSalleNom(rs.getString("salle_nom"));
            liste.add(cours);
        }
        rs.close();
        ps.close();
        return liste;
    }

    public void modifierCours(Cours c) throws SQLException {
        String sql = "UPDATE cours SET nom=?, horaire=?, duree_min=?, coach_id=?, salle_id=? WHERE id=?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, c.getNom());
        ps.setTimestamp(2, Timestamp.valueOf(c.getHoraire()));
        ps.setInt(3, c.getDureeMins());
        ps.setInt(4, c.getCoachId());
        ps.setInt(5, c.getSalleId());
        ps.setInt(6, c.getId());
        ps.executeUpdate();
        ps.close();
    }

    public void supprimerCours(int id) throws SQLException {
        String sql = "DELETE FROM cours WHERE id = ?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }
}
