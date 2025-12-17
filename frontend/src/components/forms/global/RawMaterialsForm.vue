<script setup>
import RawMaterialSelect from '@/components/forms/global/RawMaterialSelect.vue';
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';

const props = defineProps({
  modelValue: { type: Array, default: () => [] },
  phaseId: { type: Number, required: true }
});

const emit = defineEmits(['update:modelValue']);

// 1. AGREGAR
const addRow = () => {
  const current = props.modelValue || [];
  // Estructura según DTO Request
  const newRow = {
    id: null,
    rawMaterialId: null,
    value: ''
  };
  emit('update:modelValue', [...current, newRow]);
};

// 2. ELIMINAR
const removeRow = (index) => {
  const current = props.modelValue || [];
  const updated = [...current];
  updated.splice(index, 1);
  emit('update:modelValue', updated);
};

// 3. ACTUALIZAR
const updateRow = (index, field, value) => {
  const current = props.modelValue || [];
  const updated = current.map((item, i) => {
    if (i === index) {
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
      <div class="w-1/2">Matéria Prima</div>
      <div class="flex-1">Lote / Valor</div>
      <div class="w-10"></div>
    </div>

    <div v-for="(row, index) in (modelValue || [])" :key="index" class="flex gap-3 items-start">
      <div class="w-1/2">
        <RawMaterialSelect :modelValue="row.rawMaterialId" :phaseId="props.phaseId"
          @update:modelValue="(val) => updateRow(index, 'rawMaterialId', val)" />
      </div>

      <div class="flex-1">
        <InputText :modelValue="row.value" placeholder="Ex: Lote 123" class="w-full"
          @update:modelValue="(val) => updateRow(index, 'value', val)" />
      </div>

      <Button icon="pi pi-trash" severity="danger" variant="text" rounded :disabled="(modelValue || []).length === 1"
        @click="removeRow(index)" />
    </div>

    <div v-if="(!modelValue || modelValue.length === 0)" class="text-surface-500 text-sm italic mb-2">
      Nenhuma matéria prima adicionada.
    </div>

    <div>
      <Button label="Adicionar Matéria Prima" icon="pi pi-plus" severity="secondary" outlined size="small"
        @click="addRow" />
    </div>
  </div>
</template>