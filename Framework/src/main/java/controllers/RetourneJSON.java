package controllers;

import ControllerPerso.Controller;
import ControllerPerso.Mapping;
import ControllerPerso.Json;

@Controller
public class RetourneJSON {

    @Json
    @Mapping(value = "json", method = "GET")
    public Object json() {
        return new Personne(1, "Tsinjo");
    }
}