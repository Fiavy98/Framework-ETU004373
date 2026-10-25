<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Login - Demo @Authorize</title>
</head>
<body style="font-family: Arial, sans-serif; margin: 30px;">
    <h2>Page de connexion</h2>
    <p>Le framework ne contient pas encore de vrai système de connexion. Pour tester le cas positif, il faut créer une session avec :</p>
    <pre>request.getSession().setAttribute("user", "demo-user");</pre>
    <p>Ensuite, l’URL protégée doit devenir accessible.</p>
    <p><a href="${pageContext.request.contextPath}/authorize-private">Aller vers la page protégée (après authentification)</a></p>
    <p><a href="${pageContext.request.contextPath}/authorize-public">Retour à la page publique</a></p>
</body>
</html>
