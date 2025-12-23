<script setup>
import { ref, onMounted } from 'vue';
import { useTrackingStore } from '@/stores/trackingStore';
import { useFormatters } from '@/layout/composables/useFormatters';

// 1. Imports Corregidos (Rutas absolutas)
import TrackingTrace from '@/components/forms/tracking/TrackingTrace.vue';
import TrackingEditDialog from '@/components/forms/tracking/TrackingEditDialog.vue';
import TrackingDetailDialog from '@/components/forms/tracking/TrackingDetailDialog.vue';

// PrimeVue UI
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import Tag from 'primevue/tag';
import Message from 'primevue/message';

const store = useTrackingStore();
const { formatDateTime } = useFormatters();

const selectedItem = ref(null);
const showEditDialog = ref(false);
const showDetailDialog = ref(false);

// --- Lógica del Árbol de Rastreabilidad ---
const showTreeDialog = ref(false);
const selectedTreeId = ref(null);

const openTraceability = (item) => {
  selectedTreeId.value = item.id;
  showTreeDialog.value = true;
};

// --- Carga de Datos (GLOBAL) ---
const loadData = async (page = 0) => {
  await store.fetchAll({
    page: page,
    size: 10,
    sort: 'id,desc',
    phaseId: null // 🚀 NULL para traer todas las fases
  });
};

// Paginación
const onPage = (event) => {
    loadData(event.page);
};

// Carga inicial
onMounted(() => {
    loadData(0);
});

// UI Handlers
const openEditDialog = (item) => { selectedItem.value = item; showEditDialog.value = true; };
const openDetailDialog = (item) => { selectedItem.value = item; showDetailDialog.value = true; };
const handleRefresh = () => { loadData(store.page); };

</script>

<template>
  <div class="w-full">
      
      <div class="mb-4 flex flex-col sm:flex-row items-center justify-between gap-4 border-b border-surface-200 dark:border-surface-700 pb-3">
        <div class="flex items-center gap-2">
           <div class="bg-indigo-100 dark:bg-indigo-900/30 p-2 rounded-full">
              <i class="pi pi-globe text-indigo-600 dark:text-indigo-400 text-xl"></i>
           </div>
           <div>
              <h2 class="text-lg font-bold text-surface-900 dark:text-surface-0 leading-none">Histórico Global</h2>
              <span class="text-sm text-surface-500 dark:text-surface-400">Todos os registos de produção</span>
           </div>
        </div>
        
        <div class="flex gap-2">
            <Button icon="pi pi-refresh" text rounded @click="() => loadData(0)" :loading="store.loading" v-tooltip="'Atualizar'" />
        </div>
      </div>

      <Message v-if="store.fetchError" severity="error" class="mb-4">{{ store.fetchError }}</Message>

      <div class="card bg-surface-0 dark:bg-surface-800 rounded-xl border border-surface-200 dark:border-surface-700 overflow-hidden">
        
        <DataTable 
            :value="store.items || []" 
            :loading="store.loading"
            lazy
            :totalRecords="store.totalItems"
            :rows="store.size"
            @page="onPage"
            paginator
            :rowsPerPageOptions="[10, 20, 50]"
            
            stripedRows 
            size="small"
            class="p-datatable-sm"
            responsiveLayout="scroll"
            selectionMode="single"
            @rowSelect="(e) => openDetailDialog(e.data)"
        >
            <template #empty>
                <div class="text-center p-8 text-surface-500">
                    <i class="pi pi-folder-open text-4xl mb-2"></i>
                    <p>Não foram encontrados registos.</p>
                </div>
            </template>

            <Column field="id" header="#" style="width: 60px">
                <template #body="slotProps">
                    <span class="font-bold text-surface-500">{{ slotProps.data.id }}</span>
                </template>
            </Column>

            <Column field="phase.description" header="Fase" style="width: 140px">
                <template #body="slotProps">
                    <Tag :value="slotProps.data.phase?.description" severity="info" class="text-xs uppercase" />
                </template>
            </Column>

            <Column field="product.description" header="Produto" style="min-width: 200px">
                <template #body="slotProps">
                    <div class="font-medium text-surface-900 dark:text-surface-100">
                        {{ slotProps.data.product?.description || 'N/A' }}
                    </div>
                    <div class="text-xs text-surface-500">{{ slotProps.data.product?.productCode }}</div>
                </template>
            </Column>

            <Column field="quantity" header="Qtd." style="width: 100px">
                <template #body="slotProps">
                    <span class="font-bold text-surface-700 dark:text-surface-200">
                        {{ slotProps.data.quantity }}
                    </span>
                    <span class="text-xs ml-1 text-surface-500">un.</span>
                </template>
            </Column>

             <Column field="logisticUnit" header="Un. Logística" style="width: 120px">
                <template #body="slotProps">
                    <div class="flex items-center gap-2" v-if="slotProps.data.logisticUnit">
                        <i class="pi pi-box text-surface-400"></i>
                        <span>{{ slotProps.data.logisticUnit }}</span>
                    </div>
                    <span v-else class="text-surface-400">-</span>
                </template>
            </Column>

            <Column field="startTime" header="Data / Hora" style="width: 160px">
                <template #body="slotProps">
                    <div class="flex flex-col">
                        <span class="text-sm font-medium">{{ formatDateTime(slotProps.data.startTime).split(' ')[0] }}</span>
                        <span class="text-xs text-surface-500">{{ formatDateTime(slotProps.data.startTime).split(' ')[1] }}</span>
                    </div>
                </template>
            </Column>

             <Column header="Equipa" style="width: 150px">
                <template #body="slotProps">
                    <div class="text-sm truncate max-w-[140px]" :title="slotProps.data.team?.description">
                        {{ slotProps.data.team?.description }}
                    </div>
                </template>
            </Column>

            <Column header="Ações" alignFrozen="right" frozen style="width: 100px; text-align: right">
                <template #body="slotProps">
                    <div class="flex gap-1 justify-end">
                        <Button 
                            icon="pi pi-sitemap" 
                            text 
                            rounded 
                            severity="help" 
                            size="small"
                            v-tooltip.left="'Ver Rastreabilidade'"
                            @click.stop="openTraceability(slotProps.data)" 
                        />

                        <Button 
                            icon="pi pi-pencil" 
                            text 
                            rounded 
                            severity="secondary" 
                            size="small"
                            v-tooltip.top="'Editar'"
                            @click.stop="openEditDialog(slotProps.data)" 
                        />
                    </div>
                </template>
            </Column>

        </DataTable>
      </div>

    <TrackingTrace v-model:visible="showTreeDialog" :trackingId="selectedTreeId" />
    <TrackingEditDialog v-model:visible="showEditDialog" :item="selectedItem" @success="handleRefresh" />
    <TrackingDetailDialog v-model:visible="showDetailDialog" :item="selectedItem" />
  </div>
</template>