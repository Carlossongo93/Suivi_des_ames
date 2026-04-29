<template>
  <div class="contacts-container">
    <!-- EN-TÊTE -->
    <header class="page-header">
      <div class="header-info">
        <h1 class="page-title">Mes Contacts</h1>
        <p class="subtitle">Gérez et suivez les membres de votre troupeau ({{ filteredContacts.length }} contacts)</p>
      </div>
      <button class="btn-primary" @click="goToNewContact">
        ➕ Nouveau Contact
      </button>
    </header>

    <!-- BARRE DE FILTRES (US-3.3 & US-4.3) -->
    <div class="filters-card">
      <div class="filter-group search-group">
        <span class="filter-icon">🔍</span>
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="Rechercher par nom ou prénom..."
          class="filter-input"
        />
      </div>

      <div class="filter-group">
        <span class="filter-icon">📍</span>
        <select v-model="selectedQuarter" class="filter-input">
          <option value="">Tous les quartiers</option>
          <option value="Centre-Ville">Centre-Ville</option>
          <option value="Quartier Nord">Quartier Nord</option>
          <option value="Quartier Sud">Quartier Sud</option>
        </select>
      </div>

      <div class="filter-group">
        <span class="filter-icon">📊</span>
        <select v-model="selectedStatus" class="filter-input">
          <option value="">Tous les statuts</option>
          <option value="Actif">Actif (Contacté récemment)</option>
          <option value="À relancer">À relancer (> 14 jours)</option>
          <option value="Perdu de vue">Perdu de vue (> 30 jours)</option>
        </select>
      </div>
    </div>

    <!-- TABLEAU DES CONTACTS (US-2.2) -->
    <div class="table-card">
      <div v-if="isLoading" class="loading-state">
        <span class="spinner">⏳</span> Chargement de vos contacts...
      </div>

      <div v-else-if="error" class="error-state">
        ⚠️ {{ error }}
      </div>

      <div v-else class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th @click="sortBy('lastName')" class="sortable">
                Nom complet <span v-if="sortKey === 'lastName'">{{ sortAsc ? '↑' : '↓' }}</span>
              </th>
              <th>Téléphone</th>
              <th @click="sortBy('quarter')" class="sortable">
                Quartier <span v-if="sortKey === 'quarter'">{{ sortAsc ? '↑' : '↓' }}</span>
              </th>
              <th @click="sortBy('lastInteraction')" class="sortable">
                Dernière Interaction <span v-if="sortKey === 'lastInteraction'">{{ sortAsc ? '↑' : '↓' }}</span>
              </th>
              <th>Statut</th>
              <th class="text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="contact in filteredContacts" :key="contact.id">
              <td>
                <div class="contact-name">
                  <strong>{{ contact.lastName }}</strong> {{ contact.firstName }}
                </div>
              </td>
              <td>{{ contact.phone || 'Non renseigné' }}</td>
              <td>
                <span class="quarter-tag">{{ contact.quarter }}</span>
              </td>
              <td>
                <div class="interaction-date" :class="{ 'text-red font-bold': getDaysSince(contact.lastInteraction) > 30 }">
                  {{ formatDate(contact.lastInteraction) }}
                  <div class="interaction-days" v-if="contact.lastInteraction">
                    (il y a {{ getDaysSince(contact.lastInteraction) }} j)
                  </div>
                </div>
              </td>
              <td>
                <!-- Badge de Statut (AFT-CONF-01 / Alertes visuelles) -->
                <span class="status-badge" :class="getStatusClass(contact.lastInteraction)">
                  {{ getStatusText(contact.lastInteraction) }}
                </span>
              </td>
              <td class="actions-cell">
                <button class="btn-icon btn-view" title="Voir la fiche" @click="viewContact(contact.id)">👁️</button>
                <button class="btn-icon btn-edit" title="Modifier" @click="editContact(contact.id)">✏️</button>
                <button class="btn-icon btn-action" title="Ajouter une interaction">📞</button>
              </td>
            </tr>
            <!-- État vide si la recherche ne donne rien -->
            <tr v-if="filteredContacts.length === 0">
              <td colspan="6" class="empty-state">
                Aucun contact ne correspond à vos critères de recherche.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- PAGINATION SIMULÉE -->
      <div class="pagination" v-if="filteredContacts.length > 0">
        <span class="page-info">Affichage de 1 à {{ filteredContacts.length }} sur {{ contacts.length }}</span>
        <div class="page-controls">
          <button class="btn-page" disabled>Précédent</button>
          <button class="btn-page active">1</button>
          <button class="btn-page" disabled>Suivant</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();

// ÉTATS
const contacts = ref([]);
const isLoading = ref(true);
const error = ref(null);

// FILTRES & TRI
const searchQuery = ref('');
const selectedQuarter = ref('');
const selectedStatus = ref('');
const sortKey = ref('lastInteraction');
const sortAsc = ref(true);

// --- CHARGEMENT DES DONNÉES ---
onMounted(async () => {
  try {
    // 1. Appel API réel (récupère firstName, lastName, phone, etc.)
    const response = await api.get('/contacts');
    const apiContacts = response.data;

    // 2. SIMULATION DES DONNÉES MANQUANTES POUR L'UI (Quartier, Date de dernière interaction)
    // À retirer quand le backend fournira ces données !
    const quartiers = ['Centre-Ville', 'Quartier Nord', 'Quartier Sud'];
    
    contacts.value = apiContacts.map(c => {
      // Génère une date aléatoire entre aujourd'hui et il y a 45 jours
      const randomDaysAgo = Math.floor(Math.random() * 45); 
      const lastInteraction = new Date();
      lastInteraction.setDate(lastInteraction.getDate() - randomDaysAgo);

      return {
        ...c,
        quarter: quartiers[Math.floor(Math.random() * quartiers.length)], // Quartier aléatoire
        lastInteraction: lastInteraction.toISOString() // Date simulée
      };
    });

  } catch (err) {
    error.value = "Impossible de charger les contacts. Vérifiez votre connexion.";
    console.error(err);
  } finally {
    isLoading.value = false;
  }
});

// --- LOGIQUE DE FILTRAGE ---
const filteredContacts = computed(() => {
  let result = contacts.value;

  // Filtre par recherche textuelle (Nom ou Prénom)
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(c => 
      c.firstName.toLowerCase().includes(query) || 
      c.lastName.toLowerCase().includes(query)
    );
  }

  // Filtre par Quartier
  if (selectedQuarter.value) {
    result = result.filter(c => c.quarter === selectedQuarter.value);
  }

  // Filtre par Statut (calculé dynamiquement selon la date)
  if (selectedStatus.value) {
    result = result.filter(c => getStatusText(c.lastInteraction) === selectedStatus.value);
  }

  // Tri
  result = result.sort((a, b) => {
    let valA = a[sortKey.value];
    let valB = b[sortKey.value];

    if (valA < valB) return sortAsc.value ? -1 : 1;
    if (valA > valB) return sortAsc.value ? 1 : -1;
    return 0;
  });

  return result;
});

// --- FONCTIONS UTILITAIRES ---
const sortBy = (key) => {
  if (sortKey.value === key) {
    sortAsc.value = !sortAsc.value; // Inverse l'ordre si on clique sur la même colonne
  } else {
    sortKey.value = key;
    sortAsc.value = true;
  }
};

const getDaysSince = (dateString) => {
  if (!dateString) return 999;
  const date = new Date(dateString);
  const now = new Date();
  const diffTime = Math.abs(now - date);
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
};

const formatDate = (dateString) => {
  if (!dateString) return 'Jamais contacté';
  const options = { day: '2-digit', month: 'short', year: 'numeric' };
  return new Date(dateString).toLocaleDateString('fr-FR', options);
};

// Logique des statuts métiers (US-2.2 & US-3.3)
const getStatusText = (lastInteractionDate) => {
  const days = getDaysSince(lastInteractionDate);
  if (days <= 14) return 'Actif';
  if (days <= 30) return 'À relancer';
  return 'Perdu de vue';
};

const getStatusClass = (lastInteractionDate) => {
  const days = getDaysSince(lastInteractionDate);
  if (days <= 14) return 'badge-green';
  if (days <= 30) return 'badge-yellow';
  return 'badge-red'; // Alerte > 30 jours
};

// --- NAVIGATION ---
const goToNewContact = () => router.push('/contacts/new');
const viewContact = (id) => router.push(`/contacts/${id}`);
const editContact = (id) => router.push(`/contacts/${id}/edit`);

</script>

<style scoped>
.contacts-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* --- EN-TÊTE --- */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 15px;
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

.btn-primary {
  background-color: #1a8f2e; /* Vert Logo */
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(26, 143, 46, 0.2);
  transition: all 0.2s;
}

.btn-primary:hover {
  background-color: #126620;
  transform: translateY(-1px);
}

/* --- BARRE DE FILTRES --- */
.filters-card {
  background: white;
  padding: 16px 20px;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
  border: 1px solid #e5e7eb;
}

.filter-group {
  display: flex;
  align-items: center;
  background-color: #f9fafb;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 0 10px;
  flex: 1;
  min-width: 200px;
}

.search-group {
  flex: 2; /* La barre de recherche prend plus de place */
}

.filter-icon {
  font-size: 1.1rem;
  margin-right: 8px;
  color: #6b7280;
}

.filter-input {
  border: none;
  background: transparent;
  padding: 10px 0;
  width: 100%;
  font-size: 0.95rem;
  color: #374151;
  outline: none;
}

/* --- TABLEAU --- */
.table-card {
  background: white;
  border-radius: 10px;
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.table-responsive {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

.data-table th, .data-table td {
  padding: 16px 20px;
  border-bottom: 1px solid #f3f4f6;
  white-space: nowrap;
}

.data-table th {
  background-color: #f9fafb;
  color: #4b5563;
  font-weight: 600;
  font-size: 0.85rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.sortable {
  cursor: pointer;
  user-select: none;
}
.sortable:hover {
  background-color: #f3f4f6;
  color: #111827;
}

.data-table tr:hover {
  background-color: #f8fafc;
}

/* Cellules Spécifiques */
.contact-name {
  color: #111827;
  font-size: 0.95rem;
}

.quarter-tag {
  background-color: #e5e7eb;
  color: #374151;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
}

.interaction-date {
  font-size: 0.95rem;
  color: #374151;
}
.interaction-days {
  font-size: 0.75rem;
  color: #6b7280;
}
.text-red { color: #e62222 !important; }
.font-bold { font-weight: bold; }

/* Badges Statut */
.status-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: bold;
}
.badge-green { background-color: #dcfce7; color: #166534; }
.badge-yellow { background-color: #fef9c3; color: #854d0e; }
.badge-red { background-color: #fee2e2; color: #991b1b; } /* Alerte Rouge */

/* Actions */
.actions-cell {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.btn-icon {
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 1rem;
}

.btn-view:hover { border-color: #3b82f6; background-color: #eff6ff; }
.btn-edit:hover { border-color: #f59e0b; background-color: #fffbeb; }
.btn-action { background-color: #f0fdf4; border-color: #1a8f2e; } /* Bouton vert pour interagir */
.btn-action:hover { background-color: #1a8f2e; color: white; }

/* --- ÉTATS & PAGINATION --- */
.loading-state, .error-state, .empty-state {
  padding: 40px;
  text-align: center;
  color: #6b7280;
}
.error-state { color: #e62222; }

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: white;
  border-top: 1px solid #e5e7eb;
}

.page-info {
  font-size: 0.85rem;
  color: #6b7280;
}

.page-controls {
  display: flex;
  gap: 5px;
}

.btn-page {
  padding: 6px 12px;
  border: 1px solid #d1d5db;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  color: #374151;
}

.btn-page.active {
  background-color: #1a8f2e;
  color: white;
  border-color: #1a8f2e;
}

.btn-page:disabled {
  background-color: #f3f4f6;
  color: #9ca3af;
  cursor: not-allowed;
}

/* RESPONSIVE */
@media (max-width: 768px) {
  .filters-card { flex-direction: column; }
  .filter-group { width: 100%; }
}
</style>