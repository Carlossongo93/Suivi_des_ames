<template>
  <div class="team-container">
    <header class="page-header">
      <div class="header-info">
        <h1 class="page-title">Performances de l'équipe</h1>
        <p class="subtitle">Vue d'ensemble des suivis et des membres</p>
      </div>
    </header>

    <div class="table-card">
      <div v-if="isLoading" class="loading-state">
        <span class="spinner">⏳</span> Chargement des données de l'équipe...
      </div>

      <div v-else-if="error" class="error-state">
        ⚠️ {{ error }}
      </div>

      <div v-else class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>Membre</th>
              <th>Équipe / Secteur</th>
              <th>Rôle</th>
              <th>Âmes en suivi</th>
              <th class="text-right">Progression</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="member in teamMembers" :key="member.id">
              <td>
                <div class="member-name">
                  <strong>{{ member.lastName }}</strong> {{ member.firstName }}
                  <span class="member-email">{{ member.email }}</span>
                </div>
              </td>
              <td>
                <span class="team-tag">{{ member.teamName }}</span>
              </td>
              <td>
                <span class="role-badge" :class="getRoleClass(member.role)">
                  {{ member.role }}
                </span>
              </td>
              <td>
                <span class="count-badge">{{ member.contactsCount }}</span> contacts
              </td>
              <td class="text-right">
                <!-- Petite jauge visuelle simple basée sur le nombre de contacts -->
                <div class="progress-bar-bg">
                  <div class="progress-bar-fill" :style="{ width: Math.min(member.contactsCount * 2, 100) + '%' }"></div>
                </div>
              </td>
            </tr>
            <tr v-if="teamMembers.length === 0">
              <td colspan="5" class="empty-state">
                Aucun membre trouvé dans cette équipe.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const teamMembers = ref([]);
const isLoading = ref(true);
const error = ref(null);

onMounted(async () => {
  try {
    const response = await api.get('/team/dashboard');
    teamMembers.value = response.data;
  } catch (err) {
    error.value = "Impossible de charger les données de l'équipe. Vérifiez vos droits d'accès.";
    console.error(err);
  } finally {
    isLoading.value = false;
  }
});

// Esthétique des rôles
const getRoleClass = (role) => {
  if (role === 'ADMIN') return 'badge-purple';
  if (role === 'SUPERVISEUR') return 'badge-orange';
  if (role === 'LEADER') return 'badge-blue';
  return 'badge-gray'; // MEMBER
};
</script>

<style scoped>
.team-container { display: flex; flex-direction: column; gap: 20px; max-width: 1200px; margin: 0 auto; }
.page-header { margin-bottom: 20px; }
.page-title { color: #111827; margin: 0 0 4px 0; font-size: 1.8rem; }
.subtitle { color: #6b7280; margin: 0; font-size: 0.95rem; }

.table-card { background: white; border-radius: 10px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); border: 1px solid #e5e7eb; overflow: hidden; }
.table-responsive { overflow-x: auto; }
.data-table { width: 100%; border-collapse: collapse; text-align: left; }
.data-table th, .data-table td { padding: 16px 20px; border-bottom: 1px solid #f3f4f6; white-space: nowrap; }
.data-table th { background-color: #f9fafb; color: #4b5563; font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.05em; }

.member-name { display: flex; flex-direction: column; }
.member-name strong { color: #111827; font-size: 1rem; }
.member-email { font-size: 0.8rem; color: #6b7280; margin-top: 2px; }

.team-tag { background-color: #f3f4f6; color: #374151; padding: 4px 8px; border-radius: 4px; font-size: 0.85rem; font-weight: 500; }
.count-badge { background-color: #1a8f2e; color: white; padding: 2px 8px; border-radius: 12px; font-weight: bold; font-size: 0.85rem; }

/* Badges de Rôle */
.role-badge { padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; font-weight: bold; }
.badge-purple { background-color: #f3e8ff; color: #7e22ce; }
.badge-orange { background-color: #ffedd5; color: #c2410c; }
.badge-blue { background-color: #dbeafe; color: #1d4ed8; }
.badge-gray { background-color: #f3f4f6; color: #4b5563; }

/* Jauge de progression visuelle */
.progress-bar-bg { background-color: #e5e7eb; height: 8px; border-radius: 4px; width: 100px; overflow: hidden; display: inline-block; vertical-align: middle; }
.progress-bar-fill { background-color: #1a8f2e; height: 100%; transition: width 0.5s ease; }

.text-right { text-align: right; }
.loading-state, .error-state, .empty-state { padding: 40px; text-align: center; color: #6b7280; }
.error-state { color: #e62222; }
</style>