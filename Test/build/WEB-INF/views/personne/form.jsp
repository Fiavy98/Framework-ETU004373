<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Formulaire Personne</title>
</head>
<body>
    <h2>Ajouter une personne</h2>

    <form action="save" method="post" enctype="multipart/form-data">
        <div>
            <label for="nom">Nom :</label>
            <input type="text" id="nom" name="nom" required>
        </div>
        <br>
        <div>
            <label for="prenom">Prénom :</label>
            <input type="text" id="prenom" name="prenom" required>
        </div>
        <br>
        <div>
            <label for="genre">Genre :</label>
            <select id="genre" name="genre">
                <option value="Homme">Homme</option>
                <option value="Femme">Femme</option>
            </select>
        </div>
        <br>
        <div>
            <label for="tel">Téléphone :</label>
            <input type="text" id="tel" name="tel" required>
        </div>
        <br>
        <div>
            <label for="rectoCIN">Recto CIN :</label>
            <input type="file" id="rectoCIN" name="rectoCIN" accept=".jpg,.jpeg,.png,.pdf,image/jpeg,image/png,application/pdf">
        </div>
        <br>
        <button type="submit">Enregistrer</button>
    </form>
</body>
</html>
