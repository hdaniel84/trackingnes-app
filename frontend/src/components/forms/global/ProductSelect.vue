<script setup>
import { onMounted, computed } from 'vue';
import Select from 'primevue/select';
import { useProductStore } from '@/stores/productStore';

const props = defineProps({
  modelValue: { type: Object, default: null }
});

const emit = defineEmits(['update:modelValue']);
const store = useProductStore();

// Usamos un computed con get/set para manejar el v-model limpiamente
const selectedProduct = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});

onMounted(async () => {
  if (store.items.length === 0) {
    await store.fetchProducts();
  }
});
</script>

<template>
  <div class="w-full">
    <div v-if="store.loading" class="flex items-center gap-2 text-surface-500">
      <i class="pi pi-spin pi-spinner" style="font-size: 1.2rem"></i>
      <span class="text-sm">A carregar produtos...</span>
    </div>

    <div v-else-if="store.error" class="text-red-500 text-sm">
      {{ store.error }}
    </div>

    <div v-else>
      <label for="productSelect" class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">
        Artigo / Produto
      </label>
      
      <Select
        id="productSelect"
        v-model="selectedProduct"
        :options="store.items"
        optionLabel="description"
        placeholder="Seleciona um produto"
        dataKey="id" 
        filter
        :filterFields="['codigoProduto', 'description']"
        showClear
        fluid
        class="w-full"
      >
        <template #option="slotProps">
          <div class="flex flex-col">
            <span class="font-bold text-sm">{{ slotProps.option.codigoProduto }}</span>
            <span class="text-sm text-surface-600 dark:text-surface-400">{{ slotProps.option.description }}</span>
          </div>
        </template>
        
        <template #value="slotProps">
          <div v-if="slotProps.value" class="flex gap-2">
            <span class="font-bold">{{ slotProps.value.codigoProduto }}</span>
            <span>- {{ slotProps.value.description }}</span>
          </div>
          <span v-else>{{ slotProps.placeholder }}</span>
        </template>
      </Select>
    </div>
  </div>
</template>