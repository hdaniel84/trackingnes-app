<script setup>
import { onMounted, computed } from 'vue';
import { useParameterStore } from '@/stores/parameterStore';
import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Select from 'primevue/select';

const props = defineProps({
  modelValue: {
    type: [Number, String, null],
    default: null
  }
});

const emit = defineEmits(['update:modelValue']);
const store = useParameterStore();

// 🔹 Binding reactivo usando computed
const selectedValue = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});

onMounted(async () => {
  if (store.items.length === 0) {
    await store.fetchAll();
  }
});
</script>

<template>
  <div class="w-full">
    <div v-if="store.loading" class="flex items-center justify-center py-2">
      <ProgressSpinner style="width: 25px; height: 25px" strokeWidth="6" animationDuration=".5s" />
    </div>

    <div v-else-if="store.error">
      <Message severity="error" :closable="false" size="small">{{ store.error }}</Message>
    </div>

    <div v-else>
      <Select
        v-model="selectedValue"
        :options="store.items"
        optionLabel="description"
        optionValue="id"
        placeholder="Tipo"
        filter
        showClear
        fluid
        class="w-full"
      />
    </div>
  </div>
</template>