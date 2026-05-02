<template>
  <div class="contact-details-container">
    <header class="page-header">
      <button class="btn-back" @click="goBack">← Retour aux contacts</button>
      <div class="header-title" v-if="contact">
        <h1 class="page-title">{{ contact.firstName }} {{ contact.lastName }}</h1>
        <p class="subtitle">Créé le {{ formatDate(contact.createdAt) }}</p>
      </div>
    </header>

    <div v-if="isLoading" class="loading-state">
      <span class="spinner">⏳</span> Chargement de la fiche...
    </div>
    <div v-else-if="error" class="error-state">
      ⚠️ {{ error }}
    </div>

    <div v-else-if="contact" class="details-layout">
      <!-- Colonne Infos Personnelles -->
      <div class="details-card info-card">
        <div class="card-header">
          <h2>Informations personnelles</h2>
          <button class="btn-icon" title="Modifier" @click="goToEdit">✏️</button>
        </div>
        <div class="info-content">
          <div class="info-item">
            <span class="info-label">Téléphone</span>
            <span class="info-value">{{ contact.phone || 'Non renseigné' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">Quartier / Adresse</span>
            <span class="info-value">{{ contact.address || 'Non assigné' }}</span>
          </div>
        </div>
      </div>

      <!-- Colonne Interactions (Timeline - Préparation pour l'EPIC-3) -->
      <div class="details-card timeline-card">
        <div class="card-header">
          <h2>Historique du suivi</h2>
          <button class="btn-primary btn-sm" @click="showForm = true">➕ Interaction</button>
          <!-- Affichage du formulaire de création si showForm est vrai -->
          <div v-if="showForm" class="modal-overlay">
            <InteractionForm 
            :contact-id="Number(route.params.id)"
            @close="showForm = false" 
            @interaction-added="handleNewInteraction" 
            />
        </div>
        </div>
        <div class="timeline-content">
          
          <!-- S'il n'y a pas d'interactions -->
          <div v-if="interactions.length === 0" class="empty-state">
            <span class="empty-icon">📅</span>
            <p>Aucune interaction enregistrée pour l'instant.</p>
            <span class="text-muted">Commencez le suivi en ajoutant une note.</span>
          </div>

          <!-- S'il y a des interactions -->
          <div v-else class="interactions-list">
            <div v-for="interaction in interactions" :key="interaction.id" class="interaction-item">
              <div class="interaction-header">
                <span class="interaction-type">{{ interaction.type }}</span>
                <!-- Affiche la date, en utilisant interaction.createdAt ou interaction.date selon ce que le backend renvoie -->
                <span class="interaction-date">{{ formatDateTime(interaction.createdAt || interaction.date) }}</span>
              </div>
              <div class="interaction-notes">
                {{ interaction.notes }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../services/api';
import InteractionForm from '../views/InteractionForm.vue';

const showForm = ref(false); // Le fameux état qui manquait à l'appel !
const currentContactId = ref(1); // L'ID du contact actuel

const handleNewInteraction = (newInteractionData) => {
  showForm.value = false; // Ferme la popup
  // On ajoute la nouvelle interaction tout en haut de la liste
  interactions.value.unshift(newInteractionData); 
};

const route = useRoute();
const router = useRouter();

const contact = ref(null);
const isLoading = ref(true);
const error = ref(null);
const interactions = ref([]); // <-- Ajoutez cette ligne sous vos autres const ref()

onMounted(async () => {
  try {
    // 1. Récupération du contact
    const response = await api.get(`/contacts/${route.params.id}`);
    contact.value = response.data;
    
    // 2. Récupération de l'historique des interactions (NOUVEAU)
    const interactionsResponse = await api.get(`/contacts/${route.params.id}/interactions`);
    interactions.value = interactionsResponse.data;
    
  } catch (err) {
    error.value = "Impossible de charger les données.";
    console.error(err);
  } finally {
    isLoading.value = false;
  }
});

const goBack = () => router.push('/contacts');
const goToEdit = () => router.push(`/contacts/${contact.value.id}/edit`);

const parseDate = (dateVal) => {
  if (!dateVal) return null;
  if (Array.isArray(dateVal)) return new Date(dateVal[0], dateVal[1] - 1, dateVal[2], dateVal[3] || 0, dateVal[4] || 0);
  return new Date(dateVal);
};

const formatDateTime = (dateVal) => {
  const date = parseDate(dateVal);
  if (!date || isNaN(date)) return '';
  return date.toLocaleDateString('fr-FR', { day: '2-digit', month: 'long', year: 'numeric' }) 
       + ' à ' + date.toLocaleTimeString('fr-FR', { hour: '2-digit', minute:'2-digit' });
};
</script>

<style scoped>
.contact-details-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 10px;
}

.btn-back {
  background: transparent;
  border: none;
  color: #6b7280;
  cursor: pointer;
  padding: 0;
  font-size: 0.9rem;
  margin-bottom: 12px;
  transition: color 0.2s;
}

.btn-back:hover { color: #1a8f2e; }

.page-title {
  color: #111827;
  margin: 0 0 4px 0;
  font-size: 1.8rem;
}

.subtitle { color: #6b7280; margin: 0; font-size: 0.95rem; }

/* --- MISE EN PAGE : 2 COLONNES --- */
.details-layout {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 24px;
}

.details-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);
  border: 1px solid #e5e7eb;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f3f4f6;
}

.card-header h2 { margin: 0; font-size: 1.2rem; color: #1f2937; }

/* --- COLONNE GAUCHE (INFOS) --- */
.info-content { display: flex; flex-direction: column; gap: 16px; }
.info-item { display: flex; flex-direction: column; gap: 4px; }
.info-label { font-size: 0.85rem; color: #6b7280; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; }
.info-value { font-size: 1rem; color: #111827; }

/* --- COLONNE DROITE (TIMELINE) --- */
.empty-state { text-align: center; padding: 40px 20px; }
.empty-icon { font-size: 3rem; display: block; margin-bottom: 15px; opacity: 0.5; }
.text-muted { color: #9ca3af; font-size: 0.9rem; }

/* --- BOUTONS --- */
.btn-primary.btn-sm { padding: 6px 12px; font-size: 0.85rem; border-radius: 6px; background-color: #1a8f2e; color: white; border: none; cursor: pointer; transition: all 0.2s; }
.btn-primary.btn-sm:hover { background-color: #126620; }
.btn-icon { background: white; border: 1px solid #d1d5db; border-radius: 6px; width: 32px; height: 32px; cursor: pointer; transition: all 0.2s; }
.btn-icon:hover { border-color: #f59e0b; background-color: #fffbeb; }
.loading-state, .error-state { padding: 40px; text-align: center; color: #6b7280; }
.error-state { color: #e62222; }
.interactions-list { display: flex; flex-direction: column; gap: 15px; margin-top: 15px; }
.interaction-item { padding: 15px; border-left: 4px solid #1a8f2e; background: #f9fafb; border-radius: 4px; }
.interaction-header { display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 0.9rem; }
.interaction-type { font-weight: bold; color: #111827; }
.interaction-date { color: #6b7280; font-size: 0.85rem; }
.interaction-notes { color: #374151; line-height: 1.5; font-size: 0.95rem; white-space: pre-wrap; }

@media (max-width: 768px) { .details-layout { grid-template-columns: 1fr; } }
</style>