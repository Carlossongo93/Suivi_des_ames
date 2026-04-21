<template>
  <div class="login-container">
    <div class="login-card">
      <div class="header">
        <h1>Le Troupeau</h1>
        <p>Connectez-vous pour accéder à votre espace.</p>
      </div>

      <!-- PrimeVue Components (simulés avec des classes standards pour le moment) -->
      <form @submit.prevent="handleLogin" class="form-wrapper">
        
        <div v-if="authStore.authError" class="error-message">
          {{ authStore.authError }}
        </div>

        <div class="input-group">
          <label for="email">Email</label>
          <input 
            id="email" 
            v-model="email" 
            type="email" 
            required 
            placeholder="jean.dupont@eglise.com"
          />
        </div>

        <div class="input-group">
          <label for="password">Mot de passe</label>
          <input 
            id="password" 
            v-model="password" 
            type="password" 
            required 
            placeholder="••••••••"
          />
        </div>

        <button type="submit" :disabled="isLoading" class="btn-submit">
          {{ isLoading ? 'Connexion en cours...' : 'Se connecter' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const router = useRouter();
const authStore = useAuthStore();

const email = ref('');
const password = ref('');
const isLoading = ref(false);

const handleLogin = async () => {
  isLoading.value = true;
  const success = await authStore.login(email.value, password.value);
  isLoading.value = false;

  if (success) {
    // Si succès, le router guard nous laissera passer vers l'accueil
    router.push({ name: 'Dashboard' });
  }
};
</script>

<style scoped>
/* Styles de base pour rendre le formulaire présentable avant l'intégration PrimeVue complète */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f3f4f6;
}
.login-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}
.header {
  text-align: center;
  margin-bottom: 2rem;
}
.header h1 {
  margin: 0;
  color: #1f2937;
}
.header p {
  color: #6b7280;
  font-size: 0.9rem;
}
.input-group {
  margin-bottom: 1.5rem;
}
.input-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #374151;
}
.input-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  box-sizing: border-box;
}
.btn-submit {
  width: 100%;
  padding: 0.75rem;
  background-color: #2563eb;
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}
.btn-submit:hover:not(:disabled) {
  background-color: #1d4ed8;
}
.btn-submit:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}
.error-message {
  background-color: #fee2e2;
  color: #b91c1c;
  padding: 0.75rem;
  border-radius: 4px;
  margin-bottom: 1rem;
  font-size: 0.9rem;
  text-align: center;
}
</style>