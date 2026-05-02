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

    <!-- BARRE DE FILTRES -->
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
          <!-- Menu généré dynamiquement depuis les vraies adresses de la BDD -->
          <option v-for="quartier in availableQuarters" :key="quartier" :value="quartier">
            {{ quartier }}
          </option>
        </select>
      </div>

      <div class="filter-group">
        <span class="filter-icon">📊</span>
        <select v-model="selectedStatus" class="filter-input">
          <option value="">Tous les statuts</option>
          <option value="Nouveau">Nouveau</option>
          <option value="Actif">Actif (Contacté récemment)</option>
          <option value="À contacter">À contacter</option>
          <option value="À relancer">À relancer (> 14 jours)</option>
          <option value="Perdu de vue">Perdu de vue (> 30 jours)</option>
        </select>
      </div>
    </div>

    <!-- TABLEAU DES CONTACTS -->
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
              <th @click="sortBy('address')" class="sortable">
                Quartier <span v-if="sortKey === 'address'">{{ sortAsc ? '↑' : '↓' }}</span>
              </th>
              <th @click="sortBy('lastInteractionAt')" class="sortable">
                Dernière Interaction <span v-if="sortKey === 'lastInteractionAt'">{{ sortAsc ? '↑' : '↓' }}</span>
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
                <span class="quarter-tag" v-if="contact.address">{{ contact.address }}</span>
                <span class="text-muted" v-else>Non assigné</span>
              </td>
              <td>
                <!-- Si pas de dernière interaction, on affiche un message -->
                <div v-if="!contact.lastInteractionAt" class="text-muted">
                  Jamais contacté
                </div>
                <!-- Sinon on affiche la vraie date -->
                <div v-else class="interaction-date" :class="{ 'text-red font-bold': getDaysSince(contact.lastInteractionAt) > 30 }">
                  {{ formatDate(contact.lastInteraction) }}
                  <div class="interaction-days">
                    (il y a {{ getDaysSince(contact.lastInteraction) }} j)
                  </div>
                </div>
              </td>
              <td>
                <!-- Badge de Statut Intelligent -->
                <span class="status-badge" :class="getStatusClass(contact)">
                  {{ getStatusText(contact) }}
                </span>
              </td>
              <td class="actions-cell">
                <button class="btn-icon btn-view" title="Voir la fiche" @click="viewContact(contact.id)">👁️</button>
                <button class="btn-icon btn-edit" title="Modifier" @click="editContact(contact.id)">✏️</button>
                <button class="btn-icon btn-action" title="Ajouter une interaction">📞</button>
              </td>
            </tr>
            <tr v-if="filteredContacts.length === 0">
              <td colspan="6" class="empty-state">
                Aucun contact ne correspond à vos critères de recherche.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- PAGINATION -->
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
const sortKey = ref('createdAt'); // Tri par défaut sur les plus récents
const sortAsc = ref(false);

// --- CHARGEMENT DES DONNÉES RÉELLES ---
onMounted(async () => {
  try {
    const response = await api.get('/contacts');
    // On prend les données réelles et brutes du backend, sans aucune simulation
    contacts.value = response.data;
  } catch (err) {
    error.value = "Impossible de charger les contacts. Vérifiez votre connexion.";
    console.error(err);
  } finally {
    isLoading.value = false;
  }
});

// --- LISTE DYNAMIQUE DES QUARTIERS ---
// Extrait les adresses uniques de tous les contacts pour alimenter le menu déroulant
const availableQuarters = computed(() => {
  const allAddresses = contacts.value.map(c => c.address).filter(a => a != null && a.trim() !== '');
  return [...new Set(allAddresses)].sort();
});

// --- LOGIQUE DE FILTRAGE ---
const filteredContacts = computed(() => {
  let result = contacts.value;

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(c => 
      c.firstName.toLowerCase().includes(query) || 
      c.lastName.toLowerCase().includes(query)
    );
  }

  // Utilisation de c.address au lieu de c.quarter
  if (selectedQuarter.value) {
    result = result.filter(c => c.address === selectedQuarter.value);
  }

  if (selectedStatus.value) {
    result = result.filter(c => getStatusText(c).startsWith(selectedStatus.value));
  }

  // Tri
  result = result.sort((a, b) => {
    let valA = a[sortKey.value] || '';
    let valB = b[sortKey.value] || '';

    if (valA < valB) return sortAsc.value ? -1 : 1;
    if (valA > valB) return sortAsc.value ? 1 : -1;
    return 0;
  });

  return result;
});

// --- FONCTIONS UTILITAIRES ---
const sortBy = (key) => {
  if (sortKey.value === key) {
    sortAsc.value = !sortAsc.value;
  } else {
    sortKey.value = key;
    sortAsc.value = true;
  }
};

// Fonction utilitaire pour lire le format date de Spring Boot
const parseDate = (dateVal) => {
  if (!dateVal) return null;
  // Si Spring Boot envoie un tableau [année, mois, jour, heure, minute]
  if (Array.isArray(dateVal)) {
    // Attention : en JavaScript, les mois commencent à 0 (Janvier = 0). On fait donc -1 sur le mois.
    return new Date(dateVal[0], dateVal[1] - 1, dateVal[2], dateVal[3] || 0, dateVal[4] || 0);
  }
  // Si c'est déjà une chaîne de texte standard
  return new Date(dateVal);
};

const getDaysSince = (dateVal) => {
  if (!dateVal) return Infinity;
  const date = parseDate(dateVal);
  if (isNaN(date)) return Infinity; // Sécurité supplémentaire
  
  const now = new Date();
  const diffTime = Math.abs(now - date);
  return Math.floor(diffTime / (1000 * 60 * 60 * 24));
};

const formatDate = (dateVal) => {
  if (!dateVal) return '';
  const date = parseDate(dateVal);
  if (isNaN(date)) return 'Date invalide';
  
  const options = { day: '2-digit', month: 'short', year: 'numeric' };
  return date.toLocaleDateString('fr-FR', options);
};

// --- LOGIQUE MÉTIER DES STATUTS ---
const getStatusText = (contact) => {
  const daysInteraction = getDaysSince(contact.lastInteractionAt);
  
  // Si le contact n'a aucune interaction enregistrée
  if (daysInteraction === Infinity) {
      const daysCreated = getDaysSince(contact.createdAt);
      if (daysCreated <= 7) return 'Nouveau'; // Créé il y a moins de 7 jours
      return 'À contacter'; // Créé il y a plus de 7 jours mais jamais contacté
  }

  // S'il a des interactions
  if (daysInteraction <= 14) return 'Actif';
  if (daysInteraction <= 30) return 'À relancer';
  return 'Perdu de vue';
};

const getStatusClass = (contact) => {
  const status = getStatusText(contact);
  if (status === 'Nouveau') return 'badge-blue';
  if (status === 'Actif') return 'badge-green';
  if (status === 'À relancer' || status === 'À contacter') return 'badge-yellow';
  return 'badge-red';
};

// --- NAVIGATION ---
const goToNewContact = () => router.push('/contacts/new');
const viewContact = (id) => router.push(`/contacts/${id}`);
const editContact = (id) => router.push(`/contacts/${id}/edit`);

</script>

<style scoped>
/* Le CSS reste exactement le même qu'avant pour préserver ton design parfait */
.contacts-container { display: flex; flex-direction: column; gap: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: flex-end; flex-wrap: wrap; gap: 15px; }
.page-title { color: #111827; margin: 0 0 4px 0; font-size: 1.8rem; }
.subtitle { color: #6b7280; margin: 0; font-size: 0.95rem; }
.btn-primary { background-color: #1a8f2e; color: white; padding: 10px 20px; border: none; border-radius: 8px; font-weight: bold; cursor: pointer; box-shadow: 0 2px 4px rgba(26, 143, 46, 0.2); transition: all 0.2s; }
.btn-primary:hover { background-color: #126620; transform: translateY(-1px); }
.filters-card { background: white; padding: 16px 20px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.05); display: flex; gap: 15px; flex-wrap: wrap; border: 1px solid #e5e7eb; }
.filter-group { display: flex; align-items: center; background-color: #f9fafb; border: 1px solid #d1d5db; border-radius: 6px; padding: 0 10px; flex: 1; min-width: 200px; }
.search-group { flex: 2; }
.filter-icon { font-size: 1.1rem; margin-right: 8px; color: #6b7280; }
.filter-input { border: none; background: transparent; padding: 10px 0; width: 100%; font-size: 0.95rem; color: #374151; outline: none; }
.table-card { background: white; border-radius: 10px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); border: 1px solid #e5e7eb; overflow: hidden; }
.table-responsive { overflow-x: auto; }
.data-table { width: 100%; border-collapse: collapse; text-align: left; }
.data-table th, .data-table td { padding: 16px 20px; border-bottom: 1px solid #f3f4f6; white-space: nowrap; }
.data-table th { background-color: #f9fafb; color: #4b5563; font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.05em; }
.sortable { cursor: pointer; user-select: none; }
.sortable:hover { background-color: #f3f4f6; color: #111827; }
.data-table tr:hover { background-color: #f8fafc; }
.contact-name { color: #111827; font-size: 0.95rem; }
.quarter-tag { background-color: #e5e7eb; color: #374151; padding: 4px 8px; border-radius: 4px; font-size: 0.8rem; }
.interaction-date { font-size: 0.95rem; color: #374151; }
.interaction-days { font-size: 0.75rem; color: #6b7280; }
.text-red { color: #e62222 !important; }
.text-muted { color: #9ca3af; font-style: italic; font-size: 0.9rem;}
.font-bold { font-weight: bold; }
.status-badge { padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; font-weight: bold; }
.badge-blue { background-color: #dbeafe; color: #1e40af; } /* Pour les Nouveaux */
.badge-green { background-color: #dcfce7; color: #166534; }
.badge-yellow { background-color: #fef9c3; color: #854d0e; }
.badge-red { background-color: #fee2e2; color: #991b1b; }
.actions-cell { display: flex; gap: 8px; justify-content: flex-end; }
.btn-icon { background: white; border: 1px solid #d1d5db; border-radius: 6px; width: 32px; height: 32px; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: all 0.2s; font-size: 1rem; }
.btn-view:hover { border-color: #3b82f6; background-color: #eff6ff; }
.btn-edit:hover { border-color: #f59e0b; background-color: #fffbeb; }
.btn-action { background-color: #f0fdf4; border-color: #1a8f2e; }
.btn-action:hover { background-color: #1a8f2e; color: white; }
.loading-state, .error-state, .empty-state { padding: 40px; text-align: center; color: #6b7280; }
.error-state { color: #e62222; }
.pagination { display: flex; justify-content: space-between; align-items: center; padding: 15px 20px; background-color: white; border-top: 1px solid #e5e7eb; }
.page-info { font-size: 0.85rem; color: #6b7280; }
.page-controls { display: flex; gap: 5px; }
.btn-page { padding: 6px 12px; border: 1px solid #d1d5db; background-color: white; border-radius: 4px; cursor: pointer; font-size: 0.85rem; color: #374151; }
.btn-page.active { background-color: #1a8f2e; color: white; border-color: #1a8f2e; }
.btn-page:disabled { background-color: #f3f4f6; color: #9ca3af; cursor: not-allowed; }
@media (max-width: 768px) { .filters-card { flex-direction: column; } .filter-group { width: 100%; } }
</style>