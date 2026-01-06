<script setup>
import { onMounted } from 'vue'; // Agregamos onMounted
import { useParameterStore } from '@/stores/parameterStore';
import ParameterSelect from '@/components/forms/global/ParameterSelect.vue';

// PrimeVue Imports
import InputText from 'primevue/inputtext';
import InputNumber from 'primevue/inputnumber';
import Checkbox from 'primevue/checkbox';
import DatePicker from 'primevue/datepicker';
import Button from 'primevue/button';
import Tag from 'primevue/tag'; // 🆕 Importar Tag

const props = defineProps({
  modelValue: { type: Array, default: () => [] },
  phaseId: { type: Number, required: true },
  showValidation: { type: Boolean, default: false }
});

const emit = defineEmits(['update:modelValue']);
const store = useParameterStore();

// 🆕 Cargar datos al montar para poder validar mandatory
onMounted(async () => {
  if (store.items.length === 0) await store.fetchAll();
});

const getParameterType = (parameterId) => {
  if (!parameterId) return 'STRING';
  const param = store.items.find(p => p.id === parameterId);
  return param ? param.valueType : 'STRING';
};

// 🆕 Helper para saber si es obligatorio
const isMandatory = (parameterId) => {
  if (!parameterId) return false;
  const param = store.items.find(p => p.id === parameterId);
  return param?.mandatory === true && param?.phase?.id === props.phaseId;
};

// 🆕 Helper para evitar duplicados en el select
const getExcludedIds = (currentRowIndex) => {
  return (props.modelValue || [])
    .filter((row, index) => index !== currentRowIndex && row.parameterId)
    .map(row => row.parameterId);
};

// ... addRow, removeRow, updateRow (sin cambios) ...
const addRow = () => {
  const current = props.modelValue || [];
  const newRow = {
    id: null,
    parameterId: null,
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

const updateRow = (index, field, value) => {
  const current = props.modelValue || [];
  const updated = current.map((item, i) => {
    if (i === index) {
      const newItem = { ...item, [field]: value };
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
      <div class="w-3/5">Parâmetro</div>
      <div class="flex-1">Valor</div>
      <div class="w-10"></div>
    </div>

    <div v-for="(row, index) in (modelValue || [])" :key="index" class="flex gap-3 items-start relative">

      <div class="w-3/5 relative">
        <ParameterSelect :modelValue="row.parameterId" :phaseId="props.phaseId" :disabled="isMandatory(row.parameterId)"
          :excludedIds="getExcludedIds(index)" @update:modelValue="(val) => updateRow(index, 'parameterId', val)"
          class="w-full" />

        <Tag v-if="isMandatory(row.parameterId)" value="Obrigatório" severity="warning"
          class="absolute -top-2 -right-2 text-[10px] px-1 py-0 z-10 shadow-sm" />
      </div>

      <div class="flex-1">
        <template v-if="getParameterType(row.parameterId) === 'NUMBER'">
          <InputNumber :modelValue="row.valueNumber" placeholder="0.00" :minFractionDigits="0" :maxFractionDigits="4"
            mode="decimal" fluid class="w-full"
            :class="{ 'p-invalid': showValidation && row.valueNumber === null && isMandatory(row.parameterId) }"
            @update:modelValue="(val) => updateRow(index, 'valueNumber', val)" />
        </template>

        <template v-else-if="getParameterType(row.parameterId) === 'BOOL'">
          <div class="flex items-center h-full pt-2 pl-1 gap-2">
            <Checkbox :modelValue="row.valueBool" binary :inputId="'chk-' + index"
              @update:modelValue="(val) => updateRow(index, 'valueBool', val)" />
            <label :for="'chk-' + index" class="cursor-pointer text-sm select-none">
              {{ row.valueBool ? 'Sim / Ativo' : 'Não / Inativo' }}
            </label>
          </div>
        </template>

        <template v-else-if="getParameterType(row.parameterId) === 'DATE'">
          <DatePicker :modelValue="row.valueDate" showIcon fluid dateFormat="dd/mm/yy" placeholder="Selecionar data"
            class="w-full" :invalid="showValidation && !row.valueDate && isMandatory(row.parameterId)"
            @update:modelValue="(val) => updateRow(index, 'valueDate', val)" />
        </template>

        <template v-else>
          <InputText :modelValue="row.valueString" placeholder="Texto..." class="w-full"
            :class="{ 'p-invalid': showValidation && !row.valueString && isMandatory(row.parameterId) }"
            @update:modelValue="(val) => updateRow(index, 'valueString', val)" />
        </template>
      </div>

      <div class="w-10 flex justify-center" v-if="!isMandatory(row.parameterId)">
        <Button icon="pi pi-trash" severity="danger" variant="text" rounded :disabled="isMandatory(row.parameterId)"
          @click="removeRow(index)" />
      </div>
    </div>

    <div v-if="(!modelValue || modelValue.length === 0)" class="text-surface-500 text-sm italic mb-2">
      Nenhum parâmetro adicionado.
    </div>

    <div>
      <Button label="Adicionar Parâmetro" icon="pi pi-plus" severity="secondary" outlined size="small"
        @click="addRow" />
    </div>
  </div>
</template>