<script setup>
import Dialog from 'primevue/dialog';
import PrensasForm from './PrensasForm.vue'; 

const props = defineProps({
  visible: Boolean, 
  item: Object
});

const emit = defineEmits(['update:visible']);
</script>

<template>
  <Dialog 
    :visible="props.visible" 
    @update:visible="emit('update:visible', $event)" 
    modal 
    header="Editar Registro"
    :style="{ width: '90vw', maxWidth: '1000px' }"
    :breakpoints="{ '960px': '95vw' }"
    dismissableMask
    maximizable
    class="p-0"
  >
    <PrensasForm 
      v-if="props.visible" 
      :item="props.item" 
      mode="edit" 
      @success="emit('update:visible', false)"
    />
  </Dialog>
</template>