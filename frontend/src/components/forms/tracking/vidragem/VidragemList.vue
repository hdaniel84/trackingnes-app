<script setup>
import { ref, onMounted } from 'vue';
import { useTrackingStore } from '@/stores/trackingStore';
import { useFormatters } from '@/layout/composables/useFormatters'; 

// PrimeVue
import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Tag from 'primevue/tag';
import Button from 'primevue/button'; 
import VidragemEditDialog from './VidragemEditDialog.vue'; 
import VidragemDetailDialog from './VidragemDetailDialog.vue'; 

const store = useTrackingStore();
const { formatDateTime } = useFormatters(); // Usamos el formateador

const selectedItem = ref(null);
const showEditDialog = ref(false);
const showDetailDialog = ref(false);
const CURRENT_PHASE_ID = 2; // 2 = Vidragem

onMounted(() => {
  store.fetchAll({ page: 0, size: 10, sort: 'id,desc', phaseId: CURRENT_PHASE_ID });
});

const openEditDialog = (item) => {
  selectedItem.value = item; 
  showEditDialog.value = true;
};

const openDetailDialog = (item) => {
  selectedItem.value = item;
  showDetailDialog.value = true;
};
</script>

<template>
  <div class="w-full">
    <p class="text-xl mb-5 text-surface-800 dark:text-surface-100 border-b pb-2 flex items-center">
      <i class="pi pi-history mr-2 text-primary-500"></i> Últimos Registos
    </p>

    <div v-if="store.loading" class="flex flex-col items-center py-10">
      <ProgressSpinner style="width: 50px; height: 50px" strokeWidth="6" animationDuration=".5s" />
      <p class="mt-3 text-surface-600 dark:text-surface-400">A carregar registos...</p>
    </div>

    <div v-else-if="store.fetchError">
      <Message severity="error">{{ store.fetchError }}</Message>
    </div>

    <div v-else class="flex flex-col gap-4 max-h-[100vh] overflow-y-auto pr-2">
      <div v-for="item in (store.items || [])" :key="item.id" 
        class="bg-surface-50 dark:bg-surface-800 p-3 rounded-xl border border-surface-200 dark:border-surface-700 shadow-sm transition duration-300 hover:shadow-md hover:bg-surface-100 dark:hover:bg-surface-700">
        
        <div class="flex justify-between items-start gap-4 cursor-pointer group" @click="openDetailDialog(item)">
          <div class="flex items-start gap-3 flex-1">
            <div class="bg-surface-200 dark:bg-surface-700 p-2 rounded-full mt-1">
                 <i class="pi pi-pallet text-xl text-primary-600 dark:text-primary-400"></i>
            </div>
           
            <div class="flex flex-col w-full">
              <div class="flex justify-between items-center mb-1">
                 <span class="font-bold text-surface-900 dark:text-surface-0">#{{ item.id }}</span>
                 <Tag :value="`${item.quantity} Un.`" severity="success" class="text-xs font-bold" />
              </div>

              <div class="space-y-1">
                  <div class="text-sm font-semibold text-surface-800 dark:text-surface-100">
                     {{ item.product?.description || 'Produto Desconhecido' }}
                  </div>

                  <div class="text-xs text-surface-500 dark:text-surface-400 flex items-center">
                    <i class="pi pi-car mr-2 text-xs"></i> 
                    Carro: {{ item.logisticUnit || 'N/A' }}
                  </div>

                  <div class="text-xs text-surface-500 dark:text-surface-400 flex items-center">
                    <i class="pi pi-calendar mr-2"></i> 
                    {{ formatDateTime(item.startTime) }}
                  </div>

                  <div class="text-xs text-surface-500 dark:text-surface-400 flex items-center">
                    <i class="pi pi-cog mr-2"></i> 
                    {{ item.equipment?.description || 'Sem equipamento' }}
                  </div>
              </div>
            </div>
          </div>
        </div>

        <div class="flex justify-between items-center mt-3 pt-2 border-t border-surface-200 dark:border-surface-700">
          <div class="text-xs italic text-surface-500 dark:text-surface-400 truncate max-w-[70%]">
            <i class="pi pi-users mr-1"></i>
            {{ item.team?.description }} 
            <span v-if="item.team?.sectionDescription">({{ item.team.sectionDescription }})</span>
          </div>
          
          <Button 
            icon="pi pi-pencil" 
            text 
            rounded 
            severity="secondary" 
            aria-label="Editar"
            @click.stop="openEditDialog(item)" 
          />
        </div>
      </div>

      <div v-if="(!store.items || store.items.length === 0)"
        class="text-center py-10 text-surface-500 dark:text-surface-400 bg-surface-50 rounded-xl border border-dashed border-surface-300">
        <i class="pi pi-box text-4xl mb-3 text-surface-300"></i>
        <p>Nenhum registo encontrado.</p>
      </div>
    </div>

    <VidragemEditDialog v-model:visible="showEditDialog" :item="selectedItem" />
    <VidragemDetailDialog v-model:visible="showDetailDialog" :item="selectedItem" />
  </div>
</template>