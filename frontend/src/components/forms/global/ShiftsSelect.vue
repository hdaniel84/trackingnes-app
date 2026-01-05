<script setup>
import { onMounted, computed } from 'vue';
import { useShiftStore } from '@/stores/shiftStore';
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

// Función para determinar el turno actual
const findCurrentShift = (shifts) => {
  const now = new Date();
  const currentHour = now.getHours(); // 0 a 23

  return shifts.find(shift => {
    // Convertimos "08:00:00" a entero 8
    const startHour = parseInt(shift.startTime.split(':')[0], 10);
    let endHour = parseInt(shift.endTime.split(':')[0], 10);

    // CASO ESPECIAL: Si termina a las 00:00:00, lo tratamos como las 24:00
    // para que la comparación (hora < fin) funcione correctamente.
    if (endHour === 0) {
      endHour = 24;
    }

    // Lógica simple: si la hora actual está entre inicio y fin
    // Ejemplo Turno 3: 16 <= 20 < 24 (True)
    return currentHour >= startHour && currentHour < endHour;
  });
};

onMounted(async () => {
  // 1. Cargar datos si no existen
  if (store.items.length === 0) {
    await store.fetchShifts();
  }

  // 2. Si no hay un turno seleccionado previamente (modelValue es null), autoseleccionar
  if (!props.modelValue && store.items.length > 0) {
    const currentShift = findCurrentShift(store.items);

    if (currentShift) {
      selectedShift.value = currentShift; // Esto dispara el emit update:modelValue
    }
  }
});
</script>

<template>
  <div class="w-full">
    <div v-if="store.loading" class="flex flex-col gap-3">
      <Skeleton width="4rem" height="1rem" class="mb-1"></Skeleton>
      <Skeleton width="100%" height="3rem" borderRadius="6px"></Skeleton>
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