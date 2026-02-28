# 🏥 Pharmacie Management System - Production Deployment

Système complet de gestion de pharmacie avec **Spring Boot 3.5.3** + **PostgreSQL** + **Vue.js 3** déployable sur **Render.com**

## 🚀 Quick Start - Déployer en 5 minutes

### **Option 1: Deploy avec render.yaml (Recommandé)**

```bash
# 1. Pousser le code sur GitHub
git push -u origin main

# 2. Sur Render.com Dashboard:
#    - New +
#    - Créer PostgreSQL db d'abord
#    - Puis Web Service (connecter le repo GitHub)
#    - Render va détecter render.yaml et s'auto-configurer!
```

☝️ **Le render.yaml configure TOUT automatiquement** (build, start command, env vars)

---

### **Option 2: Deploy manuel**

Voir [RENDER_DEPLOYMENT_GUIDE.md](./RENDER_DEPLOYMENT_GUIDE.md) pour les étapes détaillées

---

## 📋 Architecture

```
┌─────────────────────────────────────────────────┐
│          Frontend (Vue.js 3)                    │
│  - Vite 4.5.14                                  │
│  - CRUD Medicines + Stock Management            │
│  - Image Upload/Display                         │
│  - Approvisionnement Requests                   │
└──────────────┬──────────────────────────────────┘
               │ Fetch API
               ↓
┌──────────────────────────────────────────────────┐
│        Backend (Spring Boot 3.5.3)              │
│  - Spring Data REST (auto-CRUD)                 │
│  - 6 Custom Controllers                         │
│  - ApprovisionnementService (supplier emails)   │
│  - EmailService (Postmark integration)          │
│  - 71 Medicines + 10 Categories + 6 Suppliers  │
└──────────────┬──────────────────────────────────┘
               │ JDBC
               ↓
┌──────────────────────────────────────────────────┐
│    PostgreSQL Database (Render: Free Tier)      │
│  - 500 MB storage                               │
│  - 71 medicines                                 │
│  - Persistent data                              │
└──────────────────────────────────────────────────┘
```

---

## ⚙️ Configuration

### **Profils Spring Boot**

| Profil | Database | Usage |
|--------|----------|-------|
| `default` | H2 (in-mem) | Local dev |
| `create` | H2 (in-mem) | Local testing |
| `deploy` | PostgreSQL | Render.com production |

### **Variables d'Environnement (Render)**

```env
# Database
SPRING_DATASOURCE_HOST=your-db.onrender.com
SPRING_DATASOURCE_PORT=5432
SPRING_DATASOURCE_DB=pharmacie_db
SPRING_DATASOURCE_USERNAME=db_user
SPRING_DATASOURCE_PASSWORD=db_password

# Application
SPRING_PROFILES_ACTIVE=deploy
POSTMARK_API_KEY=3c6a5f2a-8642-47ac-b90c-34d3e58470ee
```

---

## 📚 Files Importants

| File | Purpose |
|------|---------|
| **render.yaml** | Infrastructure-as-Code (Render auto-config) |
| **Dockerfile** | Docker image multi-stage build |
| **pom.xml** | Maven dependencies + build config |
| **application-deploy.properties** | PostgreSQL config |
| **RENDER_DEPLOYMENT_GUIDE.md** | Detailed deployment steps |
| **DEPLOYMENT_CHECKLIST.md** | Pre-deployment verification |

---

## 🔧 Local Development

### **Backend**
```bash
# Compiler et tester
mvn clean package -DskipTests

# Lancer en local (H2 in-memory)
mvn spring-boot:run

# Lancer avec profil deploy (PostgreSQL - nécessite DB)
SPRING_PROFILES_ACTIVE=deploy mvn spring-boot:run
```

### **Frontend**
```bash
# Voir dans: /frontend directory (separate repo ou cd vite-frontend)
npm install
npm run dev           # Dev server
npm run build         # Production build
```

---

## 🧪 Test des Endpoints API

### **Medicines**
```bash
# Lister/chercher
curl "http://localhost:8080/api/medicaments?search=aspirin"

# Créer
curl -X POST http://localhost:8080/api/medicaments \
  -H "Content-Type: application/json" \
  -d '{"designation":"Test","ref":"TEST123"}'

# Supprimer
curl -X DELETE http://localhost:8080/api/medicaments-actions/TEST123

# Stock +1
curl -X POST http://localhost:8080/api/medicaments-actions/TEST123/ajouter-quantite
```

### **Approvisionnement**
```bash
# Voir medicines à réapprovisionner
curl http://localhost:8080/api/approvisionnement/medicaments-manquants

# Déclencher approvisionnement (emails aux fournisseurs)
curl -X POST http://localhost:8080/api/approvisionnement/lancer
```

### **Test Email** (trigger automat)
```bash
# Crée des stocks à 0 pour tester approvisionnement
curl -X POST http://localhost:8080/api/test/declencheur-approvisionnement
```

---

## 📊 Base de Données

### **Schéma PostgreSQL**

Créé automatiquement par Hibernate avec `ddl-auto=update`:

**Tables principales:**
- `medicament` (71 records)
- `categorie` (10 records)
- `fournisseur` (6 records)
- `fournisseur_categories` (mapping)

### **Données Initiales**

Contenues dans:
- `src/main/resources/data-postgresql.sql` (71 medicines + categories)
- Auto-loaded via Spring's SQL initialization

---

## 📧 Email Service (Postmark)

**Status:** ⚠️ Configured, awaiting Postmark account approval

**When approved:**
1. Approvisionnement service détecte medicines faibles en stock
2. Envoie emails HTML aux fournisseurs appropriés
3. Un email par fournisseur (inclut seulement ses catégories)

**API Key:** `3c6a5f2a-8642-47ac-b90c-34d3e58470ee`  
**From Email:** `askrichayma7+pharmacy@gmail.com`

---

## 🎯 Fonctionnalités Principales

✅ **Stock Management**
- Vue liste avec search
- +1 / -1 operations
- Approvisionnement auto-déclenché @ 0

✅ **CRUD Operations**
- Voir tous les medicaments
- Ajouter/modifier/supprimer
- Image upload pour chaque medicine

✅ **Suppliers**
- 6 fournisseurs disponibles
- Email notifications
- Mapping catégories-fournisseurs

✅ **Categories**
- 10 catégories
- Filtering
- Fournisseur par catégorie

---

## 🐛 Troubleshooting

### **"Application failed to start"**
→ Vérifier les logs Render → Vérifier PostgreSQL connection string

### **"Connection refused"**
→ PostgreSQL pas encore active ? Attendre 10 min → Vérifier regions match

### **Emails non reçus**
→ Postmark account pending approval → Vérifier Postmark dashboard

### **Data disappears on restart**
→ Normal si H2! PostgreSQL persiste → Vérifier `spring.jpa.hibernate.ddl-auto=update`

---

## 📞 Support

- **Render Docs:** https://render.com/docs/
- **Spring Boot:** https://spring.io/guides
- **Postmark:** https://postmarkapp.com/support

---

## 📝 License

Projet TP - Gestion Pharmacie

---

## 💡 Tips pour Production

1. **Never commit secrets** → Use `.env` files ou env variables
2. **Monitor logs** → Check Render dashboard daily
3. **Backup database** → Render free tier = no automatic backups
4. **Scale if needed** → Upgrade to paid plan if >750h/month
5. **Test thoroughly** → All CRUD before production

---

**Last Updated:** 2024  
**Tested on:** Spring Boot 3.5.3, PostgreSQL 15, Render.com Free Tier
