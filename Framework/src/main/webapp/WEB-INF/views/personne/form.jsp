<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <title>Ajouter une personne</title>
</head>
<body>
    <h2>Ajouter une personne</h2>
    <form action="${pageContext.request.contextPath}/personne/save" method="post">
        <div>
            <label>Nom :</label>
            <input type="text" name="nom" required>
        </div>
        <div>
            <label>Prénom :</label>
            <input type="text" name="prenom" required>
        </div>
        <div>
            <label>Genre :</label>
            <select name="genre">
                <option value="Homme">Homme</option>
                <option value="Femme">Femme</option>
            </select>
        </div>
        <div>
            <label>Téléphone :</label>
            <input type="text" name="tel" required>
        </div>
        <button type="submit">Enregistrer</button>
    </form>
</body>
</html>
