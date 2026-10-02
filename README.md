## Sprint 0
- Dossier Framework 
    - src
        - main
            - java
                - HelloWorldServlet.java
            - webapp
                - WEB-INF
                    - web.xml
    - deploy.sh
- Dans Dossier Test
    - lib 
        - HeloWorldServlet.jar
    - webapp
        - WEB-INF
            - web.xml
    - deploy.sh

- le but est de fournir un .jar pour le Framework qui contient les servlets compilés
et on le deploye (le jar) dans le dossier Test qui va devenir un bibliotheque et le dossier Test va le deployer dans le webapp de tomcat.


## Sprint 1
cree un classe annotation pour controlleur (personaliser) 
Cree un classe Utilitaire qui parcourt la class path qui regarde la notation
    stocker dans  Liste<String> tous le controlleur qu on trouve
    afficher dans un pagela liste de controlleur
    on utilise 
    fonction init() : je ne comprend pas bien son role mais ce que j'ai entendue est :
        il initialiser ny projet
        but final => afficher la liste des classe  Conntroller dans un page au navigateur 

## Sprint 2
- annotation : @Mapping("url")
- target: method
- => **Afficher url avec Methode et son classe associer  (url : Methode->classe)**
    - url qu on connait : affiche tous le liste d'url qu on connait  
    - url qu on ne connait pas  : Erreur Exception qui dit qu il ne connait pas cette url 
    et il liter le url qu il connait 

## Sprint 3
- 3a : Gestion de GET/POST dans le mapping 
    - GET  /andrana  → afficher une page
    - POST /andrana  → traiter un formulaire

    ex: @RequestMapping("/test","GET")
        @RequestMapping("/test","POST")
        Map<url,MethodeInfo> 
            - Methode 1 : Concatener l'url
                Map<String, MappingInfo>
                - "GET:andrana"  → MappingInfo(TestController, andrana())
                - "POST:andrana" → MappingInfo(TestController, sauvegarder())
        
            - Methode 2 : cree un autre class :  urlMethode 
                - methode il faut surcharger un methode equals pour l'attribue methode
                C'est a dire l'annotation @RequestMapping doit prendre une deuxieme attribute methode   

                Map<UrlMethode, MappingInfo>
                UrlMethode("andrana","GET")  → MappingInfo(TestController, andrana())
                UrlMethode("andrana","POST") → MappingInfo(TestController, sauvegarder())


- 3b excecuter methode (appeler par l'url)
    => println afficher dans console

Pour voir le resultat dans console
Tsinjo@kamado-X550VC:~/Tomcat/tomcat/apache-tomcat-10.0.16/bin$ ./catalina.sh start
Using CATALINA_BASE:   /home/kamado/Tomcat/tomcat/apache-tomcat-10.0.16
Using CATALINA_HOME:   /home/kamado/Tomcat/tomcat/apache-tomcat-10.0.16
Using CATALINA_TMPDIR: /home/kamado/Tomcat/tomcat/apache-tomcat-10.0.16/temp
Using JRE_HOME:        /usr
Using CLASSPATH:       /home/kamado/Tomcat/tomcat/apache-tomcat-10.0.16/bin/bootstrap.jar:/home/kamado/Tomcat/tomcat/apache-tomcat-10.0.16/bin/tomcat-juli.jar
Using CATALINA_OPTS:   
Tomcat started.
Tsinjo@kamado-X550VC:~/Tomcat/tomcat/apache-tomcat-10.0.16/bin$ tail -f ../logs/catalina.out


cd ~/ITU/S4/WEB_DINAMIQUE/TestFramework/Framework && ./deployTest.sh
cd ../Test && ./deployTomcat.sh


## SPRINT 5
SPRINT 5: Comment envoyer les donner vers une vue 
    Exemple : dans spring mvc :
    
    @GetMapping("/secretariat/profil/{id}")
    public String profil(@PathVariable Integer id, Model model) {
        model.addAttribute("eleve", eleveService.getProfil(id));
        model.addAttribute("pageTitle", "Profil de l'Élève");
        return "Secretaire/profil_eleve";
    }
    

    methode : class ModelView : String viewName, Map<String, Object> data
    ┌─────────────────────────────────────────────────────┐
    │                    ModelView                        │
    │  ┌─────────────┐  ┌─────────────────────────────┐   │
    │  │ viewName    │  │ Map<String, Object> data    │   │
    │  │ "mappings"  │  │  "message" → "Hello"        │   │
    │  └─────────────┘  │  "timestamp" → Date         │   │
    │                    │  "eleve" → Eleve           │  │
    │                    └─────────────────────────────┘  │
    └─────────────────────────────────────────────────────┘
    
## Sprint 6 
- meth action anaty controleur tsy makany @ vue fa mamerina JSON
- cree notation 
- framework 
    - tester l'existance du notation 
    -meht : retourner un objet -> invokey

## Sprint 7
- entrer un formulaire dans view
- methode save(String nom,String prenom,int age)
    -> nul 
    -> search parametre dans view
    -> invokey