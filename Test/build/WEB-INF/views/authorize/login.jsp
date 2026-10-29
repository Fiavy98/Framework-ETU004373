<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Connexion</title>
</head>
<body style="font-family: Arial, sans-serif; margin: 30px;">
    <h2>Connexion</h2>
    <p>Pour tester l’authentification, créez une session utilisateur.</p>

    <form action="${pageContext.request.contextPath}/app/login" method="post">
        <label>Nom : </label>
        <input type="text" name="username" value="Tsinjo" />
        <button type="submit">Se connecter</button>
    </form>

    <p><a href="${pageContext.request.contextPath}/app/admin">Tester /app/admin</a></p>
    <p><a href="${pageContext.request.contextPath}/app/rh">Tester /app/rh</a></p>
</body>
</html>
