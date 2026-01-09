<script setup>
import { computed } from 'vue';
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';
import { useErrorDialog } from '@/layout/composables/errorDialog';

// ✅ Usamos el helper global (LÓGICA INTACTA)
const {
    errorDialogVisible,
    errorDialogMessage,
    errorDialogDetails,
    errorDialogType, // nuevo campo para tipo
    closeErrorDialog
} = useErrorDialog();

// Icono dinámico según el tipo (LÓGICA INTACTA)
const dialogIcon = computed(() => {
    switch (errorDialogType.value) {
        case 'error': return 'pi pi-times-circle text-red-600'; // Color más fuerte
        case 'success': return 'pi pi-check-circle text-green-600'; // Color más fuerte
        case 'warning': return 'pi pi-exclamation-triangle text-yellow-500';
        case 'info': return 'pi pi-info-circle text-blue-600'; // Color más fuerte
        default: return 'pi pi-info-circle text-gray-500';
    }
});
</script>

<template>
    <Dialog 
        v-model:visible="errorDialogVisible" 
        modal 
        :style="{ width: '38rem' }" 
        :closable="false"
    >
        <template #header>
            <div class="flex items-center gap-3 w-full p-0">
                <h3 class="text-xl font-bold text-surface-900 dark:text-surface-0">
                    {{ errorDialogType === 'error' ? 'Erro ao processar a transação' :
                       errorDialogType === 'success' ? 'Operação Concluída' :
                       errorDialogType === 'warning' ? 'Aviso Importante' :
                       'Informação do Sistema' }}
                </h3>
            </div>
        </template>

        <div class="flex items-start gap-5">
            
            <i :class="dialogIcon" class="text-4xl shrink-0 mt-1"></i>

            <div class="flex flex-col flex-1">
                <p class="font-medium text-surface-800 dark:text-surface-100 mb-4 leading-relaxed">
                    {{ errorDialogMessage }}
                </p>

                <div 
                    v-if="errorDialogDetails.length" 
                    class="bg-surface-50 dark:bg-surface-800 p-3 rounded-lg border border-surface-200 dark:border-surface-700 max-h-48 overflow-y-auto"
                >
                    <h4 class="font-semibold text-sm mb-2 text-surface-700 dark:text-surface-300">Detalhes Técnicos:</h4>
                    <ul class="list-disc pl-4 text-xs text-surface-600 dark:text-surface-400 space-y-1">
                        <li v-for="(err, i) in errorDialogDetails" :key="i">{{ err.trim() }}</li>
                    </ul>
                </div>
            </div>
        </div>
        
        <template #footer>
            <div class="w-full flex justify-end">
                <Button 
                    label="Fechar" 
                    icon="pi pi-check" 
                    class="px-6" 
                    @click="closeErrorDialog" 
                    :severity="errorDialogType === 'error' ? 'danger' : 'primary'"
                />
            </div>
        </template>
    </Dialog>
</template>