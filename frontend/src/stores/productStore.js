import { defineStore } from 'pinia';
import ProductApi from '@/api/productApi'; // Importamos el objeto completo

export const useProductStore = defineStore('product', {
  state: () => ({
    items: [],
    loading: false,
    error: null
  }),
  actions: {
    async fetchProducts() {
      this.loading = true;
      this.error = null;
      try {
        // Usamos el método getAll del objeto importado
        const response = await ProductApi.getAll();
        this.items = response.data;
      } catch (err) {
        console.error(err);
        this.error = 'Erro ao carregar produtos';
      } finally {
        this.loading = false;
      }
    }
  }
});