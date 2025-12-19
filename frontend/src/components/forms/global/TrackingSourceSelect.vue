<script setup>
import { ref, onMounted, watch } from 'vue';
import TrackingService from '@/api/trackingApi';
import Select from 'primevue/select';
import Message from 'primevue/message';

const props = defineProps({
    modelValue: { type: [Number, String], default: null },
    allowedPhases: { type: Array, required: true },

    targetReferenceId: { type: [String, Number], default: null },

    matchReference: { type: Boolean, default: false }, // ✅ Nombre correcto
    filterType: { type: String, default: 'PRODUCT_ID' },    // 'PRODUCT' o 'SHAPE'

    label: { type: String, default: 'Origem' },
    disabled: { type: Boolean, default: false }
});

const emit = defineEmits(['update:modelValue']);

const items = ref([]);
const loading = ref(false);
const error = ref(null);

const fetchCandidates = async () => {
    // Si la regla exige coincidencia, pero no tenemos ID de referencia, no buscamos
    if (props.matchReference && !props.targetReferenceId) {
        items.value = [];
        return;
    }

    loading.value = true;
    error.value = null; // Limpiar errores previos
    try {
        const response = await TrackingService.getCandidates(
            props.allowedPhases,
            props.matchReference ? props.targetReferenceId : null,
            props.filterType
        );
        items.value = response.data;
    } catch (err) {
        error.value = 'Erro ao carregar registos de origem.';
        console.error("TrackingSourceSelect Error:", err);
    } finally {
        loading.value = false;
    }
};

// 1. Cargar al montar
onMounted(() => {
    if (!props.matchReference || props.targetReferenceId) {
        fetchCandidates();
    }
});

watch(() => props.targetReferenceId, (newVal, oldVal) => {
    if (newVal !== oldVal) emit('update:modelValue', null);
    fetchCandidates();
});
</script>

<template>
    <div class="w-full">
        <label class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">
            {{ label }}
        </label>

        <div v-if="error">
            <Message severity="error" :closable="false">{{ error }}</Message>
        </div>

        <Select :modelValue="modelValue" @update:modelValue="(val) => emit('update:modelValue', val)" :options="items"
            optionLabel="description" optionValue="id" :loading="loading" dataKey="id"
            :placeholder="props.matchReference && !props.targetReferenceId ? 'Selecione primeiro o produto acima' : 'Selecione o registo de origem'"
            filter showClear fluid class="w-full"
            :disabled="disabled || (props.matchReference && !props.targetReferenceId)">
            <template #option="slotProps">
                <div class="flex flex-col">
                    <span class="font-bold text-sm">{{ slotProps.option.description.split(' - ')[0] }}</span>
                    <span class="text-xs text-surface-500">{{ slotProps.option.description.split(' - ')[1] || ''
                        }}</span>
                </div>
            </template>

            <template #empty>
                <div class="p-2 text-sm">
                    {{ props.matchReference && !props.targetReferenceId ? 'A aguardar produto...' : 'Não há registos compatíveis.' }}
                </div>
            </template>
        </Select>
    </div>
</template>