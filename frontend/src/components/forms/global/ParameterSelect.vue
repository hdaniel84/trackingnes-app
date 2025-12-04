<script setup>
import { onMounted } from 'vue';
import { useParameterStore } from '@/stores/parameterStore';
import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Select from 'primevue/select';

defineProps({
  modelValue: {
    type: [Number, String, null],
    default: null
  }
});

const emit = defineEmits(['update:modelValue']);

const parametersStore = useParameterStore();

onMounted(() => {
  if (parametersStore.items.length === 0) {
    parametersStore.fetchAll();
  }
});
</script>

<template>
  <div>
    <div v-if="parametersStore.loading">
      <ProgressSpinner
        style="width: 40px; height: 40px"
        strokeWidth="8"
        animationDuration=".5s"
      />
      <p class="text-xs text-gray-500 mt-1">Cargando...</p>
    </div>

    <div v-else-if="parametersStore.error">
      <Message severity="error">{{ parametersStore.error }}</Message>
    </div>

    <div v-else>
      <Select
        class="mt-2 text-xs w-full"
        :modelValue="modelValue"
        @update:modelValue="emit('update:modelValue', $event)"
        :options="parametersStore.items"
        optionLabel="description"
        optionValue="id"
        placeholder="Selecciona"
        filter
      />
    </div>
  </div>
</template>
