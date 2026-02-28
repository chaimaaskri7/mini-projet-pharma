#!/bin/bash
# Script de déploiement vers Render.com
# Usage: ./deploy-render.sh

set -e

echo "╔════════════════════════════════════════════════════════╗"
echo "║  Pharmacie Management System - Deploy to Render.com    ║"
echo "╚════════════════════════════════════════════════════════╝"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Step 1: Verify Git
echo -e "${BLUE}[1/5]${NC} Vérification de Git..."
if ! command -v git &> /dev/null; then
    echo -e "${RED}✗ Git not found. Please install Git first.${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Git OK${NC}"
echo ""

# Step 2: Check if repo is initialized
echo -e "${BLUE}[2/5]${NC} Vérification du dépôt Git..."
if [ ! -d ".git" ]; then
    echo -e "${YELLOW}⚠ Repo non initialisé. Initialisation...${NC}"
    git init
    git add .
    git commit -m "Initial commit: Pharmacy management system"
    echo -e "${GREEN}✓ Repo initialized${NC}"
else
    echo -e "${GREEN}✓ Repo déjà initialisé${NC}"
fi
echo ""

# Step 3: Check Maven
echo -e "${BLUE}[3/5]${NC} Vérification de Maven..."
if ! command -v mvn &> /dev/null && [ ! -f "mvnw" ]; then
    echo -e "${RED}✗ Maven not found. Please install Maven or use mvnw.${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Maven OK${NC}"
echo ""

# Step 4: Build project
echo -e "${BLUE}[4/5]${NC} Compilation du projet..."
if [ -f "mvnw" ]; then
    ./mvnw clean package -DskipTests
else
    mvn clean package -DskipTests
fi
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build réussi${NC}"
else
    echo -e "${RED}✗ Build échoué. Vérifiez les erreurs ci-dessus.${NC}"
    exit 1
fi
echo ""

# Step 5: Push to GitHub
echo -e "${BLUE}[5/5]${NC} Envoi vers GitHub..."
read -p "Git remote URL (ex: https://github.com/user/repo.git): " REMOTE_URL

if [ -z "$REMOTE_URL" ]; then
    echo -e "${RED}✗ URL non fournie${NC}"
    exit 1
fi

# Check if remote exists
if git remote | grep -q "origin"; then
    echo "Updating remote origin..."
    git remote set-url origin "$REMOTE_URL"
else
    echo "Adding remote origin..."
    git remote add origin "$REMOTE_URL"
fi

# Push to GitHub
git add .
read -p "Commit message (default: 'Deploy to Render'): " COMMIT_MSG
COMMIT_MSG=${COMMIT_MSG:-"Deploy to Render"}

git commit -m "$COMMIT_MSG" --allow-empty
git branch -M main

if git push -u origin main; then
    echo -e "${GREEN}✓ Push vers GitHub réussi${NC}"
else
    echo -e "${RED}✗ Push échoué. Vérifiez votre authentification GitHub.${NC}"
    exit 1
fi
echo ""

# Final instructions
echo -e "${GREEN}────────────────────────────────────────────────────────${NC}"
echo -e "${GREEN}✓ Prêt pour le déploiement sur Render!${NC}"
echo -e "${GREEN}────────────────────────────────────────────────────────${NC}"
echo ""
echo "Prochaines étapes:"
echo ""
echo "1. Aller sur https://render.com/dashboard"
echo "2. Cliquer sur 'New +' → 'PostgreSQL'"
echo "   - Name: pharmacie-db"
echo "   - Region: (votre région)"
echo "   - Version: 15+"
echo "3. Copier la Connection String PostgreSQL"
echo ""
echo "4. Cliquer sur 'New +' → 'Web Service'"
echo "   - Connect repository: mini-projet-pharma"
echo "   - Name: pharmacie-backend"
echo "   - Build Command: ./mvnw clean package -DskipTests"
echo "   - Start Command: java -jar target/pharmacie-spring-jpa-1.0-SNAPSHOT.jar"
echo ""
echo "5. Ajouter variables d'environnement:"
echo "   SPRING_PROFILES_ACTIVE=deploy"
echo "   SPRING_DATASOURCE_HOST=[from Connection String]"
echo "   SPRING_DATASOURCE_PORT=5432"
echo "   SPRING_DATASOURCE_DB=pharmacie_db"
echo "   SPRING_DATASOURCE_USERNAME=[from Connection String]"
echo "   SPRING_DATASOURCE_PASSWORD=[from Connection String]"
echo "   POSTMARK_API_KEY=3c6a5f2a-8642-47ac-b90c-34d3e58470ee"
echo ""
echo "6. Cliquer sur 'Create Web Service'"
echo ""
echo "Pour plus d'infos, voir: RENDER_DEPLOYMENT_GUIDE.md"
echo ""
