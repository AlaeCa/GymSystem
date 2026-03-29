package ma.ac.esi.gymsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ma.ac.esi.gymsystem.model.Coach;
import ma.ac.esi.gymsystem.DAO.CoachDAO;

import java.sql.SQLException;
import java.util.List;

public class CoachController {
    @FXML private TextField champNom;
    @FXML private TextField champPrenom;
    @FXML private TextField champEmail;
    @FXML private TextField champTelephone;
    @FXML private TextField champSpecialite;

    // --- AJOUTS POUR LA RECHERCHE ---
    @FXML private TextField champRecherche;
    @FXML private Button btnLancerRecherche;
    private String critereActuel = "";
    // --------------------------------

    @FXML private Label labelStatut;
    @FXML private Label labelValidationEmail;
    @FXML private Label labelValidationTel;

    @FXML private TableView<Coach> tableCoachs;
    @FXML private TableColumn<Coach, Integer> colId;
    @FXML private TableColumn<Coach, String> colNom;
    @FXML private TableColumn<Coach, String> colPrenom;
    @FXML private TableColumn<Coach, String> colEmail;
    @FXML private TableColumn<Coach, String> colTelephone;
    @FXML private TableColumn<Coach, String> colSpecialite;

    private CoachDAO coachDAO = new CoachDAO();
    private ObservableList<Coach> listeCoachs = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colSpecialite.setCellValueFactory(new PropertyValueFactory<>("specialite"));

        // --- AJOUT : CACHER LA RECHERCHE AU DÉPART ---
        if(champRecherche != null) {
            champRecherche.setVisible(false);
            champRecherche.setManaged(false);
        }
        if(btnLancerRecherche != null) {
            btnLancerRecherche.setVisible(false);
            btnLancerRecherche.setManaged(false);
        }
        // ---------------------------------------------

        chargerCoachs();
        tableCoachs.setOnMouseClicked(e -> remplirChampsDepuisSelection());

        champEmail.textProperty().addListener((obs, ancien, nouveau) -> {
            if (Coach.emailValide(nouveau)) {
                labelValidationEmail.setText("Email valide");
                labelValidationEmail.setStyle("-fx-text-fill: #4CAF50;");
            } else {
                labelValidationEmail.setText("Email invalide");
                labelValidationEmail.setStyle("-fx-text-fill: #F44336;");
            }
        });

        champTelephone.textProperty().addListener((obs, ancien, nouveau) -> {
            if (Coach.telephoneValide(nouveau)) {
                labelValidationTel.setText("Téléphone valide");
                labelValidationTel.setStyle("-fx-text-fill: #4CAF50;");
            } else {
                labelValidationTel.setText("Format: 06/07 ou +212...");
                labelValidationTel.setStyle("-fx-text-fill: #F44336;");
            }
        });
    }

    // --- AJOUTS : MÉTHODES DE RECHERCHE ---
    @FXML
    public void choisirRechercheNom() {
        this.critereActuel = "Nom";
        preparerInterfaceRecherche();
    }

    @FXML
    public void choisirRecherchePrenom() {
        this.critereActuel = "Prénom";
        preparerInterfaceRecherche();
    }

    @FXML
    public void choisirRechercheSpecialite() {
        this.critereActuel = "Spécialité";
        preparerInterfaceRecherche();
    }

    private void preparerInterfaceRecherche() {
        champRecherche.clear();
        champRecherche.setPromptText("Saisir " + critereActuel + "...");
        champRecherche.setVisible(true);
        champRecherche.setManaged(true);
        btnLancerRecherche.setVisible(true);
        btnLancerRecherche.setManaged(true);
        champRecherche.requestFocus();
    }

    @FXML
    public void rechercherCoachs() {
        String motCle = champRecherche.getText().trim();
        if (motCle.isEmpty()) {
            chargerCoachs();
            return;
        }
        try {
            List<Coach> resultats = coachDAO.rechercherCoachsParCritere(critereActuel, motCle);
            listeCoachs.setAll(resultats);
            tableCoachs.setItems(listeCoachs);
            labelStatut.setText(resultats.isEmpty() ? "Aucun résultat." : resultats.size() + " trouvé(s).");
        } catch (SQLException e) {
            labelStatut.setText("Erreur : " + e.getMessage());
        }
    }
    // --------------------------------------

    @FXML
    public void chargerCoachs() {
        try {
            List<Coach> coachs = coachDAO.getTousLesCoachs();
            listeCoachs.setAll(coachs);
            tableCoachs.setItems(listeCoachs);
            labelStatut.setText(coachs.size() + " coach(s) chargé(s).");
        } catch (SQLException e) {
            labelStatut.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    public void ajouterCoach() {
        if (!validerChamps()) return;
        Coach c = new Coach(champNom.getText().trim(), champPrenom.getText().trim(),
                champEmail.getText().trim(), champTelephone.getText().trim(),
                champSpecialite.getText().trim());
        try {
            coachDAO.ajouterCoach(c);
            chargerCoachs();
            viderChamps();
            labelStatut.setText("Coach ajouté avec succès.");
        } catch (SQLException e) {
            labelStatut.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    public void modifierCoach() {
        Coach selectionne = tableCoachs.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            labelStatut.setText("Sélectionnez un coach à modifier.");
            return;
        }
        if (!validerChamps()) return;
        selectionne.setNom(champNom.getText().trim());
        selectionne.setPrenom(champPrenom.getText().trim());
        selectionne.setEmail(champEmail.getText().trim());
        selectionne.setTelephone(champTelephone.getText().trim());
        selectionne.setSpecialite(champSpecialite.getText().trim());
        try {
            coachDAO.modifierCoach(selectionne);
            chargerCoachs();
            viderChamps();
            labelStatut.setText("Coach modifié.");
        } catch (SQLException e) {
            labelStatut.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    public void supprimerCoach() {
        Coach selectionne = tableCoachs.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            labelStatut.setText("Sélectionnez un coach à supprimer.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer le coach ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            try {
                coachDAO.supprimerCoach(selectionne.getId());
                chargerCoachs();
                viderChamps();
                labelStatut.setText("Coach supprimé.");
            } catch (SQLException e) {
                labelStatut.setText("Erreur : " + e.getMessage());
            }
        }
    }

    private void remplirChampsDepuisSelection() {
        Coach c = tableCoachs.getSelectionModel().getSelectedItem();
        if (c != null) {
            champNom.setText(c.getNom());
            champPrenom.setText(c.getPrenom());
            champEmail.setText(c.getEmail());
            champTelephone.setText(c.getTelephone());
            champSpecialite.setText(c.getSpecialite());
        }
    }

    @FXML
    public void viderChamps() {
        champNom.clear();
        champPrenom.clear();
        champEmail.clear();
        champTelephone.clear();
        champSpecialite.clear();
        if(champRecherche != null) champRecherche.clear();
        labelValidationEmail.setText("");
        labelValidationTel.setText("");
        tableCoachs.getSelectionModel().clearSelection();
    }

    private boolean validerChamps() {
        if (champNom.getText().trim().isEmpty() || champPrenom.getText().trim().isEmpty()) {
            labelStatut.setText("Nom et prénom obligatoires.");
            return false;
        }
        if (!Coach.emailValide(champEmail.getText().trim())) {
            labelStatut.setText("Email invalide.");
            return false;
        }
        if (!champTelephone.getText().trim().isEmpty() && !Coach.telephoneValide(champTelephone.getText().trim())) {
            labelStatut.setText("Format téléphone invalide.");
            return false;
        }
        return true;
    }
}