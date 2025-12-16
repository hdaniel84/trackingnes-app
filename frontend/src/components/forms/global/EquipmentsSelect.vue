<script setup>
import { onMounted, computed } from 'vue';
import { useEquipmentStore } from '@/stores/equipmentStore';
import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Select from 'primevue/select';

const props = defineProps({
  modelValue: { type: Object, default: null },
  filterSection: { type: String, default: null }
});

const emit = defineEmits(['update:modelValue']);
const store = useEquipmentStore();

const selectedEquipment = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});

const filteredEquipments = computed(() => {
  const items = store.items;
  if (!props.filterSection || items.length === 0) return items;
  
  return items.filter(eq => 
    eq.sectionDescription?.toLowerCase().includes(props.filterSection.toLowerCase())
  );
});

onMounted(async () => {
  if (store.items.length === 0) {
    await store.fetchEquipments();
  }
});
</script>

<template>
  <div class="w-full">
    <div v-if="store.loading" class="flex items-center gap-2 py-2">
      <ProgressSpinner style="width: 30px; height: 30px" strokeWidth="6" />
      <span class="text-sm text-surface-500">A carregar equipamentos...</span>
    </div>

    <div v-else-if="store.error">
      <Message severity="error" :closable="false" class="w-full">{{ store.error }}</Message>
    </div>

    <div v-else>
      <label for="equipmentSelect" class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">
        Equipamento
      </label>
      
      <Select
        id="equipmentSelect"
        v-model="selectedEquipment"
        :options="filteredEquipments"
        optionLabel="description"
        dataKey="id"  
        placeholder="Seleciona equipamento"
        filter
        showClear
        fluid
        class="w-full"
      />
    </div>
  </div>
</template>