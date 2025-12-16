<script setup>
import { defineProps, defineEmits } from 'vue';
import Dialog from 'primevue/dialog';
import trackingFornoEntradaForm from './TrackingFornoEntradaForm.vue';

const props = defineProps({
  modelValue: Boolean,
  item: Object
});
const emit = defineEmits(['update:modelValue']);
</script>

<template>
  <Dialog :visible="props.modelValue" @update:visible="emit('update:modelValue', $event)" modal header="Editar Registo" :style="{ width: '800px' }">
    <trackingFornoEntradaForm :item="props.item" mode="edit" />
  </Dialog>
</template>
