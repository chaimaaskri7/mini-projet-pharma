# Partie II - Backend : Gestion de l'Approvisionnement

## Ce qui a été implémenté

### 1. **Entité Fournisseur**
- Créée avec les attributs : `id`, `nom`, `email`
- Relation **many-to-many** avec `Categorie`
- Un fournisseur peut fournir plusieurs catégories
- Une catégorie peut être fournie par plusieurs fournisseurs

### 2. **Repository**
- `FournisseurRepository` : Interface pour accéder à la base de données

### 3. **Service d'Approvisionnement** 
- `ApprovisionnementService` : Service métier qui :
  - Identifie les médicaments en rupture (`unitesEnStock < niveauDeReappro`)
  - Regroupe les médicaments à réapprovisionner par catégorie
  - Envoie un mail personnalisé à chaque fournisseur
  - Les mails listent les médicaments que ce fournisseur peut fournir

- `EmailService` : Service d'envoi d'emails
  - Mode test : affiche les mails dans les logs
  - Mode production : intégration SendGrid (à compléter)

### 4. **Contrôleurs REST**
- `ApprovisionnementController` :
  - `GET /api/approvisionnement/medicaments-manquants` : Voir les médicaments à réapprovisionner
  - `POST /api/approvisionnement/lancer` : Lancer le processus d'approvisionnement

- `FournisseurController` :
  - `GET /api/fournisseurs` : Lister tous les fournisseurs
  - `POST /api/fournisseurs` : Ajouter un nouveau fournisseur

### 5. **Données Initiales**
La base de données est initialisée avec :
- **5 fournisseurs** :
  - Pharma Plus
  - Medis Distribution
  - ChemiExpress
  - ProPharm
  - MediSupply

- **Associations** : Chaque catégorie est fournie par au moins 2 fournisseurs

## Comment utiliser

### Démarrer l'application
```bash
.\mvnw.cmd spring-boot:run
```

### Tester en développement

1. **Voir les médicaments à réapprovisionner** :
```
GET http://localhost:8080/api/approvisionnement/medicaments-manquants
```

2. **Lancer l'approvisionnement** :
```
POST http://localhost:8080/api/approvisionnement/lancer
```

3. **Voir les mails envoyés** :
   - En développement, les mails s'affichent dans les logs de l'application
   - Chaque mail contient la liste des médicaments organisée par catégorie

## Configuration SendGrid

### Pour activer SendGrid (Production)

1. **Créer un compte SendGrid** : https://sendgrid.com
2. **Récupérer la clé API**
3. **Configurer dans `application-create.properties`** :
```properties
sendgrid.api-key=SG.votre-clé-api-ici
```

Ou via une variable d'environnement :
```
set SENDGRID_API_KEY=SG.votre-clé-api-ici
```

### Pour tester avec une adresse Gmail

Vous pouvez utiliser des adresses de test comme :
- `contact+fournisseur1@gmail.com`
- `contact+fournisseur2@gmail.com`
- Elles arrivent toutes dans `contact@gmail.com`

## Structure du Code

```
src/main/java/pharmacie/
├── entity/
│   ├── Fournisseur.java          (Nouvelle entité)
│   └── Categorie.java             (Modifiée : ajout relation with Fournisseur)
├── dao/
│   └── FournisseurRepository.java (Nouvelle)
├── service/
│   ├── ApprovisionnementService.java (Nouveau : logique métier)
│   └── EmailService.java          (Nouveau : gestion d'emails)
├── rest/
│   ├── ApprovisionnementController.java (Nouveau)
│   └── FournisseurController.java (Nouveau)
└── config/
    ├── SendGridConfig.java        (Nouvelle : configuration SendGrid)
```

## Points Clés

✅ **Simplifié** : Code facile à comprendre et maintenir
✅ **Modulaire** : Chaque classe a une responsabilité claire
✅ **Testable** : Mode développement simule les mails
✅ **Production-ready** : Intégration SendGrid possible

## Prochaines étapes

1. Tester en local que le service fonctionne
2. Ajouter la clé SendGrid pour envoyer de vrais mails
3. Améliorer le template d'email (HTML/CSS)
4. Ajouter des tests unitaires
