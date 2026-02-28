# Guide Complet : Déployer sur Render.com avec PostgreSQL

## 📋 Table des matières
1. [Prérequis](#prérequis)
2. [Étapes de déploiement](#étapes-de-déploiement)
3. [Configuration des variables d'environnement](#configuration-des-variables-denvironnement)
4. [Vérification post-déploiement](#vérification-post-déploiement)
5. [Troubleshooting](#troubleshooting)

---

## Prérequis

✅ **Avant de commencer:**
- Compte [Render.com](https://render.com/) (gratuit)
- Code poussé sur [GitHub](https://github.com/) ou GitLab
- Aucune variable sensible dans les fichiers de configuration (utiliser des variables d'env)

---

## Étapes de déploiement

### **Étape 1 : Pousser le code sur GitHub**

```bash
# Initialiser le repo local (si pas fait)
cd c:\Users\user\Desktop\mini-projet-pharma
git init
git add .
git commit -m "Initial commit: Pharmacy management system with PostgreSQL"

# Ajouter le remote GitHub
git remote add origin https://github.com/YOUR_USERNAME/mini-projet-pharma.git
git branch -M main
git push -u origin main
```

### **Étape 2 : Créer la base de données PostgreSQL sur Render**

1. Aller sur [Render Dashboard](https://dashboard.render.com/)
2. Cliquer sur **New +** → **PostgreSQL**
3. Configurer:
   - **Name**: `pharmacie-db`
   - **Region**: Choisir la même région que le backend
   - **PostgreSQL Version**: 15+
4. Cliquer **Create Database**
5. **Attendre 5-10 minutes** pour que la DB soit active
6. Copier la **Connection String** (elle sera utilisée plus tard)

### **Étape 3 : Créer le service Web (Backend)**

1. Cliquer sur **New +** → **Web Service**
2. **Connect your repository**: Sélectionner le repo GitHub `mini-projet-pharma`
3. Configurer le service:
   - **Name**: `pharmacie-backend`
   - **Region**: Même que la base de données (important!)
   - **Runtime**: Docker
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/pharmacie-spring-jpa-1.0-SNAPSHOT.jar`

### **Étape 4 : Ajouter les variables d'environnement**

Dans le service Web, aller dans **Environment** et ajouter:

#### **Variables PostgreSQL:**

| Clé | Valeur |
|-----|--------|
| `SPRING_PROFILES_ACTIVE` | `deploy` |
| `SPRING_DATASOURCE_HOST` | `[extraire du Connection String]` |
| `SPRING_DATASOURCE_PORT` | `5432` |
| `SPRING_DATASOURCE_DB` | `pharmacie_db` |
| `SPRING_DATASOURCE_USERNAME` | `[user du Connection String]` |
| `SPRING_DATASOURCE_PASSWORD` | `[password du Connection String]` |

#### **Variables Application:**

| Clé | Valeur |
|-----|--------|
| `PORT` | `8080` |
| `POSTMARK_API_KEY` | `3c6a5f2a-8642-47ac-b90c-34d3e58470ee` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` |

**Comment extraire les infos du Connection String PostgreSQL:**
```
postgres://user:password@host:port/database
                  ↑       ↑    ↑     ↑
        USERNAME --+       |    |     +-- DATABASE
        PASSWORD -----------+    +-- PORT
        HOST -------------------+
```

### **Étape 5 : Initialiser la base de données**

Une fois le backend déployé (voir les logs pour "Application started"):

1. Aller à l'URL du service: `https://pharmacie-backend.onrender.com` (exemple)
2. Le script `data-postgresql.sql` s'exécutera automatiquement (si configuré)
3. Les 71 médicaments seront créés automatiquement

---

## Configuration des variables d'environnement

### **Profil de déploiement utilisé:**
Le fichier `application-deploy.properties` est activé via `SPRING_PROFILES_ACTIVE=deploy`

**Connexion PostgreSQL:**
```properties
spring.datasource.url=jdbc:postgresql://${SPRING_DATASOURCE_HOST}:${SPRING_DATASOURCE_PORT}/${SPRING_DATASOURCE_DB}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
```

**Hibernate:**
```properties
spring.jpa.hibernate.ddl-auto=update  # Crée/update les tables automatiquement
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

---

## Vérification post-déploiement

### **1️⃣ Vérifier que l'application est en ligne**

```bash
curl https://pharmacie-backend.onrender.com/api/medicaments
```

Vous devriez recevoir un JSON avec les 71 médicaments.

### **2️⃣ Tester les endpoints clés**

**Récupérer les médicaments:**
```bash
curl https://pharmacie-backend.onrender.com/api/medicaments
```

**Chercher un médicament:**
```bash
curl "https://pharmacie-backend.onrender.com/api/medicaments?search=aspirin"
```

**Récupérer les catégories:**
```bash
curl https://pharmacie-backend.onrender.com/api/categories
```

### **3️⃣ Vérifier les logs du service**

1. Aller dans **Render Dashboard** → **Services** → `pharmacie-backend`
2. Cliquer sur **Logs**
3. Chercher: `"Application started in X seconds"` (succès) ou `ERROR` (problème)

### **4️⃣ Tester le service d'approvisionnement (optionnel)**

```bash
curl -X POST https://pharmacie-backend.onrender.com/api/test/declencheur-approvisionnement
```

**Résultat attendu:** Emails d'approvisionnement envoyés aux fournisseurs (si Postmark approuvé)

---

## Troubleshooting

### ❌ **"Application failed to start"**

**Cause possible:** Variables d'environnement PostgreSQL manquantes ou incorrectes

**Solution:**
1. Vérifier les logs Render
2. Vérifier que `SPRING_DATASOURCE_HOST`, `USERNAME`, `PASSWORD` sont corrects
3. S'assurer que la base PostgreSQL est active

### ❌ **"Connection refused" ou timeout**

**Cause possible:** Base de données pas encore active ou region mismatch

**Solution:**
1. Attendre 10 minutes pour que PostgreSQL soit prêt
2. Vérifier que backend et DB sont dans la même région
3. Tester la connexion avec un outil externe: `psql` ou TablePlus

### ❌ **"ddl-auto=update" crée les tables mais pas de données**

**Cause:** Les scripts de données (`data-postgresql.sql`) ne s'exécutent pas automatiquement

**Solution:** Exécuter manuellement via SQL (voir section suivante)

### 🔧 **Ajouter les données manuellement**

1. Copier le contenu de `src/main/resources/data-postgresql.sql`
2. Aller dans Render Dashboard → Base de données PostgreSQL → **Connection**
3. Utiliser l'outil SQL fourni ou ouvrir avec TablePlus
4. Exécuter les INSERT pour les catégories et médicaments

---

## URLs Production

Une fois déployé, le système sera accessible à:

| Composant | URL |
|-----------|-----|
| **Backend API** | `https://pharmacie-backend.onrender.com` |
| **API Medicaments** | `https://pharmacie-backend.onrender.com/api/medicaments` |
| **Approvisionnement** | `https://pharmacie-backend.onrender.com/api/approvisionnement/medicaments-manquants` |
| **Base de données PostgreSQL** | Connexion via Render Dashboard |

---

## Configuration du Frontend (Vue.js)

Pour connecter le frontend Vue.js au backend déployé:

📝 **Mettre à jour `src/api.js`:**

```javascript
// Avant (localhost):
const API_BASE = 'http://localhost:8080/api';

// Après (production Render):
const API_BASE = 'https://pharmacie-backend.onrender.com/api';
```

Puis déployer le frontend sur:
- Render (Static Site)
- Vercel
- Netlify
- GitHub Pages

---

## Coûts et Limitations (Plan Gratuit Render)

✅ **Inclus gratuitement:**
- Web Service (partagé: 750 heures/mois ~ 24h/jour)
- PostgreSQL Database (500 MB de stockage)
- 100 GB sortie réseau/mois
- Auto-redeploy depuis GitHub

⚠️ **Limitations:**
- Instance suspendue après 15 min d'inactivité (démarrage lent)
- Pas de SLA ou garantie de disponibilité
- Parfait pour le développement et les TP

---

## Support et Questions

Pour plus d'infos:
- [Render.com Documentation](https://render.com/docs/)
- [Spring Boot on Render](https://render.com/docs/deploy-spring)
- [PostgreSQL on Render](https://render.com/docs/databases)

---

**Date création:** 2024
**Testé avec:** Spring Boot 3.5.3 + PostgreSQL 15 + Render.com
