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

    // --- AJOUT : MÉTHODE DE RECHERCHE PAR CRITÈRE ---
    public List<Salle> rechercherSallesParCritere(String critere, String valeur) throws SQLException {
        List<Salle> liste = new ArrayList<>();
        String sql;

        if (critere.equalsIgnoreCase("Capacité")) {
            // Recherche exacte ou supérieure pour la capacité
            sql = "SELECT * FROM salles WHERE capacite >= ?";
        } else {
            // Recherche par nom (LIKE)
            sql = "SELECT * FROM salles WHERE nom LIKE ?";
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (critere.equalsIgnoreCase("Capacité")) {
                try {
                    ps.setInt(1, Integer.parseInt(valeur));
                } catch (NumberFormatException e) {
                    ps.setInt(1, 0); // Si l'utilisateur tape du texte au lieu d'un chiffre
                }
            } else {
                ps.setString(1, "%" + valeur + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Salle s = new Salle();
                    s.setId(rs.getInt("id"));
                    s.setNom(rs.getString("nom"));
                    s.setCapacite(rs.getInt("capacite"));
                    s.setDescription(rs.getString("description"));
                    liste.add(s);
                }
            }
        }
        return liste;
    }

    public void ajouterSalle(Salle s) throws SQLException {
        String sql = "INSERT INTO salles (nom, capacite, description) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getNom());
            ps.setInt(2, s.getCapacite());
            ps.setString(3, s.getDescription());
            ps.executeUpdate();
        }
    }

    public List<Salle> getToutesLesSalles() throws SQLException {
        List<Salle> liste = new ArrayList<>();
        String sql = "SELECT * FROM salles";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Salle s = new Salle();
                s.setId(rs.getInt("id"));
                s.setNom(rs.getString("nom"));
                s.setCapacite(rs.getInt("capacite"));
                s.setDescription(rs.getString("description"));
                liste.add(s);
            }
        }
        return liste;
    }

    public void modifierSalle(Salle s) throws SQLException {
        String sql = "UPDATE salles SET nom=?, capacite=?, description=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getNom());
            ps.setInt(2, s.getCapacite());
            ps.setString(3, s.getDescription());
            ps.setInt(4, s.getId());
            ps.executeUpdate();
        }
    }

    public void supprimerSalle(int id) throws SQLException {
        String sql = "DELETE FROM salles WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}