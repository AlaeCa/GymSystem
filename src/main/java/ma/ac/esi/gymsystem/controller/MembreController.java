package ma.ac.esi.gymsystem.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ma.ac.esi.gymsystem.DAO.MembreDAO;
import ma.ac.esi.gymsystem.model.Membre;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

// PDF
import ma.ac.esi.gymsystem.util.PdfService;
import java.awt.Desktop;
import java.io.File;

public class MembreController {

    // ---- FXML ----
    @FXML private TextField champNom;
    @FXML private TextField champPrenom;
    @FXML private TextField champEmail;
    @FXML private TextField champTelephone;
    @FXML private DatePicker champDateDebut;
    @FXML private DatePicker champDateFin;
    @FXML private Label labelStatut;

    @FXML private TableView<Membre> tableMembres;
    @FXML private TableColumn<Membre, Integer> colId;
    @FXML private TableColumn<Membre, String> colNom;
    @FXML private TableColumn<Membre, String> colPrenom;
    @FXML private TableColumn<Membre, String> colEmail;
    @FXML private TableColumn<Membre, String> colTelephone;
    @FXML private TableColumn<Membre, LocalDate> colDateFin;
    @FXML private TableColumn<Membre, String> colStatut;

    // ---- RECHERCHE ----
    @FXML private TextField champRecherche;
    @FXML private Button btnLancerRecherche;
    private String critereActuel = "";

    private MembreDAO membreDAO = new MembreDAO();
    private ObservableList<Membre> listeMembres = FXCollections.observableArrayList();

    // ---- INIT ----
    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));

        colStatut.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatutAbonnement())
        );

        // cacher recherche au début
        if (champRecherche != null) {
            champRecherche.setVisible(false);
            champRecherche.setManaged(false);
        }
        if (btnLancerRecherche != null) {
            btnLancerRecherche.setVisible(false);
            btnLancerRecherche.setManaged(false);
        }

        chargerMembres();
        tableMembres.setOnMouseClicked(e -> remplirChampsDepuisSelection());
    }

    // ---- CRUD ----

    @FXML
    public void chargerMembres() {
        try {
            List<Membre> membres = membreDAO.getTousLesMembres();
            listeMembres.setAll(membres);
            tableMembres.setItems(listeMembres);
            labelStatut.setText(membres.size() + " membre(s) chargé(s).");
        } catch (SQLException e) {
            labelStatut.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    public void ajouterMembre() {
        if (!validerChamps()) return;

        Membre m = new Membre(
                champNom.getText().trim(),
                champPrenom.getText().trim(),
                champEmail.getText().trim(),
                champTelephone.getText().trim(),
                champDateDebut.getValue(),
                champDateFin.getValue()
        );

        try {
            membreDAO.ajouterMembre(m);
            chargerMembres();
            viderChamps();
            labelStatut.setText("Membre ajouté.");
        } catch (SQLException e) {
            labelStatut.setText("Erreur ajout.");
        }
    }

    @FXML
    public void modifierMembre() {
        Membre m = tableMembres.getSelectionModel().getSelectedItem();
        if (m == null) {
            labelStatut.setText("Sélectionnez un membre.");
            return;
        }

        if (!validerChamps()) return;

        m.setNom(champNom.getText().trim());
        m.setPrenom(champPrenom.getText().trim());
        m.setEmail(champEmail.getText().trim());
        m.setTelephone(champTelephone.getText().trim());
        m.setDateDebut(champDateDebut.getValue());
        m.setDateFin(champDateFin.getValue());

        try {
            membreDAO.modifierMembre(m);
            chargerMembres();
            viderChamps();
            labelStatut.setText("Membre modifié.");
        } catch (SQLException e) {
            labelStatut.setText("Erreur modification.");
        }
    }

    @FXML
    public void supprimerMembre() {
        Membre m = tableMembres.getSelectionModel().getSelectedItem();
        if (m == null) {
            labelStatut.setText("Sélectionnez un membre.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer " + m.getPrenom() + " " + m.getNom() + " ?",
                ButtonType.YES, ButtonType.NO);

        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            try {
                membreDAO.supprimerMembre(m.getId());
                chargerMembres();
                viderChamps();
                labelStatut.setText("Membre supprimé.");
            } catch (SQLException e) {
                labelStatut.setText("Erreur suppression.");
            }
        }
    }

    // ---- RECHERCHE ----

    @FXML
    public void choisirRechercheNom() {
        critereActuel = "Nom";
        preparerRecherche();
    }

    @FXML
    public void choisirRecherchePrenom() {
        critereActuel = "Prénom";
        preparerRecherche();
    }

    @FXML
    public void choisirRechercheAbonnement() {
        critereActuel = "Abonnement";
        preparerRecherche();
        champRecherche.setPromptText("Valide ou Expiré");
    }

    private void preparerRecherche() {
        champRecherche.clear();
        champRecherche.setVisible(true);
        champRecherche.setManaged(true);
        btnLancerRecherche.setVisible(true);
        btnLancerRecherche.setManaged(true);
    }

    @FXML
    public void rechercherMembres() {
        String motCle = champRecherche.getText().trim();

        if (motCle.isEmpty()) {
            chargerMembres();
            return;
        }

        try {
            List<Membre> res =
                    membreDAO.rechercherMembreParCritere(critereActuel, motCle);

            listeMembres.setAll(res);
            tableMembres.setItems(listeMembres);

            labelStatut.setText(
                    res.isEmpty() ? "Aucun résultat." : res.size() + " trouvé(s)."
            );

        } catch (SQLException e) {
            labelStatut.setText("Erreur recherche.");
        }
    }

    // ---- PDF ----

    @FXML
    private void handleImprimerFiche() {
        Membre m = tableMembres.getSelectionModel().getSelectedItem();

        if (m == null) {
            labelStatut.setText("Sélectionnez un membre.");
            return;
        }

        try {
            PdfService pdfService = new PdfService();
            pdfService.genererFicheInscription(
                    m.getNom(),
                    m.getPrenom(),
                    m.getTelephone(),
                    m.getDateDebut() != null ? m.getDateDebut().toString() : "N/A"
            );

            String fileName = "Fiche_" + m.getNom() + "_" + m.getPrenom() + ".pdf";
            File pdfFile = new File(fileName);

            if (pdfFile.exists()) {
                Desktop.getDesktop().open(pdfFile);
            }

            labelStatut.setText("Fiche générée.");

        } catch (Exception e) {
            labelStatut.setText("Erreur PDF.");
        }
    }

    // ---- UI ----

    private void remplirChampsDepuisSelection() {
        Membre m = tableMembres.getSelectionModel().getSelectedItem();
        if (m != null) {
            champNom.setText(m.getNom());
            champPrenom.setText(m.getPrenom());
            champEmail.setText(m.getEmail());
            champTelephone.setText(m.getTelephone());
            champDateDebut.setValue(m.getDateDebut());
            champDateFin.setValue(m.getDateFin());
        }
    }

    @FXML
    public void viderChamps() {
        champNom.clear();
        champPrenom.clear();
        champEmail.clear();
        champTelephone.clear();
        champDateDebut.setValue(null);
        champDateFin.setValue(null);

        if (champRecherche != null) {
            champRecherche.clear();
            champRecherche.setVisible(false);
            champRecherche.setManaged(false);
        }

        if (btnLancerRecherche != null) {
            btnLancerRecherche.setVisible(false);
            btnLancerRecherche.setManaged(false);
        }

        tableMembres.getSelectionModel().clearSelection();
    }

    // ---- VALIDATION ----

    private boolean validerChamps() {
        if (champNom.getText().isEmpty() ||
                champPrenom.getText().isEmpty() ||
                champEmail.getText().isEmpty() ||
                champDateDebut.getValue() == null ||
                champDateFin.getValue() == null) {

            labelStatut.setText("Remplir tous les champs.");
            return false;
        }

        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        if (!champEmail.getText().matches(emailRegex)) {
            labelStatut.setText("Email invalide.");
            return false;
        }

        if (!champDateFin.getValue().isAfter(champDateDebut.getValue())) {
            labelStatut.setText("Date fin incorrecte.");
            return false;
        }

        return true;
    }
}