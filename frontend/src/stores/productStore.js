import { defineStore } from 'pinia';
import { getProducts } from '@/api/productApi';

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
        const response = await getProducts();
        this.items = response.data;
      } catch (err) {
        this.error = 'Erro ao cargar dados';
      } finally {
        this.loading = false;
      }
    }
  }
});
