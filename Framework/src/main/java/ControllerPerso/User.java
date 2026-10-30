package ControllerPerso;
import java.util.List;

public class User {
    private String nom;
    private List<String> roles;
    
    
    public User(String nom, List<String> roles) {
        this.nom = nom;
        this.roles = roles;
    }

    public String getNom() {
        return nom;
    }

    public List<String> getRoles() {
        return roles;
    }

    // Verifier siu l user possede un role donnne 
        
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

}
