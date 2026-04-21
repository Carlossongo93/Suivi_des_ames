<template>
  <div>
    <div class="header-actions">
      <h1 class="page-title">Mon Équipe</h1>
      <button class="btn-primary">➕ Nouveau Membre</button>
    </div>

    <div v-if="isLoading" class="loading">
      Chargement des membres...
    </div>

    <div v-else-if="error" class="error-message">
      {{ error }}
    </div>

    <div v-else class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>Nom</th>
            <th>Prénom</th>
            <th>Téléphone</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="contact in contacts" :key="contact.id">
            <td><strong>{{ contact.lastName }}</strong></td>
            <td>{{ contact.firstName }}</td>
            <td>{{ contact.phone || 'Non renseigné' }}</td>
            <td>
              <button class="btn-action">Noter une visite</button>
            </td>
          </tr>
          <tr v-if="contacts.length === 0">
            <td colspan="4" class="text-center">Aucun membre dans votre équipe pour le moment.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const contacts = ref([]);
const isLoading = ref(true);
const error = ref(null);

onMounted(async () => {
  try {
    // Cette requête passera par l'intercepteur Axios (qui ajoutera le JWT).
    // Le Backend (ContactController) lira le JWT et ne renverra QUE l'équipe du Leader.
    const response = await api.get('/contacts');
    contacts.value = response.data;
  } catch (err) {
    error.value = "Impossible de charger les contacts. Vérifiez votre connexion.";
    console.error(err);
  } finally {
    isLoading.value = false;
  }
});
</script>

<style scoped>
.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-title {
  color: #111827;
  margin: 0;
}
.btn-primary {
  background-color: #10b981;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
}
.btn-primary:hover {
  background-color: #059669;
}
.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  overflow-x: auto;
}
.data-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}
.data-table th, .data-table td {
  padding: 15px;
  border-bottom: 1px solid #e5e7eb;
}
.data-table th {
  background-color: #f9fafb;
  color: #4b5563;
  font-weight: 600;
}
.data-table tr:hover {
  background-color: #f3f4f6;
}
.text-center {
  text-align: center;
  color: #6b7280;
}
.btn-action {
  background-color: #eff6ff;
  color: #2563eb;
  border: 1px solid #bfdbfe;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
}
.btn-action:hover {
  background-color: #dbeafe;
}
.loading, .error-message {
  padding: 20px;
  text-align: center;
  border-radius: 8px;
}
.error-message {
  background-color: #fee2e2;
  color: #b91c1c;
}
</style>