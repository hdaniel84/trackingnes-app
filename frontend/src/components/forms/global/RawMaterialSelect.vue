<script setup>
import { onMounted, computed } from 'vue';
import { useRawMaterialStore } from '@/stores/rawMaterialStore';
import Message from 'primevue/message';
import Select from 'primevue/select';

const props = defineProps({
  modelValue: { type: [Number, String, null], default: null },
  phaseId: { type: Number, default: null },
  // NUEVAS PROP
  disabled: { type: Boolean, default: false },
  excludedIds: { type: Array, default: () => [] }
});

const emit = defineEmits(['update:modelValue']);
const store = useRawMaterialStore();

const filteredItems = computed(() => {
  const items = store.items;

  if (!props.phaseId) return items;

  return items.filter(item => {
    const matchPhase = !props.phaseId || item.phase?.id === props.phaseId;

    // 2. Filtro de Exclusión (Nuevo)
    // Si el ID está en la lista de excluidos, NO lo mostramos
    const isExcluded = props.excludedIds.includes(item.id);

    return matchPhase && !isExcluded;
  });
});

const selectedValue = computed({
  get: () => {
    return store.items.find(item => item.id === props.modelValue) || null;
  },
  set: (val) => {
    emit('update:modelValue', val ? val.id : null);
  }
});

onMounted(async () => {
  // Optimización: Solo cargar si está vacío para evitar llamadas repetidas en listas
  if (store.items.length === 0) await store.fetchAll();
});
</script>

<template>
  <div class="w-full">
    <div v-if="store.loading && store.items.length === 0" class="flex items-center justify-center py-2">
      <Skeleton width="100%" height="3rem" borderRadius="6px"></Skeleton>
    </div>

    <div v-else-if="store.error">
      <Message severity="error" :closable="false" size="small">{{ store.error }}</Message>
    </div>

    <div v-else>
      <Select v-model="selectedValue" :options="filteredItems" optionLabel="description"
        placeholder="Selecione Matéria Prima" filter :filterFields="['sapCode', 'description']" showClear fluid
        class="w-full" :disabled="props.disabled">
        
        <template #option="slotProps">
          <div class="flex flex-col">
            <span class="font-bold text-sm">{{ slotProps.option.sapCode }}</span>
            <span class="text-xs text-surface-600">{{ slotProps.option.description }}</span>
          </div>
        </template>

        <template #value="slotProps">
          <div v-if="slotProps.value" class="flex gap-2 text-sm">
            <span>{{ slotProps.value.description }}</span>
          </div>
          <span v-else>{{ slotProps.placeholder }}</span>
        </template>
      </Select>
    </div>
  </div>
</template>