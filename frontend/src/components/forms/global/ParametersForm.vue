<script setup>
import { defineProps, defineEmits } from 'vue';
import { useParameterStore } from '@/stores/parameterStore'; // 1. Importamos el store
import ParameterSelect from '@/components/forms/global/ParameterSelect.vue';

// PrimeVue Imports
import InputText from 'primevue/inputtext';
import InputNumber from 'primevue/inputnumber';
import Checkbox from 'primevue/checkbox';
import DatePicker from 'primevue/datepicker';
import Button from 'primevue/button';

const props = defineProps({
  modelValue: { type: Array, default: () => [] },
  phaseId: { type: Number, required: true }
});

const emit = defineEmits(['update:modelValue']);
const store = useParameterStore(); // 2. Instanciamos

// 3. Función para obtener el tipo de dato según el ID seleccionado
const getParameterType = (parameterId) => {
  if (!parameterId) return 'STRING'; // Default
  const param = store.items.find(p => p.id === parameterId);
  return param ? param.valueType : 'STRING';
};

// 4. Lógica para agregar fila (inicializamos todos los campos del DTO)
const addRow = () => {
  const current = props.modelValue || [];
  const newRow = { 
    id: null,
    parameterId: null, 
    // Campos específicos del DTO:
    valueString: '',
    valueNumber: null,
    valueBool: null,
    valueDate: null
  };
  emit('update:modelValue', [...current, newRow]);
};

const removeRow = (index) => {
  const current = props.modelValue || [];
  const updated = [...current];
  updated.splice(index, 1);
  emit('update:modelValue', updated);
};

// 5. Actualizar fila y LIMPIAR valores si cambia el ID del parámetro
const updateRow = (index, field, value) => {
  const current = props.modelValue || [];
  const updated = current.map((item, i) => {
    if (i === index) {
      const newItem = { ...item, [field]: value };
      
      // Si cambiamos el parámetro, limpiamos los valores anteriores para evitar basura
      if (field === 'parameterId') {
        newItem.valueString = '';
        newItem.valueNumber = null;
        newItem.valueBool = null;
        newItem.valueDate = null;
      }
      return newItem;
    }
    return item;
  });
  emit('update:modelValue', updated);
};
</script>

<template>
  <div class="flex flex-col gap-3">
    <div v-if="(modelValue || []).length > 0" class="flex gap-3 text-sm text-surface-500 px-1">
      <div class="min-w-40 max-w-40">Parâmetro</div>
      <div class="flex-1">Valor</div>
      <div class="w-10"></div>
    </div>

    <div 
      v-for="(row, index) in (modelValue || [])" 
      :key="index" 
      class="flex gap-3 items-start"
    >
      <div class="min-w-40 max-w-40">
        <ParameterSelect 
          :modelValue="row.parameterId" 
          :phaseId="props.phaseId" 
          @update:modelValue="(val) => updateRow(index, 'parameterId', val)"
          class="w-full" 
        />
      </div>

      <div class="flex-1">
        
        <template v-if="getParameterType(row.parameterId) === 'NUMBER'">
          <InputNumber 
            :modelValue="row.valueNumber" 
            placeholder="0.00" 
            :minFractionDigits="0" 
            :maxFractionDigits="4"
            mode="decimal"
            fluid
            class="w-full"
            @update:modelValue="(val) => updateRow(index, 'valueNumber', val)" 
          />
        </template>

        <template v-else-if="getParameterType(row.parameterId) === 'BOOL'">
           <div class="flex items-center h-full pt-2 pl-1 gap-2">
             <Checkbox 
                :modelValue="row.valueBool" 
                binary 
                inputId="chk-{{index}}"
                @update:modelValue="(val) => updateRow(index, 'valueBool', val)" 
             />
             <label :for="'chk-'+index" class="cursor-pointer text-sm select-none">
               {{ row.valueBool ? 'Sim / Ativo' : 'Não / Inativo' }}
             </label>
           </div>
        </template>

        <template v-else-if="getParameterType(row.parameterId) === 'DATE'">
          <DatePicker 
            :modelValue="row.valueDate" 
            showIcon 
            fluid 
            dateFormat="dd/mm/yy"
            placeholder="Selecionar data"
            class="w-full"
            @update:modelValue="(val) => updateRow(index, 'valueDate', val)" 
          />
        </template>

        <template v-else>
          <InputText 
            :modelValue="row.valueString" 
            placeholder="Texto..." 
            class="w-full" 
            @update:modelValue="(val) => updateRow(index, 'valueString', val)" 
          />
        </template>

      </div>

      <Button 
        icon="pi pi-trash" 
        severity="danger" 
        variant="text" 
        rounded
        @click="removeRow(index)" 
      />
    </div>

    <div v-if="(!modelValue || modelValue.length === 0)" class="text-surface-500 text-sm italic mb-2">
      Nenhum parâmetro adicionado.
    </div>

    <div>
      <Button 
        label="Adicionar Parâmetro" 
        icon="pi pi-plus" 
        severity="secondary" 
        outlined 
        size="small"
        @click="addRow" 
      />
    </div>
  </div>
</template>