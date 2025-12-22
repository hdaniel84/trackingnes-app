import { defineStore } from 'pinia';
import TrackingService from '@/api/trackingApi';

export const useTrackingStore = defineStore('tracking', {
  state: () => ({
    items: [],        // La lista de la página actual
    current: null,    // Registro en edición/detalle
    loading: false,

    // Paginación
    totalItems: 0,    // Total en base de datos
    totalPages: 0,    // Total de páginas
    page: 0,          // Página actual
    size: 10,         // Tamaño de página

    activePhaseId: null, // Filtro activo

    // Errores
    error: null,
    fetchError: null
  }),

  actions: {
    async fetchAll({ page = 0, size = 10, sort = 'startTime,desc', phaseId = null } = {}) {
      this.loading = true;
      this.fetchError = null;

      this.page = page;
      this.size = size;

      // Actualizamos la fase activa si nos pasan una
      if (phaseId !== null && phaseId !== undefined) {
        this.activePhaseId = phaseId;
      }

      try {
        const params = { page, size, sort };

        // Usamos la fase guardada en el estado
        if (this.activePhaseId) {
          params.phaseId = this.activePhaseId;
        }

        const response = await TrackingService.getAll(params);

        this.items = response.data;

        // Headers de paginación
        const totalCount = response.headers['x-total-count'];
        const totalPages = response.headers['x-total-pages'];

        this.totalItems = totalCount ? parseInt(totalCount) : 0;
        this.totalPages = totalPages ? parseInt(totalPages) : 0;

      } catch (err) {
        console.error("Error fetching tracking:", err);
        this.fetchError = 'Não foi possível carregar os registos.';
        this.items = [];
      } finally {
        this.loading = false;
      }
    },

    async getCandidates({ phaseIds, referenceId = null, filterType = 'SHAPE' } = {}) {
      const params = {};

      if (phaseIds && Array.isArray(phaseIds)) {
        params.phaseIds = phaseIds.join(',');
      } else {
        params.phaseIds = phaseIds;
      }

      if (referenceId) {
        params.referenceId = referenceId; // Axios lo enviará como string. Perfecto.
        params.filterType = filterType;
      }
      //return http.get('/tracking/candidates', { params });
      const response = await TrackingService.getCandidates(params);
      this.items = response.data;
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
        const response = await TrackingService.create(data);

        // Recargamos usando el estado actual (activePhaseId)
        await this.fetchAll({ page: 0, size: this.size, sort: 'id,desc', phaseId: this.activePhaseId });

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

        // Actualización Optimista
        const index = this.items.findIndex(item => item.id === id);
        if (index !== -1) {
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