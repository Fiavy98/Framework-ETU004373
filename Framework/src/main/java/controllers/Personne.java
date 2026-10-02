package controllers;

public class Personne {

    private int id;
    private String nom;
    private int etu;

    public Personne(int id, String nom,int etu) {
        this.id = id;
        this.nom = nom;
        this.etu=etu;
    }

    public int getId() {
        return id;
    }

    public int getEtu(){
        return etu;
    }

    public String getNom() {
        return nom;
    }
}