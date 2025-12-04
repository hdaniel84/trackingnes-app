import { defineStore } from 'pinia';
import ParameterService from '@/api/parameterApi';

export const useParameterStore = defineStore('parameter', {
  state: () => ({
    items: [],        // lista de registros
    current: null,    // registro seleccionado
    loading: false,
    error: null
  }),

  actions: {
    // Obtener todos los registros
    async fetchAll() {
      this.loading = true;
      this.error = null;
      try {
        const response = await ParameterService.getAll();
        this.items = response.data;
      } catch (err) {
        this.error = 'Não foi possível carregar os registos.';
      } finally {
        this.loading = false;
      }
    },

    // Obtener un registro por ID
    async fetchById(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await ParameterService.getById(id);
        this.current = response.data;
      } catch (err) {
        this.error = `Não foi possível carregar os registos: id ${id}`;
      } finally {
        this.loading = false;
      }
    }
  }
});
