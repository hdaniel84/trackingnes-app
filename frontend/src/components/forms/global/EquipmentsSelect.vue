<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useEquipmentStore } from '@/stores/equipmentStore';
import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Select from 'primevue/select';

const props = defineProps({
  modelValue: {
    type: Object,
    default: null
  },
  filterSection: {
    type: String,
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

// PROPIEDAD COMPUTADA para aplicar el filtro localmente
const filteredEquipments = computed(() => {
  const items = equipmentsStore.items;
  const filter = props.filterSection;

  // Si no hay filtro o no hay elementos, retorna la lista completa
  if (!filter || items.length === 0) {
    return items;
  }

  // Filtra los elementos donde sectionDescription incluye el string de filterSection (ignorando mayúsculas)
  return items.filter(team =>
    team.sectionDescription &&
    team.sectionDescription.toLowerCase().includes(filter.toLowerCase())
  );
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
      <Select id="equipmentSelect" inputId="equipmentSelect" class="mt-2" v-model="selectedEquipment"
        :options="filteredEquipments" optionLabel="description" placeholder="Selecciona equipamento" filter />
    </div>
  </div>
</template>
