<script setup>
import { ref, watch } from 'vue';
import TrackingService from '@/api/trackingApi';
import AutoComplete from 'primevue/autocomplete';
import Message from 'primevue/message';

const props = defineProps({
    modelValue: { type: Array, default: () => [] },
    allowedPhases: { type: Array, required: true },
    targetReferenceId: { type: [String, Number], default: null },
    matchReference: { type: Boolean, default: false },
    filterType: { type: String, default: 'PRODUCT_ID' },
    label: { type: String, default: 'Origens (Lotes Anteriores)' },
    disabled: { type: Boolean, default: false },
    initialSelection: { type: Array, default: () => [] }
});

const emit = defineEmits(['update:modelValue']);

const suggestions = ref([]);
const selectedItems = ref([]);
const loading = ref(false);
const error = ref(null);

// Mapeo interno: IDs (v-model) <-> Objetos (UI)
watch(() => props.modelValue, (newVal) => {
    if (!newVal || newVal.length === 0) {
        selectedItems.value = [];
    }
}, { immediate: true });

// Sincronización inicial (Edición)
watch(() => props.initialSelection, (val) => {
    if (val && val.length > 0) {
        // Mapeamos lo que viene del backend para que coincida con la estructura que espera el template
        selectedItems.value = val.map(item => ({
            ...item,
            product: item.product || { description: item.productDescription }, 
            logisticUnit: item.logisticUnit
        }));
    }
}, { immediate: true });

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
            const searchStr = `${item.id} ${item.logisticUnit || ''} ${item.product?.description || ''}`.toLowerCase();
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

const onSelectionChange = (val) => {
    const ids = val.map(v => v.id);
    emit('update:modelValue', ids);
};

// Función para remover un item específico (si la X nativa falla o queremos control total)
const removeItem = (index) => {
    const newItems = [...selectedItems.value];
    newItems.splice(index, 1);
    selectedItems.value = newItems;
    onSelectionChange(newItems);
};
</script>

<template>
    <div class="w-full">
        <label class="block font-semibold text-surface-700 dark:text-surface-200 mb-2 text-xs">
            {{ label }}
        </label>

        <div v-if="error">
            <Message severity="error" :closable="false" size="small">{{ error }}</Message>
        </div>

        <AutoComplete 
            v-model="selectedItems"
            :suggestions="suggestions"
            @complete="searchItems"
            @update:modelValue="onSelectionChange"
            multiple
            optionLabel="id"
            dataKey="id"
            :placeholder="props.matchReference && !props.targetReferenceId ? 'Selecione primeiro o produto' : 'Pesquisar...'"
            :disabled="props.disabled || (props.matchReference && !props.targetReferenceId)"
            fluid
            class="w-full"
            :dropdown="true"
        >
            <template #chip="slotProps">
                <div class="flex items-center gap-2 mr-1 bg-surface-100 dark:bg-surface-700 pl-2 pr-1 py-0.5 rounded-md border border-surface-200 dark:border-surface-600">
                    <span class="font-bold text-xs bg-primary-100 text-primary-700 px-1.5 rounded">#{{ slotProps.value.id }}</span>
                    <span class="text-xs font-medium text-surface-700 dark:text-surface-200">
                        {{ slotProps.value.logisticUnit ? 'Carro ' + slotProps.value.logisticUnit : slotProps.value.product?.description }}
                    </span>
                    
                    <button 
                        type="button" 
                        @click.stop="removeItem(selectedItems.indexOf(slotProps.value))"
                        class="ml-1 p-0.5 rounded-full hover:bg-surface-300 dark:hover:bg-surface-600 text-surface-500 hover:text-red-500 transition-colors focus:outline-none"
                        :disabled="disabled"
                    >
                        <i class="pi pi-times text-[10px] block"></i>
                    </button>
                </div>
            </template>

            <template #option="slotProps">
                <div class="flex flex-col">
                    <span class="font-bold text-sm">
                          [{{ slotProps.option.id }}] {{ slotProps.option.phase?.description }}
                    </span>
                    <span class="text-xs text-surface-500">
                        {{ slotProps.option.product?.productCode }} |
                        {{ slotProps.option.product?.description || 'Produto Desconhecido'}} 
                    </span>
                </div>
            </template>
        </AutoComplete>
        
        <small v-if="selectedItems.length === 0 && !disabled" class="text-surface-400 text-[10px] block mt-1">
            * Pode selecionar múltiplos lotes de origem
        </small>
    </div>
</template>