package ma.ac.esi.gymsystem.DAO;

import ma.ac.esi.gymsystem.model.Utilisateur;
import ma.ac.esi.gymsystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO complet pour la gestion des utilisateurs.
 * Inclut la vérification du minimum 1 admin avant suppression.
 */
public class UtilisateurDAO {

    Connection conn = DBConnection.getConnection();

    // ---- Vérification login (existant) ----
    public String verifierConnexion(String username, String password) throws SQLException {
        String sql = "SELECT role FROM utilisateurs WHERE username = ? AND password = ? AND actif = 1";


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
        return role;
    }

    // ---- READ ALL ----
    public List<Utilisateur> getTousLesUtilisateurs() throws SQLException {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs ORDER BY role, username";


        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Utilisateur u = new Utilisateur();
            u.setId(rs.getInt("id"));
            u.setUsername(rs.getString("username"));
            u.setPassword(rs.getString("password"));
            u.setRole(rs.getString("role"));
            u.setNom(rs.getString("nom"));
            u.setActif(rs.getBoolean("actif"));
            liste.add(u);
        }
        rs.close();
        ps.close();
        return liste;
    }

    // ---- CREATE ----
    public void ajouterUtilisateur(Utilisateur u) throws SQLException {
        String sql = "INSERT INTO utilisateurs (username, password, role, nom, actif) VALUES (?, ?, ?, ?, 1)";


        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, u.getUsername());
        ps.setString(2, u.getPassword());
        ps.setString(3, u.getRole());
        ps.setString(4, u.getNom());
        ps.executeUpdate();
        ps.close();
    }

    // ---- UPDATE ----
    public void modifierUtilisateur(Utilisateur u) throws SQLException {
        String sql = "UPDATE utilisateurs SET username=?, role=?, nom=?, actif=? WHERE id=?";


        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, u.getUsername());
        ps.setString(2, u.getRole());
        ps.setString(3, u.getNom());
        ps.setBoolean(4, u.isActif());
        ps.setInt(5, u.getId());
        ps.executeUpdate();
        ps.close();
    }

    // ---- UPDATE mot de passe ----
    public void modifierMotDePasse(int id, String nouveauPassword) throws SQLException {
        String sql = "UPDATE utilisateurs SET password=? WHERE id=?";


        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, nouveauPassword);
        ps.setInt(2, id);
        ps.executeUpdate();
        ps.close();
    }

    // ---- DELETE avec vérification minimum 1 admin ----
    /**
     * Supprime un utilisateur SEULEMENT si :
     * 1. Ce n'est pas le dernier admin du système.
     * @return true si suppression OK, false si bloquée (dernier admin)
     */
    public boolean supprimerUtilisateur(int id) throws SQLException {
        // Vérifier si c'est un admin
        String sqlRole = "SELECT role FROM utilisateurs WHERE id = ?";

        PreparedStatement ps1 = conn.prepareStatement(sqlRole);
        ps1.setInt(1, id);
        ResultSet rs = ps1.executeQuery();

        String role = null;
        if (rs.next()) {
            role = rs.getString("role");
        }
        rs.close();
        ps1.close();

        // Si c'est un admin, vérifier combien d'admins actifs il reste
        if ("ADMIN".equals(role)) {
            int nbAdmins = compterAdminsActifs();
            if (nbAdmins <= 1) {
                // Bloquer la suppression : dernier admin
                return false;
            }
        }

        // Suppression autorisée
        String sqlDel = "DELETE FROM utilisateurs WHERE id = ?";
        PreparedStatement ps2 = conn.prepareStatement(sqlDel);
        ps2.setInt(1, id);
        ps2.executeUpdate();
        ps2.close();
        return true;
    }

    // ---- Compter les admins actifs ----
    public int compterAdminsActifs() throws SQLException {
        String sql = "SELECT COUNT(*) FROM utilisateurs WHERE role = 'ADMIN' AND actif = 1";


        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        rs.close();
        ps.close();
        return count;
    }

    // ---- Vérifier si username déjà pris ----
    public boolean usernameExiste(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM utilisateurs WHERE username = ?";


        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();

        boolean existe = false;
        if (rs.next()) {
            existe = rs.getInt(1) > 0;
        }
        rs.close();
        ps.close();
        return existe;
    }
}