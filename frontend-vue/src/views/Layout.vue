<template>
  <div class="app-layout">
    <!-- BARRE LATÉRALE -->
    <nav class="sidebar">
      <div class="sidebar-header">
        <div class="brand">
          <!-- Icône temporaire, à remplacer par ton vrai logo plus tard -->
          <span class="logo-icon">🔥</span> 
          <h2>Le Troupeau</h2>
        </div>
        <!-- Le badge ne s'affiche que si l'utilisateur est bien chargé -->
        <span class="role-badge" v-if="authStore.user && displayRole" :class="roleClass">
          {{ displayRole }}
        </span>
      </div>

      <!-- BOUTON D'ACTION PRINCIPAL (CTA) -->
      <div class="sidebar-cta">
        <button @click="goToNewContact" class="btn-primary">
          ➕ Nouveau Contact
        </button>
      </div>

      <!-- MENUS DE NAVIGATION -->
      <ul class="nav-links">
        <li>
          <router-link to="/dashboard" active-class="active">
            📊 Tableau de Bord
          </router-link>
        </li>
        
        <!-- Accessible à tout le monde (EPIC-2) -->
        <li>
          <router-link to="/contacts" active-class="active">
            📒 Mes Contacts
          </router-link>
        </li>

        <!-- Accessible uniquement aux Leaders et Admins (US-2.5) -->
        <li v-if="isLeaderOrAdmin">
          <router-link to="/team" active-class="active">
            👥 Mon Équipe
          </router-link>
        </li>

        <!-- Accessible uniquement aux Admins (EPIC-5) -->
        <li v-if="isAdmin">
          <router-link to="/admin" active-class="active">
            ⚙️ Administration
          </router-link>
        </li>
      </ul>

      <!-- PIED DE PAGE -->
      <div class="sidebar-footer">
        <div class="user-info" v-if="authStore.user">
          <span class="user-email">{{ authStore.user.sub }}</span>
        </div>
        <button @click="handleLogout" class="btn-logout">
          🚪 Déconnexion
        </button>
      </div>
    </nav>

    <!-- CONTENU PRINCIPAL -->
    <main class="main-content">
      <!-- En-tête supérieur dynamique -->
      <header class="top-header">
        <h1>{{ currentRouteName }}</h1>
      </header>
      
      <!-- C'est ici que Vue Router injectera les autres pages (Dashboard, Contacts, etc.) -->
      <div class="page-container">
        <router-view></router-view>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useRouter, useRoute } from 'vue-router';

// Initialisation des outils Vue
const authStore = useAuthStore();
const router = useRouter();
const route = useRoute();

// --- GESTION DES RÔLES & PERMISSIONS ---
// Sécurité : On s'assure de ne pas crasher si authStore.user est temporairement null
const userRole = computed(() => authStore.user?.role || '');

// Affichage propre du rôle (enlève le "ROLE_" proprement)
const displayRole = computed(() => {
  return userRole.value ? userRole.value.replace('ROLE_', '') : '';
});

// Logique d'autorisation stricte pour l'affichage des menus
const isLeaderOrAdmin = computed(() => ['ROLE_LEADER', 'ROLE_ADMIN'].includes(userRole.value));
const isAdmin = computed(() => userRole.value === 'ROLE_ADMIN');

// Couleur dynamique du badge selon le rôle
const roleClass = computed(() => {
  if (isAdmin.value) return 'badge-admin';
  if (isLeaderOrAdmin.value) return 'badge-leader';
  return 'badge-member';
});

// --- ACTIONS DES BOUTONS ---
const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};

const goToNewContact = () => {
  router.push('/contacts/new'); 
};

// --- TITRE DYNAMIQUE DE LA PAGE ---
const currentRouteName = computed(() => {
  switch (route.path) {
    case '/dashboard': return 'Tableau de Bord';
    case '/contacts': return 'Mes Contacts';
    case '/team': return 'Mon Équipe';
    case '/admin': return 'Administration';
    case '/contacts/new': return 'Nouveau Contact';
    default: return 'Le Troupeau';
  }
});
</script>

<style scoped>
/* VARIABLES DE COULEURS (Inspirées de la charte du logo) */
:root {
  --primary-green: #1a8f2e; /* Vert principal du logo */
  --primary-green-dark: #126620;
  --accent-red: #e62222; /* Rouge des flammes */
  --accent-red-dark: #b81b1b;
  --sidebar-bg: #0f172a; /* Bleu nuit profond */
  --sidebar-hover: #1e293b;
  --bg-light: #f8fafc; /* Fond gris très clair pour l'application */
  --text-main: #334155;
}

.app-layout {
  display: flex;
  min-height: 100vh;
  background-color: var(--bg-light);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* --- BARRE LATÉRALE (SIDEBAR) --- */
.sidebar {
  width: 260px;
  background-color: var(--sidebar-bg);
  color: white;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 10px rgba(0,0,0,0.1);
  z-index: 10;
}

.sidebar-header {
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand h2 {
  margin: 0;
  font-size: 1.4rem;
  font-weight: 700;
  letter-spacing: 0.5px;
}

/* --- BADGES --- */
.role-badge {
  display: inline-block;
  margin-top: 8px;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: bold;
  letter-spacing: 0.5px;
}
.badge-admin { background-color: var(--accent-red); color: white; }
.badge-leader { background-color: #f59e0b; color: white; } /* Orange pour distinguer le Leader */
.badge-member { background-color: var(--primary-green); color: white; }

/* --- BOUTON NOUVEAU CONTACT --- */
.sidebar-cta {
  padding: 20px;
}
.btn-primary {
  width: 100%;
  padding: 12px;
  background-color: var(--primary-green);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 6px rgba(26, 143, 46, 0.2);
}
.btn-primary:hover {
  background-color: var(--primary-green-dark);
  transform: translateY(-1px);
}

/* --- LIENS DE NAVIGATION --- */
.nav-links {
  list-style: none;
  padding: 0;
  margin: 0;
  flex-grow: 1;
}
.nav-links li {
  margin-bottom: 4px;
}
.nav-links a {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 24px;
  color: #cbd5e1;
  text-decoration: none;
  font-size: 0.95rem;
  transition: all 0.2s;
  border-left: 4px solid transparent;
}
.nav-links a:hover {
  background-color: var(--sidebar-hover);
  color: white;
}
.nav-links a.active {
  background-color: var(--sidebar-hover);
  color: white;
  border-left: 4px solid var(--primary-green);
  font-weight: 600;
}

/* --- PIED DE PAGE SIDEBAR --- */
.sidebar-footer {
  padding: 20px;
  border-top: 1px solid rgba(255,255,255,0.1);
  background-color: rgba(0,0,0,0.2);
}
.user-email {
  display: block;
  font-size: 0.85rem;
  color: #94a3b8;
  margin-bottom: 15px;
  word-break: break-all;
}
.btn-logout {
  width: 100%;
  padding: 10px;
  background-color: transparent;
  color: #cbd5e1;
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
}
.btn-logout:hover {
  background-color: rgba(230, 34, 34, 0.1);
  color: var(--accent-red);
  border-color: var(--accent-red);
}

/* --- ZONE DE CONTENU PRINCIPALE --- */
.main-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.top-header {
  background-color: white;
  padding: 20px 30px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  margin-bottom: 20px;
}
.top-header h1 {
  margin: 0;
  font-size: 1.5rem;
  color: var(--text-main);
}

.page-container {
  padding: 0 30px 30px 30px;
  max-width: 1200px;
  width: 100%;
}

/* --- RESPONSIVITÉ POUR TÉLÉPHONES --- */
@media (max-width: 768px) {
  .app-layout { flex-direction: column; }
  .sidebar { width: 100%; height: auto; }
  .sidebar-header { display: flex; justify-content: space-between; align-items: center; }
  .sidebar-cta { padding: 10px 20px; }
  .nav-links { display: flex; flex-wrap: wrap; }
  .nav-links li { width: 50%; }
  .nav-links a { justify-content: center; border-left: none; border-bottom: 3px solid transparent; }
  .nav-links a.active { border-left: none; border-bottom: 3px solid var(--primary-green); }
  .page-container { padding: 0 15px 15px 15px; }
}
</style>