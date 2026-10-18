package controllers;

import ControllerPerso.Controller;
import ControllerPerso.Mapping;
import ControllerPerso.ModelView;
import models.MappFile;
import models.Personne;

@Controller
public class PersonneController {

    @Mapping(value = "personne/form", method = "GET")
    public ModelView form() {
        return new ModelView("personne/form");
    }

    @Mapping(value = "personne/save", method = "POST")
    public ModelView save(String nom, String prenom, String genre, String tel, MappFile rectoCIN) {
        Personne personne = new Personne(nom, prenom, genre, tel, rectoCIN);

        ModelView mv = new ModelView("personne/success");
        mv.addData("personne", personne);
        return mv;
    }
}
