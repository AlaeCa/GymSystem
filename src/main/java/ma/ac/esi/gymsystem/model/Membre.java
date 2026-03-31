package ma.ac.esi.gymsystem.model;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class Membre {

    private int       id;
    private String    nom;
    private String    prenom;
    private String    email;
    private String    telephone;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean   actif;

    // Constructeurs

    public Membre() {}
    public Membre(String nom, String prenom, String email,
                  String telephone, LocalDate dateDebut, LocalDate dateFin) {
        this.nom       = nom;
        this.prenom    = prenom;
        this.email     = email;
        this.telephone = telephone;
        this.dateDebut = dateDebut;
        this.dateFin   = dateFin;
        this.actif     = true;
    }

    //Méthodes métier avec java.time

    public boolean estAbonnementValide() {    //Vérifie si l'abonnement est encore valide aujourd'hui.
        LocalDate today = LocalDate.now();
        return !today.isAfter(dateFin) && !today.isBefore(dateDebut);
    }
                                //Retourne le nombre de jours restants avant expiration.
    public long joursRestants() {
        return ChronoUnit.DAYS.between(LocalDate.now(), dateFin);
    }
                                //Retourne le statut lisible de l'abonnement.
    public String getStatutAbonnement() {
        if (estAbonnementValide()) {
            return "✅ Valide (" + joursRestants() + " jours restants)";
        } else {
            return "❌ Expiré";
        }
    }

    //Getters & Setters

    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }
    public String getNom()                    { return nom; }
    public void setNom(String nom)            { this.nom = nom; }
    public String getPrenom()                 { return prenom; }
    public void setPrenom(String prenom)      { this.prenom = prenom; }
    public String getEmail()                  { return email; }
    public void setEmail(String email)        { this.email = email; }
    public String getTelephone()              { return telephone; }
    public void setTelephone(String tel)      { this.telephone = tel; }
    public LocalDate getDateDebut()           { return dateDebut; }
    public void setDateDebut(LocalDate d)     { this.dateDebut = d; }
    public LocalDate getDateFin()             { return dateFin; }
    public void setDateFin(LocalDate d)       { this.dateFin = d; }
    public boolean isActif()                  { return actif; }
    public void setActif(boolean actif)       { this.actif = actif; }

    @Override
    public String toString() {
        return prenom + " " + nom + " — " + getStatutAbonnement();
    }
}
