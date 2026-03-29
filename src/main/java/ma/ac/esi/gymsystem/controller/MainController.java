package ma.ac.esi.gymsystem.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;

public class MainController {

    @FXML private BorderPane rootLayout;
    @FXML private Label      labelNomUtilisateur;
    @FXML private Label      labelRole;
    @FXML private Label      labelPageCourante;
    @FXML private VBox       menuCoachs;
    @FXML private VBox menuUtilisateurs;  // dans le FXML : fx:id="menuUtilisateurs"

    private String username;
    private String role;

    private static final String BASE = "/ma/ac/esi/gymsystem/view/";

    // Appelé par LoginController après chargement
    public void initialiserUtilisateur(String username, String role) {
        this.username = username;
        this.role     = role;

        labelNomUtilisateur.setText("👤  " + username);
        labelRole.setText(role);

        if (!role.equals("ADMIN")) {
            menuCoachs.setVisible(false);
            menuCoachs.setManaged(false);
        }

        if (!role.equals("ADMIN")) {
            menuCoachs.setVisible(false);
            menuCoachs.setManaged(false);
            menuUtilisateurs.setVisible(false);   // ← AJOUTER
            menuUtilisateurs.setManaged(false);   // ← AJOUTER
        }

        allerMembres();
    }

    // ===== Ces 3 méthodes correspondent aux onMouseClicked du FXML =====

    @FXML
    public void allerMembres() {
        chargerPage(BASE + "membres.fxml", "👤 Gestion des Membres");
    }

    @FXML
    public void allerUtilisateurs() {
        chargerPage(BASE + "utilisateurs.fxml", "🔐 Gestion des Utilisateurs");
    }

    @FXML
    public void allerCoachs() {
        if (role == null || !role.equals("ADMIN")) {
            afficherAlerte("Accès refusé", "Seuls les admins peuvent gérer les coachs.");
            return;
        }
        chargerPage(BASE + "coachs.fxml", "🏋️ Gestion des Coachs");
    }

    @FXML
    public void allerSalles() {
        chargerPage(BASE + "salles_cours.fxml", "📅 Salles & Planning");
    }

    @FXML
    public void seDeconnecter() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous vous déconnecter ?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Déconnexion");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            try {
                URL loginUrl = getClass().getResource(BASE + "login.fxml");
                FXMLLoader loader = new FXMLLoader(loginUrl);
                Parent loginRoot = loader.load();

                Scene scene = new Scene(loginRoot, 850, 550);
                URL cssUrl = getClass().getResource("/ma/ac/esi/gymsystem/style.css");
                if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());

                Stage stage = (Stage) rootLayout.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("GymManager — Connexion");
                stage.setResizable(false);
                stage.centerOnScreen();

            } catch (Exception e) {
                e.printStackTrace();
                afficherAlerte("Erreur", "Impossible de revenir au login : " + e.getMessage());
            }
        }
    }

    // ===== Méthode centrale de navigation =====

    private void chargerPage(String fxmlPath, String titre) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                afficherAlerte("Erreur", "Page introuvable : " + fxmlPath);
                return;
            }

            Parent page = FXMLLoader.load(url);
            labelPageCourante.setText(titre);

            page.setOpacity(0);
            rootLayout.setCenter(page);

            FadeTransition fade = new FadeTransition(Duration.millis(250), page);
            fade.setToValue(1);
            fade.play();

        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur chargement", e.getMessage());
        }
    }

    private void afficherAlerte(String titre, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titre);
        a.setContentText(msg);
        a.showAndWait();
    }
}