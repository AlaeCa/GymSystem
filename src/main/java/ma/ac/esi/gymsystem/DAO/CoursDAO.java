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
    private Connection conn = DBConnection.getConnection();

    // --- AJOUT : MÉTHODE DE RECHERCHE MULTI-CRITÈRES ---
    public List<Cours> rechercherCoursParCritere(String critere, String valeur) throws SQLException {
        List<Cours> liste = new ArrayList<>();

        // Base de la requête avec les jointures pour avoir les noms lisibles
        String sql = "SELECT c.*, CONCAT(co.prenom, ' ', co.nom) AS coach_nom, s.nom AS salle_nom " +
                "FROM cours c " +
                "LEFT JOIN coachs co ON c.coach_id = co.id " +
                "LEFT JOIN salles s  ON c.salle_id = s.id WHERE ";

        // Adaptation de la clause WHERE selon le critère
        switch (critere.toLowerCase()) {
            case "coach":
                sql += "CONCAT(co.prenom, ' ', co.nom) LIKE ?";
                break;
            case "salle":
                sql += "s.nom LIKE ?";
                break;
            case "durée":
                sql += "c.duree_min = ?";
                break;
            default: // Par défaut : Nom du cours
                sql += "c.nom LIKE ?";
                break;
        }

        sql += " ORDER BY c.horaire";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (critere.toLowerCase().equals("durée")) {
                try {
                    ps.setInt(1, Integer.parseInt(valeur));
                } catch (NumberFormatException e) {
                    ps.setInt(1, 0); // Évite le crash si l'utilisateur ne tape pas un nombre
                }
            } else {
                ps.setString(1, "%" + valeur + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(mapResultSetToCours(rs));
                }
            }
        }
        return liste;
    }

    // --- UTILITAIRE : MAPPER LE RESULTSET (pour éviter la répétition) ---
    private Cours mapResultSetToCours(ResultSet rs) throws SQLException {
        Cours cours = new Cours();
        cours.setId(rs.getInt("id"));
        cours.setNom(rs.getString("nom"));
        cours.setHoraire(rs.getTimestamp("horaire").toLocalDateTime());
        cours.setDureeMins(rs.getInt("duree_min"));
        cours.setCoachId(rs.getInt("coach_id"));
        cours.setCoachNom(rs.getString("coach_nom"));
        cours.setSalleId(rs.getInt("salle_id"));
        cours.setSalleNom(rs.getString("salle_nom"));
        return cours;
    }

    // ---- CREATE ----
    public void ajouterCours(Cours c) throws SQLException {
        String sql = "INSERT INTO cours (nom, horaire, duree_min, coach_id, salle_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNom());
            ps.setTimestamp(2, Timestamp.valueOf(c.getHoraire()));
            ps.setInt(3, c.getDureeMins());
            ps.setInt(4, c.getCoachId());
            ps.setInt(5, c.getSalleId());
            ps.executeUpdate();
        }
    }

    // ---- READ ALL ----
    public List<Cours> getTousLesCours() throws SQLException {
        List<Cours> liste = new ArrayList<>();
        String sql = "SELECT c.*, CONCAT(co.prenom, ' ', co.nom) AS coach_nom, s.nom AS salle_nom " +
                "FROM cours c " +
                "LEFT JOIN coachs co ON c.coach_id = co.id " +
                "LEFT JOIN salles s  ON c.salle_id = s.id " +
                "ORDER BY c.horaire";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                liste.add(mapResultSetToCours(rs));
            }
        }
        return liste;
    }

    // ---- UPDATE ----
    public void modifierCours(Cours c) throws SQLException {
        String sql = "UPDATE cours SET nom=?, horaire=?, duree_min=?, coach_id=?, salle_id=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNom());
            ps.setTimestamp(2, Timestamp.valueOf(c.getHoraire()));
            ps.setInt(3, c.getDureeMins());
            ps.setInt(4, c.getCoachId());
            ps.setInt(5, c.getSalleId());
            ps.setInt(6, c.getId());
            ps.executeUpdate();
        }
    }

    // ---- DELETE ----
    public void supprimerCours(int id) throws SQLException {
        String sql = "DELETE FROM cours WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}