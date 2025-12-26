<script setup>
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';

const props = defineProps({
    visible: Boolean,
    trackingId: [Number, String],
    title: { type: String, default: 'Registo Criado com Sucesso!' }
});

const emit = defineEmits(['update:visible', 'close', 'create-new']);

const handleClose = () => {
    emit('update:visible', false);
    emit('close');
};

</script>

<template>
    <Dialog 
        :visible="props.visible" 
        @update:visible="emit('update:visible', $event)" 
        modal 
        :closable="false"
        :style="{ width: '400px' }"
        :pt="{ 
            root: { class: 'border-none shadow-2xl rounded-2xl' },
            header: { class: 'hidden' }, // Ocultamos header por defecto para diseño custom
            content: { class: 'p-0 rounded-2xl overflow-hidden' }
        }"
    >
        <div class="flex flex-col items-center justify-center bg-white dark:bg-surface-900">
            
            <div class="w-full bg-green-50 dark:bg-green-900/20 py-8 flex flex-col items-center border-b border-green-100 dark:border-green-800">
                <div class="w-10 h-10 rounded-full bg-green-100 dark:bg-green-800 flex items-center justify-center mb-4 animate-bounce-slow">
                    <i class="pi pi-check text-4xl text-green-600 dark:text-green-400 font-bold"></i>
                </div>
                <h2 class="text-xl font-bold text-green-700 dark:text-green-400">Sucesso!</h2>
            </div>

            <div class="p-6 w-full text-center">
                <p class="text-surface-600 dark:text-surface-300 mb-4">
                    {{ title }}
                </p>

                <div class="bg-surface-50 dark:bg-surface-800 border border-surface-200 dark:border-surface-700 rounded-xl p-4 mb-2 flex flex-col items-center">
                    <span class="text-xs text-surface-500 uppercase tracking-widest mb-1">ID do Registo</span>
                    <span class="text-4xl font-black text-primary-600 dark:text-primary-400">#{{ trackingId }}</span>
                </div>
                
                <small class="text-surface-400 text-xs">
                    Os dados já estão disponíveis para consulta.
                </small>
            </div>

            <div class="flex gap-3 w-full p-4 bg-surface-50 dark:bg-surface-800/50 border-t border-surface-100 dark:border-surface-700">
                <Button 
                    label="Fechar" 
                    severity="secondary" 
                    text 
                    class="w-full"
                    @click="handleClose"
                />
                </div>
        </div>
    </Dialog>
</template>

<style scoped>
/* Animación suave para el check */
.animate-bounce-slow {
    animation: bounce 2s infinite;
}
@keyframes bounce {
  0%, 100% { transform: translateY(-5%); }
  50% { transform: translateY(5%); }
}
</style>