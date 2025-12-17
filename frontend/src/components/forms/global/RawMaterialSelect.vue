<script setup>
import { onMounted, computed } from 'vue';
import { useRawMaterialStore } from '@/stores/rawMaterialStore';
import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Select from 'primevue/select';

const props = defineProps({
  modelValue: { type: [Number, String, null], default: null },
  phaseId: { type: Number, default: null }
});

const emit = defineEmits(['update:modelValue']);
const store = useRawMaterialStore();

// 🔹 Lógica de Filtrado por Fase
const filteredItems = computed(() => {
  const items = store.items;
  if (!props.phaseId) return items;
  return items.filter(item => item.phase?.id === props.phaseId);
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
  await store.fetchAll();
});
</script>

<template>
  <div class="w-full">
    <div v-if="store.loading" class="flex items-center justify-center py-2">
      <ProgressSpinner style="width: 25px; height: 25px" strokeWidth="6" />
    </div>

    <div v-else-if="store.error">
      <Message severity="error" :closable="false" size="small">{{ store.error }}</Message>
    </div>

    <div v-else>
      <Select v-model="selectedValue" :options="filteredItems" optionLabel="description"
        placeholder="Selecione Matéria Prima" filter :filterFields="['sapCode', 'description']" showClear fluid
        class="w-full">
        <template #option="slotProps">
          <div class="flex flex-col">
            <span class="font-bold text-sm">{{ slotProps.option.sapCode }}</span>
            <span class="text-xs text-surface-600">{{ slotProps.option.description }}</span>
          </div>
        </template>

        <template #value="slotProps">
          <div v-if="slotProps.value" class="flex gap-2 text-sm">
            <span class="font-bold">{{ slotProps.value.sapCode }}</span>
            <span>- {{ slotProps.value.description }}</span>
          </div>
          <span v-else>{{ slotProps.placeholder }}</span>
        </template>
      </Select>
    </div>
  </div>
</template>