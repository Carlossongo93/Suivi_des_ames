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
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// Gardien de navigation (Vérifie si l'utilisateur est connecté)
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  if (to.meta.requiresAuth && !authStore.token) {
    next('/login');
  } else {
    next();
  }
});

export default router;