import { defineStore } from 'pinia';
import TrackingService from '@/api/trackingApi'; // Ajusta la ruta si es necesario

export const useTrackingStore = defineStore('tracking', {
  state: () => ({
    items: [],        // La lista de la página actual
    current: null,    // Registro en edición/detalle
    loading: false,
    
    // Paginación
    totalItems: 0,    // Total en base de datos (leído del header)
    totalPages: 0,    // Total de páginas
    page: 0,          // Página actual
    size: 10,         // Tamaño de página
    
    // Errores
    error: null,      // Errores de escritura (create/update)
    fetchError: null  // Errores de lectura (get)
  }),

  actions: {
    // 🔹 MODIFICADO: Lectura de headers de paginación
    async fetchAll({ page = 0, size = 10, sort = 'startTime,desc' } = {}) {
      this.loading = true;
      this.fetchError = null;
      
      // Guardamos el estado actual
      this.page = page;
      this.size = size;

      try {
        const response = await TrackingService.getAll({ page, size, sort });
        
        // 1. El cuerpo es la lista de items (ResponseDTOs)
        this.items = response.data;

        // 2. Leemos los metadatos de los HEADERS (según tu Controller Java)
        // Nota: Los headers en axios suelen venir en minúsculas
        const totalCount = response.headers['x-total-count'];
        const totalPages = response.headers['x-total-pages'];

        this.totalItems = totalCount ? parseInt(totalCount) : 0;
        this.totalPages = totalPages ? parseInt(totalPages) : 0;

      } catch (err) {
        console.error("Error fetching tracking:", err);
        this.fetchError = 'Não foi possível carregar os registos.';
        this.items = []; // Limpiamos si hay error
      } finally {
        this.loading = false;
      }
    },

    async fetchById(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await TrackingService.getById(id);
        this.current = response.data;
      } catch (err) {
        this.error = `Não foi possível carregar o registo ${id}`;
      } finally {
        this.loading = false;
      }
    },

    async create(data) {
      this.loading = true;
      this.error = null;
      try {
        // Axios lanza error si el status no es 2xx
        const response = await TrackingService.create(data);
        
        // OPCIÓN A: Recargar la lista (Más seguro, respeta ordenamiento del server)
        await this.fetchAll({ page: 0, size: this.size, sort: 'startTime,desc' });
        
        return response.data;
      } catch (err) {
        // Extraemos mensaje del backend si existe
        const msg = err.response?.data?.message || 'Não foi possível criar o registo.';
        this.error = msg;
        throw err; // Re-lanzamos para que el componente muestre el Dialog de error
      } finally {
        this.loading = false;
      }
    },

    async update(id, data) {
      this.loading = true;
      this.error = null;
      try {
        const response = await TrackingService.update(id, data);
        
        // Actualización Optimista en la lista local
        const index = this.items.findIndex(item => item.id === id);
        if (index !== -1) {
          // Reemplazamos el item viejo con el nuevo ResponseDTO que viene del server
          this.items[index] = response.data; 
        }
        
        return response.data;
      } catch (err) {
        const msg = err.response?.data?.message || `Erro ao atualizar ID ${id}`;
        this.error = msg;
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async remove(id) {
      this.loading = true;
      this.error = null;
      try {
        await TrackingService.remove(id);
        
        // Removemos localmente
        this.items = this.items.filter(item => item.id !== id);
        this.totalItems--; // Ajustamos el contador visualmente
        
      } catch (err) {
        const msg = err.response?.data?.message || `Erro ao eliminar ID ${id}`;
        this.error = msg;
        throw err;
      } finally {
        this.loading = false;
      }
    }
  }
});