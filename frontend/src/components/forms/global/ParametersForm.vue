<script setup>
import ParameterSelect from '@/components/forms/global/ParameterSelect.vue';
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';

const props = defineProps({
  // Aseguramos que el default sea una función que retorna array
  modelValue: { type: Array, default: () => [] }
});

const emit = defineEmits(['update:modelValue']);

// 1. AGREGAR PARÁMETRO
const addParameter = () => {
  // Protección: Si modelValue es null/undefined, usamos []
  const currentParams = props.modelValue || [];
  
  const newParam = { 
    parameterId: null, 
    valueString: '' // Inicializamos vacío para evitar nulls en el input
  };
  
  emit('update:modelValue', [...currentParams, newParam]);
};

// 2. ELIMINAR PARÁMETRO
const removeParameter = (index) => {
  const currentParams = props.modelValue || [];
  const updated = [...currentParams];
  
  updated.splice(index, 1);
  emit('update:modelValue', updated);
};

// 3. ACTUALIZAR (Función Genérica y Segura)
const updateParameter = (index, field, value) => {
  const currentParams = props.modelValue || [];
  
  const updated = currentParams.map((item, i) => {
    if (i === index) {
      // Creamos copia del objeto y asignamos el valor
      return { ...item, [field]: value };
    }
    return item;
  });
  
  emit('update:modelValue', updated);
};
</script>

<template>
  <div class="flex flex-col gap-3">
    
    <div v-if="(modelValue || []).length > 0" class="flex gap-3 text-sm text-surface-500 px-1">
      <div class="min-w-40 max-w-40">Tipo</div>
      <div class="flex-1">Valor</div>
      <div class="w-10"></div>
    </div>

    <div 
      v-for="(param, index) in (modelValue || [])" 
      :key="index" 
      class="flex gap-3 items-start"
    >
      
      <div class="min-w-40 max-w-40">
        <ParameterSelect 
          :modelValue="param.parameterId" 
          @update:modelValue="(val) => updateParameter(index, 'parameterId', val)"
          class="w-full" 
        />
      </div>

      <div class="flex-1">
        <InputText 
          :modelValue="param.valueString" 
          placeholder="Valor" 
          class="w-full" 
          :id="`parametersInput-${index}`"
          @update:modelValue="(val) => updateParameter(index, 'valueString', val)" 
        />
      </div>

      <Button 
        icon="pi pi-trash" 
        severity="danger" 
        variant="text" 
        rounded
        aria-label="Eliminar parámetro"
        @click="removeParameter(index)" 
      />
    </div>

    <div v-if="(!modelValue || modelValue.length === 0)" class="text-surface-500 text-sm italic mb-2">
      Sem parâmetros definidos.
    </div>

    <div>
      <Button 
        label="Adicionar parâmetro" 
        icon="pi pi-plus" 
        severity="secondary" 
        outlined 
        size="small"
        @click="addParameter" 
      />
    </div>
  </div>
</template>