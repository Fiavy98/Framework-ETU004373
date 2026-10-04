package controllers;

import java.util.ArrayList;
import java.util.List;

import ControllerPerso.Controller;
import ControllerPerso.Mapping;
import ControllerPerso.Json;

@Controller
public class RetourneJSON {

    @Json
    @Mapping(value = "jsonReponse", method = "GET")
    public Object json() {
        return new Personne(1, "Rakoto",4444);
    }

    @Json
    @Mapping(value = "nEwMeth", method = "GET")
    public Object nEwMeth() {
        List<Personne> personnes = new ArrayList<>();

            personnes.add(new Personne(1, "Rakoto", 4444));
            personnes.add(new Personne(2, "Jean", 4374));
            personnes.add(new Personne(3, "Paul", 4375));

            return personnes;
        
    }
}