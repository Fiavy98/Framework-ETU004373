package main.java;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ControllerPerso.ControllerScanner;
import ControllerPerso.Mapping;
import ControllerPerso.MappingInfo;
import ControllerPerso.ModelView;
import ControllerPerso.UrlMethode;

import ControllerPerso.Json;


public class FrontServlet extends HttpServlet {
    private Map<UrlMethode, MappingInfo> mappingUrls = new HashMap<>();


    @Override
    public void init() throws ServletException {

        try {
            String webInfPath =
                    getServletContext().getRealPath("/WEB-INF");

            List<String> controllerNames =
                    ControllerScanner.scan(webInfPath);

            ClassLoader classLoader =
                    Thread.currentThread().getContextClassLoader();


            for (String className : controllerNames) {

                Class<?> clazz =
                        classLoader.loadClass(className);

                for (Method method : clazz.getDeclaredMethods()) {

                    if (method.isAnnotationPresent(Mapping.class)) {

                        Mapping ann =
                                method.getAnnotation(Mapping.class);


                        UrlMethode cle = new UrlMethode(ann.value(),ann.method());

                        mappingUrls.put(cle,new MappingInfo(
                                        className,
                                        method
                                )
                        );


                        System.out.println(
                                "Mapping enregistré : ["
                                + ann.method()
                                + "] "
                                + ann.value()
                                + " → "
                                + className
                        );
                    }
                }
            }

        } catch (Exception e) {

            throw new ServletException(
                    "Erreur init mappings",
                    e
            );
        }
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        traiter(request, response, "GET");
    }


    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        traiter(request, response, "POST");
    }


private void traiter(
        HttpServletRequest request,
        HttpServletResponse response,
        String httpMethod)
        throws ServletException, IOException {

    String pathInfo = request.getPathInfo();

    String urlDemandee =
            (pathInfo != null && pathInfo.length() > 1)
            ? pathInfo.substring(1)
            : "";

    String requestUrl = request.getRequestURL().toString();

    String contextPath = request.getContextPath();

    String baseUrl =requestUrl.substring(0,
                requestUrl.indexOf(contextPath)
                + contextPath.length()
            )
            + "/";

    UrlMethode cle = new UrlMethode(urlDemandee,httpMethod);

    if (mappingUrls.containsKey(cle)) {

        MappingInfo info = mappingUrls.get(cle);

        if (info.getMethod().isAnnotationPresent(Json.class)) {
            response.setContentType(
                    "application/json; charset=UTF-8"
            );


            executerMethode(info,request,response);

            return;
        }
    }

    // RÉPONSE HTML NORMALE
    response.setContentType( "text/html; charset=UTF-8" );

    PrintWriter out = response.getWriter();

    // PAGE HTML
    out.println(
            "<html><body "
            + "style='font-family: Arial; margin: 30px;'>"
    );

    // CAS 1 : URL VIDE

    if (urlDemandee.isEmpty()) {
        afficherTableau(out,baseUrl,null);

    // CAS 2 : URL EXISTANTE
    } else if (mappingUrls.containsKey(cle)) {

        MappingInfo info = mappingUrls.get(cle);
        executerMethode(info,request,response);

    // CAS 3 : URL INEXISTANTE
    } else {

        afficherErreur(out,baseUrl,urlDemandee,httpMethod);
    }

    // LIEN RETOUR
    out.println("<br><hr>");
    out.println(
            "<p><a href='"
            + baseUrl
            + "'>Retour accueil</a></p>"
    );

    out.println("</body></html>");
}

    // AFFICHER LE TABLEAU DES MAPPINGS====
    private void afficherTableau(
            PrintWriter out,
            String baseUrl,
            String urlActive) {

        out.println("<h2>Tableau des Mappings</h2>");

        out.println(
                "<table border='1' cellpadding='10' "
                + "style='border-collapse:collapse;'>"
        );

        out.println("<tr style='background:#f2f2f2'>");
        out.println(
                "<th>URL</th>"
                + "<th>HTTP</th>"
                + "<th>Classe</th>"
                + "<th>Méthode</th>"
        );

        out.println("</tr>");

        // Parcourir tous les mappings
        for (Map.Entry<UrlMethode, MappingInfo> entry : mappingUrls.entrySet()) {
            String urlComplete = baseUrl + entry.getKey().getUrl();

            // Savoir si cette URL est active
            String style =
                    entry.getKey()
                            .getUrl()
                            .equals(urlActive)
                    ?
                    "style='background:#e8f5e9; "
                    + "font-weight:bold;'"
                    :
                    "";

            out.println("<tr " + style + ">");
            out.println(
                    "<td><a href='"
                    + urlComplete
                    + "'>"
                    + urlComplete
                    + "</a></td>"
            );

            out.println(
                    "<td>"
                    + entry.getKey().getHttpMethod()
                    + "</td>"
            );

            out.println(
                    "<td>"
                    + entry.getValue().getClassName()
                    + "</td>"
            );

            out.println(
                    "<td>"
                    + entry.getValue()
                            .getMethod()
                            .getName()
                    + "()</td>"
            );

            out.println("</tr>");
        }

        out.println("</table>");
    }

   //  Execute meth du controlleur
    private void executerMethode( MappingInfo info,HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Class<?> clazz = cl.loadClass(info.getClassName());
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Object result = info.getMethod().invoke(instance);

            if (info.getMethod().isAnnotationPresent(Json.class)) {
                response.setContentType("application/json; charset=UTF-8");

                // Récupérer le Writer
                PrintWriter out = response.getWriter();

                out.println(toJson(result));

                return;
            }

            if (result instanceof ModelView) {
                ModelView mv =
                        (ModelView) result;

                // Mettre les données dans la requête
                for (
                        Map.Entry<String, Object> entry
                        : mv.getData().entrySet()
                ) {

                    request.setAttribute(
                            entry.getKey(),
                            entry.getValue()
                    );
                }

                // Construire le chemin JSP
                String jspPath =
                        "/WEB-INF/views/"
                        + mv.getViewName()
                        + ".jsp";


                // Aller vers la JSP
                request.getRequestDispatcher(
                        jspPath
                ).forward(
                        request,
                        response
                );


            } else {


                // =================================================
                //  COMPORTEMENT POUR LES AUTRES MÉTHODES
                // =================================================

                PrintWriter out = response.getWriter();
                out.println(
                        "<p>Méthode <strong>"
                        + info.getMethod().getName()
                        + "()</strong> exécutée.</p>"
                );

                out.println(
                        "<p>Voir la console Tomcat "
                        + "pour le résultat.</p>"
                );
            }


        } catch (Exception e) {
            throw new ServletException(
                    "Erreur exécution méthode",
                    e
            );
        }
    }

// Transformet Java en JSON
private String toJson(Object objet) {

    // CAS 1 : c'est une liste
    if (objet instanceof List) {
        List<?> liste = (List<?>) objet;
        StringBuilder json = new StringBuilder();
        json.append("[");

        boolean premier = true;

        for (Object element : liste) {
            if (!premier) {
                json.append(",");
            }
            json.append(toJson(element));

            premier = false;
        }

        json.append("]");

        return json.toString();
    }

    // CAS 2 : objet simple
    StringBuilder json = new StringBuilder();
    json.append("{");

    Method[] methods = objet.getClass().getMethods();
    boolean premier = true;

    for (Method method : methods) {
        String nom = method.getName();
        if (nom.startsWith("get") && !nom.equals("getClass")) {
            try {
                Object valeur = method.invoke(objet);
                String nomAttribut =
                    Character.toLowerCase(nom.charAt(3))
                    + nom.substring(4);

                if (!premier) {
                    json.append(",");
                }

                json.append("\"")
                    .append(nomAttribut)
                    .append("\":");

                if (valeur instanceof String) {
                    json.append("\"")
                        .append(valeur)
                        .append("\"");

                } else {
                    json.append(valeur);
                }

                premier = false;

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    json.append("}");
    return json.toString();
}

    // AFFICHER ERREUR URL====
    private void afficherErreur(
            PrintWriter out,
            String baseUrl,
            String urlDemandee,
            String httpMethod) {


        out.println(
                "<h2 style='color:#c62828;'>"
                + "URL Inconnue"
                + "</h2>"
        );

        out.println(
                "<div style='background:#ffebee; "
                + "padding:15px; "
                + "border-left:5px solid #c62828;'>"
        );

        out.println(
                "L'URL <strong>"
                + baseUrl
                + urlDemandee
                + "</strong> ["
                + httpMethod
                + "] n'existe pas."
        );

        out.println("</div>");
        out.println(
                "<p>URLs valides :</p><ul>"
        );

        // Afficher toutes les URLs connues
        for (UrlMethode u : mappingUrls.keySet()) {
            String urlComplete = baseUrl + u.getUrl();
            out.println(
                    "<li>["
                    + u.getHttpMethod()
                    + "] "
                    + "<a href='"
                    + urlComplete
                    + "'>"
                    + urlComplete
                    + "</a></li>"
            );
        }
        out.println("</ul>");
    }
}