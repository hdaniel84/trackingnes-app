<script setup>
import { ref, onMounted, watch } from 'vue';
import { useTeamStore } from '@/stores/teamStore';
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

const teamsStore = useTeamStore();
const selectedTeam = ref(props.modelValue);

watch(selectedTeam, (newVal) => {
  emit('update:modelValue', newVal);
});

onMounted(() => {
  if (teamsStore.items.length === 0) {
    teamsStore.fetchTeams();
  }
});
</script>

<template>
  <div>
    <div v-if="teamsStore.loading">
      <ProgressSpinner style="width: 50px; height: 50px" strokeWidth="8" animationDuration=".5s" />
      <p>Carregando equipas...</p>
    </div>

    <div v-else-if="teamsStore.error">
      <Message severity="error">{{ teamsStore.error }}</Message>
    </div>

    <div v-else class="p-field">
      <span class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">Equipa</span>
      <Select id="team" inputId="teamSelect" class="mt-2" v-model="selectedTeam" :options="teamsStore.items" optionLabel="description"
        placeholder="Selecciona equipa" filter>
        <template #option="slotProps">
          <div class="flex gap-2">
            <span class="font-bold">{{ slotProps.option.description }}</span>
            <span>{{ slotProps.option.sectionDescription }}</span>
          </div>
        </template>
      </Select>
    </div>
  </div>
</template>
