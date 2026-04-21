import { defineStore } from 'pinia';
import api from '../services/api';

// Fonction utilitaire pour décoder le JWT sans dépendance externe
function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (e) {
        return null;
    }
}

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: localStorage.getItem('jwt_token') || null,
        user: null, // Contiendra les "claims" du JWT (email, rôle, teamId)
        authError: null
    }),

    getters: {
        isAuthenticated: (state) => !!state.token,
        isAdmin: (state) => state.user?.role === 'ROLE_ADMIN',
        isLeader: (state) => state.user?.role === 'ROLE_LEADER',
        currentUser: (state) => state.user
    },

    actions: {
        async login(email, password) {
            this.authError = null;
            try {
                // Appel à l'API créée dans /dev4
                const response = await api.post('/auth/login', { email, password });
                const token = response.data.token;
                
                this.setToken(token);
                return true;
            } catch (error) {
                console.error("Erreur de connexion", error);
                this.authError = error.response?.data?.message || "Identifiants incorrects ou problème réseau.";
                return false;
            }
        },

        setToken(token) {
            this.token = token;
            localStorage.setItem('jwt_token', token);
            // On décode le token pour extraire le Rôle et le TeamId injectés par le Backend
            this.user = parseJwt(token); 
        },

        logout() {
            this.token = null;
            this.user = null;
            localStorage.removeItem('jwt_token');
        },

        // À appeler au démarrage de l'app pour restaurer l'état
        init() {
            if (this.token) {
                const decoded = parseJwt(this.token);
                // Vérification basique de l'expiration
                if (decoded && decoded.exp * 1000 > Date.now()) {
                    this.user = decoded;
                } else {
                    this.logout(); // Token expiré
                }
            }
        }
    }
});