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
     Connection conn = DBConnection.getConnection();

    // ---- CREATE ----
    public void ajouterMembre(Membre m) throws SQLException {
        String sql = "INSERT INTO membres (nom, prenom, email, telephone, date_debut, date_fin, actif) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";


        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, m.getNom());
        ps.setString(2, m.getPrenom());
        ps.setString(3, m.getEmail());
        ps.setString(4, m.getTelephone());
        ps.setDate(5, Date.valueOf(m.getDateDebut()));
        ps.setDate(6, Date.valueOf(m.getDateFin()));
        ps.setBoolean(7, m.isActif());
        ps.executeUpdate();
        ps.close();
        System.out.println("Membre ajouté : " + m.getPrenom() + " " + m.getNom());
    }

    // ---- READ ALL ----
    public List<Membre> getTousLesMembres() throws SQLException {
        List<Membre> liste = new ArrayList<>();
        String sql = "SELECT * FROM membres";


        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Membre m = new Membre();
            m.setId(rs.getInt("id"));
            m.setNom(rs.getString("nom"));
            m.setPrenom(rs.getString("prenom"));
            m.setEmail(rs.getString("email"));
            m.setTelephone(rs.getString("telephone"));
            m.setDateDebut(rs.getDate("date_debut").toLocalDate());
            m.setDateFin(rs.getDate("date_fin").toLocalDate());
            m.setActif(rs.getBoolean("actif"));
            liste.add(m);
        }
        rs.close();
        ps.close();
        return liste;
    }

    // ---- READ ONE ----
    public Membre getMembreParId(int id) throws SQLException {
        String sql = "SELECT * FROM membres WHERE id = ?";


        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        Membre m = null;
        if (rs.next()) {
            m = new Membre();
            m.setId(rs.getInt("id"));
            m.setNom(rs.getString("nom"));
            m.setPrenom(rs.getString("prenom"));
            m.setEmail(rs.getString("email"));
            m.setTelephone(rs.getString("telephone"));
            m.setDateDebut(rs.getDate("date_debut").toLocalDate());
            m.setDateFin(rs.getDate("date_fin").toLocalDate());
            m.setActif(rs.getBoolean("actif"));
        }
        rs.close();
        ps.close();
        return m;
    }

    // ---- UPDATE ----
    public void modifierMembre(Membre m) throws SQLException {
        String sql = "UPDATE membres SET nom=?, prenom=?, email=?, telephone=?, "
                + "date_debut=?, date_fin=?, actif=? WHERE id=?";


        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, m.getNom());
        ps.setString(2, m.getPrenom());
        ps.setString(3, m.getEmail());
        ps.setString(4, m.getTelephone());
        ps.setDate(5, Date.valueOf(m.getDateDebut()));
        ps.setDate(6, Date.valueOf(m.getDateFin()));
        ps.setBoolean(7, m.isActif());
        ps.setInt(8, m.getId());
        ps.executeUpdate();
        ps.close();
    }

    // ---- DELETE ----
    public void supprimerMembre(int id) throws SQLException {
        String sql = "DELETE FROM membres WHERE id = ?";


        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }

    // ---- Membres avec abonnement actif aujourd'hui ----
    public List<Membre> getMembresActifs() throws SQLException {
        List<Membre> liste = new ArrayList<>();
        String sql = "SELECT * FROM membres WHERE date_debut <= CURDATE() AND date_fin >= CURDATE()";


        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Membre m = new Membre();
            m.setId(rs.getInt("id"));
            m.setNom(rs.getString("nom"));
            m.setPrenom(rs.getString("prenom"));
            m.setEmail(rs.getString("email"));
            m.setTelephone(rs.getString("telephone"));
            m.setDateDebut(rs.getDate("date_debut").toLocalDate());
            m.setDateFin(rs.getDate("date_fin").toLocalDate());
            m.setActif(rs.getBoolean("actif"));
            liste.add(m);
        }
        rs.close();
        ps.close();
        return liste;

        }
}
