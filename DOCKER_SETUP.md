# Docker Setup pour Test Local

## Lancer PostgreSQL en local avec Docker

### **Option 1: Docker Compose (Recommandé)**

Créer un fichier `docker-compose.yml` à la racine du projet:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: pharmacie-postgres
    environment:
      POSTGRES_DB: pharmacie_db
      POSTGRES_USER: pharmacie_user
      POSTGRES_PASSWORD: password123
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - pharmacie-network

  backend:
    build: .
    container_name: pharmacie-backend
    depends_on:
      - postgres
    environment:
      SPRING_PROFILES_ACTIVE: deploy
      SPRING_DATASOURCE_HOST: postgres
      SPRING_DATASOURCE_PORT: 5432
      SPRING_DATASOURCE_DB: pharmacie_db
      SPRING_DATASOURCE_USERNAME: pharmacie_user
      SPRING_DATASOURCE_PASSWORD: password123
      POSTMARK_API_KEY: 3c6a5f2a-8642-47ac-b90c-34d3e58470ee
    ports:
      - "8080:8080"
    networks:
      - pharmacie-network

volumes:
  postgres_data:

networks:
  pharmacie-network:
    driver: bridge
```

**Usage:**

```bash
# Lancer tout (PostgreSQL + Backend)
docker-compose up --build

# Arrêter
docker-compose down

# Voir les logs
docker-compose logs -f backend
```

### **Option 2: Docker Postgres seul**

```bash
# Lancer PostgreSQL
docker run -d \
  --name pharmacie-postgres \
  -e POSTGRES_DB=pharmacie_db \
  -e POSTGRES_USER=pharmacie_user \
  -e POSTGRES_PASSWORD=password123 \
  -p 5432:5432 \
  -v pharmacie-postgres-data:/var/lib/postgresql/data \
  postgres:15-alpine

# Vérifier qu'il est lancé
docker ps | grep postgres

# Se connecter pour test
docker exec -it pharmacie-postgres psql -U pharmacie_user -d pharmacie_db
```

Puis lancer le backend avec:
```bash
mvn spring-boot:run \
  -Dspring.profiles.active=postgres-local \
  -Dspring.datasource.url="jdbc:postgresql://localhost:5432/pharmacie_db" \
  -Dspring.datasource.username="pharmacie_user" \
  -Dspring.datasource.password="password123"
```

---

## Tester la Connexion PostgreSQL

### **Depuis psql (CLI)**

```bash
# Entrer dans le container
docker exec -it pharmacie-postgres psql -U pharmacie_user -d pharmacie_db

# Commandes SQL utiles
\dt                    # Voir toutes les tables
\d medicament          # Voir schema de medicament
SELECT COUNT(*) FROM medicament;  # Voir nombre records
```

### **Depuis l'App Backend**

```bash
# Récupérer les medicaments
curl http://localhost:8080/api/medicaments

# Vérifier la DB via Spring Boot logs
mvn spring-boot:run -D…
# Chercher: "Hibernate: select medic0_..." dans les logs
```

---

## Insérer les Données Initiales

### **Option 1: Auto-load (Recommandé)**

Les données se chargeront automatiquement via:
- `spring.sql.init.mode=never` (pas de reset)
- `spring.jpa.hibernate.ddl-auto=update` (créé les tables)

**Note:** Les INSERT de `data-postgresql.sql` ne se font qu'à la PREMIÈRE fois (vérifier le schema_version)

### **Option 2: Injecter manuellement**

```bash
# Lancer psql
docker exec -it pharmacie-postgres psql -U pharmacie_user -d pharmacie_db

# Copier/coller le contenu de:
# src/main/resources/data-postgresql.sql

# Ou depuis une commande:
docker exec -i pharmacie-postgres psql -U pharmacie_user -d pharmacie_db < src/main/resources/data-postgresql.sql
```

---

## Nettoyer les Containers

```bash
# Arrêter les containers
docker-compose down

# Supprimer les données (attention!)
docker-compose down -v

# Supprimer manuellement
docker stop pharmacie-postgres
docker rm pharmacie-postgres
docker volume rm pharmacie-postgres-data
```

---

## Troubleshooting

### **"Connection refused" sur port 5432**

```bash
# Vérifier que le container est en cours d'exécution
docker ps | grep postgres

# Si pas lancé:
docker start pharmacie-postgres

# Vérifier les logs
docker logs pharmacie-postgres
```

### **"FATAL: database "pharmacie_db" does not exist"**

```bash
# Entrer dans le container et créer la DB
docker exec -it pharmacie-postgres psql -U postgres

# Dans psql:
CREATE DATABASE pharmacie_db;
CREATE USER pharmacie_user WITH PASSWORD 'password123';
ALTER ROLE pharmacie_user WITH CREATEDB;
GRANT ALL PRIVILEGES ON DATABASE pharmacie_db TO pharmacie_user;
```

### **Les données disparaissent au redémarrage**

→ Normal si vous utilisez `-v` sans volume!  
→ Ajouter `volumes:` dans docker-compose.yml:

```yaml
volumes:
  - postgres_data:/var/lib/postgresql/data
```

---

## Configuration pour CI/CD (GitHub Actions)

```yaml
services:
  postgres:
    image: postgres:15-alpine
    env:
      POSTGRES_DB: test_db
      POSTGRES_USER: test_user
      POSTGRES_PASSWORD: test_pass
    options: >-
      --health-cmd pg_isready
      --health-interval 10s
      --health-timeout 5s
      --health-retries 5
    ports:
      - 5432:5432
```

---

## Performance Notes

- **Free Tier Render:** ~500MB storage
- **Docker Local:** Unlimited (disk space)
- **Connection Pool:** Hikari (défaut Spring Boot) avec 10 connections max
- **Query Optimization:** Créer des indexes sur les colonnes recherchées (`ref`, `designation`)

---

Bon test! 🚀
