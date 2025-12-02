<script setup>
import { ref, onMounted, watch } from 'vue';
import { useShiftStore } from '@/stores/shiftStore';
import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Select from 'primevue/select';

const props = defineProps({
  modelValue: {
    type: Object,
    default: null
  }
});
const emit = defineEmits(['update:modelValue']);

const shiftsStore = useShiftStore();
const selectedShift = ref(props.modelValue);

watch(selectedShift, (newVal) => {
  emit('update:modelValue', newVal);
});

onMounted(() => {
  if (shiftsStore.items.length === 0) {
    shiftsStore.fetchShifts();
  }
});
</script>

<template>
  <div>
    <div v-if="shiftsStore.loading">
      <ProgressSpinner style="width: 50px; height: 50px" strokeWidth="8" animationDuration=".5s" />
      <p>Carregando turnos...</p>
    </div>

    <div v-else-if="shiftsStore.error">
      <Message severity="error">{{ shiftsStore.error }}</Message>
    </div>

    <div v-else class="p-field">
      <span class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">Turno</span>
      <Select inputId="shiftSelect"
        id="shift"
        class="mt-2"
        v-model="selectedShift"
        :options="shiftsStore.items"
        optionLabel="description"
        placeholder="Selecciona turno"
        filter
      />
    </div>
  </div>
</template>
