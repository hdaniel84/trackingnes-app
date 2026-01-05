<script setup>
import { onMounted, computed } from 'vue';
import { useEquipmentStore } from '@/stores/equipmentStore';
import MultiSelect from 'primevue/multiselect';

const props = defineProps({
  modelValue: { type: Array, default: () => [] }, // Array de IDs
  filterSection: { type: Number, default: null }
});

const emit = defineEmits(['update:modelValue']);
const store = useEquipmentStore();

// Computada "Passthrough"
// Esto es lo correcto: Lee del prop, escribe emitiendo el evento.
const selectedIds = computed({
  get: () => props.modelValue || [], // Aseguramos que nunca sea null
  set: (val) => emit('update:modelValue', val) // Emitimos el array de IDs nuevo
});

const filteredOptions = computed(() => {
  let items = store.auxiliaryEquipments;
  if (props.filterSection) {
    items = items.filter(e =>
      e.sectionId == props.filterSection
    );
  }
  return items;
});

onMounted(() => {
  if (store.items.length === 0) store.fetchAll();
});
</script>

<template>
  <div class="w-full">
    <div v-if="store.loading">
      <Skeleton width="100%" height="3rem" borderRadius="6px"></Skeleton>
    </div>
    <MultiSelect v-else v-model="selectedIds" :options="filteredOptions" optionLabel="description" optionValue="id"
      display="chip" inputId="aux_select" placeholder="Selecionar Equipamentos Auxiliares" filter showClear fluid class="w-full" :disabled="filteredOptions.length === 0">
      <template #option="slotProps">
        <div class="flex flex-col">
          <span class="font-bold text-sm">{{ slotProps.option.description }}</span>
          <span class="text-xs text-surface-500">{{ slotProps.option.sapCode }}</span>
        </div>
      </template>
    </MultiSelect>
  </div>
</template>