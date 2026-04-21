# === ETAPE 1 : La Construction (Build) ===
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
# On copie tout le code source dans le conteneur
COPY . .
# On demande à Gradle de compiler l'application Spring Boot
RUN gradle bootJar --no-daemon

# === ETAPE 2 : L'Exécution (Run) ===
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# On récupère l'application compilée à l'étape 1
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

# On indique le port d'écoute par défaut de Spring Boot
EXPOSE 8080

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]