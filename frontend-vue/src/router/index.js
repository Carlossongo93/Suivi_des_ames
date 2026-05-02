import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';

// Imports des vues
import Layout from '../views/Layout.vue';
import Login from '../views/Login.vue';
import Dashboard from '../views/Dashboard.vue';
import Contacts from '../views/Contacts.vue';
import NewContact from '../views/NewContact.vue';
import ContactDetails from '../views/ContactDetails.vue';
import EditContact from '../views/EditContact.vue';
import TeamDashboard from '../views/TeamDashboard.vue'; // NOUVEAU

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: true }, // Protège toutes les routes enfants
    children: [
      {
        path: '',
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
      },
      {
        path: 'contacts/new',
        name: 'NewContact',
        component: NewContact
      },
      {
        path: 'contacts/:id',
        name: 'ContactDetails',
        component: ContactDetails
      },
      {
        path: 'contacts/:id/edit',
        name: 'EditContact',
        component: EditContact
      },
      // --- NOUVELLE ROUTE SÉCURISÉE ---
      {
        path: 'team',
        name: 'TeamDashboard',
        component: TeamDashboard,
        meta: { 
          // Seuls ces rôles ont le droit d'accéder à cette page
          allowedRoles: ['ADMIN', 'SUPERVISEUR', 'LEADER'] 
        }
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// Gardien de navigation (Vérifie la connexion ET les rôles)
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  
  if (to.meta.requiresAuth && !authStore.token) {
    next('/login');
  } else if (to.meta.allowedRoles) {
    // Vérification RBAC (Contrôle d'accès basé sur le rôle)
    // On suppose que le store Auth contient l'utilisateur et son rôle
    const userRole = authStore.user?.role; 
    
    if (userRole && to.meta.allowedRoles.includes(userRole)) {
      next(); // Autorisé
    } else {
      next('/dashboard'); // Bloqué -> retour à l'accueil
    }
  } else {
    next();
  }
});

export default router;