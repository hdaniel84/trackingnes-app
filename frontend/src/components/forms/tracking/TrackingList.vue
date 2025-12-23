<script setup>
import { ref, watch, computed } from 'vue';
import { useTrackingStore } from '@/stores/trackingStore';
import { useFormatters } from '@/layout/composables/useFormatters';
import { usePhase } from '@/layout/composables/usePhase';
import TrackingTrace from './TrackingTrace.vue'

// PrimeVue & UI
import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Tag from 'primevue/tag';
import Button from 'primevue/button';
import TrackingEditDialog from './TrackingEditDialog.vue';
import TrackingDetailDialog from './TrackingDetailDialog.vue';

const store = useTrackingStore();
// const authStore = useAuthStore(); // Descomentar si vas a validar API
const { formatDateTime } = useFormatters();
const { phaseId, phaseMetadata } = usePhase();

const selectedItem = ref(null);
const showEditDialog = ref(false);
const showDetailDialog = ref(false);

// 1. COMPUTAR EL PERMISO REQUERIDO
// Ej: Si estamos en Vidragem, phaseMetadata.permissionSuffix es 'VIDRAGEM'
// Resultado: 'READ_VIDRAGEM'
const readPermission = computed(() => {
  const suffix = phaseMetadata.value?.permissionSuffix || 'ADMIN';
  return `READ_${suffix}`;
});

const loadData = async () => {
  if (!phaseId.value) return;

  // 🚀 OPTIMIZACIÓN ENTERPRISE:
  // Si tienes acceso al store de Auth en script, descomenta esto para evitar la llamada API
  // if (!authStore.hasPermission(readPermission.value)) return;

  await store.fetchAll({
    page: 0,
    size: 10,
    sort: 'id,desc',
    phaseId: phaseId.value
  });
};

watch(phaseId, async (newId, oldId) => {
  if (newId !== oldId) {
    selectedItem.value = null;
    await loadData();
  }
}, { immediate: true });

// UI Handlers
const openEditDialog = (item) => { selectedItem.value = item; showEditDialog.value = true; };
const openDetailDialog = (item) => { selectedItem.value = item; showDetailDialog.value = true; };
const handleRefresh = () => { loadData(); };
</script>

<template>
  <div class="w-full">

    <div v-can="readPermission">

      <p class="mb-2 text-surface-800 dark:text-surface-100 border-b pb-2 flex items-center justify-between">
        <span class="flex items-center">
          <i class="pi pi-history mr-2 text-primary-500"></i>
          Últimos Registos <span class="text-surface-500 ml-2 text-sm font-normal">({{ phaseMetadata.title }})</span>
        </span>
        <Button icon="pi pi-refresh" text rounded @click="loadData" :loading="store.loading" />
      </p>

      <div v-if="store.loading" class="flex flex-col items-center py-10">
        <ProgressSpinner style="width: 50px; height: 50px" strokeWidth="6" animationDuration=".5s" />
        <p class="mt-3 text-surface-600 dark:text-surface-400 animate-pulse">A carregar dados...</p>
      </div>

      <div v-else-if="store.fetchError">
        <Message severity="error" :closable="false">{{ store.fetchError }}</Message>
        <Button label="Tentar Novamente" class="mt-2" size="small" @click="loadData" />
      </div>

      <div v-else class="flex flex-col gap-4 max-h-[100vh] overflow-y-auto pr-2 custom-scrollbar">

        <div v-for="item in (store.items || [])" :key="item.id"
          class="bg-surface-50 dark:bg-surface-800 p-3 rounded-xl border border-surface-200 dark:border-surface-700 shadow-sm transition duration-300 hover:shadow-md hover:bg-surface-100 dark:hover:bg-surface-700 group relative">

          <div class="flex justify-between items-start gap-4 cursor-pointer" @click="openDetailDialog(item)">
            <div class="flex items-start gap-3 flex-1">
              <div class="bg-surface-200 dark:bg-surface-700 p-2 rounded-full mt-1 shrink-0">
                <i class="pi pi-pallet text-xl text-primary-600 dark:text-primary-400"></i>
              </div>

              <div class="flex flex-col w-full min-w-0">
                <div class="flex justify-between items-center mb-1">
                  <span class="font-bold text-surface-900 dark:text-surface-0 text-lg">#{{ item.id }}</span>
                  <Tag :value="`${item.quantity} Un.`" severity="success" class="text-xs font-bold" />
                </div>

                <div class="space-y-1.5">
                  <div class="text-sm font-bold text-surface-800 dark:text-surface-100 truncate">
                    {{ item.product?.description || 'Produto Desconhecido' }}
                  </div>
                  <div class="flex flex-wrap gap-x-4 gap-y-1 text-xs text-surface-600 dark:text-surface-400">
                    <div class="flex items-center">
                      <i class="pi pi-truck mr-1.5 text-primary-500"></i>

                      <div class="mr-1" v-if="phaseId <= 4"> Carro: </div>
                      <div class="mr-1" v-else-if="phaseId <= 6"> Vagão: </div>
                      <div class="mr-1" v-else> Palete: </div>
                      {{ item.logisticUnit ? item.logisticUnit : 'N/A' }}
                    </div>
                    <div class="flex items-center">
                      <i class="pi pi-calendar mr-1.5 text-primary-500"></i>
                      {{ formatDateTime(item.startTime) }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="flex justify-between items-center mt-3 pt-2 border-t border-surface-200 dark:border-surface-700">
            <div class="text-xs italic text-surface-500 dark:text-surface-400 truncate max-w-[75%]">
              <i class="pi pi-users mr-1"></i> {{ item.team?.description }} - {{ item.team?.sectionDescription }}
            </div>

            <Button v-can="`WRITE_${phaseMetadata.permissionSuffix}`" icon="pi pi-pencil" text rounded
              severity="secondary" @click.stop="openEditDialog(item)" />
          </div>
        </div>

        <div v-if="(!store.items || store.items.length === 0)" class="text-center py-10 opacity-60">
          <i class="pi pi-filter-slash text-4xl mb-3"></i>
          <p>Nenhum registo encontrado</p>
        </div>
      </div>

    </div>
    <TrackingEditDialog v-model:visible="showEditDialog" :item="selectedItem" @success="handleRefresh" />
    <TrackingDetailDialog v-model:visible="showDetailDialog" :item="selectedItem" />
  </div>
</template>