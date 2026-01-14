<script setup>
import { onMounted } from 'vue';
import { useRawMaterialStore } from '@/stores/rawMaterialStore'; // 1. Importar Store
import RawMaterialSelect from '@/components/forms/global/RawMaterialSelect.vue';
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
import Tag from 'primevue/tag';

const props = defineProps({
  modelValue: { type: Array, default: () => [] },
  phaseId: { type: Number, required: true },
  showValidation: { type: Boolean, default: false }
});

const emit = defineEmits(['update:modelValue']);
const store = useRawMaterialStore();

// 2. Cargar datos para saber cuáles son mandatory
onMounted(() => {
  if (store.items.length === 0) store.fetchAll();
});

// 3. Helper para saber si un ID es obligatorio
const isMandatory = (rawMaterialId) => {
  if (!rawMaterialId) return false;
  const item = store.items.find(rm => rm.id === rawMaterialId);
  return item?.mandatory === true;
};

// Regla:Se bloquea solo si es obligatorio Y es la PRIMERA aparición en la lista.
const isRowLocked = (row, index) => {
  if (!row.rawMaterialId || !isMandatory(row.rawMaterialId)) {
    return false;
  }
  const firstIndex = props.modelValue.findIndex(r => r.rawMaterialId === row.rawMaterialId);
  return firstIndex === index;
};

// Helper para detectar duplicados visualmente
const isDuplicateRow = (row, index) => {
  if (!row.rawMaterialId || !row.value) return false;

  return props.modelValue.some((otherRow, otherIndex) => {
    return (
      otherIndex !== index &&
      otherRow.rawMaterialId === row.rawMaterialId &&
      otherRow.value.trim().toLowerCase() === row.value.trim().toLowerCase()
    );
  });
};

const addRow = () => {
  const current = props.modelValue || [];
  const newRow = { id: null, rawMaterialId: null, value: '' };
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
    if (i === index) return { ...item, [field]: value };
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

    <div v-for="(row, index) in (modelValue || [])" :key="index" class="flex gap-3 items-start relative">

      <div class="w-3/5 relative">
        <RawMaterialSelect :modelValue="row.rawMaterialId" :phaseId="props.phaseId" :disabled="isRowLocked(row, index)"
          @update:modelValue="(val) => updateRow(index, 'rawMaterialId', val)" />

        <Tag v-if="isMandatory(row.rawMaterialId)" value="Obrigatório" severity="warning"
          class="absolute -top-2 -right-2 text-[10px] px-1 py-0 z-10" />
      </div>

      <div class="flex-1">
        <InputText :modelValue="row.value" placeholder="Ex: Lote 123" class="w-full" :id="`rawMaterial-value-${index}`"
          :name="`rawMaterials[${index}].value`"
          :class="{ 'p-invalid': (showValidation && !row.value && isMandatory(row.rawMaterialId)) || isDuplicateRow(row, index) }"
          @update:modelValue="(val) => updateRow(index, 'value', val)" />
        <small v-if="isDuplicateRow(row, index)" class="text-red-500 text-[10px] block mt-1">
          Entrada duplicada
        </small>
      </div>

      <div class="w-10 flex justify-center" v-if="!isRowLocked(row, index)">
        <Button icon="pi pi-trash" severity="danger" variant="text" rounded @click="removeRow(index)"
          v-tooltip.left="isMandatory(row.rawMaterialId) ? 'Item obrigatório não pode ser removido' : 'Remover'" />
      </div>
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