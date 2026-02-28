@echo off
REM Script de déploiement vers Render.com pour Windows
REM Usage: deploy-render.bat

setlocal enabledelayedexpansion

echo.
echo ════════════════════════════════════════════════════════
echo   Pharmacie Management System - Deploy to Render.com    
echo ════════════════════════════════════════════════════════
echo.

REM Check Git
echo [1/5] Vérification de Git...
git --version >nul 2>&1
if errorlevel 1 (
    echo ✗ Git not found. Please install Git first.
    exit /b 1
)
echo ✓ Git OK
echo.

REM Check if repo is initialized
echo [2/5] Vérification du dépôt Git...
if not exist ".git" (
    echo ⚠ Repo non initialisé. Initialisation...
    git init
    git add .
    git commit -m "Initial commit: Pharmacy management system"
    echo ✓ Repo initialized
) else (
    echo ✓ Repo déjà initialisé
)
echo.

REM Check Maven
echo [3/5] Vérification de Maven...
if exist "mvnw.cmd" (
    echo ✓ Maven wrapper trouvé
) else (
    mvn --version >nul 2>&1
    if errorlevel 1 (
        echo ✗ Maven not found.
        exit /b 1
    )
    echo ✓ Maven OK
)
echo.

REM Build project
echo [4/5] Compilation du projet...
if exist "mvnw.cmd" (
    call mvnw.cmd clean package -DskipTests
) else (
    call mvn clean package -DskipTests
)
if errorlevel 1 (
    echo ✗ Build échoué.
    exit /b 1
)
echo ✓ Build réussi
echo.

REM Push to GitHub
echo [5/5] Envoi vers GitHub...
set /p REMOTE_URL="Git remote URL (ex: https://github.com/user/repo.git): "

if "!REMOTE_URL!"=="" (
    echo ✗ URL non fournie
    exit /b 1
)

REM Check if remote exists and update or add
git remote > temp_remotes.txt
findstr /i "origin" temp_remotes.txt >nul 2>&1
if !errorlevel! equ 0 (
    echo Updating remote origin...
    git remote set-url origin !REMOTE_URL!
) else (
    echo Adding remote origin...
    git remote add origin !REMOTE_URL!
)
del temp_remotes.txt

REM Commit and push
set /p COMMIT_MSG="Commit message (default: 'Deploy to Render'): "
if "!COMMIT_MSG!"=="" set COMMIT_MSG=Deploy to Render

git add .
git commit -m "!COMMIT_MSG!" --allow-empty
git branch -M main

git push -u origin main
if errorlevel 1 (
    echo ✗ Push échoué.
    exit /b 1
)
echo ✓ Push vers GitHub réussi
echo.

REM Final instructions
echo ════════════════════════════════════════════════════════
echo ✓ Prêt pour le déploiement sur Render!
echo ════════════════════════════════════════════════════════
echo.
echo Prochaines étapes:
echo.
echo 1. Aller sur https://render.com/dashboard
echo 2. Cliquer sur 'New +' -^> 'PostgreSQL'
echo    - Name: pharmacie-db
echo    - Region: ^(votre région^)
echo    - Version: 15+
echo 3. Copier la Connection String PostgreSQL
echo.
echo 4. Cliquer sur 'New +' -^> 'Web Service'
echo    - Connect repository: mini-projet-pharma
echo    - Name: pharmacie-backend
echo    - Build Command: ./mvnw clean package -DskipTests
echo    - Start Command: java -jar target/pharmacie-spring-jpa-1.0-SNAPSHOT.jar
echo.
echo 5. Ajouter variables d'environnement:
echo    SPRING_PROFILES_ACTIVE=deploy
echo    SPRING_DATASOURCE_HOST=[from Connection String]
echo    SPRING_DATASOURCE_PORT=5432
echo    SPRING_DATASOURCE_DB=pharmacie_db
echo    SPRING_DATASOURCE_USERNAME=[from Connection String]
echo    SPRING_DATASOURCE_PASSWORD=[from Connection String]
echo    POSTMARK_API_KEY=3c6a5f2a-8642-47ac-b90c-34d3e58470ee
echo.
echo 6. Cliquer sur 'Create Web Service'
echo.
echo Pour plus d'infos, voir: RENDER_DEPLOYMENT_GUIDE.md
echo.
