package models;

public class Personne {
    private String nom;
    private String prenom;
    private String genre;
    private String tel;

    public Personne() {
    }

    public Personne(String nom, String prenom, String genre, String tel) {
        this.nom = nom;
        this.prenom = prenom;
        this.genre = genre;
        this.tel = tel;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
