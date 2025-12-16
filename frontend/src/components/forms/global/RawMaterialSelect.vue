<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useRawMaterialStore } from '@/stores/rawMaterialStore';
import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Select from 'primevue/select';

const props = defineProps({
  modelValue: {type: Object,default: null},
  filterSection: { type: String, default: null }
});

const emit = defineEmits(['update:modelValue']);

const rawMaterialsStore = useRawMaterialStore();
const selectedRawMaterial = ref(null);

watch(() => props.modelValue, (newVal) => {
  if (newVal && rawMaterialsStore.items.length > 0) {
    const found = rawMaterialsStore.items.find(rm => rm.id === newVal.id);
    selectedRawMaterial.value = found || newVal;
  } else {
    selectedRawMaterial.value = null;
  }
}, { immediate: true });

watch(selectedRawMaterial, (newVal) => {
  emit('update:modelValue', newVal);
});

onMounted(async () => {
  if (rawMaterialsStore.items.length === 0) {
    await rawMaterialsStore.fetchAll();
    if (props.modelValue) {
      const found = rawMaterialsStore.items.find(rm => rm.id === props.modelValue.id);
      selectedRawMaterial.value = found || props.modelValue;
    }
  }
});

// Filtro
const filteredItems = computed(() => {
  const items = rawMaterialsStore.items;
  const filter = props.filterSection;
  if (!filter || items.length === 0) return items;
  return items.filter(eq =>
    eq.sectionDescription &&
    eq.sectionDescription.toLowerCase().includes(filter.toLowerCase())
  );
});
</script>

<template>
  <div>
    <div v-if="rawMaterialsStore.loading">
      <ProgressSpinner style="width: 50px; height: 50px" strokeWidth="8" animationDuration=".5s" />
      <p>Carregando...</p>
    </div>

    <div v-else-if="rawMaterialsStore.error">
      <Message severity="error">{{ rawMaterialsStore.error }}</Message>
    </div>

    <div v-else class="p-field">
      <span class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">Matéria prima</span>
      <Select inputId="rawMaterialSelect" id="rawMaterial" class="mt-2" v-model="selectedRawMaterial"
        :options="filteredItems" optionLabel="description" placeholder="Selecciona" filter />
    </div>
  </div>
</template>
