<script setup>
import { ref, watch } from 'vue';
import OrganizationChart from 'primevue/organizationchart';
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';
import ToggleButton from 'primevue/togglebutton';
import TrackingService from '@/api/trackingApi'; 
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

const isInverted = ref(true); // Estado para invertir

const loadTree = async () => {
    if (!props.trackingId) return;
    loading.value = true;
    try {
        const response = await TrackingService.getTraceability(props.trackingId);
        treeData.value = response.data;
    } catch (e) {
        console.error("Error loading tree", e);
    } finally {
        loading.value = false;
    }
};

const openFullDetail = async (nodeId) => {
    try {
        const response = await TrackingService.getById(nodeId);
        detailItem.value = response.data;
        showDetail.value = true;
    } catch (e) {
        console.error("Error loading detail", e);
    }
};

watch(() => props.visible, (val) => {
    if (val) {
        loadTree();
        isInverted.value = true; 
    }
});
</script>

<template>
    <Dialog 
        :visible="props.visible" 
        @update:visible="emit('update:visible', $event)" 
        modal 
        header="Rastreabilidade" 
        :style="{ width: '90vw', maxWidth: '1200px' }"
        maximizable
        :contentStyle="{ padding: '0px' }"
    >
        <template #header>
            <div class="flex items-center justify-between w-full mr-4">
                <span class="p-dialog-title">Árvore de Rastreabilidade</span>
                <div class="flex gap-2">
                    <ToggleButton 
                        v-model="isInverted" 
                        onLabel="Vista Ascendente" 
                        offLabel="Vista Descendente" 
                        onIcon="pi pi-arrow-up" 
                        offIcon="pi pi-arrow-down" 
                        class="w-48 text-xs"
                    />
                </div>
            </div>
        </template>

        <div v-if="loading" class="flex justify-center p-20">
            <i class="pi pi-spin pi-spinner text-4xl text-primary-500"></i>
        </div>

        <div v-else class="overflow-auto p-8 flex justify-center bg-surface-50 dark:bg-surface-900 min-h-[600px]">
            
            <OrganizationChart 
                :value="treeData" 
                collapsible 
                class="transition-transform duration-500"
                :class="{ 'rotate-180': isInverted }" 
            >
                <template #default="slotProps">
                    <div 
                        class="flex flex-col text-left p-3 rounded-lg shadow-md hover:shadow-lg transition-all duration-300 min-w-[220px] max-w-[220px] bg-white dark:bg-surface-800 border border-surface-200 dark:border-surface-700 relative group"
                        :class="[slotProps.node.styleClass, { 'rotate-180': isInverted }]"
                    >
                        
                        <div class="absolute -top-3 -right-2 bg-surface-900 text-white text-[10px] px-2 py-0.5 rounded-full font-mono shadow-sm z-10">
                            #{{ slotProps.node.data.id }}
                        </div>

                        <div class="flex justify-between items-start mb-2 border-b border-surface-100 dark:border-surface-700 pb-1">
                            <span class="text-xs font-black uppercase tracking-wider text-surface-500 dark:text-surface-400">
                                {{ slotProps.node.data.label }}
                            </span>
                             <div class="flex items-center gap-1 text-[10px] text-surface-600 bg-surface-100 dark:bg-surface-700 px-1.5 rounded">
                                <i class="pi pi-box text-[10px]"></i>
                                {{ slotProps.node.data.logisticUnit }}
                            </div>
                        </div>

                        <div class="mb-3">
                             <div class="text-xs text-primary-600 dark:text-primary-400 font-mono mb-0.5">
                                {{ slotProps.node.data.productCode }}
                             </div>
                             <div class="text-sm font-bold text-surface-800 dark:text-surface-100 leading-tight">
                                {{ slotProps.node.data.productDesc }}
                             </div>
                        </div>

                        <div class="flex justify-between items-center bg-surface-50 dark:bg-surface-900/50 p-1.5 rounded mb-2">
                             <div class="flex flex-col">
                                <span class="text-[10px] text-surface-500">Quantidade</span>
                                <span class="text-sm font-bold text-green-600 dark:text-green-400">
                                    {{ slotProps.node.data.quantity }}
                                </span>
                             </div>
                             <div class="flex flex-col items-end">
                                <span class="text-[10px] text-surface-500">{{ slotProps.node.data.date }}</span>
                                <span class="text-[10px] font-mono">{{ slotProps.node.data.time }}</span>
                             </div>
                        </div>

                        <div class="flex justify-between items-center pt-1 mt-1">
                             <div class="flex flex-col truncate max-w-[70%]">
                                <div class="flex items-center gap-1 text-xs text-surface-600 dark:text-surface-300 truncate">
                                    <i class="pi pi-users text-[10px]"></i>
                                    <span class="truncate" :title="slotProps.node.data.team">{{ slotProps.node.data.team }}</span>
                                </div>
                                <span class="text-[10px] text-surface-400 ml-4">{{ slotProps.node.data.shift }}</span>
                             </div>
                            
                            <Button 
                                icon="pi pi-search" 
                                rounded text severity="secondary" size="small" 
                                class="w-7 h-7"
                                @click="openFullDetail(slotProps.node.data.id)"
                                v-tooltip.bottom="'Ver Detalhe Completo'"
                             />
                        </div>
                    </div>
                </template>
            </OrganizationChart>
        </div>
        
        <TrackingDetailDialog v-model:visible="showDetail" :item="detailItem" />
    </Dialog>
</template>

<style scoped>
/* No necesitamos CSS complejo, solo Tailwind 'rotate-180'. 
   Pero debemos arreglar las líneas de PrimeVue para que no se vean raras al rotar.
*/

/* Forzamos que la rotación se origine en el centro para que no se desplace */
:deep(.p-organizationchart),
:deep(.p-organizationchart-table) {
    transform-origin: center center;
}

/* Color de líneas */
:deep(.p-organizationchart .p-organizationchart-line-down) {
    background-color: var(--surface-300) !important;
}
.dark :deep(.p-organizationchart .p-organizationchart-line-down) {
    background-color: var(--surface-600) !important;
}
</style>