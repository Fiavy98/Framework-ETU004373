<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Class Protected</title>
</head>
<body style="font-family: Arial, sans-serif; margin: 30px;">
    <h2>Page protégée par @Authorize au niveau de la classe</h2>
    <p>${message}</p>
    <p><a href="${pageContext.request.contextPath}/authorize-public">Retour à la page publique</a></p>
</body>
</html>
