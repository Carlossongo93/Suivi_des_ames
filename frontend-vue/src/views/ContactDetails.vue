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
            ::contact-id="Number(route.params.id)"
            @close="showForm = false" 
            @interaction-added="handleNewInteraction" 
            />
        </div>
        </div>
        <div class="timeline-content">
          <!-- État vide en attendant l'implémentation de la table Interactions -->
          <div class="empty-state">
            <span class="empty-icon">📅</span>
            <p>Aucune interaction enregistrée pour l'instant.</p>
            <span class="text-muted">Commencez le suivi en ajoutant une note.</span>
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
  console.log("Interaction enregistrée avec succès !", newInteractionData);
  // Ici, vous pouvez ajouter l'interaction à votre liste locale pour rafraîchir l'affichage
};

const route = useRoute();
const router = useRouter();

const contact = ref(null);
const isLoading = ref(true);
const error = ref(null);

onMounted(async () => {
  try {
    // Récupération du contact spécifique via l'ID dans l'URL
    const response = await api.get(`/contacts/${route.params.id}`);
    contact.value = response.data;
  } catch (err) {
    error.value = "Impossible de charger les données du contact.";
    console.error(err);
  } finally {
    isLoading.value = false;
  }
});

const goBack = () => router.push('/contacts');
const goToEdit = () => router.push(`/contacts/${contact.value.id}/edit`);

const formatDate = (dateString) => {
  if (!dateString) return '';
  const options = { day: '2-digit', month: 'long', year: 'numeric' };
  return new Date(dateString).toLocaleDateString('fr-FR', options);
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

@media (max-width: 768px) { .details-layout { grid-template-columns: 1fr; } }
</style>