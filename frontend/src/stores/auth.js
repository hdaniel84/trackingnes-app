// src/stores/auth.js
import { defineStore } from 'pinia';
import http from '@/api/http';
import router from '@/router';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('jwtToken') || null,
    user: null,
    isLoggedIn: !!localStorage.getItem('jwtToken'),
  }),

  actions: {
    initializeStore() {
      this.isLoggedIn = !!this.token;
    },

    async login(credentials) {
      try {
        // 🚨 Backend debe devolver { token: '...' }
        const response = await http.post('/auth/login', credentials);
        const newToken = response.data.token;

        this.token = newToken;
        this.isLoggedIn = true;

        localStorage.setItem('jwtToken', newToken);

        // Redirigir al dashboard
        router.push({ name: 'dashboard' });
      } catch (error) {
        console.error('Login fallido:', error.response?.data || error.message);
        this.logout();
        throw new Error(error.response?.data?.message || 'Credenciales inválidas.');
      }
    },

    logout() {
      this.token = null;
      this.user = null;
      this.isLoggedIn = false;
      localStorage.removeItem('jwtToken');

      router.push({ name: 'login' });
    }
  }
});
