<script setup>
import { ref, onMounted, watch } from 'vue';
import TrackingService from '@/api/trackingApi'; // 1. Usamos la API directamente para no ensuciar el Store global
import Select from 'primevue/select';
import Message from 'primevue/message';

const props = defineProps({
    modelValue: { type: [Number, String], default: null },
    allowedPhases: { type: Array, required: true },
    targetReferenceId: { type: [String, Number], default: null },
    matchReference: { type: Boolean, default: false },
    filterType: { type: String, default: 'PRODUCT_ID' },
    label: { type: String, default: 'Origem' },
    disabled: { type: Boolean, default: false }
});

const emit = defineEmits(['update:modelValue']);

// 2. Estado LOCAL (Descomentamos lo que tenías, es la forma correcta)
const items = ref([]); 
const loading = ref(false);
const error = ref(null);

const fetchCandidates = async () => {
    // Validación: Si exige coincidencia pero no hay referencia, limpiamos y salimos
    if (props.matchReference && !props.targetReferenceId) {
        items.value = [];
        return;
    }

    loading.value = true;
    error.value = null;

    try {
        // 3. Llamada directa al servicio. 
        // Obtenemos los datos SIN modificar el 'store.items' global.
        const response = await TrackingService.getCandidates({
             phaseIds: props.allowedPhases.join(','), // Aseguramos formato array -> string si tu API lo requiere así
             referenceId: props.matchReference ? props.targetReferenceId : null,
             filterType: props.filterType
        });
        
        items.value = response.data;

    } catch (err) {
        error.value = 'Erro ao carregar registos de origem.';
        console.error("TrackingSourceSelect Error:", err);
        items.value = [];
    } finally {
        loading.value = false;
    }
};

// 4. Ciclo de Vida
onMounted(() => {
    // Si no requiere referencia O si ya tiene referencia, cargamos.
    if (!props.matchReference || props.targetReferenceId) {
        fetchCandidates();
    }
});

// 5. Watcher: Si cambia el producto padre, recargamos las opciones de origen
watch(() => props.targetReferenceId, (newVal, oldVal) => {
    if (newVal !== oldVal) {
        emit('update:modelValue', null); // Reseteamos la selección si cambia el padre
        fetchCandidates();
    }
});
</script>

<template>
    <div class="w-full">
        <label class="block font-semibold text-surface-700 dark:text-surface-200 mb-2 text-xs">
            {{ label }}
        </label>

        <div v-if="error">
            <Message severity="error" :closable="false" size="small">{{ error }}</Message>
        </div>

        <Select 
            :modelValue="modelValue" 
            @update:modelValue="(val) => emit('update:modelValue', val)" 
            :options="items"
            optionLabel="id" 
            optionValue="id" 
            :loading="loading" 
            dataKey="id"
            :placeholder="props.matchReference && !props.targetReferenceId ? 'Selecione primeiro o produto acima' : 'Selecione o registo de origem'"
            filter 
            showClear 
            fluid 
            class="w-full"
            :disabled="disabled || (props.matchReference && !props.targetReferenceId)"
        >
            <template #option="slotProps">
                <div class="flex flex-col">
                    <span class="font-bold text-sm">
                        ID# {{ slotProps.option.id }} | 
                        {{ slotProps.option.logisticUnit ? 'Carro: ' + slotProps.option.logisticUnit : 'S/ Carro' }}
                    </span>
                    <span class="text-xs text-surface-500">
                        {{ slotProps.option.product?.description || 'Produto Desconhecido'}} 
                        ({{ slotProps.option.phase?.description }})
                    </span>
                </div>
            </template>

            <template #value="slotProps">
                <div v-if="slotProps.value" class="flex flex-col text-sm">
                     <span class="font-bold">ID# {{ slotProps.value }}</span> 
                     </div>
                <span v-else>{{ slotProps.placeholder }}</span>
            </template>

            <template #empty>
                <div class="p-2 text-sm">
                    {{ props.matchReference && !props.targetReferenceId ? 'A aguardar produto...' : 'Não há registos compatíveis.' }}
                </div>
            </template>
        </Select>
    </div>
</template>