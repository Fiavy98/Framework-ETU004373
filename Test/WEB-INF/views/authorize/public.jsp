<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Public Page</title>
</head>
<body style="font-family: Arial, sans-serif; margin: 30px;">
    <h2>Page publique</h2>
    <p>${message}</p>
    <p><a href="${pageContext.request.contextPath}/authorize-private">Essayer la page protégée</a></p>
    <p><a href="${pageContext.request.contextPath}/login">Aller à la page de connexion</a></p>
</body>
</html>
