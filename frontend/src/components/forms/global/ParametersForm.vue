<script setup>
import ParameterSelect from '@/components/forms/global/ParameterSelect.vue';
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';

const props = defineProps({
  modelValue: { type: Array, default: () => [] }
});
const emit = defineEmits(['update:modelValue']);

const addParameter = () => {
  const newParam = { parameterId: null, value: '' };
  emit('update:modelValue', [...props.modelValue, newParam]);
};

const removeParameter = (index) => {
  const updated = [...props.modelValue];
  updated.splice(index, 1);
  emit('update:modelValue', updated);
};

const updateParameter = (index, field, value) => {
  const updated = [...props.modelValue];
  updated[index][field] = value;
  emit('update:modelValue', updated);
};
</script>

<template>
  <div class="flex flex-col gap-4">
    <div v-for="(param, index) in modelValue" :key="index" class="flex gap-3 items-center">

      <ParameterSelect v-model="param.parameterId" class="min-w-40 max-w-40"/>

      <InputText v-model="param.value" placeholder="Valor" class="flex-1"
        @input="updateParameter(index, 'value', $event.target.value)" />

      <Button icon="pi pi-trash" severity="danger" @click="removeParameter(index)" />
    </div>

    <Button label="Agregar parâmetro" icon="pi pi-plus" @click="addParameter" />
  </div>
</template>
