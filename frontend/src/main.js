// src/main.js
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { createPinia } from 'pinia';

import Aura from '@primeuix/themes/aura';
import PrimeVue from 'primevue/config';
import ConfirmationService from 'primevue/confirmationservice';
import ToastService from 'primevue/toastservice';

import { useAuthStore } from './stores/auth';

import '@/assets/tailwind.css';
import '@/assets/styles.scss';

const app = createApp(App);

app.use(createPinia());
app.use(router);
app.use(PrimeVue, {
  theme: {
    preset: Aura,
    options: {
      darkModeSelector: '.app-dark',
      primary: 'blue'
    }
  }
});
app.use(ToastService);
app.use(ConfirmationService);

// Guard de rutas
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next({ name: 'login' });
  } else if (to.name === 'login' && authStore.isLoggedIn) {
    next({ name: 'dashboard' });
  } else {
    next();
  }
});

// Inicializar store antes de montar
router.isReady().then(() => {
  const authStore = useAuthStore();
  authStore.initializeStore();
  app.mount('#app');
});
