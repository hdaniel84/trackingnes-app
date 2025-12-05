<script setup>
import { ref, onMounted, watch } from 'vue';
import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Select from 'primevue/select';
import { useProductStore } from '@/stores/productStore';

// ✅ Props y emits para que funcione con v-model
const props = defineProps({
  modelValue: {
    type: Object,
    default: null
  }
});
const emit = defineEmits(['update:modelValue']);

const productStore = useProductStore();

// Estado local para el valor seleccionado
const selectedProduct = ref(props.modelValue);

// Sincronizar cambios hacia el padre
watch(selectedProduct, (newVal) => {
  emit('update:modelValue', newVal);
});

// Cargar productos al montar
onMounted(() => {
  if (productStore.items.length === 0) {
    productStore.fetchProducts();
  }
});
</script>

<template>
  <div>
    <div v-if="productStore.loading">
      <ProgressSpinner style="width: 50px; height: 50px" strokeWidth="8" animationDuration=".5s" />
      <p>Carregando produtos...</p>
    </div>

    <div v-else-if="productStore.error">
      <Message severity="error">{{ productStore.error }}</Message>
    </div>

    <div v-else class="p-field">
      <span class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">Artigo</span>
      <Select inputId="productSelect" id="product" class="mt-2" v-model="selectedProduct" :options="productStore.items"
        optionLabel="description" placeholder="Seleciona" filter :filterFields="['codigoProduto','description']">
        <template #option="slotProps">
          <div class="flex gap-2">
            <span class="font-bold">{{ slotProps.option.codigoProduto }}</span>
            <span>{{ slotProps.option.description }}</span>
          </div>
        </template>
      </Select>
    </div>
  </div>
</template>
