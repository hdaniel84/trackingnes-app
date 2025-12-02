import { defineStore } from 'pinia';
import { getTeams } from '@/api/teamApi';

export const useTeamStore = defineStore('team', {
  state: () => ({
    items: [],
    loading: false,
    error: null
  }),
  actions: {
    async fetchTeams() {
      this.loading = true;
      this.error = null;
      try {
        const response = await getTeams();
        this.items = response.data;
      } catch (err) {
        this.error = 'Erro ao cargar dados';
      } finally {
        this.loading = false;
      }
    }
  }
});
