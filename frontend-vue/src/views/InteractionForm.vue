<template>
  <div class="interaction-form-wrapper">
    <h3>Enregistrer une nouvelle interaction</h3>
    
    <!-- Affichage des erreurs éventuelles -->
    <div v-if="errorMessage" class="error-alert">
      {{ errorMessage }}
    </div>

    <form @submit.prevent="submitInteraction" class="interaction-form">
      
      <!-- Champ : Type d'interaction -->
      <div class="form-group">
        <label for="type">Type d'interaction</label>
        <select id="type" v-model="formData.type" required>
          <option value="APPEL">Appel téléphonique</option>
          <option value="VISITE">Visite / Rencontre</option>
          <option value="MESSAGE">Message / Email</option>
          <option value="REUNION">Réunion</option>
        </select>
      </div>

      <!-- Champ : Date et Heure -->
      <div class="form-group">
        <label for="date">Date et Heure</label>
        <input 
          type="datetime-local" 
          id="date" 
          v-model="formData.date" 
          required 
        />
      </div>

      <!-- Champ : Notes / Description -->
      <div class="form-group">
        <label for="notes">Compte-rendu / Notes</label>
        <textarea 
          id="notes" 
          v-model="formData.notes" 
          rows="4" 
          placeholder="Résumé de l'échange..."
          required
        ></textarea>
      </div>

      <!-- Boutons d'action -->
      <div class="form-actions">
        <!-- Bouton pour annuler (fermer le formulaire) -->
        <button type="button" class="btn-cancel" @click="$emit('close')">
          Annuler
        </button>
        
        <!-- Bouton de soumission -->
        <button type="submit" class="btn-submit" :disabled="isLoading">
          {{ isLoading ? 'Enregistrement en cours...' : 'Enregistrer' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import api from '../services/api';

// Définition des événements que ce composant peut envoyer à son parent
const emit = defineEmits(['close', 'interaction-added']);

// Si vous avez besoin de l'ID du contact/âme concerné par l'interaction
const props = defineProps({
  contactId: {
    type: Number,
    required: false // Passez à true si ce champ est obligatoire dans votre BDD
  }
});

const isLoading = ref(false);
const errorMessage = ref('');

// État réactif du formulaire
const formData = reactive({
  type: 'APPEL',
  // Initialise la date à l'instant présent au format requis par l'input datetime-local
  date: new Date().toISOString().slice(0, 16),
  notes: '',
  contactId: props.contactId
});

// Fonction appelée lors de la soumission
const submitInteraction = async () => {
  isLoading.value = true;
  errorMessage.value = '';

  try {
    // ⚠️ Remplacez l'URL par l'endpoint exact de votre backend Spring Boot
    const response = await api.post('/interactions', formData); 
// Note : Mettez '/api/interactions' si votre fichier api.js ne gère pas déjà le '/api;
    
    // On prévient le composant parent que l'enregistrement a réussi
    emit('interaction-added', response.data);
    
    // On ferme le formulaire
    emit('close');
    
  } catch (error) {
    console.error("Erreur d'enregistrement:", error);
    errorMessage.value = "Impossible d'enregistrer l'interaction. Veuillez réessayer.";
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* Un peu de CSS de base pour que ça soit lisible (à adapter à votre design) */
.interaction-form-wrapper {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  max-width: 500px;
  margin: 0 auto;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input, 
.form-group select, 
.form-group textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.error-alert {
  color: #721c24;
  background-color: #f8d7da;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 15px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.btn-cancel {
  padding: 8px 15px;
  background: #f8f9fa;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}

.btn-submit {
  padding: 8px 15px;
  background: #0d6efd;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-submit:disabled {
  background: #6c757d;
  cursor: not-allowed;
}
</style>