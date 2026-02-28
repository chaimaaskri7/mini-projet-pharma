# Checklist Pré-Déploiement Render.com

Date: ___________  
Effectué par: _____________

## 📋 Vérifications Locales

### Code Backend
- [ ] Tous les fichiers compilent sans erreur: `mvn clean package -DskipTests`
- [ ] Pas de `System.out.println()` (utiliser Logger à la place)
- [ ] Pas de clés API/secrets dans le code source
- [ ] `application-deploy.properties` utilise variables d'environnement (`${VAR_NAME}`)
- [ ] `render.yaml` est configuré correctement
- [ ] Dockerfile compile l'image Docker localement: `docker build -t pharmacie .`

### Code Frontend (Vue.js)
- [ ] Aucun appel à `http://localhost:8080` en dur
- [ ] `src/api.js` utilise une variable de configuration pour l'URL API
- [ ] Build Frontend OK: `npm run build`
- [ ] Code compilé (pas de TypeScript/JSX non compilé)

### Git et GitHub
- [ ] Dépôt Git initialisé: `git init` ✓
- [ ] Tous les fichiers ajoutés: `git add .` ✓
- [ ] Premier commit fait: `git commit -m "Initial commit"` ✓
- [ ] Repository GitHub créé
- [ ] Remote configuré: `git remote add origin https://github.com/user/repo.git` ✓
- [ ] Code poussé sur GitHub: `git push -u origin main` ✓
- [ ] `.gitignore` inclut:
  - [ ] `application-local.properties`
  - [ ] `.env` et `.env.local`
  - [ ] `target/`
  - [ ] `node_modules/`

---

## 🏗️ Configuration Infrastructure (Render.com)

### Compte Render.com
- [ ] Compte créé et actif
- [ ] Authentifiée via GitHub

### Base de Données PostgreSQL
- [ ] PostgreSQL créé sur Render
  - Name: `pharmacie-db`
  - Version: 15+
  - Status: ✓ **Active**
- [ ] Connection String copiée et sauvegardée
- [ ] Port: 5432 (par défaut)
- [ ] Informations extraites:
  - SPRING_DATASOURCE_HOST: _______________
  - SPRING_DATASOURCE_PORT: 5432
  - SPRING_DATASOURCE_DB: pharmacie_db
  - SPRING_DATASOURCE_USERNAME: _______________
  - SPRING_DATASOURCE_PASSWORD: _______________

### Service Web (Backend)
- [ ] Repository GitHub sélectionné: `mini-projet-pharma`
- [ ] Service name: `pharmacie-backend`
- [ ] Region: Même que PostgreSQL
- [ ] Runtime: Docker (détecté automatiquement)
- [ ] Build Command: `./mvnw clean package -DskipTests`
- [ ] Start Command: `java -jar target/pharmacie-spring-jpa-1.0-SNAPSHOT.jar`
- [ ] Auto-deploy: Activé

### Variables d'Environnement Backend
- [ ] `SPRING_PROFILES_ACTIVE` = `deploy`
- [ ] `SPRING_DATASOURCE_HOST` = [DB host]
- [ ] `SPRING_DATASOURCE_PORT` = `5432`
- [ ] `SPRING_DATASOURCE_DB` = `pharmacie_db`
- [ ] `SPRING_DATASOURCE_USERNAME` = [DB user]
- [ ] `SPRING_DATASOURCE_PASSWORD` = [DB password]
- [ ] `POSTMARK_API_KEY` = `3c6a5f2a-8642-47ac-b90c-34d3e58470ee`
- [ ] `PORT` = `8080` (optionnel)

---

## ✅ Tests Avant Déploiement

### Déploiement Local (Debug)
- [ ] Backend compile: `mvn clean package`
- [ ] Docker image crée: `docker build -t pharmacie .`
- [ ] Container démarre: `docker run -p 8080:8080 pharmacie`
- [ ] API accessible: `http://localhost:8080/api/medicaments`

### Application Fonctionnelle
- [ ] GET `/api/medicaments` retourne le JSON
- [ ] GET `/api/categories` retourne les catégories
- [ ] POST `/api/medicaments` crée un médicament
- [ ] DELETE `/api/medicaments-actions/{ref}` supprime correctement
- [ ] Stock +1/-1 fonctionne
- [ ] Approvisionnement détecte les meds manquants

### Configuration
- [ ] Postmark API key confirma dans `application-deploy.properties`
- [ ] Aucune clé API en dur dans le code
- [ ] Logging configuré (INFO level pour prod)

---

## 🚀 Après le Déploiement

### Monitoring Initial
- [ ] Service Web créé avec succès
- [ ] Build logs ne montrent pas d'erreurs
- [ ] Application started dans les logs Render
- [ ] URL de service notée: `https://pharmacie-backend.onrender.com`

### Tests Produit
- [ ] Endpoint accessible: `curl https://pharmacie-backend.onrender.com/api/medicaments`
- [ ] Réponse JSON valide
- [ ] Données persistent après redémarrage
- [ ] Emails d'approvisionnement se déclenchent (si Postmark approuvé)

### Performance
- [ ] Temps de réponse acceptable (<500ms)
- [ ] Pas de memory leak (vérifier logs)
- [ ] Pas de violations de quotas Render

### Sécurité
- [ ] CORS configuré pour frontend URL
- [ ] Pas de données sensibles exposées
- [ ] Authentification en place (si applicable)
- [ ] HTTPS utilisé par défaut (Render)

---

## 📊 Documentation

- [ ] `RENDER_DEPLOYMENT_GUIDE.md` à jour
- [ ] URLs production documentées
- [ ] Variables d'env documentées
- [ ] Procédure de scaling documentée
- [ ] Runbook de troubleshooting complété

---

## 🔔 Points d'Attention

⚠️ **Critical**
- [ ] PostgreSQL doit être active et accessible
- [ ] Connection String formatée correctement
- [ ] Variables d'env setups avant le deploy
- [ ] Pas de secrets en dur dans le code

⚠️ **Important**
- [ ] Backend et DB dans la même région
- [ ] Logs vérifiés pour les erreurs
- [ ] Postmark API key valide et configuré
- [ ] Load testing si traffic attendu

---

## 🎯 Résultats

**Date déploiement:** _______________

**Backend URL:** https://pharmacie-backend.onrender.com

**Statut:** 
- [ ] ✓ Production
- [ ] ⚠️ Avec problèmes (spécifier): _______________
- [ ] ✗ Échoué (raison): _______________

**Notes additionnelles:**

```
[Espace pour notes]




```

---

**Signature:** _______________  
**Date:** _______________
