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

// ⭐ SPRINT 6 : import de la nouvelle annotation @Json
import ControllerPerso.Json;


public class FrontServlet extends HttpServlet {

    // ============================================================
    // ANNuaire des mappings
    // ============================================================

    /*
     * Clé :
     *      UrlMethode("andrana", "GET")
     *
     * Valeur :
     *      MappingInfo(classe, méthode)
     */
    private Map<UrlMethode, MappingInfo> mappingUrls = new HashMap<>();


    // ============================================================
    // INIT
    // ============================================================

    /*
     * init() est appelé UNE SEULE FOIS par Tomcat.
     *
     * Son rôle :
     * - chercher les classes @Controller
     * - chercher leurs méthodes @Mapping
     * - enregistrer les mappings dans mappingUrls
     */
    @Override
    public void init() throws ServletException {

        try {

            // Chemin physique de WEB-INF
            String webInfPath =
                    getServletContext().getRealPath("/WEB-INF");

            // Chercher tous les @Controller
            List<String> controllerNames =
                    ControllerScanner.scan(webInfPath);

            ClassLoader classLoader =
                    Thread.currentThread().getContextClassLoader();


            // Parcourir tous les Controllers trouvés
            for (String className : controllerNames) {

                Class<?> clazz =
                        classLoader.loadClass(className);


                // Parcourir toutes les méthodes du Controller
                for (Method method : clazz.getDeclaredMethods()) {


                    // Vérifier si la méthode possède @Mapping
                    if (method.isAnnotationPresent(Mapping.class)) {

                        Mapping ann =
                                method.getAnnotation(Mapping.class);


                        // Créer la clé URL + méthode HTTP
                        UrlMethode cle =
                                new UrlMethode(
                                        ann.value(),
                                        ann.method()
                                );


                        // Enregistrer le mapping
                        mappingUrls.put(
                                cle,
                                new MappingInfo(
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


    // ============================================================
    // GET
    // ============================================================

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        traiter(request, response, "GET");
    }


    // ============================================================
    // POST
    // ============================================================

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        traiter(request, response, "POST");
    }


    // ============================================================
    // TRAITER LA REQUÊTE
    // ============================================================

    private void traiter(
            HttpServletRequest request,
            HttpServletResponse response,
            String httpMethod)
            throws ServletException, IOException {


        /*
         * Par défaut, on répond en HTML.
         *
         * ⭐ SPRINT 6 :
         * Si une méthode possède @Json, cette valeur sera
         * changée en application/json dans executerMethode().
         */
        response.setContentType(
                "text/html; charset=UTF-8"
        );


        PrintWriter out = response.getWriter();


        // ========================================================
        // RÉCUPÉRER L'URL DEMANDÉE
        // ========================================================

        /*
         * Exemple :
         *
         * /Framework-Test/app/andrana
         *
         * pathInfo :
         *
         * /andrana
         *
         */

        String pathInfo = request.getPathInfo();


        String urlDemandee =
                (pathInfo != null && pathInfo.length() > 1)
                ? pathInfo.substring(1)
                : "";


        // ========================================================
        // CONSTRUIRE L'URL DE BASE
        // ========================================================

        String requestUrl =
                request.getRequestURL().toString();

        String contextPath =
                request.getContextPath();


        String baseUrl =
                requestUrl.substring(
                        0,
                        requestUrl.indexOf(contextPath)
                                + contextPath.length()
                )
                + "/";


        // ========================================================
        // PAGE HTML
        // ========================================================

        out.println(
                "<html><body "
                + "style='font-family: Arial; margin: 30px;'>"
        );


        // ========================================================
        // CHERCHER LE MAPPING
        // ========================================================

        UrlMethode cle =
                new UrlMethode(
                        urlDemandee,
                        httpMethod
                );


        // ========================================================
        // CAS 1 : URL VIDE
        // ========================================================

        if (urlDemandee.isEmpty()) {

            afficherTableau(
                    out,
                    baseUrl,
                    null
            );


        // ========================================================
        // CAS 2 : URL EXISTANTE
        // ========================================================

        } else if (mappingUrls.containsKey(cle)) {
    MappingInfo info = mappingUrls.get(cle);

    executerMethode(
        info,
        request,
        response
    );

    // Si c'est une méthode JSON,
    // on arrête ici pour ne pas ajouter du HTML au JSON.
    if (info.getMethod().isAnnotationPresent(Json.class)) {
        return;
    }
}else {

            afficherErreur(
                    out,
                    baseUrl,
                    urlDemandee,
                    httpMethod
            );
        }


        // ========================================================
        // LIEN RETOUR
        // ========================================================

        out.println("<br><hr>");

        out.println(
                "<p><a href='"
                + baseUrl
                + "'>Retour accueil</a></p>"
        );

        out.println("</body></html>");
    }


    // ============================================================
    // AFFICHER LE TABLEAU DES MAPPINGS
    // ============================================================

    private void afficherTableau(
            PrintWriter out,
            String baseUrl,
            String urlActive) {


        out.println("<h2>Tableau des Mappings</h2>");


        out.println(
                "<table border='1' cellpadding='10' "
                + "style='border-collapse:collapse;'>"
        );


        out.println(
                "<tr style='background:#f2f2f2'>"
        );


        out.println(
                "<th>URL</th>"
                + "<th>HTTP</th>"
                + "<th>Classe</th>"
                + "<th>Méthode</th>"
        );


        out.println("</tr>");


        // Parcourir tous les mappings
        for (
                Map.Entry<UrlMethode, MappingInfo> entry
                : mappingUrls.entrySet()
        ) {


            String urlComplete =
                    baseUrl
                    + entry.getKey().getUrl();


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


            out.println(
                    "<tr " + style + ">"
            );


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


    // ============================================================
    // ⭐⭐⭐ SPRINT 6 : EXÉCUTER UNE MÉTHODE DU CONTROLLER
    // ============================================================

    private void executerMethode(
            MappingInfo info,
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {


        try {

            // ====================================================
            // CHARGER LA CLASSE
            // ====================================================

            ClassLoader cl =
                    Thread.currentThread()
                            .getContextClassLoader();


            Class<?> clazz =
                    cl.loadClass(
                            info.getClassName()
                    );


            // ====================================================
            // CRÉER UNE INSTANCE DU CONTROLLER
            // ====================================================

            Object instance =
                    clazz.getDeclaredConstructor()
                            .newInstance();


            // ====================================================
            // ⭐ SPRINT 6
            // EXÉCUTER LA MÉTHODE AVEC invoke()
            // ====================================================

            /*
             * Exemple :
             *
             * Controller :
             *
             * @Json
             * @Mapping("json")
             * public Personne json() {
             *     return new Personne(1, "Tsinjo");
             * }
             *
             * invoke() exécute json()
             *
             * result contient alors :
             *
             * Personne(1, "Tsinjo")
             */

            Object result =
                    info.getMethod().invoke(instance);


            // ====================================================
            // ⭐⭐⭐ SPRINT 6
            // TESTER SI LA MÉTHODE POSSÈDE @Json
            // ====================================================

            /*
             * C'est ici que nous appliquons la note du professeur :
             *
             * "tester l'existence de l'annotation"
             *
             * On demande :
             *
             * Est-ce que la méthode possède @Json ?
             */

            if (
                    info.getMethod()
                            .isAnnotationPresent(Json.class)
            ) {


                // =================================================
                // ⭐ SPRINT 6
                // RÉPONSE HTTP = JSON
                // =================================================

                /*
                 * Avant :
                 *
                 * text/html
                 *
                 * Maintenant :
                 *
                 * application/json
                 */

                response.setContentType(
                        "application/json; charset=UTF-8"
                );


                // Récupérer le Writer
                PrintWriter out =
                        response.getWriter();


                // =================================================
                // ⭐ SPRINT 6
                // OBJET JAVA → JSON
                // =================================================

                /*
                 * result contient l'objet retourné
                 * par le Controller.
                 *
                 * Exemple :
                 *
                 * Personne
                 *
                 * On le transforme en :
                 *
                 * {
                 *     "id": 1,
                 *     "nom": "Tsinjo"
                 * }
                 */

                out.println(
                        toJson(result)
                );


                /*
                 * Très important :
                 *
                 * On ne fait PAS :
                 *
                 * ModelView
                 * JSP
                 *
                 * car @Json signifie que la réponse
                 * est directement du JSON.
                 */

                return;
            }


            // ====================================================
            // ANCIEN FONCTIONNEMENT : ModelView
            // ====================================================

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
                // ANCIEN COMPORTEMENT POUR LES AUTRES MÉTHODES
                // =================================================

                PrintWriter out =
                        response.getWriter();


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


    // ============================================================
    // ⭐⭐⭐ SPRINT 6
    // TRANSFORMER UN OBJET JAVA EN JSON
    // ============================================================

    private String toJson(Object objet) {


        /*
         * Exemple :
         *
         * objet = Personne
         *
         * Personne possède :
         *
         * getId()
         * getNom()
         *
         * On va utiliser la réflexion pour récupérer
         * automatiquement ces valeurs.
         */

        StringBuilder json =
                new StringBuilder();


        json.append("{");


        // Récupérer toutes les méthodes publiques
        Method[] methods =
                objet.getClass().getMethods();


        boolean premier = true;


        // Parcourir les méthodes
        for (Method method : methods) {


            String nom =
                    method.getName();


            /*
             * On cherche uniquement les getters :
             *
             * getId()
             * getNom()
             *
             * On ignore :
             *
             * getClass()
             */

            if (
                    nom.startsWith("get")
                    && !nom.equals("getClass")
            ) {


                try {


                    // Exécuter le getter
                    Object valeur =
                            method.invoke(objet);


                    /*
                     * Transformer :
                     *
                     * getNom()
                     *
                     * en :
                     *
                     * nom
                     */

                    String nomAttribut =
                            Character.toLowerCase(
                                    nom.charAt(3)
                            )
                            + nom.substring(4);


                    // Ajouter une virgule
                    // entre les attributs
                    if (!premier) {
                        json.append(",");
                    }


                    // Nom de l'attribut
                    json.append("\"")
                        .append(nomAttribut)
                        .append("\":");


                    // =================================================
                    // STRING
                    // =================================================

                    if (valeur instanceof String) {

                        json.append("\"")
                            .append(valeur)
                            .append("\"");


                    // =================================================
                    // AUTRES TYPES
                    // =================================================

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


    // ============================================================
    // AFFICHER ERREUR URL
    // ============================================================

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
        for (
                UrlMethode u
                : mappingUrls.keySet()
        ) {


            String urlComplete =
                    baseUrl + u.getUrl();


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