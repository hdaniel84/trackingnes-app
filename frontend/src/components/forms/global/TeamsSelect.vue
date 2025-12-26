<script setup>
import { onMounted, computed } from 'vue';
import { useTeamStore } from '@/stores/teamStore';
import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Select from 'primevue/select';

const props = defineProps({
  modelValue: { type: Object, default: null },
  filterSection: { type: Number, default: null },
  label: { type: String, default: 'Equipa:' }
});

const emit = defineEmits(['update:modelValue']);
const store = useTeamStore();

// 🔹 MAGIA AQUÍ: Enriquecimiento de datos
const selectedTeam = computed({
  get: () => {
    // Si no hay valor seleccionado, devolvemos null
    if (!props.modelValue) return null;

    // Intentamos encontrar este equipo en la lista cargada del Store (que tiene todos los datos)
    const foundInStore = store.items.find(t => t.id === props.modelValue.id);

    // Si lo encontramos en el store, devolvemos el COMPLETO (con sectionDescription).
    // Si no (ej: store cargando), devolvemos el que nos pasó el padre.
    return foundInStore || props.modelValue;
  },
  set: (val) => emit('update:modelValue', val)
});

// Filtro (Igual que antes)
const filteredTeams = computed(() => {
  const items = store.items;
  if (!props.filterSection || items.length === 0) return items;

  return items.filter(team =>
    team.sectionId == props.filterSection
  );
});

onMounted(async () => {
  if (store.items.length === 0) {
    await store.fetchTeams();
  }
});
</script>

<template>
  <div class="w-full">
    <div v-if="store.loading" class="flex items-center gap-2 py-2">
      <ProgressSpinner style="width: 30px; height: 30px" strokeWidth="6" />
      <span class="text-sm text-surface-500">A carregar equipas...</span>
    </div>

    <div v-else-if="store.error">
      <Message severity="error" :closable="false" class="w-full">{{ store.error }}</Message>
    </div>

    <div v-else>
      <label v-if="label" class="block text-xs font-medium text-surface-500 mb-1 ml-1">
        {{ label }}
      </label>

      <Select id="teamSelect" v-model="selectedTeam" :options="filteredTeams" optionLabel="description" dataKey="id"
        placeholder="Seleciona equipa" filter :filterFields="['description', 'sectionDescription']" showClear fluid
        class="w-full">
        <template #option="slotProps">
          <div class="flex flex-col">
            <span class="font-bold text-sm">{{ slotProps.option.description }}</span>
            <span class="text-xs text-surface-500">{{ slotProps.option.sectionDescription }}</span>
          </div>
        </template>

        <template #value="slotProps">
          <div v-if="slotProps.value" class="flex items-center gap-2">
            <span>{{ slotProps.value.description }}</span>
            <span class="text-surface-400 text-sm" v-if="slotProps.value.sectionDescription">
              ({{ slotProps.value.sectionDescription }})
            </span>
          </div>
          <span v-else>{{ slotProps.placeholder }}</span>
        </template>
      </Select>
    </div>
  </div>
</template>