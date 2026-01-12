<script setup>
import { ref, watch, computed } from 'vue';
import { useTrackingStore } from '@/stores/trackingStore';
import { useFormatters } from '@/layout/composables/useFormatters';
import { usePhase } from '@/layout/composables/usePhase';

import Message from 'primevue/message';
import Tag from 'primevue/tag';
import Button from 'primevue/button';
import Select from 'primevue/select';
import TrackingEditDialog from './TrackingEditDialog.vue';
import TrackingDetailDialog from './TrackingDetailDialog.vue';
import TrackingTrace from './TrackingTrace.vue'; // Asegúrate de importar esto

const store = useTrackingStore();
const { formatDateTime } = useFormatters();
const { phaseId, phaseMetadata } = usePhase();

const selectedItem = ref(null);
const showEditDialog = ref(false);
const showDetailDialog = ref(false);

// Traceability Logic
const showTreeDialog = ref(false);
const selectedTreeId = ref(null);
const openTraceability = (item) => {
  selectedTreeId.value = item.id;
  showTreeDialog.value = true;
};

// Filtros
const pageSize = ref(10);
const sortOrder = ref('id,desc');

const sizeOptions = [10, 20, 50, 100];
const sortOptions = [
  { label: 'Mais Recentes', value: 'id,desc' },
  { label: 'Mais Antigos', value: 'id,asc' },
  { label: 'Maior Quantidade', value: 'quantity,desc' },
  { label: 'Menor Quantidade', value: 'quantity,asc' }
];

const readPermission = computed(() => {
  const suffix = phaseMetadata.value?.permissionSuffix || 'ADMIN';
  return `READ_${suffix}`;
});

const loadData = async () => {
  if (!phaseId.value) return;
  await store.fetchAll({
    page: 0,
    size: pageSize.value,
    sort: sortOrder.value,
    phaseId: phaseId.value
  });
};

watch([phaseId, pageSize, sortOrder], async ([newPhase], [oldPhase]) => {
  if (newPhase !== oldPhase) selectedItem.value = null;
  await loadData();
}, { immediate: true });

const openEditDialog = (item) => { selectedItem.value = item; showEditDialog.value = true; };
const openDetailDialog = (item) => { selectedItem.value = item; showDetailDialog.value = true; };
const handleRefresh = () => { loadData(); };
</script>

<template>
  <div class="w-full flex flex-col h-[calc(100vh-10rem)]">

    <div v-can="readPermission" class="flex flex-col h-full min-h-0">

      <div class="flex-none mb-3 border-b border-surface-200 dark:border-surface-700 pb-3">
        <div class="flex items-center mb-3">
          <i class="pi pi-history mr-2 text-primary-500 text-xl"></i>
          <span class="font-bold text-lg text-surface-800 dark:text-surface-100">
            Últimos Registos
            <span class="text-surface-500 font-normal text-sm ml-1">({{ phaseMetadata.title }})</span>
          </span>
        </div>

        <div class="flex flex-col sm:flex-row gap-3 justify-between items-start sm:items-center">
          <div class="flex gap-2 w-full sm:w-auto">
            <Select v-model="sortOrder" :options="sortOptions" optionLabel="label" optionValue="value"
              placeholder="Ordenar" size="small" class="w-full sm:w-48">
              <template #value="slotProps">
                <div v-if="slotProps.value" class="flex items-center">
                  <i class="pi pi-sort-alt mr-2 text-primary-500"></i>
                  {{sortOptions.find(o => o.value === slotProps.value)?.label}}
                </div>
                <span v-else>{{ slotProps.placeholder }}</span>
              </template>
            </Select>

            <Select v-model="pageSize" :options="sizeOptions" placeholder="Qtd" size="small" class="w-24" />
          </div>

          <Button icon="pi pi-refresh" text rounded @click="handleRefresh" :loading="store.loading" />
        </div>
      </div>

      <div v-if="store.loading" class="flex flex-col gap-3">
        <Skeleton width="100%" height="30rem" borderRadius="6px"></Skeleton>
      </div>

      <div v-else-if="store.fetchError" class="flex-none">
        <Message severity="error" :closable="false">{{ store.fetchError }}</Message>
        <Button label="Tentar Novamente" class="mt-2" size="small" @click="handleRefresh" />
      </div>

      <div v-else class="flex-1 overflow-y-auto pr-2 min-h-0 custom-scrollbar pb-4">

        <div v-for="item in (store.items || [])" :key="item.id"
          class="bg-surface-50 dark:bg-surface-800 p-3 mb-3 rounded-xl border border-surface-200 dark:border-surface-700 shadow-sm transition duration-300 hover:shadow-md hover:bg-surface-100 dark:hover:bg-surface-700 group relative">

          <div class="flex justify-between items-start gap-3 cursor-pointer" @click="openDetailDialog(item)">
            <div class="bg-surface-200 dark:bg-surface-700 p-2.5 rounded-full mt-1 shrink-0">
              <i class="pi pi-pallet text-lg text-primary-600 dark:text-primary-400"></i>
            </div>

            <div class="flex flex-col w-full min-w-0">
              <div class="flex justify-between items-center mb-1">
                <span class="font-bold text-surface-900 dark:text-surface-0">#{{ item.id }}</span>
                <Tag :value="`${item.quantity} Un.`" severity="success" class="text-xs font-bold" />
              </div>

              <div class="space-y-1">
                <div class="text-sm font-semibold text-surface-700 dark:text-surface-200 truncate leading-tight">
                  {{ item.product?.productCode || '' }}
                </div>
                <div class="text-sm font-semibold text-surface-700 dark:text-surface-200 truncate leading-tight">
                  {{ item.product?.description || 'Produto Desconhecido' }}
                </div>

                <div class="flex flex-wrap gap-x-3 gap-y-1 text-xs text-surface-500 dark:text-surface-400">
                  <div class="flex items-center">
                    <i class="pi pi-calendar mr-1 text-primary-500"></i>
                    {{ formatDateTime(item.startTime) }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="flex justify-between items-center mt-2 pt-2 border-t border-surface-200 dark:border-surface-700">
            <div class="text-xs italic text-surface-500 dark:text-surface-400 truncate max-w-[60%] flex items-center">
              <i class="pi pi-users mr-1.5"></i>
              <span class="truncate">{{ item.team?.description }} ({{ item.team?.sectionDescription }})</span>
            </div>

            <div class="flex gap-1">
              <Button icon="pi pi-sitemap" text rounded severity="info" size="small" v-tooltip.top="'Rastreabilidade'"
                @click.stop="openTraceability(item)" />
              <Button v-can="`WRITE_${phaseMetadata.permissionSuffix}`" icon="pi pi-pencil" text rounded
                severity="secondary" size="small" @click.stop="openEditDialog(item)" />
            </div>
          </div>
        </div>

        <div v-if="(!store.items || store.items.length === 0)" class="text-center py-10 opacity-60">
          <div class="bg-surface-100 dark:bg-surface-800 rounded-full p-4 inline-block mb-3">
            <i class="pi pi-filter-slash text-2xl text-surface-500"></i>
          </div>
          <p class="text-sm text-surface-600">Nenhum registo encontrado com estes filtros.</p>
        </div>
      </div>

    </div>

    <TrackingTrace v-model:visible="showTreeDialog" :trackingId="selectedTreeId" />
    <TrackingEditDialog v-model:visible="showEditDialog" :item="selectedItem" @success="handleRefresh" />
    <TrackingDetailDialog v-model:visible="showDetailDialog" :item="selectedItem" />
  </div>
</template>

<style scoped>
/* Scrollbar fina y moderna para la lista */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: var(--surface-300);
  border-radius: 20px;
}

.dark .custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: var(--surface-600);
}
</style>