package controllers;

import ControllerPerso.Authorize;
import ControllerPerso.Controller;
import ControllerPerso.Mapping;
import ControllerPerso.ModelView;

@Controller
public class TestAuthorizeController {

    @Mapping(value = "authorize-public", method = "GET")
    public ModelView publicPage() {
        ModelView mv = new ModelView("authorize/public");
        mv.addData("message", "Page publique : accès autorisé sans authentification.");
        return mv;
    }

    @Authorize
    @Mapping(value = "authorize-private", method = "GET")
    public ModelView privatePage() {
        ModelView mv = new ModelView("authorize/private");
        mv.addData("message", "Page protégée par @Authorize : accès autorisé après authentification.");
        return mv;
    }

    @Mapping(value = "login", method = "GET")
    public ModelView loginPage() {
        return new ModelView("authorize/login");
    }
}
