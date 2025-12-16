<script setup>
import { onMounted, computed } from 'vue';
import { useTeamStore } from '@/stores/teamStore';
import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Select from 'primevue/select';

const props = defineProps({
  modelValue: { type: Object, default: null },
  filterSection: { type: String, default: null }
});

const emit = defineEmits(['update:modelValue']);
const store = useTeamStore();

// 🔹 Binding bidireccional limpio
const selectedTeam = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});

// 🔹 Filtro
const filteredTeams = computed(() => {
  const items = store.items;
  if (!props.filterSection || items.length === 0) return items;
  
  return items.filter(team => 
    team.sectionDescription?.toLowerCase().includes(props.filterSection.toLowerCase())
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
      <label for="teamSelect" class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">
        Equipa
      </label>
      <Select
        id="teamSelect"
        v-model="selectedTeam"
        :options="filteredTeams"
        optionLabel="description"
        placeholder="Seleciona equipa"
        dataKey="id" 
        filter
        :filterFields="['description', 'sectionDescription']"
        showClear
        fluid
        class="w-full"
      >
        <template #option="slotProps">
          <div class="flex flex-col">
            <span class="font-bold text-sm">{{ slotProps.option.description }}</span>
            <span class="text-xs text-surface-500">{{ slotProps.option.sectionDescription }}</span>
          </div>
        </template>
        
        <template #value="slotProps">
          <div v-if="slotProps.value" class="flex items-center gap-2">
            <span>{{ slotProps.value.description }}</span>
            <span class="text-surface-400 text-sm">({{ slotProps.value.sectionDescription }})</span>
          </div>
          <span v-else>{{ slotProps.placeholder }}</span>
        </template>
      </Select>
    </div>
  </div>
</template>