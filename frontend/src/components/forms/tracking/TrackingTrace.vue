<script setup>
import { ref, watch } from 'vue';
import OrganizationChart from 'primevue/organizationchart';
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';
import ToggleButton from 'primevue/togglebutton';
import TrackingService from '@/api/trackingApi';
// Asegúrate de que la ruta a tu detalle sea correcta, a veces es .vue, a veces no en los imports
import TrackingDetailDialog from './TrackingDetailDialog.vue';

const props = defineProps({
    visible: Boolean,
    trackingId: Number
});

const emit = defineEmits(['update:visible']);

const treeData = ref(null);
const loading = ref(false);
const detailItem = ref(null);
const showDetail = ref(false);

// ✅ CAMBIO 1: Iniciar en FALSE para vista estándar (Arriba hacia Abajo)
const isInverted = ref(false);

// Función recursiva para enriquecer los datos sin destruir la estructura original
const enrichTreeData = (node) => {
    if (!node) return null;

    const data = node.data || {};
    // Detectamos scrap si existe y es mayor a 0
    const hasScrap = (data.quantityScrap || 0) > 0;

    return {
        ...node, // Mantenemos las claves originales del nodo (key, type, children, etc)
        // Inyectamos la clase CSS condicional
        styleClass: hasScrap ? 'has-scrap' : 'status-ok',
        data: {
            ...data, // Mantenemos todos los datos originales dentro de data
            // Aseguramos valores por defecto para evitar errores en el template si faltan datos
            quantity: data.quantity || 0,
            quantityScrap: data.quantityScrap || 0,
            productCode: data.productCode || 'N/A',
            productDesc: data.productDesc || 'Produto Desconhecido',
            team: data.team || '---',
            // Usamos 'label' (común en PrimeVue) o 'phase' según venga del backend
            phase: data.label || data.phase || 'Fase',
        },
        // Procesamos recursivamente los hijos
        children: node.children ? node.children.map(enrichTreeData) : []
    };
};

const loadTree = async () => {
    if (!props.trackingId) return;
    loading.value = true;
    treeData.value = null; // Limpiar antes de cargar
    try {
        const response = await TrackingService.getTraceability(props.trackingId);
        // Enriquecemos los datos manteniendo la estructura original
        treeData.value = enrichTreeData(response.data);
    } catch (e) {
        console.error("Error loading tree", e);
        // Podrías añadir una notificación de error aquí si lo deseas
    } finally {
        loading.value = false;
    }
};

const openFullDetail = async (nodeId) => {
    // Intentamos obtener el ID de varias ubicaciones posibles en la estructura del nodo
    const id = nodeId || (detailItem.value && detailItem.value.id);
    if (!id) return;

    try {
        const response = await TrackingService.getById(id);
        detailItem.value = response.data;
        showDetail.value = true;
    } catch (e) {
        console.error("Error loading detail", e);
    }
};

watch(() => props.visible, (val) => {
    if (val) {
        loadTree();
        // ✅ CAMBIO 2: Asegurar que al abrir siempre empiece en vista normal
        isInverted.value = false;
    }
});
</script>

<template>
    <Dialog :visible="props.visible" @update:visible="emit('update:visible', $event)" modal header="Rastreabilidade"
        :style="{ width: '95vw', maxWidth: '1400px', height: '90vh' }" maximizable
        :contentStyle="{ padding: '0px', height: '100%', overflow: 'hidden' }">
        <template #header>
            <div class="flex items-center justify-between w-full mr-4">
                <div class="flex flex-col">
                    <span class="text-lg font-bold">Árvore de Rastreabilidade</span>
                    <span class="text-xs text-surface-500" v-if="treeData && treeData.data">
                        Raiz: {{ treeData.data.productDesc }} (#{{ treeData.data.id || treeData.key }})
                    </span>
                </div>
                <div class="flex gap-2">
                    <ToggleButton v-model="isInverted" onLabel="Vista Invertida (Ascendente)"
                        offLabel="Vista Padrão (Descendente)" onIcon="pi pi-arrow-up" offIcon="pi pi-arrow-down"
                        class="w-56 text-xs" />
                </div>
            </div>
        </template>

        <div v-if="loading" class="flex justify-center items-center h-full">
            <i class="pi pi-spin pi-spinner text-4xl text-primary-500"></i>
        </div>

        <div v-else class="w-full h-full overflow-auto bg-surface-50 dark:bg-surface-900 p-10 relative">

            <div class="min-w-fit min-h-fit flex justify-center">
                <OrganizationChart v-if="treeData" :value="treeData" collapsible
                    class="transition-transform duration-500 origin-center" :class="{ 'rotate-180': isInverted }">
                    <template #default="slotProps">
                        <div class="flex flex-col text-left rounded-xl shadow-sm hover:shadow-xl transition-all duration-300 w-[240px] bg-white dark:bg-surface-800 border-l-4 relative group overflow-hidden"
                            :class="[
                                { 'rotate-180': isInverted },
                                slotProps.node.data.quantityScrap > 0 ? 'border-l-red-500 border-y border-r border-y-red-100 border-r-red-100 dark:border-red-900/50' : 'border-l-primary-500 border-y border-r border-surface-200 dark:border-surface-700'
                            ]">
                            <div
                                class="absolute top-0 right-0 px-2 py-1 bg-surface-100 dark:bg-surface-700 rounded-bl-lg text-[9px] font-mono text-surface-500 font-bold">
                                #{{ slotProps.node.data.id || slotProps.node.key }}
                            </div>

                            <div class="p-3 pt-4">
                                <div class="mb-1">
                                    <span
                                        class="text-[10px] font-black uppercase tracking-widest text-surface-400 block mb-0.5">
                                        {{ slotProps.node.data.phase }}
                                    </span>
                                </div>

                                <div class="mb-3">
                                    <div
                                        class="text-xs text-primary-600 dark:text-primary-400 font-mono font-bold mb-0.5">
                                        {{ slotProps.node.data.productCode }}
                                    </div>
                                    <div class="text-sm font-bold text-surface-800 dark:text-surface-100 leading-tight line-clamp-2"
                                        :title="slotProps.node.data.productDesc">
                                        {{ slotProps.node.data.productDesc }}
                                    </div>
                                </div>

                                <div
                                    class="grid grid-cols-2 gap-2 bg-surface-50 dark:bg-surface-900/50 rounded-lg p-2 mb-2 border border-surface-100 dark:border-surface-700/50">
                                    <div class="flex flex-col">
                                        <span class="text-[9px] uppercase font-bold text-surface-400">OK</span>
                                        <span class="text-sm font-bold text-green-600 dark:text-green-400">
                                            {{ slotProps.node.data.quantity }}
                                        </span>
                                    </div>
                                    <div
                                        class="flex flex-col items-end border-l border-surface-200 dark:border-surface-700 pl-2">
                                        <span class="text-[9px] uppercase font-bold"
                                            :class="slotProps.node.data.quantityScrap > 0 ? 'text-red-500' : 'text-surface-300'">
                                            NOK
                                        </span>
                                        <span class="text-sm font-bold"
                                            :class="slotProps.node.data.quantityScrap > 0 ? 'text-red-600' : 'text-surface-300'">
                                            {{ slotProps.node.data.quantityScrap }}
                                        </span>
                                    </div>
                                </div>

                                <div
                                    class="flex justify-between items-end border-t border-surface-100 dark:border-surface-700 pt-2">
                                    <div class="flex flex-col max-w-[65%]">
                                        <span
                                            class="text-[10px] text-surface-400 font-bold flex items-center gap-1 mb-0.5">
                                            <i class="pi pi-calendar text-[9px]"></i> {{ slotProps.node.data.date }}
                                        </span>
                                        <span class="text-[9px] text-surface-500 truncate flex items-center gap-1"
                                            :title="slotProps.node.data.team">
                                            <i class="pi pi-users text-[8px]"></i> {{ slotProps.node.data.team }}
                                        </span>
                                        <span v-if="slotProps.node.data.shift" class="text-[8px] text-surface-400 ml-4">
                                            {{ slotProps.node.data.shift }}
                                        </span>
                                    </div>

                                    <Button icon="pi pi-external-link" rounded text severity="secondary" size="small"
                                        class="w-6 h-6 !p-0"
                                        @click="openFullDetail(slotProps.node.data.id || slotProps.node.key)"
                                        v-tooltip.top="'Ver Ficha Técnica'" />
                                </div>
                            </div>
                        </div>
                    </template>
                </OrganizationChart>
            </div>
        </div>

        <TrackingDetailDialog v-model:visible="showDetail" :item="detailItem" />
    </Dialog>
</template>

<style scoped>
/* Mantener el origen de transformación por si el usuario usa el botón de invertir */
:deep(.p-organizationchart),
:deep(.p-organizationchart-table) {
    transform-origin: center center;
}

/* Conectores sutiles */
:deep(.p-organizationchart .p-organizationchart-line-down),
:deep(.p-organizationchart .p-organizationchart-line-left),
:deep(.p-organizationchart .p-organizationchart-line-right) {
    border-color: var(--surface-300) !important;
}

.dark :deep(.p-organizationchart .p-organizationchart-line-down),
.dark :deep(.p-organizationchart .p-organizationchart-line-left),
.dark :deep(.p-organizationchart .p-organizationchart-line-right) {
    border-color: var(--surface-600) !important;
}
</style>