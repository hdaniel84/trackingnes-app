import { defineStore } from 'pinia';
import TrackingService from '@/api/trackingApi';

export const useTrackingStore = defineStore('tracking', {
  state: () => ({
    items: [],        // La lista principal (Tabla)
    current: null,    // Registro en edición/detalle
    loading: false,

    // Paginación (Estado del servidor)
    totalItems: 0,    
    totalPages: 0,    
    page: 0,          
    size: 10,         

    // Filtros activos (Opcional, pero útil para persistencia básica)
    lastParams: {}, 

    // Errores
    error: null,
    fetchError: null
  }),

  actions: {
    /**
     * Carga datos con filtros dinámicos.
     * Acepta cualquier objeto de parámetros que coincida con el Backend (TrackingListDTO/Specification).
     */
    async fetchAll(params = {}) {
      this.loading = true;
      this.fetchError = null;

      // 1. Guardamos parámetros básicos en el estado por si se necesitan leer
      if (params.page !== undefined) this.page = params.page;
      if (params.size !== undefined) this.size = params.size;
      
      // Guardamos la última consulta completa (útil para "refrescar" sin pasar params de nuevo)
      this.lastParams = { ...params };

      try {
        // 2. Limpieza de Parámetros:
        // Eliminamos claves con valor null, undefined o string vacío para no ensuciar la URL
        const cleanParams = Object.fromEntries(
          Object.entries(params).filter(([_, v]) => v != null && v !== '')
        );

        // 3. Llamada al Servicio
        const response = await TrackingService.getAll(cleanParams);

        this.items = response.data.content || response.data; // Soporte para Page<T> o List<T>

        // 4. Mapeo de Headers de Paginación (Spring Boot suele devolver esto en headers o en el body 'page')
        // Ajusta esto según si tu backend devuelve Page<?> en el body o headers X-Total-Count
        const totalCount = response.headers['x-total-count'];
        const totalPages = response.headers['x-total-pages'];

        // Si el backend devuelve objeto Page de Spring en el body:
        if (response.data.totalElements !== undefined) {
             this.totalItems = response.data.totalElements;
             this.totalPages = response.data.totalPages;
        } else {
             // Fallback a headers
             this.totalItems = totalCount ? parseInt(totalCount) : 0;
             this.totalPages = totalPages ? parseInt(totalPages) : 0;
        }

      } catch (err) {
        console.error("Error fetching tracking:", err);
        this.fetchError = 'Não foi possível carregar os registos.';
        this.items = [];
        this.totalItems = 0;
      } finally {
        this.loading = false;
      }
    },

    /**
     * Obtiene candidatos para dropdowns (Padres, Materias Primas, etc).
     * ⚠️ IMPORTANTE: No guarda en 'this.items' para no romper la tabla principal.
     * Retorna la data para que el componente la maneje localmente.
     */
    async getCandidates({ phaseIds, referenceId = null, filterType = 'SHAPE' } = {}) {
      const params = {};

      if (phaseIds) {
        params.phaseIds = Array.isArray(phaseIds) ? phaseIds.join(',') : phaseIds;
      }

      if (referenceId) {
        params.referenceId = referenceId;
        params.filterType = filterType;
      }

      // Devolvemos la promesa/datos directamente. NO modificamos el estado global 'items'.
      return await TrackingService.getCandidates(params);
    },

    async fetchById(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await TrackingService.getById(id);
        this.current = response.data;
        return response.data; // Retornamos también por si el componente lo necesita
      } catch (err) {
        this.error = `Não foi possível carregar o registo ${id}`;
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async create(data) {
      this.loading = true;
      this.error = null;
      try {
        const response = await TrackingService.create(data);
        
        // Recargar la tabla con los últimos filtros usados
        // Esto mantiene al usuario en la misma página/filtro donde estaba
        await this.fetchAll(this.lastParams);

        return response.data;
      } catch (err) {
        const msg = err.response?.data?.message || 'Não foi possível criar o registo.';
        this.error = msg;
        throw err;
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
          // Fusionamos para no perder datos que quizas no vengan en la respuesta
          this.items[index] = { ...this.items[index], ...response.data };
        }
        
        // Actualizamos el current si es el mismo
        if (this.current && this.current.id === id) {
            this.current = response.data;
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

        this.items = this.items.filter(item => item.id !== id);
        this.totalItems--;

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