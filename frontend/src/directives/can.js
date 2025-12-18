// src/directives/can.js
import { useAuthStore } from '@/stores/auth';

export const can = {
  mounted(el, binding) {
    const authStore = useAuthStore();
    const permission = binding.value; // El string que se pase: 'CAN_CREATE'

    // Si es un array de permisos, verificamos si tiene ALGUNO de ellos
    if (Array.isArray(permission)) {
      const hasAny = permission.some(p => authStore.hasPrivilege(p));
      if (!hasAny) {
        el.parentNode && el.parentNode.removeChild(el);
      }
    } 
    // Si es un solo permiso
    else {
      if (!authStore.hasPrivilege(permission)) {
        // Si no tiene el privilegio, eliminamos el elemento del DOM
        el.parentNode && el.parentNode.removeChild(el);
      }
    }
  },
  // Opcional: Si tus permisos cambian dinámicamente sin recargar página
  updated(el, binding) {
    const authStore = useAuthStore();
    const permission = binding.value;
    
    // Lógica para volver a verificar si algo cambió (generalmente con mounted basta)
    if (!authStore.hasPrivilege(permission)) {
         el.parentNode && el.parentNode.removeChild(el);
    }
  }
};