<template>
  <div class="login-layout">
    <div class="login-container">
      
      <!-- Carte de connexion -->
      <div class="login-card">
        
        <!-- En-tête / Logo -->
        <div class="brand-header">
          <div class="logo-circle">
            <span class="logo-icon">🔥</span>
          </div>
          <h1>Le Troupeau</h1>
        </div>

        <!-- Message d'accueil spirituel -->
        <div class="welcome-message">
          <p>
            Shalom Pasteur(e). Vous êtes béni(e), connectez-vous pour prendre soin des âmes.
          </p>
          <span class="bible-verse">« Jean 6 : 39 »</span>
        </div>

        <!-- Formulaire -->
        <form @submit.prevent="handleLogin" class="form-wrapper">
          
          <!-- Alerte d'erreur -->
          <div v-if="authStore.authError" class="error-alert">
            <svg class="error-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
            <span>{{ authStore.authError }}</span>
          </div>

          <!-- Champ Email -->
          <div class="input-group">
            <label for="email">Adresse email</label>
            <div class="input-wrapper">
              <svg class="input-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path></svg>
              <input 
                id="email" 
                v-model="email" 
                type="email" 
                required 
                placeholder="pasteur@eglise.com"
                :disabled="isLoading"
              />
            </div>
          </div>

          <!-- Champ Mot de passe -->
          <div class="input-group">
            <label for="password">Mot de passe</label>
            <div class="input-wrapper">
              <svg class="input-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path></svg>
              
              <input 
                id="password" 
                v-model="password" 
                :type="showPassword ? 'text' : 'password'" 
                required 
                placeholder="••••••••"
                :disabled="isLoading"
              />
              
              <!-- Bouton Afficher/Masquer -->
              <button 
                type="button" 
                class="btn-toggle-password" 
                @click="showPassword = !showPassword"
                title="Afficher/Masquer le mot de passe"
              >
                <svg v-if="!showPassword" class="eye-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path></svg>
                <svg v-else class="eye-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"></path></svg>
              </button>
            </div>
          </div>

          <!-- Bouton de validation -->
          <button type="submit" :disabled="isLoading" class="btn-submit">
            <span v-if="isLoading" class="spinner-inline"></span>
            {{ isLoading ? 'Connexion en cours...' : 'Se connecter à l\'espace' }}
          </button>
        </form>
      </div>

    </div>

    <!-- Pied de page -->
    <footer class="login-footer">
      <p>
        Développé par 
        <a href="https://international-ia.tech" target="_blank" rel="noopener noreferrer" class="agency-link">
          international-ia
        </a>
      </p>
    </footer>
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
const showPassword = ref(false); // État pour la visibilité du mot de passe
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
/* --- MISE EN PAGE GLOBALE --- */
.login-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f3f4f6;
  background-image: radial-gradient(#e5e7eb 1px, transparent 1px); /* Motif de fond subtil */
  background-size: 20px 20px;
}

.login-container {
  flex-grow: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

/* --- CARTE DE CONNEXION --- */
.login-card {
  background: white;
  padding: 40px 32px;
  border-radius: 16px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 440px;
  border-top: 4px solid #1a8f2e; /* Bordure verte signature */
}

/* --- EN-TÊTE --- */
.brand-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24px;
}

.logo-circle {
  width: 64px;
  height: 64px;
  background-color: #111827;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 16px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.logo-icon {
  font-size: 2rem;
}

.brand-header h1 {
  margin: 0;
  color: #111827;
  font-size: 1.8rem;
  font-weight: 800;
  letter-spacing: -0.025em;
}

/* --- MESSAGE D'ACCUEIL SPIRITUEL --- */
.welcome-message {
  text-align: center;
  margin-bottom: 30px;
  padding: 16px;
  background-color: #f8fafc;
  border-radius: 8px;
  border-left: 3px solid #1a8f2e;
}

.welcome-message p {
  color: #4b5563;
  font-size: 0.95rem;
  line-height: 1.5;
  margin: 0 0 8px 0;
}

.bible-verse {
  display: block;
  color: #1a8f2e;
  font-weight: 700;
  font-style: italic;
  font-size: 0.9rem;
  letter-spacing: 0.5px;
}

/* --- FORMULAIRE --- */
.form-wrapper {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-group label {
  font-weight: 600;
  color: #374151;
  font-size: 0.9rem;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 14px;
  width: 20px;
  height: 20px;
  color: #9ca3af;
}

.input-wrapper input {
  width: 100%;
  padding: 12px 14px 12px 42px; /* Espace pour l'icône à gauche */
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 1rem;
  color: #111827;
  background-color: #ffffff;
  transition: all 0.2s ease;
}

/* Espace supplémentaire à droite pour le bouton de l'œil */
#password {
  padding-right: 45px;
}

.input-wrapper input:focus {
  outline: none;
  border-color: #1a8f2e;
  box-shadow: 0 0 0 3px rgba(26, 143, 46, 0.15);
}

.input-wrapper input::placeholder {
  color: #9ca3af;
}

/* --- BOUTON AFFICHER/MASQUER MOT DE PASSE --- */
.btn-toggle-password {
  position: absolute;
  right: 10px;
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}

.btn-toggle-password:hover {
  color: #1a8f2e;
}

.eye-icon {
  width: 22px;
  height: 22px;
}

/* --- BOUTON DE SOUMISSION --- */
.btn-submit {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 14px;
  background-color: #1a8f2e;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-top: 10px;
  box-shadow: 0 4px 6px -1px rgba(26, 143, 46, 0.2);
}

.btn-submit:hover:not(:disabled) {
  background-color: #157324;
  transform: translateY(-1px);
}

.btn-submit:disabled {
  background-color: #71b87d;
  cursor: not-allowed;
  box-shadow: none;
}

/* Animation de chargement dans le bouton */
.spinner-inline {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* --- GESTION DES ERREURS --- */
.error-alert {
  display: flex;
  align-items: center;
  gap: 10px;
  background-color: #fef2f2;
  color: #b91c1c;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 0.9rem;
  border: 1px solid #fecaca;
}

.error-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

/* --- PIED DE PAGE (SIGNATURE) --- */
.login-footer {
  text-align: center;
  padding: 24px;
}

.login-footer p {
  margin: 0;
  color: #6b7280;
  font-size: 0.9rem;
}

.agency-link {
  color: #111827;
  font-weight: 700;
  text-decoration: none;
  transition: all 0.2s;
  border-bottom: 1px solid transparent;
}

.agency-link:hover {
  color: #1a8f2e;
  border-bottom-color: #1a8f2e;
}
</style>