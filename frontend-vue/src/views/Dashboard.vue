<template>
  <div class="dashboard-container">
    <header class="dashboard-header">
      <div>
        <h1 class="page-title">Tableau de Bord</h1>
        <p class="welcome-text">Bienvenue, voici un résumé de l'activité de votre troupeau.</p>
      </div>
    </header>

    <!-- LIGNE 1 : LES KPIs (Widgets) -->
    <div class="stats-grid">
      <!-- Widget 1 : Total -->
      <div class="stat-card">
        <div class="stat-icon bg-blue">👥</div>
        <div class="stat-content">
          <h3>Total Contacts</h3>
          <div class="stat-value">{{ kpiTotalContacts }}</div>
          <div class="stat-label">Assignés à vous / votre équipe</div>
        </div>
      </div>
      
      <!-- Widget 2 : Urgences (En rouge) -->
      <div class="stat-card highlight-danger">
        <div class="stat-icon bg-red">⚠️</div>
        <div class="stat-content">
          <h3>À Contacter</h3>
          <div class="stat-value text-red">{{ kpiToContact }}</div>
          <div class="stat-label">Pas de nouvelles > 30j</div>
        </div>
      </div>

      <!-- Widget 3 : Activité (En vert) -->
      <div class="stat-card highlight-success">
        <div class="stat-icon bg-green">📞</div>
        <div class="stat-content">
          <h3>Actions Récentes</h3>
          <div class="stat-value text-green">{{ kpiInteractions }}</div>
          <div class="stat-label">Interactions cette semaine</div>
        </div>
      </div>
    </div>

    <!-- LIGNE 2 : GRAPHIQUE ET URGENCES -->
    <div class="dashboard-main-row">
      
      <!-- Bloc Gauche : Graphique CSS natif (US-4.1) -->
      <div class="dashboard-card chart-card">
        <div class="card-header">
          <h2>Activité (30 derniers jours)</h2>
          <span class="subtitle">Nombre d'interactions</span>
        </div>
        
        <div class="css-chart-container">
          <!-- Génération des barres du graphique -->
          <div class="chart-bar-wrapper" v-for="data in chartData" :key="data.label">
            <div class="chart-bar-bg">
              <div 
                class="chart-bar-fill" 
                :style="{ height: (data.value / maxChartValue * 100) + '%' }"
                :title="data.value + ' interactions'"
              ></div>
            </div>
            <span class="chart-label">{{ data.label }}</span>
          </div>
        </div>
      </div>

      <!-- Bloc Droite : Liste des contacts urgents (US-2.2) -->
      <div class="dashboard-card urgencies-card">
        <div class="card-header">
          <h2>Suivi Prioritaire</h2>
          <span class="subtitle">Contacts inactifs</span>
        </div>
        
        <ul class="urgent-list">
          <li v-for="contact in urgentContacts" :key="contact.id" class="urgent-item">
            <div class="urgent-info">
              <strong>{{ contact.name }}</strong>
              <span class="days-alert">⏳ {{ contact.daysSince }} jours</span>
            </div>
            <button class="btn-small-action">Noter</button>
          </li>
          <!-- Si la liste est vide -->
          <li v-if="urgentContacts.length === 0" class="empty-state">
            🎉 Tout le monde a été contacté récemment !
          </li>
        </ul>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import api from '../services/api';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();

// --- DONNÉES REACTIVES ---
const kpiTotalContacts = ref(0);
const kpiToContact = ref(0);
const kpiInteractions = ref(0);

// Données simulées pour le graphique (À relier à une future API /api/stats)
const chartData = ref([
  { label: 'S1', value: 12 },
  { label: 'S2', value: 19 },
  { label: 'S3', value: 8 },
  { label: 'S4', value: 24 },
  { label: 'S5', value: 15 }
]);

// Données simulées pour la liste d'urgence
const urgentContacts = ref([
  { id: 1, name: 'Jean Dupont', daysSince: 34 },
  { id: 2, name: 'Marie Curie', daysSince: 41 },
  { id: 3, name: 'Paul Martin', daysSince: 56 }
]);

// Calcul de la valeur max pour proportionner les barres du graphique CSS
const maxChartValue = computed(() => {
  return Math.max(...chartData.value.map(d => d.value), 1); // Evite division par zéro
});

// --- CHARGEMENT DES DONNÉES ---
onMounted(async () => {
  try {
    // Récupération des vrais contacts depuis l'API pour alimenter le compteur total
    const response = await api.get('/contacts');
    const contacts = response.data;
    
    kpiTotalContacts.value = contacts.length;
    
    // NOTE : Plus tard, nous calculerons kpiToContact en regardant 
    // la date de "last_interaction_at" de chaque contact renvoyé par l'API.
    kpiToContact.value = urgentContacts.value.length; // Temporaire
    kpiInteractions.value = 45; // Temporaire

  } catch (error) {
    console.error("Erreur lors de la récupération des statistiques :", error);
  }
});
</script>

<style scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.dashboard-header {
  margin-bottom: 8px;
}

.page-title {
  color: #111827;
  margin: 0 0 4px 0;
  font-size: 1.8rem;
}

.welcome-text {
  color: #6b7280;
  margin: 0;
  font-size: 1rem;
}

/* --- KPIs GRID --- */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);
  display: flex;
  align-items: center;
  gap: 20px;
  border: 1px solid #f3f4f6;
  border-left: 5px solid #3b82f6; /* Bleu par défaut */
}

.stat-card.highlight-danger { border-left-color: #e62222; }
.stat-card.highlight-success { border-left-color: #1a8f2e; }

.stat-icon {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
}
.bg-blue { background-color: #eff6ff; }
.bg-red { background-color: #fef2f2; }
.bg-green { background-color: #f0fdf4; }

.stat-content h3 {
  margin: 0 0 4px 0;
  font-size: 0.95rem;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-value {
  font-size: 2rem;
  font-weight: 800;
  color: #111827;
  line-height: 1.2;
}
.text-red { color: #e62222; }
.text-green { color: #1a8f2e; }

.stat-label {
  font-size: 0.85rem;
  color: #9ca3af;
  margin-top: 4px;
}

/* --- MAIN ROW (Graphique + Urgences) --- */
.dashboard-main-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

.dashboard-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);
  border: 1px solid #f3f4f6;
}

.card-header {
  margin-bottom: 20px;
}

.card-header h2 {
  margin: 0;
  font-size: 1.2rem;
  color: #1f2937;
}

.card-header .subtitle {
  font-size: 0.85rem;
  color: #9ca3af;
}

/* --- GRAPHIQUE CSS NATIF --- */
.css-chart-container {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  height: 200px;
  padding-top: 20px;
  border-bottom: 2px solid #e5e7eb;
}

.chart-bar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  width: 40px;
  height: 100%;
}

.chart-bar-bg {
  width: 100%;
  flex-grow: 1;
  background-color: #f3f4f6;
  border-radius: 6px 6px 0 0;
  display: flex;
  align-items: flex-end;
}

.chart-bar-fill {
  width: 100%;
  background-color: #1a8f2e; /* Vert de ta charte */
  border-radius: 6px 6px 0 0;
  transition: height 0.5s ease-out;
}

.chart-bar-fill:hover {
  background-color: #126620;
}

.chart-label {
  font-size: 0.85rem;
  color: #6b7280;
  font-weight: 500;
}

/* --- LISTE DES URGENCES --- */
.urgent-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.urgent-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f3f4f6;
}

.urgent-item:last-child {
  border-bottom: none;
}

.urgent-info {
  display: flex;
  flex-direction: column;
}

.urgent-info strong {
  color: #1f2937;
  font-size: 0.95rem;
}

.days-alert {
  font-size: 0.8rem;
  color: #e62222;
  font-weight: 600;
  margin-top: 2px;
}

.btn-small-action {
  background-color: #fef2f2;
  color: #e62222;
  border: 1px solid #fecaca;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-small-action:hover {
  background-color: #e62222;
  color: white;
}

.empty-state {
  text-align: center;
  color: #1a8f2e;
  padding: 20px 0;
  font-weight: 500;
}

/* --- RESPONSIVE MOBILE --- */
@media (max-width: 1024px) {
  .dashboard-main-row {
    grid-template-columns: 1fr;
  }
}
</style>