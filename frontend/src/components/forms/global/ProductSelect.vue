<script setup>
import { onMounted, ref, watch } from 'vue';
import Select from 'primevue/select';
import ProductApi from '@/api/productApi';

const props = defineProps({
  modelValue: { type: Object, default: null },
  prefixes: { type: Array, default: () => [] },
  label: { type: String, default: 'Artigo / Produto' }
});

const emit = defineEmits(['update:modelValue']);

const items = ref([]);
const loading = ref(false);
const error = ref(null);

// Función para cargar productos filtrados
const fetchProducts = async () => {
  loading.value = true;
  error.value = null;
  try {
    // Llamamos al endpoint que acepta ?prefixes=FF,W
    const response = await ProductApi.getByPrefixes(props.prefixes);
    items.value = response.data;
  } catch (err) {
    error.value = 'Erro ao carregar produtos.';
    console.error(err);
  } finally {
    loading.value = false;
  }
};

// Si cambian los prefijos (ej: cambio de fase dinámica), recargamos
watch(() => props.prefixes, () => {
  fetchProducts();
}, { deep: true });

onMounted(() => {
  fetchProducts();
});
</script>

<template>
  <div class="w-full">
    <div v-if="error" class="text-red-500 text-xs mb-1">{{ error }}</div>

    <label v-if="label" class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">
      {{ label }}
    </label>

    <Select :modelValue="modelValue" @update:modelValue="(val) => emit('update:modelValue', val)" :options="items"
      optionLabel="description" placeholder="Seleciona um produto" dataKey="id" filter
      :filterFields="['productCode', 'description']" showClear fluid class="w-full h-12" :loading="loading"
      :virtualScrollerOptions="{ itemSize: 38 }">
      <template #option="slotProps">
        <div class="flex flex-col">
          <span class="font-bold text-sm">{{ slotProps.option.productCode }}</span>
          <span class="text-sm text-surface-600 dark:text-surface-400">{{ slotProps.option.description }}</span>
        </div>
      </template>

      <template #value="slotProps">
        <div v-if="slotProps.value" class="flex gap-2 items-center">
          <span class="font-bold bg-primary-100 text-primary-700 px-2 rounded text-xs">{{ slotProps.value.productCode
            }}</span>
          <span class="truncate">{{ slotProps.value.description }}</span>
        </div>
        <span v-else>{{ slotProps.placeholder }}</span>
      </template>
    </Select>
  </div>
</template>