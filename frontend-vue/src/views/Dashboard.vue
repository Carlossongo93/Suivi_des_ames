<template>
  <div class="dashboard-container">
    <header class="dashboard-header">
      <div>
        <h1 class="page-title">Tableau de Bord</h1>
        <p class="welcome-text">Bienvenue, voici l'état de santé actuel de votre troupeau.</p>
      </div>
    </header>

    <div v-if="isLoading" class="loading-state">
      <span class="spinner-inline"></span> Analyse de votre troupeau en cours...
    </div>

    <div v-else>
      <!-- LIGNE 1 : LES KPIs (Widgets) -->
      <div class="stats-grid">
        <!-- Widget 1 : Total -->
        <div class="stat-card">
          <div class="stat-icon bg-blue">👥</div>
          <div class="stat-content">
            <h3>Total Contacts</h3>
            <div class="stat-value">{{ kpiTotalContacts }}</div>
            <div class="stat-label">Assignés à votre équipe</div>
          </div>
        </div>
        
        <!-- Widget 2 : Urgences (En rouge) -->
        <div class="stat-card highlight-danger">
          <div class="stat-icon bg-red">⚠️</div>
          <div class="stat-content">
            <h3>À Contacter</h3>
            <div class="stat-value text-red">{{ kpiToContact }}</div>
            <div class="stat-label">Inactifs > 30j ou jamais contactés</div>
          </div>
        </div>

        <!-- Widget 3 : Récents (En vert) -->
        <div class="stat-card highlight-success">
          <div class="stat-icon bg-green">📞</div>
          <div class="stat-content">
            <h3>Suivis Récents</h3>
            <div class="stat-value text-green">{{ kpiActiveThisWeek }}</div>
            <div class="stat-label">Contacts joints ces 7 derniers jours</div>
          </div>
        </div>
      </div>

      <!-- LIGNE 2 : GRAPHIQUE ET URGENCES -->
      <div class="dashboard-main-row mt-4">
        
        <!-- Bloc Gauche : Graphique Santé du Troupeau -->
        <div class="dashboard-card chart-card">
          <div class="card-header">
            <h2>Santé du Troupeau</h2>
            <span class="subtitle">Répartition de vos contacts par statut d'activité</span>
          </div>
          
          <div class="css-chart-container">
            <!-- Génération des barres du graphique -->
            <div class="chart-bar-wrapper" v-for="data in chartData" :key="data.label">
              <div class="chart-value-label">{{ data.value }}</div>
              <div class="chart-bar-bg">
                <div 
                  class="chart-bar-fill" 
                  :style="{ height: (data.value / maxChartValue * 100) + '%', backgroundColor: data.color }"
                  :title="data.value + ' contacts ' + data.label"
                ></div>
              </div>
              <span class="chart-label">{{ data.label }}</span>
            </div>
          </div>
        </div>

        <!-- Bloc Droite : Liste des contacts urgents -->
        <div class="dashboard-card urgencies-card">
          <div class="card-header">
            <h2>Suivi Prioritaire</h2>
            <span class="subtitle">Les âmes nécessitant une attention urgente</span>
          </div>
          
          <ul class="urgent-list">
            <li v-for="contact in urgentContactsList" :key="contact.id" class="urgent-item">
              <div class="urgent-info">
                <strong>{{ contact.firstName }} {{ contact.lastName }}</strong>
                <span class="days-alert">
                  ⏳ {{ contact.isNeverContacted ? 'Jamais contacté' : `Inactif depuis ${contact.daysInactive} j` }}
                </span>
              </div>
              <button class="btn-small-action" @click="goToContact(contact.id)">
                Noter
              </button>
            </li>
            
            <!-- Si la liste est vide -->
            <li v-if="urgentContactsList.length === 0" class="empty-state">
              🎉 Alléluia ! Tout votre troupeau est à jour de suivi.
            </li>
          </ul>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();
const contacts = ref([]);
const isLoading = ref(true);

// --- FONCTION UTILITAIRE : Calculer l'écart en jours (Ultra-Robuste) ---
const getDaysSince = (dateInput) => {
  if (!dateInput) return Infinity;
  
  let date;
  // Sécurité 1 : Spring Boot sérialise parfois les dates en tableau [Année, Mois, Jour, Heure, Minute]
  if (Array.isArray(dateInput)) {
    // Attention JS : Les mois commencent à 0 (0 = Janvier), donc on fait -1 sur le mois
    date = new Date(dateInput[0], dateInput[1] - 1, dateInput[2], dateInput[3] || 0, dateInput[4] || 0);
  } else {
    date = new Date(dateInput);
  }

  // Sécurité 2 : Si la date est invalide
  if (isNaN(date.getTime())) return Infinity;

  const now = new Date();
  // On ignore les heures pour avoir une différence en jours francs
  now.setHours(0, 0, 0, 0);
  date.setHours(0, 0, 0, 0);

  const diffTime = now - date;
  return Math.max(0, Math.floor(diffTime / (1000 * 60 * 60 * 24))); 
};

// --- CHARGEMENT DES DONNÉES RÉELLES ---
onMounted(async () => {
  try {
    const response = await api.get('/contacts');
    // Sécurité 3 : S'assurer qu'on reçoit bien un tableau pour éviter le crash des calculs
    contacts.value = Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error("Erreur lors de la récupération des données du dashboard :", error);
    contacts.value = []; // Fallback sécurisé en cas de plantage réseau
  } finally {
    isLoading.value = false;
  }
});

// --- CALCUL DES KPIs ---
const kpiTotalContacts = computed(() => contacts.value.length);

const kpiToContact = computed(() => {
  return contacts.value.filter(c => {
    const daysSinceInteraction = getDaysSince(c.lastInteractionAt);
    // Urgent si inactif > 30j OU (jamais contacté ET créé il y a plus de 7j)
    return daysSinceInteraction > 30 || (daysSinceInteraction === Infinity && getDaysSince(c.createdAt) > 7);
  }).length;
});

const kpiActiveThisWeek = computed(() => {
  return contacts.value.filter(c => getDaysSince(c.lastInteractionAt) <= 7).length;
});

// --- LISTE DE SUIVI PRIORITAIRE (Top 5) ---
const urgentContactsList = computed(() => {
  let urgentList = contacts.value.map(c => {
    let days = getDaysSince(c.lastInteractionAt);
    let isNeverContacted = false;
    
    if (days === Infinity) {
      days = getDaysSince(c.createdAt);
      isNeverContacted = true;
    }
    return { ...c, daysInactive: days, isNeverContacted };
  }).filter(c => c.daysInactive > 30 || (c.isNeverContacted && c.daysInactive > 7));

  // Tri décroissant (le plus grand nombre de jours d'inactivité en premier)
  urgentList.sort((a, b) => b.daysInactive - a.daysInactive);
  
  return urgentList.slice(0, 5); // Ne garder que les 5 plus critiques
});

// --- GRAPHIQUE SANTÉ DU TROUPEAU ---
const chartData = computed(() => {
  let actifs = 0;    // <= 14j
  let aRelancer = 0; // 15j à 30j
  let perdus = 0;    // > 30j
  let nouveaux = 0;  // Jamais contacté, créé il y a <= 7j

  contacts.value.forEach(c => {
    const daysInt = getDaysSince(c.lastInteractionAt);
    if (daysInt === Infinity) {
      if (getDaysSince(c.createdAt) <= 7) nouveaux++;
      else perdus++; // Consolidé avec les urgents/perdus de vue
    } else {
      if (daysInt <= 14) actifs++;
      else if (daysInt <= 30) aRelancer++;
      else perdus++;
    }
  });

  return [
    { label: 'Nouveaux', value: nouveaux, color: '#3b82f6' }, // Bleu
    { label: 'Actifs', value: actifs, color: '#1a8f2e' },     // Vert
    { label: 'À relancer', value: aRelancer, color: '#f59e0b' },// Jaune/Orange
    { label: 'Urgents', value: perdus, color: '#e62222' }     // Rouge
  ];
});

// Échelle du graphique (évite une division par 0)
const maxChartValue = computed(() => {
  return Math.max(...chartData.value.map(d => d.value), 1);
});

// --- NAVIGATION ---
const goToContact = (id) => {
  router.push(`/contacts/${id}`);
};
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

.mt-4 {
  margin-top: 1rem;
}

/* --- ETAT DE CHARGEMENT --- */
.loading-state {
  display: flex;
  align-items: center;
  gap: 12px;
  background: white;
  padding: 24px;
  border-radius: 12px;
  color: #6b7280;
  font-weight: 500;
  border: 1px solid #e5e7eb;
}

.spinner-inline {
  width: 20px;
  height: 20px;
  border: 3px solid #f3f4f6;
  border-top-color: #1a8f2e;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

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

/* --- GRAPHIQUE CSS NATIF : SANTÉ --- */
.css-chart-container {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  height: 220px;
  padding-top: 10px;
  border-bottom: 2px solid #e5e7eb;
}

.chart-bar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  width: 60px;
  height: 100%;
}

.chart-value-label {
  font-size: 1rem;
  font-weight: bold;
  color: #4b5563;
}

.chart-bar-bg {
  width: 40px;
  flex-grow: 1;
  background-color: #f3f4f6;
  border-radius: 6px 6px 0 0;
  display: flex;
  align-items: flex-end;
}

.chart-bar-fill {
  width: 100%;
  border-radius: 6px 6px 0 0;
  transition: height 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

.chart-label {
  font-size: 0.85rem;
  color: #6b7280;
  font-weight: 600;
  text-align: center;
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
  padding: 30px 20px;
  font-weight: 500;
  font-size: 0.95rem;
  background-color: #f0fdf4;
  border-radius: 8px;
  border: 1px dashed #bbf7d0;
}

/* --- RESPONSIVE MOBILE --- */
@media (max-width: 1024px) {
  .dashboard-main-row {
    grid-template-columns: 1fr;
  }
}
</style>