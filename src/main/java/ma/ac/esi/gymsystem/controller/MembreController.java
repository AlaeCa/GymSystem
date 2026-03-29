package ma.ac.esi.gymsystem.controller;

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

/**
 * @author LENOVO
 **/
public class MembreController {
    // ---- Liens avec les composants FXML ----
    @FXML private TextField champNom;
    @FXML private TextField    champPrenom;
    @FXML private TextField    champEmail;
    @FXML private TextField    champTelephone;
    @FXML private DatePicker champDateDebut;
    @FXML private DatePicker   champDateFin;
    @FXML private Label labelStatut;
    @FXML private TableView<Membre> tableMembres;
    @FXML private TableColumn<Membre, Integer> colId;
    @FXML private TableColumn<Membre, String>    colNom;
    @FXML private TableColumn<Membre, String>    colPrenom;
    @FXML private TableColumn<Membre, String>    colEmail;
    @FXML private TableColumn<Membre, String>    colTelephone;
    @FXML private TableColumn<Membre, LocalDate> colDateFin;
    @FXML private TableColumn<Membre, String>    colStatut;

    // --- AJOUTS POUR LA RECHERCHE ---
    @FXML private TextField champRecherche;
    @FXML private Button btnLancerRecherche;
    private String critereActuel = "";
    // --------------------------------

    private MembreDAO membreDAO = new MembreDAO();
    private ObservableList<Membre> listeMembres = FXCollections.observableArrayList();

    // ---- Initialisation automatique ----
    @FXML
    public void initialize() {
        // Lier colonnes aux propriétés du modèle
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));

        // Colonne statut calculée dynamiquement
        colStatut.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getStatutAbonnement()
                )
        );

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

        // Remplir le tableau au démarrage
        chargerMembres();

        // Clic sur une ligne => remplir les champs
        tableMembres.setOnMouseClicked(event -> remplirChampsDepuisSelection());
    }

    // --- AJOUTS : MÉTHODES DE RECHERCHE DYNAMIQUE ---
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
    public void choisirRechercheAbonnement() {
        this.critereActuel = "Abonnement";
        preparerInterfaceRecherche();
        champRecherche.setPromptText("Taper 'Valide' ou 'Expiré'...");
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
    public void rechercherMembres() {
        String motCle = champRecherche.getText().trim();
        if (motCle.isEmpty()) {
            chargerMembres();
            return;
        }
        try {
            // Utilisation de la méthode créée dans MembreDAO
            List<Membre> resultats = membreDAO.rechercherMembreParCritere(critereActuel, motCle);
            listeMembres.setAll(resultats);
            tableMembres.setItems(listeMembres);
            labelStatut.setText(resultats.isEmpty() ? "Aucun résultat." : resultats.size() + " trouvé(s).");
        } catch (SQLException e) {
            labelStatut.setText(" Erreur recherche : " + e.getMessage());
        }
    }
    // ------------------------------------------------

    // ---- Charger la liste depuis la base ----
    @FXML
    public void chargerMembres() {
        try {
            List<Membre> membres = membreDAO.getTousLesMembres();
            listeMembres.setAll(membres);
            tableMembres.setItems(listeMembres);
            labelStatut.setText(membres.size() + " membre(s) chargé(s).");
        } catch (SQLException e) {
            labelStatut.setText(" Erreur : " + e.getMessage());
        }
    }

    // ---- Ajouter un membre ----
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
            labelStatut.setText(" Membre ajouté avec succès.");
        } catch (SQLException e) {
            labelStatut.setText(" Erreur ajout : " + e.getMessage());
        }
    }

    // ---- Modifier un membre sélectionné ----
    @FXML
    public void modifierMembre() {
        Membre selectionne = tableMembres.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            labelStatut.setText(" Veuillez sélectionner un membre à modifier.");
            return;
        }
        if (!validerChamps()) return;

        selectionne.setNom(champNom.getText().trim());
        selectionne.setPrenom(champPrenom.getText().trim());
        selectionne.setEmail(champEmail.getText().trim());
        selectionne.setTelephone(champTelephone.getText().trim());
        selectionne.setDateDebut(champDateDebut.getValue());
        selectionne.setDateFin(champDateFin.getValue());

        try {
            membreDAO.modifierMembre(selectionne);
            chargerMembres();
            viderChamps();
            labelStatut.setText("Membre modifié avec succès.");
        } catch (SQLException e) {
            labelStatut.setText("Erreur modification : " + e.getMessage());
        }
    }

    // ---- Supprimer un membre ----
    @FXML
    public void supprimerMembre() {
        Membre selectionne = tableMembres.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            labelStatut.setText("Veuillez sélectionner un membre à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer " + selectionne.getPrenom() + " " + selectionne.getNom() + " ?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            try {
                membreDAO.supprimerMembre(selectionne.getId());
                chargerMembres();
                viderChamps();
                labelStatut.setText("Membre supprimé.");
            } catch (SQLException e) {
                labelStatut.setText("Erreur suppression : " + e.getMessage());
            }
        }
    }

    // ---- Remplir les champs depuis la ligne sélectionnée ----
    private void remplirChampsDepuisSelection() {
        Membre m = tableMembres.getSelectionModel().getSelectedItem();
        if (m != null) {
            champNom.setText(m.getNom());
            champPrenom.setText(m.getPrenom());
            champEmail.setText(m.getEmail());
            champTelephone.setText(m.getTelephone());
            champDateDebut.setValue(m.getDateDebut());
            champDateFin.setValue(m.getDateFin());
            labelStatut.setText(m.getStatutAbonnement());
        }
    }

    // ---- Vider les champs ----
    @FXML
    public void viderChamps() {
        champNom.clear();
        champPrenom.clear();
        champEmail.clear();
        champTelephone.clear();
        champDateDebut.setValue(null);
        champDateFin.setValue(null);
        if(champRecherche != null) {
            champRecherche.clear();
            champRecherche.setVisible(false);
            champRecherche.setManaged(false);
        }
        if(btnLancerRecherche != null) {
            btnLancerRecherche.setVisible(false);
            btnLancerRecherche.setManaged(false);
        }
        tableMembres.getSelectionModel().clearSelection();
    }

    // ---- Validation simple des champs ----
    private boolean validerChamps() {
        if (champNom.getText().trim().isEmpty() ||
                champPrenom.getText().trim().isEmpty() ||
                champEmail.getText().trim().isEmpty() ||
                champDateDebut.getValue() == null ||
                champDateFin.getValue() == null) {
            labelStatut.setText(" Veuillez remplir tous les champs obligatoires.");
            return false;
        }

        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        if (!champEmail.getText().trim().matches(emailRegex)) {
            labelStatut.setText(" Format email invalide.");
            return false;
        }

        if (!champDateFin.getValue().isAfter(champDateDebut.getValue())) {
            labelStatut.setText("La date de fin doit être après la date de début.");
            return false;
        }

        return true;
    }
}