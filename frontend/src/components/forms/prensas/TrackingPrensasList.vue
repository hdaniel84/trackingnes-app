<script setup>
import { onMounted } from 'vue';
import { useTrackingPrensasStore } from '@/stores/trackingPrensasStore';

import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Tag from 'primevue/tag'; // Útil para mostrar la cantidad

const store = useTrackingPrensasStore();

onMounted(() => {
    store.fetchAll({ page: 0, size: 10, sort: 'startTime,desc' }); // cargar registros al montar
});

/**
 * Función auxiliar para formatear la fecha
 */
const formatDateTime = (dateString) => {
    if (!dateString) return 'N/A';
    // Asumiendo que es una cadena ISO o un objeto Date, ajustamos a la localización (PT-PT)
    const date = new Date(dateString);
    if (isNaN(date)) return dateString; // Si no es una fecha válida, devuelve la cadena original

    return date.toLocaleDateString('pt-PT', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
};
</script>

<template>
    <div class="w-full">
        <p class="text-xl  mb-5 text-surface-800 dark:text-surface-100 border-b pb-2">
            <i class="pi pi-history mr-2 text-primary-500"></i> Últimos Registos
        </p>

        <div v-if="store.loading" class="flex flex-col items-center py-10">
            <ProgressSpinner style="width: 50px; height: 50px" strokeWidth="6" animationDuration=".5s" />
            <p class="mt-3 text-surface-600 dark:text-surface-400">A carregar registos...</p>
        </div>

        <div v-else-if="store.fetchError">
            <Message severity="error">{{ store.fetchError }}</Message>
        </div>

        <div v-else class="flex flex-col gap-4 max-h-[80vh] overflow-y-auto pr-2">

            <div v-for="item in store.items" :key="item.id"
                class="bg-surface-50 dark:bg-surface-800 p-4 rounded-xl border border-surface-200 dark:border-surface-700 shadow-md transition duration-300 hover:shadow-lg hover:bg-surface-100 dark:hover:bg-surface-700 cursor-pointer">
                <div class="flex justify-between items-start gap-4">

                    <div class="flex items-start gap-3 flex-1">
                        <i class="pi pi-pallet text-3xl text-primary-600 dark:text-primary-400 mt-1"></i>

                        <div class="flex flex-col">
                            <div class="font-semibold text-surface-900 dark:text-surface-0 leading-tight mb-3">
                                Carro #{{ item.logisticUnit }}
                                <Tag :value="`${item.quantity} Un.`" severity="success" class="text-sm font-bold p-2 ml-6">
                                </Tag>

                            </div>
                            <span class="text-sm text-surface-600 dark:text-surface-300">
                                <i class="pi pi-bullseye mr-1"></i> {{ item.productDescription || 'Produto Desconhecido' }}
                            </span>

                            <span class="text-xs text-surface-400 dark:text-surface-500 mt-2">
                                <i class="pi pi-calendar mr-1"></i> {{ formatDateTime(item.startTime) }}
                            </span>
                            <span class="text-xs text-surface-400 dark:text-surface-500 mt-2">
                                <i class="pi pi-chart-scatter mr-1"></i> {{ item.equipmentDescription }}
                            </span>
                        </div>
                    </div>
                </div>

                <p class="text-xs italic text-surface-500 dark:text-surface-400 mt-3 border-t border-surface-100 dark:border-surface-700 pt-2 line-clamp-2">
                    **Equipa:** {{ item.teamDescription }} ({{ item.teamSectionDescription }})
                </p>

            </div>

            <div v-if="store.items && store.items.length === 0"
                class="text-center py-6 text-surface-500 dark:text-surface-400">
                <i class="pi pi-box text-3xl mb-2"></i>
                <p>Nenhum registo de produção encontrado.</p>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* Estilos para asegurar que las etiquetas Tag no se rompan y el scroll sea suave */
.line-clamp-2 {
    overflow: hidden;
    display: -webkit-box;
    -webkit-box-orient: vertical;

}
</style>