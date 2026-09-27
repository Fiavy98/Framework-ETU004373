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

public class FrontServlet extends HttpServlet {

    private Map<UrlMethode, MappingInfo> mappingUrls = new HashMap<>();

    @Override
    public void init() throws ServletException {
        try {

            String webInfPath = getServletContext().getRealPath("/WEB-INF");

            List<String> controllerNames = ControllerScanner.scan(webInfPath);

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            for (String className : controllerNames) {
                Class<?> clazz = classLoader.loadClass(className);

                for (Method method : clazz.getDeclaredMethods()) {

                    if (method.isAnnotationPresent(Mapping.class)) {
                        Mapping ann = method.getAnnotation(Mapping.class);

                        UrlMethode cle = new UrlMethode(ann.value(), ann.method());

                        mappingUrls.put(cle, new MappingInfo(className, method));

                        System.out.println("✅ Mapping enregistré : ["
                                + ann.method() + "] " + ann.value()
                                + " → " + className);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException("Erreur init mappings", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        traiter(request, response, "GET");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        traiter(request, response, "POST");
    }

    // traiter() : lire l'URL demandée et décider quoi faire
    private void traiter(HttpServletRequest request, HttpServletResponse response,
                         String httpMethod) throws ServletException, IOException { 

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        String urlDemandee = (pathInfo != null && pathInfo.length() > 1)
                ? pathInfo.substring(1) : "";

        String requestUrl  = request.getRequestURL().toString();
        String contextPath = request.getContextPath();
        String baseUrl     = requestUrl.substring(0,
                requestUrl.indexOf(contextPath) + contextPath.length()) + "/";

        out.println("<html><body style='font-family: Arial; margin: 30px;'>");

        UrlMethode cle = new UrlMethode(urlDemandee, httpMethod);

        if (urlDemandee.isEmpty()) {
            // CAS 1 : URL vide → page d'accueil avec le tableau complet
            afficherTableau(out, baseUrl, null);

        } else if (mappingUrls.containsKey(cle)) {

            MappingInfo info = mappingUrls.get(cle);
            executerMethode(info, request, response);

        } else {
           
            afficherErreur(out, baseUrl, urlDemandee, httpMethod);
        }

        out.println("<br><hr>");
        out.println("<p><a href='" + baseUrl + "'>Retour accueil</a></p>");
        out.println("</body></html>");
    }

    private void afficherTableau(PrintWriter out, String baseUrl, String urlActive) {
        out.println("<h2>Tableau des Mappings</h2>");
        out.println("<table border='1' cellpadding='10' style='border-collapse:collapse;'>");
        out.println("<tr style='background:#f2f2f2'>");
        out.println("  <th>URL</th><th>HTTP</th><th>Classe</th><th>Méthode</th>");
        out.println("</tr>");

        for (Map.Entry<UrlMethode, MappingInfo> entry : mappingUrls.entrySet()) {
            String urlComplete = baseUrl + entry.getKey().getUrl();

            String style = entry.getKey().getUrl().equals(urlActive)
                    ? "style='background:#e8f5e9; font-weight:bold;'" : "";

            out.println("<tr " + style + ">");
            out.println("  <td><a href='" + urlComplete + "'>" + urlComplete + "</a></td>");
            out.println("  <td>" + entry.getKey().getHttpMethod() + "</td>");
            out.println("  <td>" + entry.getValue().getClassName() + "</td>");
            out.println("  <td>" + entry.getValue().getMethod().getName() + "()</td>");
            out.println("</tr>");
        }
        out.println("</table>");
    }

    private void executerMethode(MappingInfo info, HttpServletRequest request,
                                  HttpServletResponse response)
                                  throws ServletException, IOException {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            Class<?> clazz = cl.loadClass(info.getClassName());

            Object instance = clazz.getDeclaredConstructor().newInstance();

            Object result = info.getMethod().invoke(instance);
 
            if (result instanceof ModelView) {
                ModelView mv = (ModelView) result;

                for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }

                String jspPath = "/WEB-INF/views/" + mv.getViewName() + ".jsp";

                request.getRequestDispatcher(jspPath).forward(request, response);

            } else {
                PrintWriter out = response.getWriter();
                out.println("<p>Méthode <strong>"
                        + info.getMethod().getName() + "()</strong> exécutée.</p>");
                out.println("<p>Voir la console Tomcat pour le résultat.</p>");
            }

        } catch (Exception e) {
            throw new ServletException("Erreur exécution méthode", e);
        }
    }

    //  URL inconnue -> message + liste des URLs valides    
    private void afficherErreur(PrintWriter out, String baseUrl,
                                 String urlDemandee, String httpMethod) {
        out.println("<h2 style='color:#c62828;'>URL Inconnue</h2>");
        out.println("<div style='background:#ffebee; padding:15px; border-left:5px solid #c62828;'>");
        out.println("L'URL <strong>" + baseUrl + urlDemandee
                + "</strong> [" + httpMethod + "] n'existe pas.");
        out.println("</div>");
        out.println("<p>URLs valides :</p><ul>");

        // Lister toutes les URLs enregistrées dans la Map
        for (UrlMethode u : mappingUrls.keySet()) {
            String urlComplete = baseUrl + u.getUrl();
            out.println("<li>[" + u.getHttpMethod() + "] "
                    + "<a href='" + urlComplete + "'>" + urlComplete + "</a></li>");
        }
        out.println("</ul>");
    }
}