<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <title>Personne enregistrée</title>
</head>
<body>
    <h2>Personne enregistrée avec succès</h2>
    <p>Nom : ${personne.nom}</p>
    <p>Prénom : ${personne.prenom}</p>
    <p>Genre : ${personne.genre}</p>
    <p>Téléphone : ${personne.tel}</p>
    <a href="${pageContext.request.contextPath}/personne/form">Retour au formulaire</a>
</body>
</html>
