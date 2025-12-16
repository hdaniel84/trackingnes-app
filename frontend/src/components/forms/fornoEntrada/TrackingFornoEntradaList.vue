<script setup>
import { onMounted, ref } from 'vue';
import { useTrackingFornoEntradaStore } from '@/stores/trackingFornoEntradaStore';
import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Tag from 'primevue/tag';
import { useFormatters } from '@/layout/composables/useFormatters';

const { formatDateTime } = useFormatters();
const store = useTrackingFornoEntradaStore();

const selectedItem = ref(null);
const showDetailDialog = ref(false);
const showEditDialog = ref(false);

onMounted(() => {
    store.fetchAll({ page: 0, size: 10, sort: 'startTime,desc' });
});

const openDetailDialog = (item) => {
    selectedItem.value = item;
    showDetailDialog.value = true;
};

const openEditDialog = (item) => {
  selectedItem.value = item;
  showEditDialog.value = true;
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
                class="bg-surface-50 dark:bg-surface-800 p-2 rounded-xl border border-surface-200 dark:border-surface-700 shadow-md transition duration-300 hover:shadow-lg hover:bg-surface-100 dark:hover:bg-surface-700 cursor-pointer">
                <div class="flex justify-between items-start gap-4">

                    <div class="flex items-start gap-3 flex-1" @click="openDetailDialog(item)">
                        <i class="pi pi-pallet text-3xl text-primary-600 dark:text-primary-400 mt-1"></i>

                        <div class="flex flex-col w-full">
                            <div
                                class="font-semibold text-surface-900 dark:text-surface-0 leading-tight mb-1 flex justify-end">
                                Vagona #{{ item.wagonId }}
                                <Tag :value="`${item.quantity} Un.`" severity="success" class="text-sm font-bold ml-6">
                                </Tag>

                            </div>
                            <span class="text-sm text-surface-600 dark:text-surface-300">
                                <i class="pi pi-bullseye mr-1"></i> {{ item.productDescription || 'Produto Desconhecido'
                                }}
                            </span>

                            <span class="text-xs text-surface-400 dark:text-surface-500 mt-2">
                                <i class="pi pi-calendar mr-1"></i> {{ formatDateTime(item.startTime) }}
                            </span>
                        </div>
                    </div>
                </div>

                <div class="flex justify-between items-start gap-4">
                    <div
                        class="text-xs italic text-surface-500 dark:text-surface-400 mt-3 border-t border-surface-100 dark:border-surface-700 pt-2 line-clamp-2">
                        **Equipa:** {{ item.teamDescription }} ({{ item.teamSectionDescription }})
                    </div>
                    <div class="text-xs">
                        <Button icon="pi pi-pencil" severity="secondary" @click="openEditDialog(item)" raised />
                    </div>
                </div>

            </div>

            <div v-if="store.items && store.items.length === 0"
                class="text-center py-6 text-surface-500 dark:text-surface-400">
                <i class="pi pi-box text-3xl mb-2"></i>
                <p>Nenhum registo de produção encontrado.</p>
            </div>
        </div>

        <!-- Diálogo de detalle -->
        <TrackingFornoEntradaDetailDialog v-model:visible="showDetailDialog" :item="selectedItem" />

        <TrackingFornoEntradaEditDialog v-model:visible="showEditDialog" :item="selectedItem" />
    </div>
</template>
