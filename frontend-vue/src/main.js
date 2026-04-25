import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router';
import { useAuthStore } from './stores/auth';

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);

// Initialisation de l'authentification (restauration du token si existant)
const authStore = useAuthStore();
authStore.init();

app.use(router);
app.mount('#app');
