<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useTrackingPrensasStore } from '@/stores/trackingPrensasStore';
import ProgressSpinner from 'primevue/progressspinner';
import Message from 'primevue/message';
import Select from 'primevue/select';

const props = defineProps({
    modelValue: {
        type: Object,
        default: null
    },
    filterProduct: {
        type: String,
        default: null // Por defecto, no filtra nada
    }
});
const emit = defineEmits(['update:modelValue']);

const trackingPrensasStore = useTrackingPrensasStore();
const selectedTrackingPrensas = ref(props.modelValue);

watch(selectedTrackingPrensas, (newVal) => {
    emit('update:modelValue', newVal);
});

watch(() => props.filterProduct, (newFiltro, oldFiltro) => {
    // Solo resetea si el filtro realmente ha cambiado
    if (newFiltro !== oldFiltro) {
        // Limpia el valor interno del Select
        selectedTrackingPrensas.value = null;

        // Emite el cambio al componente padre para que resetee su v-model (logisticUnitInId = null)
        emit('update:modelValue', null);
    }
});

onMounted(() => {
    if (trackingPrensasStore.items.length === 0) {
        trackingPrensasStore.fetchAll({
            page: 0,
            size: 5
        });
    }
});

const filteredProducts = computed(() => {
    const items = trackingPrensasStore.items;
    const filter = props.filterProduct;

    if (!filter || items.length === 0) {
        return items;
    }

    return items.filter(item =>
        item.productDescription &&
        item.productDescription.toLowerCase().includes(filter.toLowerCase())
    );
}); 
</script>

<template>
    <div>
        <div v-if="trackingPrensasStore.loading">
            <ProgressSpinner style="width: 50px; height: 50px" strokeWidth="8" animationDuration=".5s" />
            <p>Carregando...</p>
        </div>

        <div v-else-if="trackingPrensasStore.error">
            <Message severity="error">{{ trackingPrensasStore.error }}</Message>
        </div>

        <div v-else class="p-field">
            <Select inputId="trackingPrensasSelect" id="trackingPrensas" class="mt-2" v-model="selectedTrackingPrensas"
                :options="filteredProducts" optionLabel="logisticUnit" placeholder="Selecciona ou preenche" filter
                editable>
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
