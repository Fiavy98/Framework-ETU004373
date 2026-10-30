package controllers;

import ControllerPerso.Authorize;
import ControllerPerso.Controller;
import ControllerPerso.Mapping;
import ControllerPerso.ModelView;

@Controller
@Authorize
public class TestAuthorizeClassController {

    @Mapping(value = "authorize-class", method = "GET")
    public ModelView classProtectedPage() {
        ModelView mv = new ModelView("authorize/class-protected");
        mv.addData("message", "Cette page est protégée par @Authorize au niveau de la classe.");
        return mv;
    }
}
