<template>
  <div class="app-layout">
    <nav class="sidebar">
      <div class="sidebar-header">
        <h2>Le Troupeau</h2>
        <span class="role-badge" v-if="authStore.user">
          {{ authStore.user.role.replace('ROLE_', '') }}
        </span>
      </div>

      <ul class="nav-links">
        <li>
          <router-link to="/dashboard" active-class="active">
            📊 Tableau de Bord
          </router-link>
        </li>
        <li>
          <router-link to="/contacts" active-class="active">
            👥 Mon Équipe
          </router-link>
        </li>
      </ul>

      <div class="sidebar-footer">
        <div class="user-info" v-if="authStore.user">
          {{ authStore.user.sub }}
        </div>
        <button @click="handleLogout" class="btn-logout">Déconnexion</button>
      </div>
    </nav>

    <main class="main-content">
      <router-view></router-view>
    </main>
  </div>
</template>

<script setup>
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.app-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f9fafb;
}

.sidebar {
  width: 250px;
  background-color: #1f2937;
  color: white;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #374151;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 1.5rem;
}

.role-badge {
  display: inline-block;
  margin-top: 5px;
  background-color: #3b82f6;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: bold;
}

.nav-links {
  list-style: none;
  padding: 0;
  margin: 20px 0;
  flex-grow: 1;
}

.nav-links li {
  margin-bottom: 5px;
}

.nav-links a {
  display: block;
  padding: 15px 20px;
  color: #d1d5db;
  text-decoration: none;
  transition: background-color 0.2s;
}

.nav-links a:hover, .nav-links a.active {
  background-color: #374151;
  color: white;
  border-left: 4px solid #3b82f6;
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid #374151;
}

.user-info {
  font-size: 0.85rem;
  margin-bottom: 10px;
  color: #9ca3af;
  word-break: break-all;
}

.btn-logout {
  width: 100%;
  padding: 8px;
  background-color: #ef4444;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-logout:hover {
  background-color: #dc2626;
}

.main-content {
  flex-grow: 1;
  padding: 30px;
  overflow-y: auto;
}

/* Responsive basique pour Mobile-First */
@media (max-width: 768px) {
  .app-layout {
    flex-direction: column;
  }
  .sidebar {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: space-between;
    align-items: center;
  }
  .nav-links {
    display: flex;
    width: 100%;
    margin: 0;
  }
  .sidebar-footer {
    border-top: none;
    padding: 10px 20px;
  }
}
</style>