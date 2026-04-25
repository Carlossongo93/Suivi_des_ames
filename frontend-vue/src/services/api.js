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
    
    // Si pas de token ET route protégée → redirection immédiate
    if (!token && config.url !== '/login' && config.url !== '/register') {
        window.location.href = '/login';
        return Promise.reject(new Error('Token manquant'));
    }
    
    config.headers.Authorization = `Bearer ${token}`;
    return config;
}, error => {
    return Promise.reject(error);
});

export default api;
