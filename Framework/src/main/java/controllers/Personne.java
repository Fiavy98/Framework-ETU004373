package controllers;

public class Personne {

    private int id;
    private String nom;

    public Personne(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }
}