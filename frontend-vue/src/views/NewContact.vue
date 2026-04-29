<template>
  <div class="new-contact-container">
    <header class="page-header">
      <div class="header-info">
        <button class="btn-back" @click="goBack">← Retour</button>
        <h1 class="page-title">Ajouter un Contact</h1>
        <p class="subtitle">Créez une nouvelle fiche pour un membre du troupeau.</p>
      </div>
    </header>

    <div class="form-card">
      <form @submit.prevent="submitForm" class="contact-form">
        
        <!-- Message de succès -->
        <div v-if="successMessage" class="alert-success">
          ✅ {{ successMessage }}
        </div>

        <!-- Message d'erreur -->
        <div v-if="errorMessage" class="alert-error">
          ⚠️ {{ errorMessage }}
        </div>

        <div class="form-row">
          <div class="input-group">
            <label for="firstName">Prénom <span class="required">*</span></label>
            <input 
              id="firstName" 
              v-model="form.firstName" 
              type="text" 
              required 
              placeholder="Ex: Jean"
            />
          </div>

          <div class="input-group">
            <label for="lastName">Nom complet <span class="required">*</span></label>
            <input 
              id="lastName" 
              v-model="form.lastName" 
              type="text" 
              required 
              placeholder="Ex: Dupont"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="input-group">
            <label for="phone">Numéro de téléphone</label>
            <input 
              id="phone" 
              v-model="form.phone" 
              type="tel" 
              placeholder="Ex: 06 12 34 56 78"
            />
          </div>

          <div class="input-group">
            <label for="address">Quartier / Adresse</label>
            <input 
              id="address" 
              v-model="form.address" 
              type="text" 
              placeholder="Ex: Centre-Ville"
            />
          </div>
        </div>

        <!-- Le Team ID sera déduit automatiquement par le backend -->
        <div class="info-note">
          ℹ️ Ce contact sera automatiquement assigné à votre équipe.
        </div>

        <div class="form-actions">
          <button type="button" class="btn-secondary" @click="goBack" :disabled="isLoading">Annuler</button>
          <button type="submit" class="btn-primary" :disabled="isLoading">
            {{ isLoading ? 'Enregistrement...' : 'Enregistrer le contact' }}
          </button>
        </div>

      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();

const form = reactive({
  firstName: '',
  lastName: '',
  phone: '',
  address: ''
});

const isLoading = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

const goBack = () => {
  router.push('/contacts');
};

const submitForm = async () => {
  isLoading.value = true;
  errorMessage.value = '';
  successMessage.value = '';

  try {
    // Appel POST vers le backend
    await api.post('/contacts', form);
    
    successMessage.value = "Contact ajouté avec succès ! Redirection...";
    
    // Redirection vers la liste après un court délai pour voir le message
    setTimeout(() => {
      router.push('/contacts');
    }, 1500);

  } catch (error) {
    console.error("Erreur création:", error);
    errorMessage.value = error.response?.data?.message || "Une erreur est survenue lors de l'enregistrement.";
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.new-contact-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 800px; /* On limite la largeur pour un formulaire plus lisible */
  margin: 0 auto; /* Centrage */
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
  margin-bottom: 8px;
  transition: color 0.2s;
}

.btn-back:hover {
  color: #1a8f2e;
}

.page-title {
  color: #111827;
  margin: 0 0 4px 0;
  font-size: 1.8rem;
}

.subtitle {
  color: #6b7280;
  margin: 0;
  font-size: 0.95rem;
}

/* --- FORMULAIRE --- */
.form-card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);
  border: 1px solid #e5e7eb;
}

.contact-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: flex;
  gap: 20px;
}

.input-group {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-group label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #374151;
}

.required {
  color: #e62222;
}

.input-group input {
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.95rem;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.input-group input:focus {
  border-color: #1a8f2e;
  box-shadow: 0 0 0 3px rgba(26, 143, 46, 0.1);
}

.info-note {
  background-color: #eff6ff;
  color: #1d4ed8;
  padding: 12px;
  border-radius: 6px;
  font-size: 0.85rem;
  border-left: 4px solid #3b82f6;
}

/* --- ALERTES --- */
.alert-success {
  background-color: #dcfce7;
  color: #166534;
  padding: 12px;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
}

.alert-error {
  background-color: #fee2e2;
  color: #991b1b;
  padding: 12px;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
}

/* --- BOUTONS --- */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 10px;
  padding-top: 20px;
  border-top: 1px solid #f3f4f6;
}

.btn-secondary {
  background-color: white;
  color: #374151;
  border: 1px solid #d1d5db;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #f9fafb;
  border-color: #9ca3af;
}

.btn-primary {
  background-color: #1a8f2e;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 4px rgba(26, 143, 46, 0.2);
}

.btn-primary:hover:not(:disabled) {
  background-color: #126620;
  transform: translateY(-1px);
}

.btn-primary:disabled, .btn-secondary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* --- RESPONSIVE --- */
@media (max-width: 640px) {
  .form-row {
    flex-direction: column;
    gap: 20px;
  }
}
</style>