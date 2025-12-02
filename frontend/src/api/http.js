// src/api/http.js
import axios from 'axios';

// Crear instancia de Axios
const http = axios.create({
  baseURL: 'http://localhost:8080/api', 
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor para añadir token en cada request
http.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor para manejar errores globales
http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      console.warn('Token inválido');
      localStorage.removeItem('jwtToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default http;
