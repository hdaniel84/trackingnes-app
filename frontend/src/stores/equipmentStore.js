import { defineStore } from 'pinia';
import  EquipmentService from '@/api/equipmentApi';

export const useEquipmentStore = defineStore('equipment', {
  state: () => ({
    items: [],
    loading: false,
    error: null
  }),
  getters: {
    // Para el Select Principal (Solo Prensas/Máquinas)
    mainEquipments: (state) => state.items.filter(e => e.mandatory === true),

    // Para el MultiSelect Auxiliar (Moldes, Herramientas)
    auxiliaryEquipments: (state) => state.items.filter(e => e.mandatory === false)
  },
  actions: {
    async fetchAll() {
      this.loading = true;
      this.error = null;
      try {
        const response = await EquipmentService.getAll();
        this.items = response.data;
      } catch (err) {
        this.error = 'Erro ao cargar dados';
      } finally {
        this.loading = false;
      }
    }
  }
});
