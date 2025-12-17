import { defineStore } from 'pinia';
import RawMaterialService from '@/api/rawMaterialApi';


export const useRawMaterialStore = defineStore('rawMaterial', {
  state: () => ({
    items: [],
    loading: false,
    error: null
  }),
  actions: {
    async fetchAll() {
      if (this.items.length > 0) return; // Caché simple
      this.loading = true;
      try {
        // Ajusta la URL según tu endpoint real de catálogo de materias primas
        const response = await RawMaterialService.getAll();
        this.items = response.data;
      } catch (err) {
        this.error = 'Erro ao carregar matérias-primas';
      } finally {
        this.loading = false;
      }
    }
  }
});