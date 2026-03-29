package ma.ac.esi.gymsystem.controller;

import ma.ac.esi.gymsystem.DAO.UtilisateurDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField     champUsername;
    @FXML private PasswordField champPassword;
    @FXML private Label         labelErreur;

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @FXML
    public void initialize() {
        labelErreur.setText("");
        champPassword.setOnAction(event -> seConnecter());
        champUsername.setOnAction(event -> champPassword.requestFocus());
    }

    @FXML
    public void seConnecter() {
        String username = champUsername.getText().trim();
        String password = champPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            labelErreur.setText("Remplissez tous les champs.");
            return;
        }

        try {
            String role = utilisateurDAO.verifierConnexion(username, password);

            if (role != null) {
                ouvrirDashboard(username, role);
            } else {
                labelErreur.setText("Login ou mot de passe incorrect.");
                champPassword.clear();
            }

        } catch (SQLException e) {
            labelErreur.setText("Erreur BDD : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ouvrirDashboard(String username, String role) {
        try {

            URL fxmlUrl = getClass().getResource(
                    "/ma/ac/esi/gymsystem/view/main.fxml"
            );





            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Passer username et role au MainController
            MainController mainCtrl = loader.getController();
            mainCtrl.initialiserUtilisateur(username, role);

            // Changer la scène
            Stage stage = (Stage) champUsername.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 700);

            // Charger le CSS
            URL cssUrl = getClass().getResource("/ma/ac/esi/gymsystem/style.css");


            stage.setScene(scene);
            stage.setTitle("GymManager — " + username);
            stage.setResizable(true);
            stage.centerOnScreen();

        } catch (Exception e) {
            labelErreur.setText("Erreur : " + e.getMessage());
            e.printStackTrace();
            if (e.getCause() != null) {
                System.err.println("CAUSE : " + e.getCause().getMessage());
                e.getCause().printStackTrace();
            }
        }
    }
}