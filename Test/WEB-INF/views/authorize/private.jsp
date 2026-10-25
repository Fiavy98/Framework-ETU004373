<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Protected Page</title>
</head>
<body style="font-family: Arial, sans-serif; margin: 30px;">
    <h2>Page protégée</h2>
    <p>${message}</p>
    <p>Cette page n’est accessible que si un utilisateur est présent dans la session sous la clé "user".</p>
    <p><a href="${pageContext.request.contextPath}/authorize-public">Retour à la page publique</a></p>
</body>
</html>
