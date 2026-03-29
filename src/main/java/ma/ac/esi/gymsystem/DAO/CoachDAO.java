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

    private Connection conn = DBConnection.getConnection();


    // Cette méthode évite la répétition de code dans getTous, getParId et rechercher
    private Coach mapResultSetToCoach(ResultSet rs) throws SQLException {
        Coach c = new Coach();
        c.setId(rs.getInt("id"));
        c.setNom(rs.getString("nom"));
        c.setPrenom(rs.getString("prenom"));
        c.setEmail(rs.getString("email"));
        c.setTelephone(rs.getString("telephone"));
        c.setSpecialite(rs.getString("specialite"));
        return c;
    }

    // ---- CREATE ----
    public void ajouterCoach(Coach c) throws SQLException {
        String sql = "INSERT INTO coachs (nom, prenom, email, telephone, specialite) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNom());
            ps.setString(2, c.getPrenom());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getTelephone());
            ps.setString(5, c.getSpecialite());
            ps.executeUpdate();
        }
    }

    // ---- READ ALL ----
    public List<Coach> getTousLesCoachs() throws SQLException {
        List<Coach> liste = new ArrayList<>();
        String sql = "SELECT * FROM coachs";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                liste.add(mapResultSetToCoach(rs));
            }
        }
        return liste;
    }

    // ---- READ ONE ----
    public Coach getCoachParId(int id) throws SQLException {
        String sql = "SELECT * FROM coachs WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCoach(rs);
                }
            }
        }
        return null;
    }

    // ---- UPDATE ----
    public void modifierCoach(Coach c) throws SQLException {
        String sql = "UPDATE coachs SET nom=?, prenom=?, email=?, telephone=?, specialite=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNom());
            ps.setString(2, c.getPrenom());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getTelephone());
            ps.setString(5, c.getSpecialite());
            ps.setInt(6, c.getId());
            ps.executeUpdate();
        }
    }

    // ---- DELETE ----
    public void supprimerCoach(int id) throws SQLException {
        String sql = "DELETE FROM coachs WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ---- SEARCH BY CRITERIA ----
    public List<Coach> rechercherCoachsParCritere(String critere, String valeur) throws SQLException {
        List<Coach> liste = new ArrayList<>();

        // Sécurisation du choix de la colonne
        String colonneSql = switch (critere.toLowerCase()) {
            case "prénom" -> "prenom";
            case "spécialité" -> "specialite";
            default -> "nom"; // Par défaut recherche par nom
        };

        String sql = "SELECT * FROM coachs WHERE " + colonneSql + " LIKE ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + valeur + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(mapResultSetToCoach(rs));
                }
            }
        }
        return liste;
    }
}

