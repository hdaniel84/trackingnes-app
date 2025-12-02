<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRawMaterialStore } from '@/stores/rawMaterialStore';
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

const rawMaterialsStore = useRawMaterialStore();
const selectedRawMaterial = ref(props.modelValue);

watch(selectedRawMaterial, (newVal) => {
  emit('update:modelValue', newVal);
});

onMounted(() => {
  if (rawMaterialsStore.items.length === 0) {
    rawMaterialsStore.fetchAll();
  }
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
      <Select
      inputId="rawMaterialSelect"
        id="rawMaterial"
        class="mt-2"
        v-model="selectedRawMaterial"
        :options="rawMaterialsStore.items"
        optionLabel="description"
        placeholder="Selecciona"
        filter
      />
    </div>
  </div>
</template>
