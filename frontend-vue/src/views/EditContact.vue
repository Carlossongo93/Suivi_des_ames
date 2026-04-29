<template>
  <div class="edit-contact-container">
    <header class="page-header">
      <button class="btn-back" @click="goBack">← Retour</button>
      <h1 class="page-title">Modifier le contact</h1>
    </header>

    <div class="form-card">
      <div v-if="isLoadingFetch" class="loading-state">
        <span class="spinner">⏳</span> Chargement des données...
      </div>
      
      <div v-else-if="errorFetch" class="error-state">
        ⚠️ {{ errorFetch }}
      </div>

      <form v-else @submit.prevent="submitForm" class="contact-form">
        <!-- Message de succès -->
        <div v-if="successMessage" class="alert-success">✅ {{ successMessage }}</div>
        <!-- Message d'erreur -->
        <div v-if="errorMessage" class="alert-error">⚠️ {{ errorMessage }}</div>

        <div class="form-row">
          <div class="input-group">
            <label>Prénom <span class="required">*</span></label>
            <input v-model="form.firstName" type="text" required />
          </div>
          <div class="input-group">
            <label>Nom complet <span class="required">*</span></label>
            <input v-model="form.lastName" type="text" required />
          </div>
        </div>

        <div class="form-row">
          <div class="input-group">
            <label>Numéro de téléphone</label>
            <input v-model="form.phone" type="tel" />
          </div>
          <div class="input-group">
            <label>Quartier / Adresse</label>
            <input v-model="form.address" type="text" />
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="btn-secondary" @click="goBack" :disabled="isLoadingSubmit">Annuler</button>
          <button type="submit" class="btn-primary" :disabled="isLoadingSubmit">
            {{ isLoadingSubmit ? 'Enregistrement...' : 'Mettre à jour' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../services/api';

const route = useRoute();
const router = useRouter();

const form = reactive({
  firstName: '',
  lastName: '',
  phone: '',
  address: ''
});

const isLoadingFetch = ref(true);
const isLoadingSubmit = ref(false);
const errorFetch = ref('');
const errorMessage = ref('');
const successMessage = ref('');

onMounted(async () => {
  try {
    // On va chercher le contact actuel pour pré-remplir le formulaire
    const response = await api.get(`/contacts/${route.params.id}`);
    
    // Assigne les valeurs de la BDD directement à notre formulaire réactif
    Object.assign(form, response.data);
  } catch (err) {
    errorFetch.value = "Impossible de charger les données du contact.";
  } finally {
    isLoadingFetch.value = false;
  }
});

const submitForm = async () => {
  isLoadingSubmit.value = true;
  errorMessage.value = '';
  successMessage.value = '';

  try {
    // Requête PUT pour mettre à jour
    await api.put(`/contacts/${route.params.id}`, form);
    
    successMessage.value = "Fiche contact mise à jour avec succès !";
    setTimeout(() => router.push('/contacts'), 1500);
  } catch (error) {
    errorMessage.value = error.response?.data?.message || "Une erreur est survenue lors de la mise à jour.";
  } finally {
    isLoadingSubmit.value = false;
  }
};

const goBack = () => router.push('/contacts');
</script>

<style scoped>
/* Identique à NewContact.vue pour maintenir la cohérence de l'interface */
.edit-contact-container { display: flex; flex-direction: column; gap: 20px; max-width: 800px; margin: 0 auto; }
.page-header { margin-bottom: 10px; }
.btn-back { background: transparent; border: none; color: #6b7280; cursor: pointer; padding: 0; font-size: 0.9rem; margin-bottom: 8px; transition: color 0.2s; }
.btn-back:hover { color: #1a8f2e; }
.page-title { color: #111827; margin: 0 0 4px 0; font-size: 1.8rem; }
.form-card { background: white; padding: 30px; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); border: 1px solid #e5e7eb; }
.contact-form { display: flex; flex-direction: column; gap: 20px; }
.form-row { display: flex; gap: 20px; }
.input-group { flex: 1; display: flex; flex-direction: column; gap: 8px; }
.input-group label { font-size: 0.9rem; font-weight: 600; color: #374151; }
.required { color: #e62222; }
.input-group input { padding: 10px 12px; border: 1px solid #d1d5db; border-radius: 6px; font-size: 0.95rem; outline: none; transition: border-color 0.2s; }
.input-group input:focus { border-color: #1a8f2e; box-shadow: 0 0 0 3px rgba(26, 143, 46, 0.1); }
.alert-success { background-color: #dcfce7; color: #166534; padding: 12px; border-radius: 6px; font-size: 0.9rem; font-weight: 500; }
.alert-error, .error-state { background-color: #fee2e2; color: #991b1b; padding: 12px; border-radius: 6px; font-size: 0.9rem; font-weight: 500; }
.loading-state { padding: 40px; text-align: center; color: #6b7280; }
.form-actions { display: flex; justify-content: flex-end; gap: 15px; margin-top: 10px; padding-top: 20px; border-top: 1px solid #f3f4f6; }
.btn-secondary { background-color: white; color: #374151; border: 1px solid #d1d5db; padding: 10px 20px; border-radius: 8px; font-weight: 600; cursor: pointer; transition: all 0.2s; }
.btn-secondary:hover:not(:disabled) { background-color: #f9fafb; border-color: #9ca3af; }
.btn-primary { background-color: #1a8f2e; color: white; border: none; padding: 10px 24px; border-radius: 8px; font-weight: bold; cursor: pointer; transition: all 0.2s; box-shadow: 0 2px 4px rgba(26, 143, 46, 0.2); }
.btn-primary:hover:not(:disabled) { background-color: #126620; transform: translateY(-1px); }
.btn-primary:disabled, .btn-secondary:disabled { opacity: 0.6; cursor: not-allowed; }
@media (max-width: 640px) { .form-row { flex-direction: column; gap: 20px; } }
</style>