import axios from 'axios';

// Crear instancia de Axios
const http = axios.create({
  //baseURL: 'http://localhost:8080/api', //Va al archivo de conf de vite vite.config.mjs
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor REQUEST → agrega Token
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

// Interceptor RESPONSE → Manejo global de errores
http.interceptors.response.use(
  (response) => response,
  (error) => {
    // Si no hay response → caída de red, CORS, backend caído
    if (!error.response) {
      return Promise.reject({
        status: null,
        message: 'No hay conexión con el servidor',
        detail: null,
      });
    }

    const { status, data } = error.response;

    // Manejo de Auth
    if (status === 401) {
      console.warn('[AUTH] Token inválido o expirado');
      localStorage.removeItem('jwtToken');
      window.location.href = '/login';
    }

    // Estructura del backend Spring
    // ApiError: { status, message, detail, timestamp }
    return Promise.reject({
      status: data?.status || status,
      message: data?.message || 'Error inesperado',
      detail: data?.detail || null,
    });
  }
);

export default http;
