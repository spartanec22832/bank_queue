// src/services/api.js
import axios from 'axios';

const api = axios.create({
    // Относительный базовый URL, чтобы прокси Vite/nginx перенаправлял /api на бэкенд
    baseURL: ''
});

api.interceptors.request.use(
    config => {
        // Читаем токен из того же ключа, куда сохраняем в AuthContext
        const token = localStorage.getItem('jwtToken');
        console.log('[API] Interceptor got token:', token);
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
            console.log('[API] Will send header:', config.headers.Authorization);
        }
        return config;
    },
    error => Promise.reject(error)
);

export default api;
