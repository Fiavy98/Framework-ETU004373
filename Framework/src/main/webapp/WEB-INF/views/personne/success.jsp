<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Enregistrement réussi</title>
</head>
<body>
    <h2>Personne enregistrée avec succès</h2>

    <p><strong>Nom :</strong> ${personne.nom}</p>
    <p><strong>Prénom :</strong> ${personne.prenom}</p>
    <p><strong>Genre :</strong> ${personne.genre}</p>
    <p><strong>Téléphone :</strong> ${personne.tel}</p>

    <a href="form">Retour au formulaire</a>
</body>
</html>
