package main.java;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ControllerPerso.ControllerScanner;
import ControllerPerso.Mapping;
import ControllerPerso.MappingInfo;
import ControllerPerso.ModelView;
import ControllerPerso.UrlMethode;
import models.MappFile;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 2 * 1024 * 1024,
        maxRequestSize = 3 * 1024 * 1024
)
public class FrontServlet extends HttpServlet {

    private static final long MAX_UPLOAD_SIZE = 2L * 1024L * 1024L;
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "pdf"));
    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg",
            "image/png",
            "application/pdf"
    ));

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
                ? pathInfo.substring(1)
                : "";

        String requestUrl = request.getRequestURL().toString();
        String contextPath = request.getContextPath();
        String baseUrl = requestUrl.substring(0,
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
                    ? "style='background:#e8f5e9; font-weight:bold;'"
                    : "";

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
            Method method = info.getMethod();
            Object[] arguments = construireArguments(method, request);

            Object result = method.invoke(instance, arguments);

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
                        + method.getName() + "()</strong> executee.</p>");
                out.println("<p>Voir la console Tomcat pour le resultat.</p>");
            }

        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ServletException("Erreur exécution méthode", e);
        }
    }

    private Object[] construireArguments(Method method, HttpServletRequest request) {
        Parameter[] params = method.getParameters();
        Object[] arguments = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            Parameter parameter = params[i];
            String paramName = parameter.getName();

            if (parameter.getType() == MappFile.class) {
                arguments[i] = construireFichier(request, paramName);
                continue;
            }

            String rawValue = request.getParameter(paramName);

            if (rawValue == null) {
                arguments[i] = valeurParDefaut(parameter.getType());
                continue;
            }

            arguments[i] = convertirValeur(rawValue, parameter.getType());
        }

        return arguments;
    }

    private MappFile construireFichier(HttpServletRequest request, String paramName) {
        try {
            Part part = request.getPart(paramName);

            if (part == null || part.getSize() == 0L) {
                return null;
            }

            if (part.getSize() > MAX_UPLOAD_SIZE) {
                throw new IllegalArgumentException(
                        "Le fichier " + paramName + " dépasse la taille maximale autorisée de 2 Mo.");
            }

            String submittedFileName = part.getSubmittedFileName();
            if (submittedFileName == null || submittedFileName.trim().isEmpty()) {
                throw new IllegalArgumentException("Le fichier " + paramName + " est invalide ou vide.");
            }

            String sanitizedName = submittedFileName.replace('\\', '/');
            String fileName = sanitizedName.substring(sanitizedName.lastIndexOf('/') + 1);

            int lastDot = fileName.lastIndexOf('.');
            String name = (lastDot > 0) ? fileName.substring(0, lastDot) : fileName;
            String extension = (lastDot > 0 && lastDot < fileName.length() - 1)
                    ? fileName.substring(lastDot + 1).toLowerCase()
                    : "";

            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new IllegalArgumentException(
                        "Extension non autorisée pour le fichier " + paramName + ": " + extension);
            }

            String contentType = part.getContentType();
            if (contentType != null && !contentType.trim().isEmpty()) {
                String normalizedType = contentType.toLowerCase();
                if (!ALLOWED_CONTENT_TYPES.contains(normalizedType)
                        && !("image/jpg".equals(normalizedType) || "image/pjpeg".equals(normalizedType))) {
                    throw new IllegalArgumentException(
                            "Type MIME non autorisé pour le fichier " + paramName + ": " + contentType);
                }
            }

            byte[] content = new byte[(int) part.getSize()];
            try (InputStream inputStream = part.getInputStream()) {
                int read = 0;
                int total = 0;
                while ((read = inputStream.read(content, total, content.length - total)) != -1) {
                    total += read;
                    if (total >= content.length) {
                        break;
                    }
                }
            }

            return new MappFile(name, extension, content);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Erreur de traitement du fichier " + paramName + ".", e);
        }
    }

    private Object valeurParDefaut(Class<?> type) {
        if (type == boolean.class)
            return false;
        if (type == byte.class)
            return (byte) 0;
        if (type == short.class)
            return (short) 0;
        if (type == int.class)
            return 0;
        if (type == long.class)
            return 0L;
        if (type == float.class)
            return 0.0f;
        if (type == double.class)
            return 0.0d;
        if (type == char.class)
            return '\0';
        return null;
    }

    private Object convertirValeur(String rawValue, Class<?> type) {
        if (type == String.class) {
            return rawValue;
        }
        if (type == Integer.class || type == int.class) {
            return Integer.valueOf(rawValue);
        }
        if (type == Long.class || type == long.class) {
            return Long.valueOf(rawValue);
        }
        if (type == Double.class || type == double.class) {
            return Double.valueOf(rawValue);
        }
        if (type == Float.class || type == float.class) {
            return Float.valueOf(rawValue);
        }
        if (type == Boolean.class || type == boolean.class) {
            return Boolean.valueOf(rawValue);
        }
        if (type == Short.class || type == short.class) {
            return Short.valueOf(rawValue);
        }
        if (type == Byte.class || type == byte.class) {
            return Byte.valueOf(rawValue);
        }
        if (type == Character.class || type == char.class) {
            if (rawValue == null || rawValue.isEmpty()) {
                return '\0';
            }
            return rawValue.charAt(0);
        }

        throw new IllegalArgumentException(
                "Type de paramètre non supporté : " + type.getName() + " pour la valeur '" + rawValue + "'");
    }

    // URL inconnue -> message + liste des URLs valides
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