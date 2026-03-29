package ma.ac.esi.gymsystem.model;

import java.util.regex.Pattern;

/**
 * @author LENOVO
 **/
public class Coach {
    // Patterns RegEx de validation
    private static final String EMAIL_REGEX = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
    private static final String TEL_REGEX   = "^(\\+212|0)[5-7][0-9]{8}$";

    private int    id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String specialite;

    // ---- Constructeurs ----

    public Coach() {}

    public Coach(String nom, String prenom, String email,
                 String telephone, String specialite) {
        this.nom        = nom;
        this.prenom     = prenom;
        this.email      = email;
        this.telephone  = telephone;
        this.specialite = specialite;
    }

    // ---- Méthodes de validation RegEx ----

    public static boolean emailValide(String email) {
        return email != null && Pattern.matches(EMAIL_REGEX, email);
    }

    public static boolean telephoneValide(String tel) {
        return tel != null && Pattern.matches(TEL_REGEX, tel);
    }

    public boolean contactsValides() {
        return emailValide(this.email) && telephoneValide(this.telephone);
    }

    // ---- Getters & Setters ----

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
    public String getSpecialite()             { return specialite; }
    public void setSpecialite(String s)       { this.specialite = s; }

    @Override
    public String toString() {
        return prenom + " " + nom + " (" + specialite + ")";
    }
}
