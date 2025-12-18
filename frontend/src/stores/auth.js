import { defineStore } from 'pinia';
import http from '@/api/http';
import router from '@/router';
import { jwtDecode } from 'jwt-decode'; // Recomiendo instalar: npm install jwt-decode

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('jwtToken') || null,
    user: JSON.parse(localStorage.getItem('userData')) || null, // Persistimos datos de usuario
    isLoggedIn: !!localStorage.getItem('jwtToken'),
  }),

  getters: {
    // 🔑 LA MAGIA: Verificador de privilegios
    hasPrivilege: (state) => (privilege) => {
      if (!state.user || !state.user.privileges) return false;
      // Soporta verificar un solo privilegio o un array
      return state.user.privileges.includes(privilege);
    },

    hasRole: (state) => (role) => {
      return state.user?.roles?.includes(role) || false;
    }
  },

  actions: {
    initializeStore() {
      const token = localStorage.getItem('jwtToken');

      if (token) {
        try {
          // 1. Decodificamos el token
          const decoded = jwtDecode(token);
          const currentTime = Date.now() / 1000;

          // 2. Verificamos si expiró (exp viene en segundos UNIX)
          if (decoded.exp < currentTime) {
            console.warn('Token expirado. Cerrando sesión...');
            this.logout(); // Limpia todo
          } else {
            // 3. Token válido: Restauramos estado
            this.token = token;
            this.isLoggedIn = true;
            this.user = JSON.parse(localStorage.getItem('userData'));
          }
        } catch (error) {
          // Si el token es basura/corrupto
          this.logout();
        }
      } else {
        this.logout();
      }
    },
    async login(credentials) {
      try {
        // Tu backend debería devolver token Y datos del usuario (o decodificarlos del token)
        const response = await http.post('/auth/login', credentials);
        const { token, username, roles, privileges } = response.data;

        this.token = token;
        this.isLoggedIn = true;

        // Creamos el objeto de usuario con sus permisos
        this.user = { username, roles, privileges };

        localStorage.setItem('jwtToken', token);
        localStorage.setItem('userData', JSON.stringify(this.user)); // Guardamos para F5

        const redirectPath = router.currentRoute.value.query.redirect;

        if (redirectPath) {
          router.push(redirectPath);
        } else {
          router.push({ name: 'dashboard' });
        }
      } catch (error) {
        this.logout();
        throw error;
      }
    },

    logout() {
      this.token = null;
      this.user = null;
      this.isLoggedIn = false;
      localStorage.removeItem('jwtToken');
      localStorage.removeItem('userData');
      router.push({ name: 'login' });
    }
  }
});