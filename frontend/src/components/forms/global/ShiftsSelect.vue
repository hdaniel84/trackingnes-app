<script setup>
import { onMounted, computed } from 'vue';
import { useShiftStore } from '@/stores/shiftStore';
import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Select from 'primevue/select';

const props = defineProps({
  modelValue: { type: Object, default: null }
});

const emit = defineEmits(['update:modelValue']);
const store = useShiftStore();

const selectedShift = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});

onMounted(async () => {
  if (store.items.length === 0) {
    await store.fetchShifts();
  }
});
</script>

<template>
  <div class="w-full">
    <div v-if="store.loading" class="flex items-center gap-2 py-2">
      <ProgressSpinner style="width: 30px; height: 30px" strokeWidth="6" />
      <span class="text-sm text-surface-500">A carregar turnos...</span>
    </div>

    <div v-else-if="store.error">
      <Message severity="error" :closable="false" class="w-full">{{ store.error }}</Message>
    </div>

    <div v-else>
      <label class="block text-xs font-medium text-surface-500 mb-1 ml-1">
        Turno
      </label>
      <Select id="shiftSelect" v-model="selectedShift" :options="store.items" optionLabel="description"
        placeholder="Seleciona turno" dataKey="id" showClear fluid class="w-full">

        <template #option="slotProps">
          <div class="flex flex-col">
            <span class="font-bold text-sm">{{ slotProps.option.description.split('-')[0] }}</span>
            <span class="text-xs text-surface-500">{{ slotProps.option.description.split('-')[1] }}</span>
          </div>
        </template>

        <template #value="slotProps">
          <div v-if="slotProps.value" class="flex items-center gap-2">
            <span>{{ slotProps.value.description }}</span>
          </div>
          <span v-else>{{ slotProps.placeholder }}</span>
        </template>
      </Select>
    </div>
  </div>
</template>