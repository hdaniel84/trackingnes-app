<script setup>
import { onMounted, computed, watch } from 'vue';
import { useEquipmentStore } from '@/stores/equipmentStore';
import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Select from 'primevue/select';

const props = defineProps({
  modelValue: { type: Object, default: null },
  filterSection: { type: Number, default: null }
});

const emit = defineEmits(['update:modelValue']);
const store = useEquipmentStore();

const selectedEquipment = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});



// 🔹 MODIFICACIÓN AQUÍ: Filtramos por mandatory === true
const filteredEquipments = computed(() => {
  const items = store.items;
  if (items.length === 0) return [];

  return items.filter(eq => {
    // 1. Condición estricta: Solo mostrar equipos principales (Mandatory)
    const isMain = eq.mandatory === true;

    // 2. Condición opcional: Filtrar por sección si el prop existe
    const matchesSection = props.filterSection
      ? eq.sectionId == props.filterSection
      : true;

    // Ambas deben cumplirse
    return isMain && matchesSection;
  });
});

watch(
  filteredEquipments,
  (items) => {
    // Si solo hay un equipo y aún no hay uno seleccionado
    if (items.length === 1 && !props.modelValue) {
      emit('update:modelValue', items[0]);
    }
  },
  { immediate: true }
);

onMounted(async () => {
  // Aseguramos que el store cargue los datos si está vacío
  if (store.items.length === 0) {
    await store.fetchAll();
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
        Equipamento Principal
      </label>

      <Select id="equipmentSelect" v-model="selectedEquipment" :options="filteredEquipments" optionLabel="description"
        dataKey="id" placeholder="Seleciona equipamento" filter showClear fluid class="w-full" :disabled="filteredEquipments.length===1">
        <template #option="slotProps">
          <div class="flex flex-col">
            <span class="font-bold text-sm">{{ slotProps.option.description }}</span>
            <span class="text-xs text-surface-500">{{ slotProps.option.sapCode }}</span>
          </div>
        </template>
      </Select>
    </div>
  </div>
</template>