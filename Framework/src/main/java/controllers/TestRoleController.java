package controllers;

import ControllerPerso.Controller;
import ControllerPerso.Mapping;
import ControllerPerso.ModelView;
import ControllerPerso.RoleRequired;

@Controller
public class TestRoleController {

    @RoleRequired("ADMIN")
    @Mapping(value = "admin", method = "GET")
    public String admin() {
        // return new ModelView("admin");
         return "Acces autorise : ADMIN";
    }

    @RoleRequired({"ADMIN", "RH"})
    @Mapping(value = "rh", method = "GET")
    public String rh() {
        // return new ModelView("rh");
        return "Acces autorise : RH";
    }
}