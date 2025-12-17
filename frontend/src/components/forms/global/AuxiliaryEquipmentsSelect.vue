<script setup>
import { onMounted, computed } from 'vue';
import { useEquipmentStore } from '@/stores/equipmentStore';
import MultiSelect from 'primevue/multiselect';
import ProgressSpinner from 'primevue/progressspinner';

const props = defineProps({
  modelValue: { type: Array, default: () => [] }, // Array de IDs
  filterSection: { type: String, default: null }
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
      e.sectionDescription?.toLowerCase().includes(props.filterSection.toLowerCase())
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
     <div v-if="store.loading"><ProgressSpinner style="width:20px;height:20px"/></div>
     <MultiSelect
        v-else
        v-model="selectedIds" 
        :options="filteredOptions"
        optionLabel="description"
        optionValue="id" 
        display="chip"
        placeholder="Selecionar Equipamentos Auxiliares"
        filter
        showClear
        fluid
        class="w-full"
     >
        <template #option="slotProps">
            <div class="flex flex-col">
              <span class="font-bold text-sm">{{ slotProps.option.description }}</span>
              <span class="text-xs text-surface-500">{{ slotProps.option.sapCode }}</span>
           </div>
        </template>
     </MultiSelect>
  </div>
</template>