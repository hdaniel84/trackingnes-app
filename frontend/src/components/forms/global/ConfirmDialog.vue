<script setup>
import { defineProps, defineEmits } from 'vue';
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';

const props = defineProps({
  visible: Boolean,
  message: {
    type: String,
    default: 'Tem a certeza de que quer continuar?'
  }
});

const emit = defineEmits(['update:visible', 'confirm']);
</script>

<template>
  <Dialog :visible="props.visible" @update:visible="emit('update:visible', $event)"  modal header="Confirmação" :style="{ width: '400px' }">
    <p>{{ props.message }}</p>
    <template #footer>
      <Button label="Sí" icon="pi pi-check" severity="danger" @click="emit('confirm')" />
      <Button label="Cancelar" icon="pi pi-times" text @click="emit('update:visible', false)" />
    </template>
  </Dialog>
</template>
