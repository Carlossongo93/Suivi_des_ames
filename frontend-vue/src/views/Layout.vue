<template>
  <div class="app-layout">
    <!-- BARRE LATÉRALE -->
    <nav class="sidebar">
      <div class="sidebar-header">
        <div class="brand">
          <img src="../assets/Logo.png" alt="Logo de l'Église" class="church-logo" /> 
          <h2>Le Troupeau</h2>
        </div>
        <!-- Badge de rôle dynamique -->
        <span class="role-badge" v-if="authStore.user && displayRole" :class="roleClass">
          {{ displayRole }}
        </span>
      </div>

      <!-- BOUTON D'ACTION PRINCIPAL -->
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
        
        <li>
          <router-link to="/contacts" active-class="active">
            📒 Mes Contacts
          </router-link>
        </li>

        <!-- Le lien "Mon Équipe" n'est visible que pour LEADER, SUPERVISEUR, ou ADMIN -->
        <li v-if="canViewTeam">
          <router-link to="/team" active-class="active">
            👥 Mon Équipe
          </router-link>
        </li>

        <li v-if="isAdmin">
          <router-link to="/admin" active-class="active">
            ⚙️ Administration
          </router-link>
        </li>
      </ul>

      <!-- PIED DE PAGE (Déconnexion) -->
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
      <!-- Le titre en double a été supprimé pour laisser tes vues gérer leur header -->
      <router-view></router-view>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

// --- GESTION DES RÔLES ---
const userRole = computed(() => authStore.user?.role || '');

const displayRole = computed(() => {
  return userRole.value ? userRole.value.replace('ROLE_', '') : '';
});

// Autorise l'accès au menu "Mon Équipe" pour ces 3 rôles
const canViewTeam = computed(() => ['ROLE_LEADER', 'ROLE_SUPERVISEUR', 'ROLE_ADMIN'].includes(userRole.value));
const isAdmin = computed(() => userRole.value === 'ROLE_ADMIN');

// Détermine la couleur du badge en fonction du rôle
const roleClass = computed(() => {
  if (isAdmin.value) return 'badge-admin';
  if (userRole.value === 'ROLE_SUPERVISEUR') return 'badge-supervisor';
  if (userRole.value === 'ROLE_LEADER') return 'badge-leader';
  return 'badge-member';
});

// --- ACTIONS ---
const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};

const goToNewContact = () => {
  router.push('/contacts/new'); 
};
</script>

<style scoped>
/* Reset pour éviter les débordements */
* {
  box-sizing: border-box;
}

.app-layout {
  display: flex;
  min-height: 100vh;
  width: 100vw;
  background-color: #f3f4f6; /* Gris très clair pour le fond de l'application */
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  overflow-x: hidden;
}

/* NOUVEAUX STYLES POUR LE LOGO OFFICIEL */
.church-logo {
  width: 60px; /* Vous pouvez augmenter ou réduire cette valeur (ex: 100px) */
  height: auto;
  margin-bottom: 16px;
  object-fit: contain;
  /* Décommentez les lignes ci-dessous si vous voulez que le logo soit dans un cercle parfait (idéal pour les logos carrés) */
  /* border-radius: 50%; */
  /* box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); */
}

/* --- BARRE LATÉRALE (SIDEBAR) --- */
.sidebar {
  width: 260px;
  flex-shrink: 0; /* Empêche la sidebar de se compresser */
  background-color: #111827; /* Bleu nuit très profond */
  color: #f9fafb;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 10px rgba(0,0,0,0.1);
  z-index: 10;
}

.sidebar-header {
  padding: 24px 20px;
  border-bottom: 1px solid #374151;
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
  color: #ffffff;
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
  text-transform: uppercase;
}
/* Couleurs des rôles */
.badge-admin { background-color: #e62222; color: white; }
.badge-supervisor { background-color: #f97316; color: white; } /* Orange vif */
.badge-leader { background-color: #f59e0b; color: white; } /* Orange doux */
.badge-member { background-color: #1a8f2e; color: white; } /* Vert */

/* --- BOUTON NOUVEAU CONTACT --- */
.sidebar-cta {
  padding: 20px;
}

.btn-primary {
  width: 100%;
  padding: 12px;
  background-color: #1a8f2e; /* Vert principal de ton logo */
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
  background-color: #126620; /* Vert plus foncé au survol */
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
  color: #9ca3af; /* Gris clair */
  text-decoration: none;
  font-size: 0.95rem;
  transition: all 0.2s;
  border-left: 4px solid transparent;
}

.nav-links a:hover {
  background-color: #1f2937;
  color: white;
}

.nav-links a.active {
  background-color: #1f2937;
  color: white;
  border-left: 4px solid #1a8f2e; /* Ligne verte pour le menu actif */
  font-weight: 600;
}

/* --- PIED DE PAGE SIDEBAR --- */
.sidebar-footer {
  padding: 20px;
  border-top: 1px solid #374151;
  background-color: #0f172a;
}

.user-email {
  display: block;
  font-size: 0.85rem;
  color: #9ca3af;
  margin-bottom: 15px;
  word-break: break-all;
}

.btn-logout {
  width: 100%;
  padding: 10px;
  background-color: transparent;
  color: #9ca3af;
  border: 1px solid #4b5563;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
}

.btn-logout:hover {
  background-color: rgba(230, 34, 34, 0.1);
  color: #e62222; /* Rouge du logo au survol */
  border-color: #e62222;
}

/* --- ZONE DE CONTENU PRINCIPALE --- */
.main-content {
  flex-grow: 1;
  padding: 2rem;
  overflow-y: auto;
}

/* --- RESPONSIVITÉ (Mobiles) --- */
@media (max-width: 768px) {
  .app-layout { flex-direction: column; }
  .sidebar { width: 100%; height: auto; flex-shrink: 1; }
  .sidebar-header { display: flex; justify-content: space-between; align-items: center; }
  .sidebar-cta { padding: 10px 20px; }
  .nav-links { display: flex; flex-wrap: wrap; }
  .nav-links li { width: 50%; }
  .nav-links a { justify-content: center; border-left: none; border-bottom: 3px solid transparent; }
  .nav-links a.active { border-left: none; border-bottom: 3px solid #1a8f2e; }
  .main-content { padding: 1rem; }
}
</style>