import { defineStore } from 'pinia';
import { getShifts } from '@/api/shiftApi';

export const useShiftStore = defineStore('shift', {
  state: () => ({
    items: [],
    loading: false,
    error: null
  }),
  actions: {
    async fetchShifts() {
      this.loading = true;
      this.error = null;
      try {
        const response = await getShifts();
        this.items = response.data;
      } catch (err) {
        this.error = 'Erro ao cargar dados';
      } finally {
        this.loading = false;
      }
    }
  }
});
