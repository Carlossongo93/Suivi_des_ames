import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import Login from '../views/Login.vue';
import Layout from '../views/Layout.vue';
import Dashboard from '../views/Dashboard.vue';
import Contacts from '../views/Contacts.vue';

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: Login,
        meta: { requiresGuest: true }
    },
    {
        // Layout est la route parente pour toutes les pages connectées
        path: '/',
        component: Layout,
        meta: { requiresAuth: true },
        children: [
            {
                path: '', // Redirection par défaut de / vers /dashboard
                redirect: '/dashboard'
            },
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: Dashboard
            },
            {
                path: 'contacts',
                name: 'Contacts',
                component: Contacts
            }
        ]
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

// Gardien de navigation (Navigation Guard) : Le videur de la boîte de nuit
router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    const isAuthenticated = authStore.isAuthenticated;

    if (to.meta.requiresAuth && !isAuthenticated) {
        // Redirection vers le login si on essaie d'accéder à une page protégée sans token
        next({ name: 'Login' });
    } else if (to.meta.requiresGuest && isAuthenticated) {
        // Redirection vers le dashboard si on est déjà connecté et qu'on va sur /login
        next({ name: 'Dashboard' });
    } else {
        next(); // On laisse passer
    }
});

export default router;