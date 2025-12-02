// src/stores/trackingPrensasStore.js
import { defineStore } from 'pinia';
import TrackingPrensasService from '@/api/trackingPrensasApi';

export const useTrackingPrensasStore = defineStore('trackingPrensas', {
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
        const response = await TrackingPrensasService.getAll();
        this.items = response.data;
      } catch (err) {
        this.error = 'No se pudieron cargar los registros';
      } finally {
        this.loading = false;
      }
    },

    // Obtener un registro por ID
    async fetchById(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await TrackingPrensasService.getById(id);
        this.current = response.data;
      } catch (err) {
        this.error = `No se pudo cargar el registro con id ${id}`;
      } finally {
        this.loading = false;
      }
    },

    // Crear un nuevo registro
    async create(data) {
      this.loading = true;
      this.error = null;
      try {
        const response = await TrackingPrensasService.create(data);
        this.items.push(response.data); // añadir al listado
        return response.data;
      } catch (err) {
        this.error = 'No se pudo crear el registro';
        throw err;
      } finally {
        this.loading = false;
      }
    },

    // Actualizar un registro existente
    async update(id, data) {
      this.loading = true;
      this.error = null;
      try {
        const response = await TrackingPrensasService.update(id, data);
        const index = this.items.findIndex(item => item.id === id);
        if (index !== -1) {
          this.items[index] = response.data;
        }
        return response.data;
      } catch (err) {
        this.error = `No se pudo actualizar el registro con id ${id}`;
        throw err;
      } finally {
        this.loading = false;
      }
    },

    // Eliminar un registro
    async remove(id) {
      this.loading = true;
      this.error = null;
      try {
        await TrackingPrensasService.remove(id);
        this.items = this.items.filter(item => item.id !== id);
      } catch (err) {
        this.error = `No se pudo eliminar el registro con id ${id}`;
        throw err;
      } finally {
        this.loading = false;
      }
    }
  }
});
