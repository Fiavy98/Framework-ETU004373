package controllers;

import ControllerPerso.Controller;
import ControllerPerso.Mapping;
import ControllerPerso.ModelView;
import ControllerPerso.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class LoginController {

    @Mapping(value = "login", method = "GET")
    public ModelView loginPage() {
        return new ModelView("authorize/login");
    }

    @Mapping(value = "login", method = "POST")
    public ModelView connecter(HttpServletRequest request) {

        // Utilisateur de test
        User user = new User("Test", List.of("ADMIN"));

        // Création de la session et stockage de l'utilisateur
        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        return new ModelView("authorize/login-success");
    }
}