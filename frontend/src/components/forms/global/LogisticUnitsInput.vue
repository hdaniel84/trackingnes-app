<script setup>
import { ref } from 'vue';
import AutoComplete from 'primevue/autocomplete';
import Message from 'primevue/message';

const props = defineProps({
    modelValue: { type: Array, default: () => [] },
    label: { type: String, default: 'Unidades Logísticas' },
    placeholder: { type: String, default: 'Digite e Enter...' },
    disabled: { type: Boolean, default: false },
    errorMessage: { type: String, default: null }
});

const emit = defineEmits(['update:modelValue']);

const items = ref([]);

// TRUCO CLAVE:
// Para que funcione como inserción libre, devolvemos lo que el usuario escribe
// como la única "sugerencia". Así el AutoComplete acepta el Enter.
const searchItems = (event) => {
    if (!event.query.trim().length) {
        items.value = [];
    } else {
        // Mostramos el número que está escribiendo como opción seleccionable
        items.value = [event.query];
    }
};

// Interceptamos la actualización para asegurar que sean números
const onUpdate = (val) => {
    if (!val) {
        emit('update:modelValue', []);
        return;
    }
    
    // 1. Convertimos a números
    // 2. Filtramos NaNs y Ceros
    // 3. Eliminamos duplicados (Set) para limpieza
    const rawNumbers = val.map(v => Number(v)).filter(n => !isNaN(n) && n !== 0);
    const uniqueNumbers = [...new Set(rawNumbers)];

    emit('update:modelValue', uniqueNumbers);
};
</script>

<template>
    <div class="w-full">
        <label class="block font-semibold text-surface-700 dark:text-surface-200 mb-2 text-xs">
            {{ label }}
        </label>

        <AutoComplete 
            :modelValue="modelValue" 
            @update:modelValue="onUpdate"
            :suggestions="items"
            @complete="searchItems"
            :placeholder="placeholder" 
            :disabled="disabled"
            multiple
            fluid
            class="w-full"
            :class="{ 'p-invalid': !!errorMessage }"
            :typeahead="false"
            :pt="{
                input: { class: 'w-full' },
                token: { class: 'bg-surface-100 dark:bg-surface-700 border border-surface-300 dark:border-surface-600' }
            }"
        >
            <template #chip="slotProps">
                <div class="flex items-center gap-2 px-1">
                    <i class="pi pi-hashtag text-[10px] text-primary-500"></i>
                    <span class="text-sm font-bold text-surface-700 dark:text-surface-200">
                        {{ slotProps.value }}
                    </span>
                </div>
            </template>
            
            <template #option="slotProps">
                <div class="text-sm">Adicionar: <strong>{{ slotProps.option }}</strong></div>
            </template>
        </AutoComplete>
        
        <small v-if="errorMessage" class="text-red-500 mt-1 flex items-center gap-1 text-xs">
             {{ errorMessage }}
        </small>
        <small v-else class="text-surface-400 text-[10px] block mt-1">
            * Digite o número e pressione Enter
        </small>
    </div>
</template>