package ma.ac.esi.gymsystem.DAO;

import ma.ac.esi.gymsystem.model.Membre;
import ma.ac.esi.gymsystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LENOVO
 **/
public class MembreDAO {
    private Connection conn = DBConnection.getConnection();

    // --- UTILITAIRE : MAPPER LE RESULTSET EN OBJET MEMBRE ---
    // Centralise la lecture des colonnes pour éviter les erreurs et les répétitions
    private Membre mapResultSetToMembre(ResultSet rs) throws SQLException {
        Membre m = new Membre();
        m.setId(rs.getInt("id"));
        m.setNom(rs.getString("nom"));
        m.setPrenom(rs.getString("prenom"));
        m.setEmail(rs.getString("email"));
        m.setTelephone(rs.getString("telephone"));

        // Conversion des dates SQL vers LocalDate
        Date dateDeb = rs.getDate("date_debut");
        if (dateDeb != null) m.setDateDebut(dateDeb.toLocalDate());

        Date dateF = rs.getDate("date_fin");
        if (dateF != null) m.setDateFin(dateF.toLocalDate());

        m.setActif(rs.getBoolean("actif"));
        return m;
    }

    // ---- CREATE ----
    public void ajouterMembre(Membre m) throws SQLException {
        String sql = "INSERT INTO membres (nom, prenom, email, telephone, date_debut, date_fin, actif) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNom());
            ps.setString(2, m.getPrenom());
            ps.setString(3, m.getEmail());
            ps.setString(4, m.getTelephone());
            ps.setDate(5, Date.valueOf(m.getDateDebut()));
            ps.setDate(6, Date.valueOf(m.getDateFin()));
            ps.setBoolean(7, m.isActif());
            ps.executeUpdate();
            System.out.println("Membre ajouté : " + m.getPrenom() + " " + m.getNom());
        }
    }

    // ---- READ ALL ----
    public List<Membre> getTousLesMembres() throws SQLException {
        List<Membre> liste = new ArrayList<>();
        String sql = "SELECT * FROM membres";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                liste.add(mapResultSetToMembre(rs));
            }
        }
        return liste;
    }

    // ---- READ ONE ----
    public Membre getMembreParId(int id) throws SQLException {
        String sql = "SELECT * FROM membres WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMembre(rs);
                }
            }
        }
        return null;
    }

    // ---- UPDATE ----
    public void modifierMembre(Membre m) throws SQLException {
        String sql = "UPDATE membres SET nom=?, prenom=?, email=?, telephone=?, "
                + "date_debut=?, date_fin=?, actif=? WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNom());
            ps.setString(2, m.getPrenom());
            ps.setString(3, m.getEmail());
            ps.setString(4, m.getTelephone());
            ps.setDate(5, Date.valueOf(m.getDateDebut()));
            ps.setDate(6, Date.valueOf(m.getDateFin()));
            ps.setBoolean(7, m.isActif());
            ps.setInt(8, m.getId());
            ps.executeUpdate();
        }
    }

    // ---- DELETE ----
    public void supprimerMembre(int id) throws SQLException {
        String sql = "DELETE FROM membres WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ---- SEARCH BY CRITERIA ----
    public List<Membre> rechercherMembreParCritere(String critere, String valeur) throws SQLException {
        List<Membre> liste = new ArrayList<>();
        String sql;

        if (critere.equalsIgnoreCase("Abonnement")) {
            // Si on cherche les valides, on compare les dates avec aujourd'hui
            if (valeur.toLowerCase().contains("valide")) {
                sql = "SELECT * FROM membres WHERE date_debut <= CURDATE() AND date_fin >= CURDATE() AND actif = 1";
            } else {
                // Sinon on cherche les expirés
                sql = "SELECT * FROM membres WHERE date_fin < CURDATE() OR actif = 0";
            }
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(mapResultSetToMembre(rs));
                }
            }
        } else {
            // Recherche classique (Nom, Prénom)
            String colonneSql = critere.toLowerCase().equals("prénom") ? "prenom" : "nom";
            sql = "SELECT * FROM membres WHERE " + colonneSql + " LIKE ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "%" + valeur + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        liste.add(mapResultSetToMembre(rs));
                    }
                }
            }
        }
        return liste;
    }
    // ---- MEMBRES AVEC ABONNEMENT ACTIF AUJOURD'HUI ----
    public List<Membre> getMembresActifs() throws SQLException {
        List<Membre> liste = new ArrayList<>();
        // Utilisation de CURDATE() pour MySQL (ou SYSDATE pour Oracle)
        String sql = "SELECT * FROM membres WHERE date_debut <= CURDATE() AND date_fin >= CURDATE() AND actif = 1";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                liste.add(mapResultSetToMembre(rs));
            }
        }
        return liste;
    }
}