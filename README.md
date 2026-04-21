 Le Troupeau - Cloud Edition ☁️

Application de suivi pastoral et social, conçue pour un environnement distribué.

🏗️ Architecture

Frontend : Vue.js 3 + Vite + Pinia (Hébergé sur Vercel/Netlify)

Backend : Spring Boot 3 + Spring Security JWT (Hébergé sur Railway/Render)

Base de données : PostgreSQL (Neon/Supabase)

🚀 Guide de Déploiement

1. Base de données (PostgreSQL)

Créez une instance sur Neon.tech ou Supabase.

Récupérez l'URL de connexion JDBC (jdbc:postgresql://...).

2. Backend (Spring Boot)

Créez un projet sur votre PaaS Java (ex: Render, Railway).

Connectez-le à ce dépôt GitHub et pointez vers le dossier backend-spring.

Configurez les variables d'environnement (Secrets) :

DATABASE_URL : URL JDBC récupérée à l'étape 1.

DB_USER : Nom d'utilisateur de la base de données.

DB_PASSWORD : Mot de passe de la base de données.

JWT_SECRET : Une chaîne aléatoire longue (ex: générée via openssl rand -base64 64).

FRONTEND_URL : L'URL de production de votre frontend (ex: https://letroupeau.vercel.app).

Au premier démarrage, Flyway exécutera automatiquement le script V1__init_schema.sql.

3. Frontend (Vue.js)

Importez le dépôt sur Vercel ou Netlify.

Spécifiez le Root Directory : frontend-vue.

Configurez la variable d'environnement :

VITE_API_BASE_URL : L'URL publique de votre backend (ex: https://api.letroupeau.app/api).

🛠️ Développement Local

Installez PostgreSQL localement.

Définissez les variables d'environnement locales (dans votre IDE ou via .env).

Lancez le Backend : cd backend-spring && ./gradlew bootRun

Lancez le Frontend : cd frontend-vue && npm run dev
