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
      <span class="spinner">⏳</span> Chargement...
    </div>

    <!-- Ajout de l'affichage de l'erreur -->
    <div v-else-if="error" class="error-state">
      ⚠️ {{ error }}
    </div>

    <div v-else-if="contact" class="details-layout">
      <!-- Colonne Infos Personnelles -->
      <div class="details-card info-card">
        <div class="card-header">
          <h2>Informations</h2>
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
          <div class="info-item">
            <span class="info-label">Dernier Contact</span>
            <span class="info-value">
              <span class="status-badge" :class="contact.lastInteractionAt ? 'badge-green' : 'badge-yellow'">
                {{ contact.lastInteractionAt ? formatDate(contact.lastInteractionAt) : 'Jamais contacté' }}
              </span>
            </span>
          </div>
        </div>
      </div>

      <!-- Colonne Interactions (Timeline) -->
      <div class="details-card timeline-card">
        <div class="card-header">
          <h2>Historique du suivi</h2>
          <button 
            class="btn-primary btn-sm" 
            @click="showInteractionForm = !showInteractionForm"
            :class="{ 'btn-cancel': showInteractionForm }"
          >
            {{ showInteractionForm ? '✖ Annuler' : '➕ Interaction' }}
          </button>
        </div>

        <!-- FORMULAIRE D'INTERACTION (US-3.1) -->
        <div v-if="showInteractionForm" class="interaction-form-box">
          <form @submit.prevent="submitInteraction">
            <div class="form-row-small">
              <div class="input-group">
                <label>Type <span class="required">*</span></label>
                <select v-model="formInteraction.type" required>
                  <option value="APPEL">📞 Appel</option>
                  <option value="SMS">💬 SMS</option>
                  <option value="WHATSAPP">📱 WhatsApp</option>
                  <option value="VISITE">🏠 Visite Physique</option>
                  <option value="EGLISE">⛪ Rencontre à l'Église</option>
                </select>
              </div>
              <div class="input-group">
                <label>Objet / Sous-type</label>
                <select v-model="formInteraction.subType">
                  <option value="Prise de nouvelles">Prise de nouvelles</option>
                  <option value="Prière">Prière / Soutien</option>
                  <option value="Exhortation">Exhortation</option>
                  <option value="Invitation">Invitation (Culte/Cellule)</option>
                  <option value="Autre">Autre</option>
                </select>
              </div>
            </div>
            
            <div class="input-group mt-2">
              <label>Notes / Compte rendu</label>
              <textarea 
                v-model="formInteraction.notes" 
                rows="3" 
                placeholder="Ex: Il va mieux, a demandé qu'on prie pour sa famille..."
              ></textarea>
            </div>

            <div class="form-actions mt-2">
              <button type="submit" class="btn-primary w-100" :disabled="isSaving">
                {{ isSaving ? 'Enregistrement...' : '✔ Enregistrer' }}
              </button>
            </div>
          </form>
        </div>

        <!-- TIMELINE (US-3.2) -->
        <div class="timeline-content">
          <div v-if="interactions.length === 0" class="empty-state">
            <span class="empty-icon">📅</span>
            <p>Aucune interaction enregistrée.</p>
          </div>
          
          <ul v-else class="timeline-list">
            <li v-for="interaction in interactions" :key="interaction.id" class="timeline-item">
              <!-- Icône dynamique selon le type -->
              <div class="timeline-icon" :class="getIconColor(interaction.type)">
                {{ getIcon(interaction.type) }}
              </div>
              
              <div class="timeline-content-box">
                <div class="timeline-box-header">
                  <div>
                    <h4 class="interaction-title">
                      {{ formatType(interaction.type) }}
                      <span v-if="interaction.subType" class="sub-type-badge">{{ interaction.subType }}</span>
                    </h4>
                    <span class="interaction-author">par {{ interaction.createdBy?.email || 'Vous' }}</span>
                  </div>
                  <span class="timeline-date">{{ formatDateTime(interaction.createdAt) }}</span>
                </div>
                <p v-if="interaction.notes" class="interaction-notes">{{ interaction.notes }}</p>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../services/api';

const route = useRoute();
const router = useRouter();

const contact = ref(null);
const interactions = ref([]);
const isLoading = ref(true);
const isSaving = ref(false);
const error = ref(null);

const showInteractionForm = ref(false);
const formInteraction = reactive({
  type: 'APPEL',
  subType: 'Prise de nouvelles',
  notes: ''
});

// --- CHARGEMENT ---
const fetchData = async () => {
  try {
    const contactId = route.params.id;
    // Charge le contact
    const resContact = await api.get(`/contacts/${contactId}`);
    contact.value = resContact.data;
    
    // Charge les interactions
    const resInteractions = await api.get(`/contacts/${contactId}/interactions`);
    interactions.value = resInteractions.data;
  } catch (err) {
    error.value = "Impossible de charger la fiche.";
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchData);

// --- SOUMISSION DE L'INTERACTION ---
const submitInteraction = async () => {
  isSaving.value = true;
  try {
    await api.post(`/contacts/${route.params.id}/interactions`, formInteraction);
    
    // Réinitialise le formulaire et recharge les données
    formInteraction.notes = '';
    showInteractionForm.value = false;
    await fetchData(); // Recharge la timeline en direct
  } catch (err) {
    alert("Erreur lors de l'enregistrement de l'interaction.");
  } finally {
    isSaving.value = false;
  }
};

// --- UTILITAIRES & FORMATAGE ---
const goBack = () => router.push('/contacts');
const goToEdit = () => router.push(`/contacts/${contact.value.id}/edit`);

// --- PARSING DE DATE ROBUSTE ---
const parseDateInput = (dateInput) => {
  if (!dateInput) return null;
  if (Array.isArray(dateInput)) {
    return new Date(dateInput[0], dateInput[1] - 1, dateInput[2], dateInput[3] || 0, dateInput[4] || 0);
  }
  const d = new Date(dateInput);
  return isNaN(d.getTime()) ? null : d;
};

const formatDate = (dateInput) => {
  const date = parseDateInput(dateInput);
  if (!date) return '';
  return date.toLocaleDateString('fr-FR', { day: '2-digit', month: 'long', year: 'numeric' });
};

const formatDateTime = (dateInput) => {
  const date = parseDateInput(dateInput);
  if (!date) return '';
  return date.toLocaleString('fr-FR', { day: '2-digit', month: 'short', hour: '2-digit', minute:'2-digit' });
};

const formatType = (type) => {
  const map = { APPEL: 'Appel', SMS: 'SMS', WHATSAPP: 'WhatsApp', VISITE: 'Visite', EGLISE: 'Église' };
  return map[type] || type;
};

const getIcon = (type) => {
  const map = { APPEL: '📞', SMS: '💬', WHATSAPP: '📱', VISITE: '🏠', EGLISE: '⛪' };
  return map[type] || '📌';
};

const getIconColor = (type) => {
  if (type === 'APPEL') return 'icon-blue';
  if (type === 'WHATSAPP') return 'icon-green';
  if (type === 'VISITE') return 'icon-orange';
  return 'icon-gray';
};
</script>

<style scoped>
.contact-details-container { display: flex; flex-direction: column; gap: 20px; max-width: 1000px; margin: 0 auto; }
.page-header { margin-bottom: 10px; }
.btn-back { background: transparent; border: none; color: #6b7280; cursor: pointer; padding: 0; font-size: 0.9rem; margin-bottom: 12px; transition: color 0.2s; }
.btn-back:hover { color: #1a8f2e; }
.page-title { color: #111827; margin: 0 0 4px 0; font-size: 1.8rem; }
.subtitle { color: #6b7280; margin: 0; font-size: 0.95rem; }

.details-layout { display: grid; grid-template-columns: 1fr 2fr; gap: 24px; align-items: start;}
.details-card { background: white; border-radius: 12px; padding: 24px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); border: 1px solid #e5e7eb; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid #f3f4f6; }
.card-header h2 { margin: 0; font-size: 1.2rem; color: #1f2937; }

/* INFOS */
.info-content { display: flex; flex-direction: column; gap: 16px; }
.info-item { display: flex; flex-direction: column; gap: 4px; }
.info-label { font-size: 0.85rem; color: #6b7280; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; }
.info-value { font-size: 1rem; color: #111827; }
.status-badge { padding: 4px 10px; border-radius: 12px; font-size: 0.8rem; font-weight: bold; }
.badge-green { background-color: #dcfce7; color: #166534; }
.badge-yellow { background-color: #fef9c3; color: #854d0e; }

/* FORMULAIRE INTERACTION */
.interaction-form-box { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 15px; margin-bottom: 25px; border-left: 4px solid #1a8f2e; }
.form-row-small { display: flex; gap: 15px; }
.input-group { flex: 1; display: flex; flex-direction: column; gap: 6px; }
.input-group label { font-size: 0.85rem; font-weight: 600; color: #374151; }
.required { color: #e62222; }
.input-group select, .input-group textarea { padding: 8px 10px; border: 1px solid #d1d5db; border-radius: 6px; font-size: 0.9rem; outline: none; font-family: inherit; }
.input-group select:focus, .input-group textarea:focus { border-color: #1a8f2e; box-shadow: 0 0 0 2px rgba(26, 143, 46, 0.1); }
.mt-2 { margin-top: 15px; }
.w-100 { width: 100%; }

/* TIMELINE */
.timeline-content { position: relative; }
.timeline-list { list-style: none; padding: 0; margin: 0; border-left: 2px solid #e5e7eb; margin-left: 15px; }
.timeline-item { position: relative; padding-left: 30px; margin-bottom: 25px; }
.timeline-item:last-child { margin-bottom: 0; }
.timeline-icon { position: absolute; left: -16px; top: 0; width: 30px; height: 30px; border-radius: 50%; background: white; border: 2px solid #d1d5db; display: flex; align-items: center; justify-content: center; font-size: 0.9rem; }
.icon-blue { border-color: #3b82f6; background-color: #eff6ff; }
.icon-green { border-color: #22c55e; background-color: #f0fdf4; }
.icon-orange { border-color: #f59e0b; background-color: #fffbeb; }

.timeline-content-box { background: #f9fafb; border: 1px solid #e5e7eb; border-radius: 8px; padding: 15px; }
.timeline-box-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 8px; }
.interaction-title { margin: 0; font-size: 1rem; color: #111827; display: flex; align-items: center; gap: 8px; }
.sub-type-badge { background: #e5e7eb; color: #4b5563; font-size: 0.75rem; padding: 2px 6px; border-radius: 4px; font-weight: normal; }
.interaction-author { font-size: 0.8rem; color: #9ca3af; display: block; margin-top: 2px; }
.timeline-date { font-size: 0.8rem; color: #6b7280; white-space: nowrap; }
.interaction-notes { margin: 0; font-size: 0.95rem; color: #4b5563; line-height: 1.5; white-space: pre-wrap; }

/* BOUTONS */
.btn-primary { padding: 8px 16px; font-size: 0.9rem; border-radius: 6px; background-color: #1a8f2e; color: white; border: none; cursor: pointer; transition: all 0.2s; font-weight: bold; }
.btn-primary:hover:not(:disabled) { background-color: #126620; }
.btn-cancel { background-color: white; color: #6b7280; border: 1px solid #d1d5db; }
.btn-cancel:hover { background-color: #f3f4f6; color: #374151; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-icon { background: white; border: 1px solid #d1d5db; border-radius: 6px; width: 32px; height: 32px; cursor: pointer; transition: all 0.2s; }
.btn-icon:hover { border-color: #f59e0b; background-color: #fffbeb; }

/* Ajout du style error-state */
.loading-state, .error-state { padding: 40px; text-align: center; color: #6b7280; }
.error-state { color: #e62222; font-weight: 500; }

.empty-state { text-align: center; padding: 40px 20px; color: #9ca3af; }
.empty-icon { font-size: 3rem; display: block; margin-bottom: 10px; opacity: 0.5; }

@media (max-width: 768px) { .details-layout { grid-template-columns: 1fr; } .form-row-small { flex-direction: column; } }
</style>