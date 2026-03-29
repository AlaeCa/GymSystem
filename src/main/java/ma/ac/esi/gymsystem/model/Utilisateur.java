package ma.ac.esi.gymsystem.model;

/**
 * POJO représentant un utilisateur du système.
 */
public class Utilisateur {

    private int     id;
    private String  username;
    private String  password;
    private String  role;
    private String  nom;
    private boolean actif;

    public Utilisateur() {}

    public Utilisateur(String username, String password, String role, String nom) {
        this.username = username;
        this.password = password;
        this.role     = role;
        this.nom      = nom;
        this.actif    = true;
    }

    // ---- Getters & Setters ----
    public int getId()                  { return id; }
    public void setId(int id)           { this.id = id; }
    public String getUsername()         { return username; }
    public void setUsername(String u)   { this.username = u; }
    public String getPassword()         { return password; }
    public void setPassword(String p)   { this.password = p; }
    public String getRole()             { return role; }
    public void setRole(String r)       { this.role = r; }
    public String getNom()              { return nom; }
    public void setNom(String n)        { this.nom = n; }
    public boolean isActif()            { return actif; }
    public void setActif(boolean a)     { this.actif = a; }

    @Override
    public String toString() {
        return username + " — " + role + (actif ? "" : " [inactif]");
    }
}