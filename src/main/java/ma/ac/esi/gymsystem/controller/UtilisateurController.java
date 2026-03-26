package ma.ac.esi.gymsystem.controller;

import ma.ac.esi.gymsystem.DAO.UtilisateurDAO;
import ma.ac.esi.gymsystem.model.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller pour la gestion des utilisateurs.
 * Accessible uniquement aux ADMINS.
 * Suppression bloquée si c'est le dernier admin.
 */
public class UtilisateurController {

    // ---- Formulaire ----
    @FXML private TextField     champNom;
    @FXML private TextField     champUsername;
    @FXML private PasswordField champPassword;
    @FXML private ComboBox<String> comboRole;
    @FXML private CheckBox      checkActif;
    @FXML private Label         labelStatut;
    @FXML private Label         labelNbAdmins;
    @FXML private HBox          panneauMotDePasse;

    // ---- Tableau ----
    @FXML private TableView<Utilisateur>              tableUtilisateurs;
    @FXML private TableColumn<Utilisateur, Integer>   colId;
    @FXML private TableColumn<Utilisateur, String>    colNom;
    @FXML private TableColumn<Utilisateur, String>    colUsername;
    @FXML private TableColumn<Utilisateur, String>    colRole;
    @FXML private TableColumn<Utilisateur, Boolean>   colActif;

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private ObservableList<Utilisateur> listeUtilisateurs = FXCollections.observableArrayList();

    // Utilisateur actuellement sélectionné (null si nouveau)
    private Utilisateur utilisateurSelectionne = null;

    @FXML
    public void initialize() {
        // Lier colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colActif.setCellValueFactory(new PropertyValueFactory<>("actif"));

        // Colonne Actif : afficher Oui/Non
        colActif.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean actif, boolean empty) {
                super.updateItem(actif, empty);
                if (empty || actif == null) {
                    setText(null);
                } else {
                    setText(actif ? "✅ Actif" : "❌ Inactif");
                    setStyle(actif
                            ? "-fx-text-fill: #4caf50;"
                            : "-fx-text-fill: #ff6b6b;");
                }
            }
        });

        // Remplir le ComboBox des rôles
        comboRole.setItems(FXCollections.observableArrayList("ADMIN", "COACH", "RECEPTIONNISTE"));
        comboRole.setValue("RECEPTIONNISTE");

        // Clic sur une ligne → remplir les champs
        tableUtilisateurs.setOnMouseClicked(e -> selectionnerUtilisateur());

        chargerUtilisateurs();
        mettreAJourCompteurAdmins();
    }

    // ======== Charger la liste ========

    @FXML
    public void chargerUtilisateurs() {
        try {
            List<Utilisateur> liste = utilisateurDAO.getTousLesUtilisateurs();
            listeUtilisateurs.setAll(liste);
            tableUtilisateurs.setItems(listeUtilisateurs);
            labelStatut.setText("✅ " + liste.size() + " utilisateur(s) chargé(s).");
            mettreAJourCompteurAdmins();
        } catch (SQLException e) {
            labelStatut.setText("❌ Erreur chargement : " + e.getMessage());
        }
    }

    // ======== Ajouter ========

    @FXML
    public void ajouterUtilisateur() {
        if (!validerChamps(true)) return;

        try {
            // Vérifier si username déjà utilisé
            if (utilisateurDAO.usernameExiste(champUsername.getText().trim())) {
                labelStatut.setText("⚠️ Ce nom d'utilisateur est déjà pris.");
                return;
            }

            Utilisateur u = new Utilisateur(
                    champUsername.getText().trim(),
                    champPassword.getText().trim(),
                    comboRole.getValue(),
                    champNom.getText().trim()
            );

            utilisateurDAO.ajouterUtilisateur(u);
            chargerUtilisateurs();
            viderChamps();
            labelStatut.setText("✅ Utilisateur '" + u.getUsername() + "' ajouté.");

        } catch (SQLException e) {
            labelStatut.setText("❌ Erreur ajout : " + e.getMessage());
        }
    }

    // ======== Modifier ========

    @FXML
    public void modifierUtilisateur() {
        if (utilisateurSelectionne == null) {
            labelStatut.setText("⚠️ Sélectionnez un utilisateur à modifier.");
            return;
        }
        if (!validerChamps(false)) return;

        try {
            utilisateurSelectionne.setNom(champNom.getText().trim());
            utilisateurSelectionne.setUsername(champUsername.getText().trim());
            utilisateurSelectionne.setRole(comboRole.getValue());
            utilisateurSelectionne.setActif(checkActif.isSelected());

            utilisateurDAO.modifierUtilisateur(utilisateurSelectionne);

            // Changer le mot de passe seulement si un nouveau est saisi
            String nouveauMdp = champPassword.getText().trim();
            if (!nouveauMdp.isEmpty()) {
                utilisateurDAO.modifierMotDePasse(utilisateurSelectionne.getId(), nouveauMdp);
            }

            chargerUtilisateurs();
            viderChamps();
            labelStatut.setText("✅ Utilisateur modifié avec succès.");

        } catch (SQLException e) {
            labelStatut.setText("❌ Erreur modification : " + e.getMessage());
        }
    }

    // ======== Supprimer (avec vérification dernier admin) ========

    @FXML
    public void supprimerUtilisateur() {
        if (utilisateurSelectionne == null) {
            labelStatut.setText("⚠️ Sélectionnez un utilisateur à supprimer.");
            return;
        }

        String username = utilisateurSelectionne.getUsername();
        String role     = utilisateurSelectionne.getRole();

        // Message d'alerte adapté
        String message = "Supprimer l'utilisateur '" + username + "' (" + role + ") ?";
        if ("ADMIN".equals(role)) {
            message += "\n\n⚠️ Attention : cet utilisateur est un administrateur.";
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmer la suppression");
        confirm.setHeaderText("Suppression d'utilisateur");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            try {
                boolean supprime = utilisateurDAO.supprimerUtilisateur(utilisateurSelectionne.getId());

                if (supprime) {
                    chargerUtilisateurs();
                    viderChamps();
                    labelStatut.setText("✅ Utilisateur '" + username + "' supprimé.");
                } else {
                    // Bloqué : dernier admin
                    Alert erreur = new Alert(Alert.AlertType.ERROR);
                    erreur.setTitle("Suppression impossible");
                    erreur.setHeaderText("⛔ Opération bloquée");
                    erreur.setContentText(
                            "Impossible de supprimer '" + username + "'.\n\n" +
                                    "C'est le DERNIER administrateur du système.\n" +
                                    "Il doit y avoir au minimum un administrateur actif."
                    );
                    erreur.showAndWait();
                    labelStatut.setText("⛔ Suppression bloquée : dernier admin.");
                }

            } catch (SQLException e) {
                labelStatut.setText("❌ Erreur suppression : " + e.getMessage());
            }
        }
    }

    // ======== Sélection depuis le tableau ========

    private void selectionnerUtilisateur() {
        Utilisateur u = tableUtilisateurs.getSelectionModel().getSelectedItem();
        if (u != null) {
            utilisateurSelectionne = u;
            champNom.setText(u.getNom());
            champUsername.setText(u.getUsername());
            champPassword.setText("");  // Ne jamais pré-remplir le mot de passe
            comboRole.setValue(u.getRole());
            checkActif.setSelected(u.isActif());
            labelStatut.setText("Utilisateur sélectionné : " + u.getUsername() + " (" + u.getRole() + ")");
        }
    }

    // ======== Vider les champs ========

    @FXML
    public void viderChamps() {
        utilisateurSelectionne = null;
        champNom.clear();
        champUsername.clear();
        champPassword.clear();
        comboRole.setValue("RECEPTIONNISTE");
        checkActif.setSelected(true);
        tableUtilisateurs.getSelectionModel().clearSelection();
        labelStatut.setText("Prêt.");
    }

    // ======== Compteur admins ========

    private void mettreAJourCompteurAdmins() {
        try {
            int nb = utilisateurDAO.compterAdminsActifs();
            labelNbAdmins.setText("👑 Admins actifs : " + nb);
            if (nb == 1) {
                labelNbAdmins.setStyle("-fx-text-fill: #ff9800; -fx-font-weight: bold;");
            } else {
                labelNbAdmins.setStyle("-fx-text-fill: #4caf50;");
            }
        } catch (SQLException e) {
            labelNbAdmins.setText("Admins : ?");
        }
    }

    // ======== Validation des champs ========

    private boolean validerChamps(boolean motDePasseObligatoire) {
        if (champNom.getText().trim().isEmpty()) {
            labelStatut.setText("⚠️ Le nom est obligatoire.");
            return false;
        }
        if (champUsername.getText().trim().isEmpty()) {
            labelStatut.setText("⚠️ Le nom d'utilisateur est obligatoire.");
            return false;
        }
        if (champUsername.getText().trim().length() < 3) {
            labelStatut.setText("⚠️ Le nom d'utilisateur doit avoir au moins 3 caractères.");
            return false;
        }
        if (motDePasseObligatoire && champPassword.getText().trim().isEmpty()) {
            labelStatut.setText("⚠️ Le mot de passe est obligatoire pour un nouvel utilisateur.");
            return false;
        }
        if (!champPassword.getText().trim().isEmpty() && champPassword.getText().trim().length() < 4) {
            labelStatut.setText("⚠️ Le mot de passe doit avoir au moins 4 caractères.");
            return false;
        }
        if (comboRole.getValue() == null) {
            labelStatut.setText("⚠️ Sélectionnez un rôle.");
            return false;
        }
        return true;
    }
}