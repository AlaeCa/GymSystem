package ma.ac.esi.gymsystem.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author LENOVO
 **/
public class Cours {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int           id;
    private String        nom;
    private LocalDateTime horaire;
    private int           dureeMins;
    private int           coachId;
    private String        coachNom;   // Pour affichage joint
    private int           salleId;
    private String        salleNom;   // Pour affichage joint

    public Cours() {}

    public Cours(String nom, LocalDateTime horaire, int dureeMins,
                 int coachId, int salleId) {
        this.nom       = nom;
        this.horaire   = horaire;
        this.dureeMins = dureeMins;
        this.coachId   = coachId;
        this.salleId   = salleId;
    }

    public String getHoraireFormate() {
        return horaire != null ? horaire.format(FORMATTER) : "";
    }

    // Getters & Setters
    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }
    public String getNom()                    { return nom; }
    public void setNom(String nom)            { this.nom = nom; }
    public LocalDateTime getHoraire()         { return horaire; }
    public void setHoraire(LocalDateTime h)   { this.horaire = h; }
    public int getDureeMins()                 { return dureeMins; }
    public void setDureeMins(int d)           { this.dureeMins = d; }
    public int getCoachId()                   { return coachId; }
    public void setCoachId(int id)            { this.coachId = id; }
    public String getCoachNom()               { return coachNom; }
    public void setCoachNom(String n)         { this.coachNom = n; }
    public int getSalleId()                   { return salleId; }
    public void setSalleId(int id)            { this.salleId = id; }
    public String getSalleNom()               { return salleNom; }
    public void setSalleNom(String n)         { this.salleNom = n; }

    @Override
    public String toString() {
        return nom + " — " + getHoraireFormate() + " (" + dureeMins + " min)";
    }
}
