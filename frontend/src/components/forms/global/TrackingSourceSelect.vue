<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import TrackingService from '@/api/trackingApi';
import Select from 'primevue/select';
import Message from 'primevue/message';

const props = defineProps({
    modelValue: { type: [Number, String], default: null }, // Recibe el ID
    sourcePhaseId: { type: Number, required: true }, // ID de la fase anterior (Ej: 1 para Prensas)
    label: { type: String, default: 'Origem (Fase Anterior)' },
    filterProductId: { type: String, default: null }
});

const emit = defineEmits(['update:modelValue']);

const items = ref([]);
const loading = ref(false);
const error = ref(null);

const filteredItems = computed(() => {
    // Si no hay producto seleccionado en el padre, mostramos todo (o nada, según prefieras)
    if (!props.filterProductId) {
        return [];
    }
    // Filtramos solo los que coincidan con el producto o con el codigoProduto (WXXX de SAP, para prensas)
    return items.value.filter(item => item.productId === props.filterProductId || item.codigoProduto === props.filterProductId);
});

// Watch para limpiar la selección si el producto cambia y el tracking seleccionado ya no coincide
watch(() => props.filterProductId, (newVal) => {
    // Si hay un valor seleccionado
    if (props.modelValue) {
        const selectedItem = items.value.find(i => i.id === props.modelValue);
        // Y ese item existe pero su producto NO coincide con el nuevo filtro...
        if (selectedItem && selectedItem.productId !== newVal) {
            emit('update:modelValue', null); // ... limpiamos el campo para evitar inconsistencias
        }
    }
});

const fetchCandidates = async () => {
    loading.value = true;
    try {
        const response = await TrackingService.getCandidates(props.sourcePhaseId);
        items.value = response.data;
    } catch (err) {
        error.value = 'Erro ao carregar lotes anteriores';
        console.error(err);
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
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

        <Select :modelValue="modelValue" @update:modelValue="(val) => emit('update:modelValue', val)"
            :options="filteredItems" optionLabel="description" optionValue="id" :loading="loading"
            :placeholder="props.filterProductId ? 'Selecione o lote deste produto' : 'Selecione primeiro o produto'"
            filter showClear fluid class="w-full" :disabled="!items.length">
            <template #option="slotProps">
                <div class="flex flex-col">
                    <span class="font-bold text-sm">{{ slotProps.option.description.split(' - ')[0] }}</span>
                    <span class="text-xs text-surface-500">{{ slotProps.option.description.split(' - ')[1] || ''
                        }}</span>
                </div>
            </template>

            <template #empty>
                <div class="p-2">Não há registos anteriores para este produto.</div>
            </template>
        </Select>
    </div>
</template>