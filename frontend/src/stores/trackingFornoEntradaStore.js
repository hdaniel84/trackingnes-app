import { defineStore } from 'pinia';
import TrackingFornoEntradaService from '@/api/trackingFornoEntradaApi';

export const useTrackingFornoEntradaStore = defineStore('trackingFornoEntrada', {
  state: () => ({
    items: [],       
    current: null,    
    loading: false,
    error: null,
    fetchError: null,
    totalItems: 0
  }),

  actions: {
    async fetchAll({ page = 0, size = 20, sort = 'startTime,desc' } = {}) {
      this.loading = true;
      this.fetchError = null;
      try {
        const response = await TrackingFornoEntradaService.getAll({ page, size, sort });
        this.items = response.data;
        this.totalItems = response.data.length;
      } catch (err) {
        this.fetchError = 'Não foi possível carregar os registos.';
      } finally {
        this.loading = false;
      }
    },

    async fetchById(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await TrackingFornoEntradaService.getById(id);
        this.current = response.data;
      } catch (err) {
        this.error = `Não foi possível carregar o registo com o ID ${id}`;
      } finally {
        this.loading = false;
      }
    },

    async create(data) {
      this.loading = true;
      this.error = null;
      try {
        const response = await TrackingFornoEntradaService.create(data);
        await this.fetchAll({ page: 0, size: 10, sort: 'startTime,desc' });
        return response.data;
      } catch (err) {
        this.error = 'Não foi possível criar o registo.';
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async update(id, data) {
      this.loading = true;
      this.error = null;
      try {
        const response = await TrackingFornoEntradaService.update(id, data);
        const index = this.items.findIndex(item => item.id === id);
        if (index !== -1) {
          this.items[index] = response.data;
        }
        return response.data;
      } catch (err) {
        this.error = `Não foi possível atualizar o registo com o ID ${id}`;
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async remove(id) {
      this.loading = true;
      this.error = null;
      try {
        await TrackingFornoEntradaService.remove(id);
        this.items = this.items.filter(item => item.id !== id);
      } catch (err) {
        this.error = `O registo não pôde ser eliminado. ID ${id}`;
        throw err;
      } finally {
        this.loading = false;
      }
    }
  }
});
