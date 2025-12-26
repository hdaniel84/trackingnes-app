<script setup>
import { ref, onMounted } from 'vue';
import { useTrackingStore } from '@/stores/trackingStore';
import { useFormatters } from '@/layout/composables/useFormatters';

// Componentes Globales
import ProductSelect from '@/components/forms/global/ProductSelect.vue';
import TeamsSelect from '@/components/forms/global/TeamsSelect.vue';
import TrackingTrace from '@/components/forms/tracking/TrackingTrace.vue';
import TrackingEditDialog from '@/components/forms/tracking/TrackingEditDialog.vue';
import TrackingDetailDialog from '@/components/forms/tracking/TrackingDetailDialog.vue';

// PrimeVue UI
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import Tag from 'primevue/tag';
import Select from 'primevue/select';
import DatePicker from 'primevue/datepicker';
import InputText from 'primevue/inputtext';

const store = useTrackingStore();
const { formatDateTime } = useFormatters();

// --- ESTADOS DE UI ---
const selectedItem = ref(null);
const showEditDialog = ref(false);
const showDetailDialog = ref(false);
const showTreeDialog = ref(false);
const selectedTreeId = ref(null);
const loadingTreeId = ref(null);

// --- CONFIGURACIÓN DE FILTROS (Lazy Load) ---
// Definimos las claves que coinciden con los 'field' de las columnas o claves virtuales
const filters = ref({
    'id': { value: null, matchMode: 'equals' },
    'phase.id': { value: null, matchMode: 'equals' },
    'productId': { value: null, matchMode: 'equals' }, // Virtual para el filtro
    'team.id': { value: null, matchMode: 'equals' },
    'startTime': { value: null, matchMode: 'dateIs' },
    'logisticUnit': { value: null, matchMode: 'contains' }
});

// Estado de paginación y orden
const lazyParams = ref({
    page: 0,
    size: 10,
    sortField: 'id',
    sortOrder: -1 // -1 desc, 1 asc
});

// Catálogo de Fases (FALTA CREAR EL ENDPOINT PARA TRAER LAS FASES Y STYLE
const phases = ref([
    { id: 1, description: 'Prensagem' },
    { id: 2, description: 'Conformação' },
    { id: 3, description: 'Vidragem' },
    { id: 5, description: 'Entrada do forno' },
    { id: 6, description: 'Saída do forno' },
    { id: 7, description: 'Escolha' },
    { id: 9, description: 'Embalagem' }
]);

// --- MOTOR DE CARGA ---
const loadData = async () => {
    // Obtenemos el valor crudo del filtro
    const productFilter = filters.value['productId'].value;
    const teamFilter = filters.value['team.id'].value;
    // Mapeo: UI Params -> Backend API Params
    const params = {
        page: lazyParams.value.page,
        size: lazyParams.value.size,
        sort: `${lazyParams.value.sortField},${lazyParams.value.sortOrder === 1 ? 'asc' : 'desc'}`,

        // Extraemos los valores de los filtros de cabecera
        phaseId: filters.value['phase.id'].value || null,
        productId: productFilter?.id ?? null, //Porque viene como objeto desde el componente
        teamId: teamFilter?.id ?? teamFilter ?? null,
        logisticUnit: filters.value['logisticUnit'].value || null,
        date: filters.value['startTime'].value ? filters.value['startTime'].value.toISOString().split('T')[0] : null,
        id: filters.value['id'].value || null
    };

    await store.fetchAll(params);
};

// Evento unificado de PrimeVue (Página, Orden, Filtro)
const onPageSortFilter = (event) => {
    lazyParams.value = {
        page: event.page !== undefined ? event.page : lazyParams.value.page,
        size: event.rows,
        sortField: event.sortField || 'id',
        sortOrder: event.sortOrder || -1
    };

    // Si es un evento de filtro, reseteamos a página 0
    if (event.type === 'filter') {
        lazyParams.value.page = 0;
    }

    loadData();
};

const clearFilters = () => {
    // Resetear valores
    Object.keys(filters.value).forEach(key => filters.value[key].value = null);
    lazyParams.value.page = 0;
    loadData();
};

// --- HANDLERS ---
const openTraceability = async (item) => {
    loadingTreeId.value = item.id;
    selectedTreeId.value = item.id;
    // Pequeño delay para feedback visual
    setTimeout(() => {
        showTreeDialog.value = true;
        loadingTreeId.value = null;
    }, 150);
};

const openEdit = (item) => { selectedItem.value = item; showEditDialog.value = true; };
const openDetail = (item) => { selectedItem.value = item; showDetailDialog.value = true; };

onMounted(() => {
    loadData();
});
</script>

<template>
    <div class="w-full flex flex-col h-[calc(100vh-10rem)] p-4">

        <div class="flex-none mb-4 flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div class="flex items-center gap-3">
                <div class="bg-surface-900 dark:bg-surface-0 p-2.5 rounded-xl shadow-lg">
                    <i class="pi pi-search-plus text-surface-0 dark:text-surface-900 text-xl"></i>
                </div>
                <div>
                    <h1 class="text-xl font-bold text-surface-900 dark:text-surface-0 tracking-tight">Rastreabilidade
                        Global</h1>
                    <p class="text-surface-500 text-xs font-medium">Histórico completo de produção</p>
                </div>
            </div>

            <div class="flex gap-2">
                <Button label="Limpar Filtros" icon="pi pi-filter-slash" text severity="secondary" size="small"
                    @click="clearFilters" />
                <Button label="Actualizar" icon="pi pi-refresh" outlined size="small" @click="loadData"
                    :loading="store.loading" />
            </div>
        </div>

        <div
            class="flex-1 overflow-hidden bg-surface-0 dark:bg-surface-800 rounded-xl border border-surface-200 dark:border-surface-700 shadow-sm flex flex-col">
            <DataTable v-model:filters="filters" :value="store.items || []" :loading="store.loading" lazy
                :paginator="true" :rows="lazyParams.size" :totalRecords="store.totalItems"
                :rowsPerPageOptions="[10, 20, 50, 100]" @page="onPageSortFilter" @sort="onPageSortFilter"
                @filter="onPageSortFilter" filterDisplay="row" scrollable scrollHeight="flex" stripedRows size="small"
                class="p-datatable-sm flex-1" selectionMode="single" @rowSelect="(e) => openDetail(e.data)"
                sortField="id" :sortOrder="-1" dataKey="id">
                <template #empty>
                    <div class="flex flex-col items-center py-12 text-surface-400">
                        <i class="pi pi-database text-5xl mb-4 opacity-20"></i>
                        <span class="text-lg">Sem dados disponíveis</span>
                    </div>
                </template>

                <Column field="id" header="ID" sortable style="min-width: 100px" :showFilterMenu="false">
                    <template #body="{ data }">
                        <span class="font-bold text-surface-600 dark:text-surface-400">#{{ data.id }}</span>
                    </template>
                    <template #filter="{ filterModel, filterCallback }">
                        <InputText v-model="filterModel.value" type="text" @input="filterCallback()"
                            class="p-column-filter w-full" placeholder="Buscar ID" />
                    </template>
                </Column>

                <Column field="phase.id" header="Fase" style="min-width: 160px" :showFilterMenu="false">
                    <template #body="{ data }">
                        <Tag :value="data.phase?.description" severity="info" class="text-[10px] uppercase font-bold" />
                    </template>
                    <template #filter="{ filterModel, filterCallback }">
                        <Select v-model="filterModel.value" :options="phases" optionLabel="description" optionValue="id"
                            placeholder="Todas" showClear class="p-column-filter w-full" @change="filterCallback()" />
                    </template>
                </Column>

                <Column field="productId" header="Produto" style="min-width: 250px" :showFilterMenu="false">
                    <template #body="{ data }">
                        <div class="flex flex-col truncate">
                            <span class="font-bold text-sm text-surface-800 dark:text-surface-100 truncate"
                                :title="data.product?.description">
                                {{ data.product?.description }}
                            </span>
                            <span class="text-[10px] text-primary-500 font-mono">{{ data.product?.productCode }}</span>
                        </div>
                    </template>
                    <template #filter="{ filterModel, filterCallback }">
                        <ProductSelect v-model="filterModel.value" placeholder="Filtrar..." class="w-full"
                            @update:modelValue="filterCallback()" :showLabel="false" :label="null" />
                    </template>
                </Column>

                <Column field="team.id" header="Equipa" style="min-width: 180px" :showFilterMenu="false">
                    <template #body="{ data }">
                        <div class="flex flex-col text-xs">
                            <span class="font-medium">{{ data.team?.description }}</span>
                            <span class="text-surface-500 text-[10px]">{{ data.team?.sectionDescription }}</span>
                        </div>
                    </template>
                    <template #filter="{ filterModel, filterCallback }">
                        <TeamsSelect v-model="filterModel.value" placeholder="Todas" class="w-full"
                            @update:modelValue="filterCallback()" :showLabel="false" :label="null"/>
                    </template>
                </Column>

                <Column field="logisticUnit" header="Un. Log." style="min-width: 120px" :showFilterMenu="false">
                    <template #body="{ data }">
                        <span v-if="data.logisticUnit"
                            class="text-xs font-mono bg-surface-100 dark:bg-surface-700 px-2 py-0.5 rounded">{{
                                data.logisticUnit }}</span>
                        <span v-else class="text-surface-300">-</span>
                    </template>
                    <template #filter="{ filterModel, filterCallback }">
                        <InputText v-model="filterModel.value" type="text" @input="filterCallback()"
                            class="p-column-filter w-full" placeholder="Buscar..." />
                    </template>
                </Column>

                <Column field="quantity" header="Qtd." sortable style="min-width: 100px">
                    <template #body="{ data }">
                        <span class="font-bold">{{ data.quantity }}</span> <span class="text-[10px]">un</span>
                    </template>
                </Column>

                <Column field="startTime" header="Data" sortable style="min-width: 150px" :showFilterMenu="false">
                    <template #body="{ data }">
                        <div class="flex flex-col text-xs">
                            <span class="font-semibold">{{ formatDateTime(data.startTime).split(' ')[0] }}</span>
                            <span class="text-surface-500">{{ formatDateTime(data.startTime).split(' ')[1] }}</span>
                        </div>
                    </template>
                    <template #filter="{ filterModel, filterCallback }">
                        <DatePicker v-model="filterModel.value" dateFormat="dd/mm/yy" placeholder="Data"
                            class="p-column-filter w-full" @update:modelValue="filterCallback()" />
                    </template>
                </Column>

                <Column alignFrozen="right" frozen style="width: 100px">
                    <template #body="{ data }">
                        <div class="flex gap-1 justify-end">
                            <Button icon="pi pi-sitemap" severity="help" text rounded size="small"
                                :loading="loadingTreeId === data.id" v-tooltip.left="'Rastreabilidade'"
                                @click.stop="openTraceability(data)" />
                            <Button icon="pi pi-pencil" severity="secondary" text rounded size="small"
                                @click.stop="openEdit(data)" />
                        </div>
                    </template>
                </Column>
            </DataTable>
        </div>

        <TrackingTrace v-model:visible="showTreeDialog" :trackingId="selectedTreeId" />
        <TrackingEditDialog v-model:visible="showEditDialog" :item="selectedItem" @success="loadData" />
        <TrackingDetailDialog v-model:visible="showDetailDialog" :item="selectedItem" />
    </div>
</template>

<style scoped>
/* Reducir tamaño de inputs en cabeceras para ahorrar espacio */
:deep(.p-column-filter) {
    width: 100%;
}

:deep(.p-inputtext),
:deep(.p-select-label) {
    padding-top: 0.35rem;
    padding-bottom: 0.35rem;
    font-size: 0.8rem;
}
</style>