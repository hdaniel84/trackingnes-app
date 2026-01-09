<!-- TrackingSourceManager.vue -->
<script setup>
import { ref, watch } from 'vue';
import TrackingSourceSelect from './TrackingSourceSelect.vue';
import InputNumber from 'primevue/inputnumber';
import Button from 'primevue/button';

const props = defineProps({
  modelValue: { type: Array, default: () => [] },
  allowedPhases: Array,
  targetReferenceId: [Number, String],
  matchReference: Boolean,
  filterType: String,
  disabled: Boolean,
  initialData: { type: Array, default: () => [] }
});
console.log(props.initialData);
const emit = defineEmits(['update:modelValue']);
const rows = ref([]);

const emitUpdate = () => {
  const cleanPayload = rows.value.map(r => ({
    trackingId: r.trackingId,
    quantityUsed: r.quantityUsed
  }));
  emit('update:modelValue', cleanPayload);
};

const onSourceAdded = (sourceObj) => {
  if (rows.value.some(r => r.trackingId === sourceObj.id)) return;
  const newItem = {
    trackingId: sourceObj.id,
    quantityUsed: null,
    productDesc: sourceObj.product?.description,
    available: (sourceObj.remainingQuantity !== undefined && sourceObj.remainingQuantity !== null)
      ? sourceObj.remainingQuantity : sourceObj.quantity
  };
  rows.value.unshift(newItem);
};

watch(() => props.initialData, (val) => {
  if (val && val.length > 0) {
    rows.value = val.map(item => {

      // A. Obtenemos el stock remanente que reporta la base de datos
      // (Si es null, asumimos 0, pero idealmente el DTO nuevo ya lo trae)
      const dbRemaining = (item.remainingQuantity !== undefined && item.remainingQuantity !== null)
        ? item.remainingQuantity : 0; 

      // B. Obtenemos lo que este registro YA está consumiendo actualmente
      const currentUsage = item.quantityUsed || 0;
      const realAvailable = dbRemaining + currentUsage;

      return {
        trackingId: item.trackingId || item.id,
        quantityUsed: item.quantityUsed,
        productDesc: item.productDescription || item.product?.description,
        available: realAvailable
      };
    });

    // Importante: Emitimos para que el padre sepa que hay datos cargados
    emitUpdate();
  } else {
    rows.value = [];
  }
}, { immediate: true });

const removeRow = (index) => {
  rows.value.splice(index, 1);
  emitUpdate();
};
</script>

<template>
  <div class="w-full">
    <label class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">
      Origens (Lotes Anteriores)
    </label>

    <div class="flex flex-col md:flex-row gap-4 items-start">

      <div class="w-full md:w-5/12 xl:w-4/12 flex flex-col gap-2 sticky top-4">
        <div class="bg-surface-50 dark:bg-surface-800 p-3 rounded-lg border border-surface-200 dark:border-surface-700">
          <span class="text-[10px] font-bold text-surface-500 uppercase block mb-2">Adicionar Novo</span>

          <TrackingSourceSelect :modelValue="[]" @item-select="onSourceAdded" :allowedPhases="allowedPhases"
            :target-reference-id="targetReferenceId" :match-reference="matchReference" :filter-type="filterType"
            :disabled="disabled" placeholder="Pesquisar lote..." :label="null" />

          <div class="mt-2 text-[10px] text-surface-400 leading-tight">
            <i class="pi pi-info-circle mr-1"></i>
            Selecione primeiro produto de saída
          </div>
        </div>
      </div>

      <div class="w-full md:w-7/12 xl:w-8/12">

        <div v-if="rows.length > 0" class="flex flex-col gap-2">
          <transition-group name="list" tag="div" class="flex flex-col gap-2">
            <div v-for="(row, idx) in rows" :key="row.trackingId"
              class="group flex items-center gap-3 bg-white dark:bg-surface-900 p-2 pl-3 rounded-lg shadow-sm border border-surface-200 dark:border-surface-700 hover:border-primary-300 dark:hover:border-primary-700 transition-colors">

              <div
                class="flex flex-col items-center justify-center min-w-[36px] h-[36px] bg-surface-100 dark:bg-surface-800 rounded border border-surface-200 dark:border-surface-700">
                <span class="text-[9px] font-bold text-surface-400 leading-none">REF</span>
                <span class="text-xs font-bold text-surface-700 dark:text-surface-200 leading-none mt-0.5">#{{
                  row.trackingId }}</span>
              </div>

              <div class="flex-1 min-w-0 flex flex-col justify-center">
                <span class="text-xs font-semibold text-surface-800 dark:text-surface-100 truncate"
                  :title="row.productDesc">
                  {{ row.productDesc }}
                </span>

                <div class="flex items-center gap-2 mt-0.5">
                  <span class="text-[10px] text-surface-500 bg-surface-50 px-1 rounded border border-surface-100">
                    Qtd. Disp: <strong class="text-green-600">{{ row.available }}</strong>
                  </span>
                </div>
              </div>

              <div class="w-24 shrink-0 flex flex-col">
                <InputNumber v-model="row.quantityUsed" @update:modelValue="emitUpdate" @blur="emitUpdate"
                  :max="row.available" placeholder="Qtd usado" size="small" inputClass="text-right font-bold text-sm"
                  class="w-full" :class="{ 'p-invalid': !row.quantityUsed && row.quantityUsed !== 0 }" />
              </div>

              <Button icon="pi pi-trash" text rounded severity="danger" @click="removeRow(idx)"
                class="w-8 h-8 opacity-50 group-hover:opacity-100 transition-opacity" />
            </div>
          </transition-group>
        </div>

        <div v-else
          class="h-32 flex flex-col items-center justify-center border-2 border-dashed border-surface-200 dark:border-surface-700 rounded-lg bg-surface-50/50 dark:bg-surface-800/20 text-surface-400">
          <i class="pi pi-link text-2xl mb-2 opacity-50"></i>
          <span class="text-xs font-medium">Nenhuma origem selecionada</span>
          <span class="text-[10px] opacity-70">Utilize a pesquisa ao lado para adicionar.</span>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>
.list-enter-active,
.list-leave-active {
  transition: all 0.3s ease;
}

.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateX(10px);
}
</style>