<script setup>
import { onMounted, computed } from 'vue';
import { useEquipmentStore } from '@/stores/equipmentStore';
import MultiSelect from 'primevue/multiselect';
import Skeleton from 'primevue/skeleton';
import Message from 'primevue/message';

const props = defineProps({
  modelValue: { type: Array, default: () => [] }, 
  label: { type: String, default: 'Equipamentos' },
  filterSection: { type: [Number, String], default: null },
  placeholder: { type: String, default: 'Selecionar equipamentos' }
});

const emit = defineEmits(['update:modelValue']);
const store = useEquipmentStore();

const selectedIds = computed({
  get: () => props.modelValue || [],
  set: (val) => emit('update:modelValue', val)
});

// Filtro de opciones
const filteredOptions = computed(() => {
  let items = store.items;

  if (!items || items.length === 0) return [];

  // Filtramos por sección si se provee la prop
  if (props.filterSection) {
    // Usamos '==' para que no importe si es string "1" o number 1
    items = items.filter(e => e.sectionId == props.filterSection);
  }

  return items;
});

onMounted(async () => {
  if (store.items.length === 0) {
    await store.fetchAll();
  }
});
</script>

<template>
  <div class="w-full">
    <div v-if="store.loading" class="py-2">
      <Skeleton width="100%" height="3rem" borderRadius="6px"></Skeleton>
    </div>

    <div v-else-if="store.error">
      <Message severity="error" :closable="false">{{ store.error }}</Message>
    </div>

    <div v-else>
      <label v-if="label" class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">
        {{ label }}
      </label>

      <MultiSelect v-model="selectedIds" :options="filteredOptions" optionLabel="description" optionValue="id"
        display="chip" :placeholder="placeholder" filter showClear fluid class="w-full h-12"
        :disabled="filteredOptions.length === 0" :maxSelectedLabels="3">
        <template #option="slotProps">
          <div class="flex flex-col">
            <span class="font-bold text-sm">{{ slotProps.option.description }}</span>
            <span v-if="slotProps.option.sapCode" class="text-xs text-surface-500">
              SAP: {{ slotProps.option.sapCode }}
            </span>
          </div>
        </template>

        <template #empty>
          <div class="p-2">Não há equipamentos disponíveis.</div>
        </template>
      </MultiSelect>

      <small v-if="filteredOptions.length === 0 && !store.loading" class="text-red-500 text-xs mt-1">
        Nenhum equipamento encontrado para esta seção.
      </small>
    </div>
  </div>
</template>