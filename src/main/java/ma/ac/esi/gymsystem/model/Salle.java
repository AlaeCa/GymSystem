package ma.ac.esi.gymsystem.model;

/**
 * @author LENOVO
 **/
public class Salle {
    private int    id;
    private String nom;
    private int    capacite;
    private String description;

    public Salle() {}

    public Salle(String nom, int capacite, String description) {
        this.nom         = nom;
        this.capacite    = capacite;
        this.description = description;
    }

    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }
    public String getNom()                  { return nom; }
    public void setNom(String nom)          { this.nom = nom; }
    public int getCapacite()                { return capacite; }
    public void setCapacite(int capacite)   { this.capacite = capacite; }
    public String getDescription()          { return description; }
    public void setDescription(String d)    { this.description = d; }

    @Override
    public String toString() { return nom + " (cap. " + capacite + ")"; }
}
