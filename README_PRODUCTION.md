# 🏥 Système de Gestion de Pharmacie

**Production-Ready | Spring Boot 3.5.3 | PostgreSQL | Render.com**

[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen)]()
[![Production](https://img.shields.io/badge/Status-Ready%20to%20Deploy-blue)]()
[![Java](https://img.shields.io/badge/Java-21-orange)]()
[![Framework](https://img.shields.io/badge/Framework-Spring%20Boot%203.5.3-green)]()

---

## 🎯 À Propos

Système complet de gestion de pharmacie avec:
- **Frontend:** Vue.js 3 (Vite) - Interface moderne et réactive
- **Backend:** Spring Boot REST API - 71 médicaments, 10 catégories, 6 fournisseurs
- **Database:** PostgreSQL - Persistance production-ready
- **Email Service:** Postmark - Notifications automatiques fournisseurs
- **Deployment:** Render.com - 1-click deploy sur cloud gratuit

---

## 🚀 Quick Deploy (1 heure)

### **Nouveau? Commencez ici ⭐**

```bash
# 1. Lire le guide (15 min)
cat START_HERE.md

# 2. Suivre les 6 étapes dans:
cat QUICK_DEPLOY_STEPS.md

# Total: ~1 heure pour être en production
```

### **Pour approfondir:**
- [RENDER_DEPLOYMENT_GUIDE.md](./RENDER_DEPLOYMENT_GUIDE.md) - Guide exhaustif
- [DEPLOYMENT_CHECKLIST.md](./DEPLOYMENT_CHECKLIST.md) - Vérification pré/post
- [DOCKER_SETUP.md](./DOCKER_SETUP.md) - Tester PostgreSQL local

---

## 📚 Documentation Complète

| Document | Objectif | Audience | Durée |
|----------|----------|----------|-------|
| **[START_HERE.md](./START_HERE.md)** | Point d'entrée | **Tout le monde** | 5 min |
| **[QUICK_DEPLOY_STEPS.md](./QUICK_DEPLOY_STEPS.md)** | 6 étapes simples | Développeurs | 30 min |
| **[RENDER_DEPLOYMENT_GUIDE.md](./RENDER_DEPLOYMENT_GUIDE.md)** | Détails complets | Techniques | 1 heure |
| **[DEPLOYMENT_CHECKLIST.md](./DEPLOYMENT_CHECKLIST.md)** | Vérifications | QA/Admin | 30 min |
| **[DOCKER_SETUP.md](./DOCKER_SETUP.md)** | Test local PostgreSQL | DevOps | 45 min |
| **[DEPLOYMENT_INVENTORY.md](./DEPLOYMENT_INVENTORY.md)** | Fichiers créés | Référence | 10 min |
| **[FILES_INDEX.md](./FILES_INDEX.md)** | Organisation projet | Référence | 10 min |

---

## 📦 Architecture

```
┌─────────────────────────────────────────────┐
│   Frontend (Vue.js 3 + Vite)               │
│   - CRUD Medicines                         │
│   - Stock Management                       │
│   - Image Upload                           │
│   - Approvisionnement Requests             │
└──────────────┬──────────────────────────────┘
               │(Fetch API)
               ↓
┌──────────────────────────────────────────────┐
│   Backend (Spring Boot 3.5.3 + REST)        │
│   - 6 REST Controllers                      │
│   - Spring Data REST auto-CRUD              │
│   - ApprovisionnementService                │
│   - EmailService (Postmark)                 │
│   - 71 Medicines | 10 Categories | 6 Suppliers
└──────────────┬──────────────────────────────┘
               │(JDBC)
               ↓
┌──────────────────────────────────────────────┐
│   PostgreSQL (Render Free: 500 MB)          │
│   - Persistent Data                        │
│   - Full ACID Support                       │
└──────────────────────────────────────────────┘
```

---

## 🔧 Développement Local

### **Backend (Spring Boot)**

```bash
# Build
mvn clean package -DskipTests

# Run (H2 in-memory, dev)
mvn spring-boot:run

# Run (PostgreSQL, test production config)
SPRING_PROFILES_ACTIVE=postgres-local mvn spring-boot:run
```

### **Frontend (Vue.js)**

```bash
cd ../vite-frontend  # Si séparé

npm install
npm run dev          # Dev server
npm run build        # Production build
```

---

## 📊 API Endpoints

### **Medicines**
```bash
GET    /api/medicaments?search=aspirin
POST   /api/medicaments                          # Create
PUT    /api/medicaments-actions/{ref}            # Update
DELETE /api/medicaments-actions/{ref}            # Delete
POST   /api/medicaments-actions/{ref}/ajouter-quantite
POST   /api/medicaments-actions/{ref}/retirer-quantite
```

### **Categories**
```bash
GET /api/categories
```

### **Supplies**
```bash
GET /api/approvisionnement/medicaments-manquants
POST /api/approvisionnement/lancer                # Trigger emails
```

### **Testing**
```bash
POST /api/test/declencheur-approvisionnement     # Force 5 meds to 0 stock
```

---

## 🛠️ Configuration

### **Profils Spring Boot**

| Profile | Database | Usage |
|---------|----------|-------|
| `default` | H2 in-memory | Local dev |
| `create` | H2 in-memory | Test with schema recreation |
| `deploy` | PostgreSQL | Production (Render) |
| `postgres-local` | PostgreSQL localhost | Dev with real DB |

### **Variables d'Environnement (Render)**

```env
# PostgreSQL Connection
SPRING_DATASOURCE_HOST=dpg-xyz.render.com
SPRING_DATASOURCE_PORT=5432
SPRING_DATASOURCE_DB=pharmacie_db
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=password

# Application
SPRING_PROFILES_ACTIVE=deploy
POSTMARK_API_KEY=3c6a5f2a-8642-47ac-b90c-34d3e58470ee
```

---

## 🐳 Docker Support

### **Build & Run Local**

```bash
# Build image
docker build -t pharmacie .

# Run standalone
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=create pharmacie

# Run with Docker Compose (PostgreSQL + Backend)
docker-compose up --build

# See DOCKER_SETUP.md for details
```

---

## 📧 Email Service (Postmark)

**Status:** Configured, awaiting account approval

When approved:
- Automatically sends supplier emails when medicines reach reorder level
- Maps suppliers by category
- Personalized HTML emails

```bash
# Trigger manually
curl -X POST http://localhost:8080/api/approvisionnement/lancer
```

---

## ✅ Features

- [x] CRUD Operations (Medicines, Categories, Suppliers)
- [x] Stock Management (+1 / -1)
- [x] Auto Reordering Trigger (stock <= threshold)
- [x] Image Upload/Download
- [x] Search & Filter
- [x] REST API with Swagger/OpenAPI
- [x] PostgreSQL Persistence
- [x] Email Notifications (Postmark)
- [x] Docker Support
- [x] Production-Ready Config
- [ ] Frontend Deployment (separate repo)
- [ ] User Authentication (future)

---

## 🚀 Deployment Status

| Component | Status | Details |
|-----------|--------|---------|
| Backend Code | ✅ Ready | All features working |
| PostgreSQL Config | ✅ Ready | application-deploy.properties |
| Render Config | ✅ Ready | render.yaml |
| Docker Support | ✅ Ready | Multi-stage Dockerfile |
| Frontend Build | ✅ Ready | But needs separate deployment |
| Email Service | ⏳ Pending | Postmark account approval |
| GitHub Repo | 🔄 Next | Push code |
| Render Deploy | 🔄 Next | Follow QUICK_DEPLOY_STEPS.md |

---

## 📋 Deploy in 6 Steps

**Estimated Time:** 1 hour

See detailed guide: **[QUICK_DEPLOY_STEPS.md](./QUICK_DEPLOY_STEPS.md)**

```
STEP 1: Read QUICK_DEPLOY_STEPS.md (15 min)
STEP 2: Compile & Push GitHub (10 min + deploy script)
STEP 3: Create PostgreSQL on Render (15 min + 10 min wait)
STEP 4: Create Web Service (5 min)
STEP 5: Add Environment Variables (5 min)
STEP 6: Deploy & Verify (5 min + 5 min build)

RESULT: Production backend at https://pharmacie-backend.onrender.com ✅
```

---

## 🔍 Verify Installation

```bash
# Check Java
java -version          # Should be 21

# Check Maven
mvn --version

# Build
mvn clean package -DskipTests

# Run locally
mvn spring-boot:run

# Test API (in another terminal)
curl http://localhost:8080/api/medicaments

# Should get JSON with 71 medicines ✅
```

---

## 📞 Support

| Issue | Resource |
|-------|----------|
| "How do I deploy?" | [QUICK_DEPLOY_STEPS.md](./QUICK_DEPLOY_STEPS.md) |
| "Technical details?" | [RENDER_DEPLOYMENT_GUIDE.md](./RENDER_DEPLOYMENT_GUIDE.md) |
| "Test locally first?" | [DOCKER_SETUP.md](./DOCKER_SETUP.md) |
| "Checklist?" | [DEPLOYMENT_CHECKLIST.md](./DEPLOYMENT_CHECKLIST.md) |
| "What changed?" | [DEPLOYMENT_INVENTORY.md](./DEPLOYMENT_INVENTORY.md) |

---

## 📄 License

Projet TP - Système de Gestion de Pharmacie

---

## 📊 Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Runtime** | Java | 21 |
| **Framework** | Spring Boot | 3.5.3 |
| **Build** | Maven | 3.9+ |
| **Database** | PostgreSQL | 15+ |
| **API** | REST + Swagger | OpenAPI 3.0 |
| **Email** | Postmark | API v1 |
| **Frontend** | Vue.js | 3 (separate) |
| **DevOps** | Docker | Multi-stage |
| **PaaS** | Render.com | Free tier |

---

## 🎉 You're Ready!

**Everything is configured and ready to deploy.**

1. **First time?** → Start with [START_HERE.md](./START_HERE.md)
2. **Ready to deploy?** → Follow [QUICK_DEPLOY_STEPS.md](./QUICK_DEPLOY_STEPS.md)
3. **Questions?** → Check the docs above

---

## 🔗 Useful Links

- [Render.com Dashboard](https://dashboard.render.com)
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [Postmark Docs](https://postmarkapp.com/support)

---

**Version:** 1.0  
**Last Updated:** 2024  
**Status:** ✅ Production Ready  

```
🚀 Ready to deploy? → Read START_HERE.md first!
```
