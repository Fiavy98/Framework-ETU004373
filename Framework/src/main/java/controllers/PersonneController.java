package controllers;

import ControllerPerso.Controller;
import ControllerPerso.GetParameter;
import ControllerPerso.Mapping;
import ControllerPerso.ModelView;
import models.Personne;

@Controller
public class PersonneController {

    @Mapping(value = "personne/form", method = "GET")
    public ModelView form() {
        return new ModelView("personne/form");
    }

    @Mapping(value = "personne/save", method = "POST")
    public ModelView save(@GetParameter("nom") String nom,
                         @GetParameter("prenom") String prenom,
                         @GetParameter("genre") String genre,
                         @GetParameter("tel") String tel) {
        Personne personne = new Personne(nom, prenom, genre, tel);

        ModelView mv = new ModelView("personne/success");
        mv.addData("personne", personne);
        return mv;
    }
}
