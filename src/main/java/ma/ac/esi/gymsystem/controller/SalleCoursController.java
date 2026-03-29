package ma.ac.esi.gymsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ma.ac.esi.gymsystem.model.*;
import ma.ac.esi.gymsystem.DAO.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;


public class SalleCoursController {

    // --- Onglet Salles ---
    @FXML private TextField champNomSalle;
    @FXML private TextField champCapacite;
    @FXML private TextField champDescriptionSalle;
    @FXML private Label     labelStatutSalle;

    @FXML private TableView<Salle>               tableSalles;
    @FXML private TableColumn<Salle, Integer>    colSalleId;
    @FXML private TableColumn<Salle, String>     colSalleNom;
    @FXML private TableColumn<Salle, Integer>    colSalleCapacite;
    @FXML private TableColumn<Salle, String>     colSalleDesc;

    // Ajouts Recherche Salles
    @FXML private TextField champRechercheSalle;
    @FXML private Button btnRechercheSalle;
    private String critereSalle = "";

    // --- Onglet Cours ---
    @FXML private TextField    champNomCours;
    @FXML private TextField    champHoraire;
    @FXML private TextField    champDuree;
    @FXML private ComboBox<Coach>  comboCoachs;
    @FXML private ComboBox<Salle>  comboSalles;
    @FXML private Label            labelStatutCours;

    @FXML private TableView<Cours>              tableCours;
    @FXML private TableColumn<Cours, Integer>   colCoursId;
    @FXML private TableColumn<Cours, String>    colCoursNom;
    @FXML private TableColumn<Cours, String>    colCoursHoraire;
    @FXML private TableColumn<Cours, Integer>   colCoursDuree;
    @FXML private TableColumn<Cours, String>    colCoursCoach;
    @FXML private TableColumn<Cours, String>    colCoursSalle;

    // Ajouts Recherche Cours
    @FXML private TextField champRechercheCours;
    @FXML private Button btnRechercheCours;
    private String critereCours = "";

    private SalleDAO salleDAO = new SalleDAO();
    private CoursDAO  coursDAO = new CoursDAO();
    private CoachDAO  coachDAO = new CoachDAO();

    private ObservableList<Salle> listeSalles = FXCollections.observableArrayList();
    private ObservableList<Cours> listeCours  = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Init tableau salles
        colSalleId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSalleNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colSalleCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colSalleDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        tableSalles.setOnMouseClicked(e -> remplirChampsSalle());

        // Init tableau cours
        colCoursId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCoursNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colCoursHoraire.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getHoraireFormate()
                )
        );
        colCoursDuree.setCellValueFactory(new PropertyValueFactory<>("dureeMins"));
        colCoursCoach.setCellValueFactory(new PropertyValueFactory<>("coachNom"));
        colCoursSalle.setCellValueFactory(new PropertyValueFactory<>("salleNom"));

        // Masquer les barres de recherche au début
        initialiserVisibiliteRecherche();

        chargerSalles();
        chargerCours();
        chargerComboSalles();
        chargerComboCoachs();
    }

    private void initialiserVisibiliteRecherche() {
        if(champRechercheSalle != null) {
            champRechercheSalle.setVisible(false); champRechercheSalle.setManaged(false);
        }
        if(btnRechercheSalle != null) {
            btnRechercheSalle.setVisible(false); btnRechercheSalle.setManaged(false);
        }
        if(champRechercheCours != null) {
            champRechercheCours.setVisible(false); champRechercheCours.setManaged(false);
        }
        if(btnRechercheCours != null) {
            btnRechercheCours.setVisible(false); btnRechercheCours.setManaged(false);
        }
    }

    // ======== MÉTHODES RECHERCHE SALLES ========
    @FXML public void choisirRechercheNomSalle() { critereSalle = "Nom"; preparerInterfaceSalle(); }
    @FXML public void choisirRechercheCapacite() { critereSalle = "Capacité"; preparerInterfaceSalle(); }

    private void preparerInterfaceSalle() {
        champRechercheSalle.setVisible(true); champRechercheSalle.setManaged(true);
        btnRechercheSalle.setVisible(true); btnRechercheSalle.setManaged(true);
        champRechercheSalle.setPromptText("Saisir " + critereSalle + "...");
    }

    @FXML
    public void rechercherSalles() {
        String val = champRechercheSalle.getText().trim();
        if(val.isEmpty()) { chargerSalles(); return; }
        try {
            List<Salle> resultats = salleDAO.rechercherSallesParCritere(critereSalle, val);
            listeSalles.setAll(resultats);
            tableSalles.setItems(listeSalles);
        } catch (SQLException e) { labelStatutSalle.setText("Erreur recherche."); }
    }

    // ======== MÉTHODES RECHERCHE COURS ========
    @FXML public void choisirRechercheNomCours() { critereCours = "Nom"; preparerInterfaceCours(); }
    @FXML public void choisirRechercheCoach() { critereCours = "Coach"; preparerInterfaceCours(); }
    @FXML public void choisirRechercheSalle() { critereCours = "Salle"; preparerInterfaceCours(); }
    @FXML public void choisirRechercheDuree() { critereCours = "Durée"; preparerInterfaceCours(); }

    private void preparerInterfaceCours() {
        champRechercheCours.setVisible(true); champRechercheCours.setManaged(true);
        btnRechercheCours.setVisible(true); btnRechercheCours.setManaged(true);
        champRechercheCours.setPromptText("Saisir " + critereCours + "...");
    }

    @FXML
    public void rechercherCours() {
        String val = champRechercheCours.getText().trim();
        if(val.isEmpty()) { chargerCours(); return; }
        try {
            List<Cours> resultats = coursDAO.rechercherCoursParCritere(critereCours, val);
            listeCours.setAll(resultats);
            tableCours.setItems(listeCours);
        } catch (SQLException e) { labelStatutCours.setText("Erreur recherche."); }
    }

    // ======== SALLES (CRUD existant) ========

    @FXML
    public void chargerSalles() {
        try {
            List<Salle> salles = salleDAO.getToutesLesSalles();
            listeSalles.setAll(salles);
            tableSalles.setItems(listeSalles);
            labelStatutSalle.setText(salles.size() + " salle(s) chargée(s).");
        } catch (SQLException e) {
            labelStatutSalle.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    public void ajouterSalle() {
        if (champNomSalle.getText().trim().isEmpty() || champCapacite.getText().trim().isEmpty()) {
            labelStatutSalle.setText(" Nom et capacité obligatoires.");
            return;
        }
        try {
            int capacite = Integer.parseInt(champCapacite.getText().trim());
            Salle s = new Salle(champNomSalle.getText().trim(), capacite, champDescriptionSalle.getText().trim());
            salleDAO.ajouterSalle(s);
            chargerSalles();
            chargerComboSalles();
            viderChampsSalle();
            labelStatutSalle.setText(" Salle ajoutée.");
        } catch (NumberFormatException e) {
            labelStatutSalle.setText(" La capacité doit être un nombre entier.");
        } catch (SQLException e) {
            labelStatutSalle.setText(" Erreur : " + e.getMessage());
        }
    }

    @FXML
    public void modifierSalle() {
        Salle sel = tableSalles.getSelectionModel().getSelectedItem();
        if (sel == null) { labelStatutSalle.setText(" Sélectionnez une salle."); return; }
        try {
            sel.setNom(champNomSalle.getText().trim());
            sel.setCapacite(Integer.parseInt(champCapacite.getText().trim()));
            sel.setDescription(champDescriptionSalle.getText().trim());
            salleDAO.modifierSalle(sel);
            chargerSalles();
            chargerComboSalles();
            viderChampsSalle();
            labelStatutSalle.setText("Salle modifiée.");
        } catch (Exception e) {
            labelStatutSalle.setText(" Erreur : " + e.getMessage());
        }
    }

    @FXML
    public void supprimerSalle() {
        Salle sel = tableSalles.getSelectionModel().getSelectedItem();
        if (sel == null) { labelStatutSalle.setText("Sélectionnez une salle."); return; }
        try {
            salleDAO.supprimerSalle(sel.getId());
            chargerSalles();
            chargerComboSalles();
            labelStatutSalle.setText("Salle supprimée.");
        } catch (SQLException e) {
            labelStatutSalle.setText("Erreur : " + e.getMessage());
        }
    }

    private void remplirChampsSalle() {
        Salle s = tableSalles.getSelectionModel().getSelectedItem();
        if (s != null) {
            champNomSalle.setText(s.getNom());
            champCapacite.setText(String.valueOf(s.getCapacite()));
            champDescriptionSalle.setText(s.getDescription());
        }
    }

    @FXML
    public void viderChampsSalle() {
        champNomSalle.clear();
        champCapacite.clear();
        champDescriptionSalle.clear();
        if(champRechercheSalle != null) champRechercheSalle.clear();
        tableSalles.getSelectionModel().clearSelection();
    }

    // ======== COURS (CRUD existant) ========

    @FXML
    public void chargerCours() {
        try {
            List<Cours> cours = coursDAO.getTousLesCours();
            listeCours.setAll(cours);
            tableCours.setItems(listeCours);
            labelStatutCours.setText(cours.size() + " cours chargé(s).");
        } catch (SQLException e) {
            labelStatutCours.setText(" Erreur : " + e.getMessage());
        }
    }

    @FXML
    public void ajouterCours() {
        if (champNomCours.getText().trim().isEmpty() || champHoraire.getText().trim().isEmpty()
                || champDuree.getText().trim().isEmpty()
                || comboCoachs.getValue() == null || comboSalles.getValue() == null) {
            labelStatutCours.setText("Tous les champs sont obligatoires.");
            return;
        }
        try {
            LocalDateTime horaire = LocalDateTime.parse(champHoraire.getText().trim());
            Cours c = new Cours(
                    champNomCours.getText().trim(),
                    horaire,
                    Integer.parseInt(champDuree.getText().trim()),
                    comboCoachs.getValue().getId(),
                    comboSalles.getValue().getId()
            );
            coursDAO.ajouterCours(c);
            chargerCours();
            viderChampsCours();
            labelStatutCours.setText("Cours ajouté.");
        } catch (Exception e) {
            labelStatutCours.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    public void supprimerCours() {
        Cours sel = tableCours.getSelectionModel().getSelectedItem();
        if (sel == null) { labelStatutCours.setText("Sélectionnez un cours."); return; }
        try {
            coursDAO.supprimerCours(sel.getId());
            chargerCours();
            labelStatutCours.setText("Cours supprimé.");
        } catch (SQLException e) {
            labelStatutCours.setText("Erreur : " + e.getMessage());
        }
    }

    private void chargerComboCoachs() {
        try {
            List<Coach> coachs = coachDAO.getTousLesCoachs();
            comboCoachs.setItems(FXCollections.observableArrayList(coachs));
        } catch (SQLException e) { }
    }

    private void chargerComboSalles() {
        try {
            List<Salle> salles = salleDAO.getToutesLesSalles();
            comboSalles.setItems(FXCollections.observableArrayList(salles));
        } catch (SQLException e) { }
    }

    @FXML
    public void viderChampsCours() {
        champNomCours.clear();
        champHoraire.clear();
        champDuree.clear();
        comboCoachs.setValue(null);
        comboSalles.setValue(null);
        if(champRechercheCours != null) champRechercheCours.clear();
        tableCours.getSelectionModel().clearSelection();
    }
}