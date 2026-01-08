<script setup>
import { ref, watch } from 'vue';
import TrackingService from '@/api/trackingApi';
import AutoComplete from 'primevue/autocomplete';
import Message from 'primevue/message';

const props = defineProps({
    modelValue: { type: [Array, Object], default: () => [] },
    allowedPhases: { type: Array, required: true },
    targetReferenceId: { type: [String, Number], default: null },
    matchReference: { type: Boolean, default: false },
    filterType: { type: String, default: 'PRODUCT_ID' },
    label: { type: String, default: 'Origens' },
    disabled: { type: Boolean, default: false },
    placeholder: { type: String, default: 'Pesquisar...' }
});

const emit = defineEmits(['update:modelValue', 'item-select']);

const suggestions = ref([]);
const selectedItems = ref([]);
const loading = ref(false);
const error = ref(null);

watch(() => props.modelValue, (newVal) => {
    // Si el padre nos limpia (v-model = []), limpiamos local
    if (!newVal || (Array.isArray(newVal) && newVal.length === 0)) {
        selectedItems.value = [];
    }
}, { immediate: true });

// Sincronización inicial (Edición)
/*
watch(() => props.initialSelection, (val) => {
    if (val && val.length > 0) {
        // Mapeamos lo que viene del backend para que coincida con la estructura que espera el template
        selectedItems.value = val.map(item => ({
            ...item,
            product: item.product || { description: item.productDescription },
            //logisticUnit: item.logisticUnit
        }));
    }
}, { immediate: true })
*/

const searchItems = async (event) => {
    const query = event.query.toLowerCase();

    if (props.matchReference && !props.targetReferenceId) {
        suggestions.value = [];
        return;
    }

    loading.value = true;
    error.value = null;

    try {
        const response = await TrackingService.getCandidates({
            phaseIds: props.allowedPhases.join(','),
            referenceId: props.matchReference ? props.targetReferenceId : null,
            filterType: props.filterType
        });

        const allCandidates = response.data || [];

        // Filtro local simple
        suggestions.value = allCandidates.filter(item => {
            const searchStr = `${item.id} ${item.product?.description || ''}`.toLowerCase();
            return searchStr.includes(query);
        });

    } catch (err) {
        error.value = 'Erro ao carregar registos de origem.';
        console.error(err);
        suggestions.value = [];
    } finally {
        loading.value = false;
    }
};

/*
const onSelectionChange = (val) => {
    const ids = val.map(v => v.id);
    emit('update:modelValue', ids);
};
*/
const onItemSelect = (event) => {
    emit('item-select', event.value); // Pasamos el objeto completo al padre (Manager)
};

// Función para remover un item específico (si la X nativa falla o queremos control total)
/*
const removeItem = (index) => {
    const newItems = [...selectedItems.value];
    newItems.splice(index, 1);
    selectedItems.value = newItems;
    onSelectionChange(newItems);
};
*/
</script>

<template>
    <div class="w-full">
        <label class="block font-semibold text-surface-700 dark:text-surface-200 mb-2 text-xs">
            {{ label }}
        </label>

        <div v-if="error">
            <Message severity="error" :closable="false" size="small">{{ error }}</Message>
        </div>

        <AutoComplete v-model="selectedItems" :suggestions="suggestions" @complete="searchItems"
            @item-select="onItemSelect" optionLabel="id" :placeholder="placeholder" :disabled="disabled" fluid
            class="w-full" :dropdown="true" forceSelection>
            <template #option="slotProps">
                <div class="flex flex-col">
                    <span class="font-bold text-sm">
                        [{{ slotProps.option.id }}] {{ slotProps.option.product?.description }}
                    </span>
                    <span class="text-xs text-surface-500">
                        Disponível: {{ slotProps.option.remainingQuantity ?? slotProps.option.quantity }} un.
                    </span>
                </div>
            </template>
        </AutoComplete>

        <small v-if="selectedItems.length === 0 && !disabled" class="text-surface-400 text-[10px] block mt-1">
            * Pode selecionar múltiplos lotes de origem
        </small>
    </div>
</template>