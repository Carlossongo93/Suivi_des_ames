import axios from 'axios';

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

// Intercepteur : Attache automatiquement le JWT à chaque requête sortante
api.interceptors.request.use(config => {
    const token = localStorage.getItem('jwt_token');
    
    // Routes publiques (login, register) → pas de vérification
    if (config.url === '/auth/login' || config.url === '/auth/register') {
        return config;
    }
    
    // Routes protégées sans token → redirection
    if (!token) {
        window.location.href = '/login';
        return Promise.reject(new Error('Token manquant'));
    }
    
    // Ajouter le token
    config.headers.Authorization = `Bearer ${token}`;
    return config;
}, error => {
    return Promise.reject(error);
});

// Intercepteur : Gère les erreurs d'authentification
api.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 401 || error.response?.status === 403) {
            localStorage.removeItem('jwt_token');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default api;
