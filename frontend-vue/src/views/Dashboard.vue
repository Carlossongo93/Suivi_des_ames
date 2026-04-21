<template>
  <div>
    <h1 class="page-title">Tableau de Bord</h1>
    <p class="welcome-text">Bienvenue dans votre espace Le Troupeau.</p>

    <div class="stats-grid">
      <div class="stat-card">
        <h3>Total Équipe</h3>
        <div class="stat-value">{{ contactsCount }}</div>
        <div class="stat-label">Membres assignés</div>
      </div>
      
      <div class="stat-card highlight">
        <h3>À Contacter</h3>
        <div class="stat-value">3</div>
        <div class="stat-label">Pas de nouvelles depuis 30j</div>
      </div>

      <div class="stat-card">
        <h3>Dernières actions</h3>
        <div class="stat-value">12</div>
        <div class="stat-label">Interactions cette semaine</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const contactsCount = ref(0);

// On charge une donnée réelle (le nombre de contacts) pour illustrer la connexion API
onMounted(async () => {
  try {
    const response = await api.get('/contacts');
    contactsCount.value = response.data.length;
  } catch (error) {
    console.error("Erreur lors de la récupération des stats", error);
  }
});
</script>

<style scoped>
.page-title {
  color: #111827;
  margin-top: 0;
}
.welcome-text {
  color: #4b5563;
  margin-bottom: 30px;
}
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}
.stat-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  border-left: 4px solid #3b82f6;
}
.stat-card.highlight {
  border-left-color: #f59e0b;
}
.stat-card h3 {
  margin: 0 0 10px 0;
  font-size: 1rem;
  color: #6b7280;
}
.stat-value {
  font-size: 2.5rem;
  font-weight: bold;
  color: #111827;
}
.stat-label {
  font-size: 0.85rem;
  color: #9ca3af;
  margin-top: 5px;
}
</style>