Le projet suivant affiche la liste de controlleurs annotés avec l'annotation `@Controller` dans un projet Java. Le projet est structuré comme suit :
- TESTFRAMEWORK/
- Framework/
    - src/main/java/
        - ControllerPerso
```java
            package ControllerPerso;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)      
@Retention(RetentionPolicy.RUNTIME) 
public @interface Controller {
}
```

package ControllerPerso;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ControllerScanner {
    public static List<String> scan(String webInfPath) throws Exception {
        List<String> controllers = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        File classesDir = new File(webInfPath + "/classes");
        if (classesDir.exists()) {
            scanDirectory(classesDir, classesDir, classLoader, controllers);
        }

        File libDir = new File(webInfPath + "/lib");
        if (libDir.exists()) {
            for (File jar : libDir.listFiles()) {
                if (jar.getName().endsWith(".jar")) {
                    URL jarUrl = new URL("jar:file:" + jar.getAbsolutePath() + "!/");
                    java.net.URLClassLoader jarLoader =
                        new java.net.URLClassLoader(new URL[]{jarUrl}, classLoader);
                    scanJar(jar, jarLoader, controllers);
                    jarLoader.close();
                }
            }
        }

        return controllers;
    }

    // Scan .class files
    private static void scanDirectory(File root, File dir,
                                      ClassLoader loader,
                                      List<String> result) {
        if (!dir.exists() || !dir.isDirectory()) return;

        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scanDirectory(root, file, loader, result);
            } else if (file.getName().endsWith(".class")) {
                String className = root.toURI()
                        .relativize(file.toURI())
                        .getPath()
                        .replace("/", ".")
                        .replace(".class", "");
                tryLoad(className, loader, result);
            }
        }
    }

    private static void scanJar(File jar, ClassLoader loader,
                                 List<String> result) {
        try (java.util.jar.JarFile jarFile = new java.util.jar.JarFile(jar)) {
            java.util.Enumeration<java.util.jar.JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.endsWith(".class")) {
                    String className = name.replace("/", ".").replace(".class", "");
                    tryLoad(className, loader, result);
                }
            }
        } catch (Exception ignored) {}
    }
    
    private static void tryLoad(String className, ClassLoader loader,
                                 List<String> result) {
        try {
            Class<?> clazz = loader.loadClass(className);
            if (clazz.isAnnotationPresent(Controller.class)) {
                result.add(className);
            }
        } catch (Throwable ignored) {}
    }
}



- controllers
```java
package controllers;

import ControllerPerso.Controller;

@Controller
public class TestContoller {
}

package controllers;

import ControllerPerso.Controller;

public class Test2Contoller {
}

```

- src/main/java/
```java
package main.java;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import ControllerPerso.ControllerScanner;

public class DispatcherServlet extends HttpServlet {

    private List<String> controllers;

    @Override
    public void init() throws ServletException {
        try {
            String webInfPath = getServletContext().getRealPath("/WEB-INF");
            controllers = ControllerScanner.scan(webInfPath);
            System.out.println("Controllers trouvés : " + controllers);
        } catch (Exception e) {
            throw new ServletException("Erreur scan controllers", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h2>Liste Controllers detectes</h2>");
        out.println("<ul>");
        for (String c : controllers) {
            out.println("<li>" + c + "</li>");
        }
        out.println("</ul>");
        out.println("</body></html>");
    }
}

```
deployTest.sh
```bash
#!/bin/bash
# Framework/deployTest.sh

APP_NAME="HelloWorldServlet"
SRC_DIR="src/main/java"
BUILD_DIR="build"
LIB_DIR="lib"
OUTPUT_DIR="../Test/lib"
SERVLET_API_JAR="$LIB_DIR/servlet-api.jar"

# ── Nettoyage ─────────────────────────────────────
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/classes"

# ── Compilation ───────────────────────────────────
find "$SRC_DIR" -name "*.java" > sources.txt
javac -cp "$SERVLET_API_JAR" -d "$BUILD_DIR/classes" @sources.txt
rm sources.txt

if [ $? -ne 0 ]; then
    echo "❌ Erreur de compilation. Abandon."
    exit 1
fi

# ── Empaquetage → .jar ────────────────────────────
cd "$BUILD_DIR/classes" || exit
jar -cvf "../../$BUILD_DIR/$APP_NAME.jar" .
cd ../..

# ── Copie dans Test/lib/ ──────────────────────────
mkdir -p "$OUTPUT_DIR"
cp -f "$BUILD_DIR/$APP_NAME.jar" "$OUTPUT_DIR/"

echo ""
echo "✅ $APP_NAME.jar → Test/lib/"
echo ""
```


- TESTFRAMEWORK/
- Test/
    - lib/
        - servlet-api.jar
        - HelloWorldServlet.jar
- web.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://java.sun.com/xml/ns/j2ee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee
    http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd"
    version="2.4">

    <display-name>Application WEB affichant HelloWorld</display-name>
     <servlet>
        <servlet-name>HelloWorldServlet</servlet-name>
        <servlet-class>main.java.HelloWorldServlet</servlet-class>
    </servlet>
    <servlet>
        <servlet-name>DispatcherServlet</servlet-name>
        <servlet-class>main.java.DispatcherServlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>HelloWorldServlet</servlet-name>
        <url-pattern>/zeze</url-pattern>
    </servlet-mapping>

    <servlet-mapping>
        <servlet-name>DispatcherServlet</servlet-name>
        <url-pattern>/LsControlleur</url-pattern>
    </servlet-mapping>

</web-app>


```
deployTomcat.sh
```bash
#!/bin/bash
# ─────────────────────────────────────────────
# Test/deploy.sh
# Rôle : assembler le .war à partir du .jar Framework
#        et déployer dans Tomcat
# ─────────────────────────────────────────────

APP_NAME="Framework-Test"
WEB_DIR="webapp"
BUILD_DIR="build"
LIB_DIR="lib"
TOMCAT_WEBAPPS="/home/kamado/Tomcat/tomcat/apache-tomcat-10.0.16/webapps"

# Vérification que le .jar Framework est présent
if [ -z "$(ls $LIB_DIR/*.jar 2>/dev/null)" ]; then
    echo "❌ Aucun .jar trouvé dans $LIB_DIR/. Lance d'abord Framework/deploy.sh"
    exit 1
fi

# Nettoyage
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/WEB-INF/lib"

# ── Compilation des controllers de Test ───────────
mkdir -p "$BUILD_DIR/WEB-INF/classes"
find src/ -name "*.java" > sources.txt
javac -cp "$LIB_DIR/*.jar" -d "$BUILD_DIR/WEB-INF/classes" @sources.txt
rm sources.txt

if [ $? -ne 0 ]; then
    echo "❌ Erreur de compilation controllers. Abandon."
    exit 1
fi

# Copier le web.xml
cp "web.xml" "$BUILD_DIR/WEB-INF/"

# Copier le(s) .jar de lib/ dans WEB-INF/lib/
# Tomcat les chargera automatiquement comme bibliothèques
cp "$LIB_DIR/"*.jar "$BUILD_DIR/WEB-INF/lib/"

# Générer le .war
cd "$BUILD_DIR" || exit
jar -cvf "$APP_NAME.war" .
cd ..

# Déploiement dans Tomcat
cp -f "$BUILD_DIR/$APP_NAME.war" "$TOMCAT_WEBAPPS/"

echo ""
echo "✅ Déploiement de $APP_NAME.war terminé dans Tomcat."
echo "   Redémarrez Tomcat si nécessaire."
echo ""
echo "⏳ Attente du rechargement Tomcat..."
sleep 5
echo "Accueil : http://localhost:8080/Framework-Test/zeze"
echo "Liste Controlleur  : http://localhost:8080/Framework-Test/LsControlleur"

```
