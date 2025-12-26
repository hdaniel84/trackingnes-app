<script setup>
import { onMounted, computed } from 'vue';
import { useParameterStore } from '@/stores/parameterStore';
import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Select from 'primevue/select';

const props = defineProps({
  modelValue: {
    type: [Number, String, null],
    default: null
  },
  phaseId: { type: Number, default: null },
  disabled: { type: Boolean, default: false },
  excludedIds: { type: Array, default: () => [] }
});

const emit = defineEmits(['update:modelValue']);
const store = useParameterStore();

// Binding reactivo usando computed
const selectedValue = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});

// Lógica de Filtrado por Fase
const filteredItems = computed(() => {
  const items = store.items;

  return items.filter(item => {
    // 1. Filtro de Fase
    const matchPhase = !props.phaseId || item.phase?.id === props.phaseId;

    // 2. Filtro de Exclusión
    const isExcluded = props.excludedIds.includes(item.id);

    return matchPhase && !isExcluded;
  });
});

onMounted(async () => {
  if (store.items.length === 0) {
    await store.fetchAll();
  }
});
</script>

<template>
  <div class="w-full">
    <div v-if="store.loading" class="flex items-center justify-center py-2">
      <ProgressSpinner style="width: 25px; height: 25px" strokeWidth="6" animationDuration=".5s" />
    </div>

    <div v-else-if="store.error">
      <Message severity="error" :closable="false" size="small">{{ store.error }}</Message>
    </div>

    <div v-else>
      <Select v-model="selectedValue" :options="filteredItems" optionLabel="description" optionValue="id"
        placeholder="Tipo" filter showClear fluid class="w-full" :disabled="disabled" />
    </div>
  </div>
</template>