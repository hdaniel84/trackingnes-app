<script setup>
import { ref, onMounted, watch } from 'vue';
import { useEquipmentStore } from '@/stores/equipmentStore';
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

const equipmentsStore = useEquipmentStore();
const selectedEquipment = ref(props.modelValue);

watch(selectedEquipment, (newVal) => {
  emit('update:modelValue', newVal);
});

onMounted(() => {
  if (equipmentsStore.items.length === 0) {
    equipmentsStore.fetchEquipments();
  }
});
</script>

<template>
  <div>
    <div v-if="equipmentsStore.loading">
      <ProgressSpinner style="width: 50px; height: 50px" strokeWidth="8" animationDuration=".5s" />
      <p>Carregando equipamentos...</p>
    </div>

    <div v-else-if="equipmentsStore.error">
      <Message severity="error">{{ equipmentsStore.error }}</Message>
    </div>

    <div v-else class="p-field">
      <span class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">Equipamento</span>
      <Select
        id="equipmentSelect" inputId="equipmentSelect"
        class="mt-2"
        v-model="selectedEquipment"
        :options="equipmentsStore.items"
        optionLabel="description"
        placeholder="Selecciona equipamento"
        filter
      />
    </div>
  </div>
</template>
