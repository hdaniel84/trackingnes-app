<script setup>
import { ref, watch, computed, onMounted } from 'vue';
import { useTrackingPrensasStore } from '@/stores/trackingPrensasStore';
import Message from 'primevue/message';
import Select from 'primevue/select';

const props = defineProps({
    modelValue: {
        type: [String, Number],
        default: null
    },
    filterProduct: {
        type: String,
        default: null
    }
});

const emit = defineEmits(['update:modelValue']);

const trackingPrensasStore = useTrackingPrensasStore();
const selectedValue = ref(props.modelValue);

// Lista de resultados filtrados desde backend
const filteredBackendItems = ref([]);

watch(selectedValue, (newVal) => {
    emit('update:modelValue', newVal); // emitimos el valor primitivo directamente
});

// Cuando cambia el filtro, buscar en backend
watch(
    () => props.filterProduct,
    async (newFiltro) => {

        if (!props.modelValue) {
            selectedValue.value = null;
            emit('update:modelValue', null);
        }

        if (!newFiltro) {
            filteredBackendItems.value = [];
            return;
        }

        try {
            await trackingPrensasStore.searchByCodigoProduto(newFiltro, 0, 10, 'startTime,desc');
            filteredBackendItems.value = trackingPrensasStore.items;
        } catch (err) {
            console.error('Error buscando por códigoProducto:', err);
            filteredBackendItems.value = [];
        }
    },
    { immediate: true }
);

//Para llevar la info al formulario en edicion
watch(
    [() => trackingPrensasStore.items, () => filteredBackendItems.value, () => props.modelValue],
    () => {
        const list = props.filterProduct && filteredBackendItems.value.length > 0
            ? filteredBackendItems.value
            : trackingPrensasStore.items;

        if (!props.modelValue) return;

        const numericVal = Number(props.modelValue);
        const match = list.find(item => Number(item.logisticUnit) === numericVal);

        if (match) {
            selectedValue.value = match.logisticUnit; // ✅ muestra el número del carro
        } else {
            selectedValue.value = numericVal;
        }
    },
    { immediate: true }
);



onMounted(async () => {
    if (trackingPrensasStore.items.length === 0) {
        await trackingPrensasStore.fetchAll({ page: 0, size: 5 });
    }
});

// Selección de opciones para mostrar en el Select
const filteredProducts = computed(() => {
    return props.filterProduct && filteredBackendItems.value.length > 0
        ? filteredBackendItems.value
        : trackingPrensasStore.items;
});
</script>

<template>
    <div>
        <div v-if="trackingPrensasStore.loading">
            <Skeleton width="100%" height="3rem" borderRadius="6px"></Skeleton>
        </div>

        <div v-else-if="trackingPrensasStore.error">
            <Message severity="error">{{ trackingPrensasStore.error }}</Message>
        </div>

        <div v-else class="p-field">
            <label for="logisticUnitInSelect" class="block font-semibold mb-2">Nro. Carro entrada:</label>
            <Select v-model="selectedValue" :options="filteredProducts" optionLabel="logisticUnit"
                placeholder="Selecciona ou preenche" filter editable :return-object="false" optionValue="logisticUnit">
                <template #option="slotProps">
                    <div class="flex gap-2">
                        <span class="font-bold">{{ slotProps.option.logisticUnit }} - </span>
                        <span>{{ slotProps.option.productDescription }}</span>
                    </div>
                </template>
            </Select>
        </div>
    </div>
</template>
