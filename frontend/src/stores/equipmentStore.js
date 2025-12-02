import { defineStore } from 'pinia';
import { getEquipments } from '@/api/equipmentApi';

export const useEquipmentStore = defineStore('equipment', {
  state: () => ({
    items: [],
    loading: false,
    error: null
  }),
  actions: {
    async fetchEquipments() {
      this.loading = true;
      this.error = null;
      try {
        const response = await getEquipments();
        this.items = response.data;
      } catch (err) {
        this.error = 'Erro ao cargar dados';
      } finally {
        this.loading = false;
      }
    }
  }
});
