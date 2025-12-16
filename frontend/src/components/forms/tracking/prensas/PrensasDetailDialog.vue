<script setup>
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';
import Tag from 'primevue/tag';
import Divider from 'primevue/divider';
import { useFormatters } from '@/layout/composables/useFormatters';

const { formatDateTime } = useFormatters();

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
        header="Detalhes do Registo" 
        :style="{ width: '90vw', maxWidth: '500px' }"
        dismissableMask
    >
        <div v-if="props.item" class="flex flex-col gap-4">
            
            <div class="bg-surface-50 dark:bg-surface-800 p-4 rounded-xl border border-surface-100 dark:border-surface-700">
                <div class="flex justify-between items-start mb-2">
                    <span class="text-xs uppercase tracking-wider text-surface-500 font-semibold">Registo #{{ props.item.id }}</span>
                    <Tag :value="`${props.item.quantity} Peças`" severity="success" rounded />
                </div>
                <h2 class="text-xl font-bold text-primary-700 dark:text-primary-400 leading-tight">
                    {{ props.item.product?.description || 'Sem produto definido' }}
                </h2>
                <div class="text-sm text-surface-500 mt-1">
                    {{ props.item.product?.codigoProduto }}
                </div>
            </div>

            <div class="grid grid-cols-2 gap-3">
                <div class="p-3 bg-surface-0 dark:bg-surface-900 border rounded-lg">
                    <p class="text-xs text-surface-500 mb-1">Início da Produção</p>
                    <p class="font-medium text-surface-900 dark:text-surface-100">
                        {{ formatDateTime(props.item.startTime) }}
                    </p>
                </div>
                <div class="p-3 bg-surface-0 dark:bg-surface-900 border rounded-lg">
                    <p class="text-xs text-surface-500 mb-1">Fim da Produção</p>
                    <p class="font-medium text-surface-900 dark:text-surface-100">
                        {{ props.item.endTime ? formatDateTime(props.item.endTime) : '-' }}
                    </p>
                </div>
            </div>

            <div class="space-y-3">
                <div class="flex justify-between border-b border-surface-100 dark:border-surface-700 pb-2">
                    <span class="text-surface-500 text-sm">Equipamento</span>
                    <span class="font-medium text-sm">{{ props.item.equipment?.description }}</span>
                </div>
                
                <div class="flex justify-between border-b border-surface-100 dark:border-surface-700 pb-2">
                    <span class="text-surface-500 text-sm">Carro (Logística)</span>
                    <span class="font-medium text-sm">{{ props.item.logisticUnit }}</span>
                </div>

                <div class="flex justify-between border-b border-surface-100 dark:border-surface-700 pb-2">
                    <span class="text-surface-500 text-sm">Turno</span>
                    <span class="font-medium text-sm">{{ props.item.shift?.description }}</span>
                </div>

                <div class="py-2">
                    <span class="text-surface-500 text-sm block mb-1">Equipa</span>
                    <div class="font-medium text-sm flex items-center gap-2">
                        <i class="pi pi-users text-primary-500"></i>
                        {{ props.item.team?.description }}
                        <span class="text-surface-400 text-xs" v-if="props.item.team?.sectionDescription">
                            ({{ props.item.team.sectionDescription }})
                        </span>
                    </div>
                </div>
            </div>

            <div v-if="props.item.comments" class="bg-yellow-50 dark:bg-yellow-900/20 p-3 rounded-lg border border-yellow-200 dark:border-yellow-800">
                <p class="text-xs text-yellow-700 dark:text-yellow-500 font-semibold mb-1">Observações</p>
                <p class="text-sm text-surface-800 dark:text-surface-200 italic">"{{ props.item.comments }}"</p>
            </div>

            <div v-if="props.item.rawMaterial" class="p-3 border rounded-lg bg-surface-50">
                <p class="text-xs text-surface-500 mb-1">Matéria Prima</p>
                <p class="font-medium text-sm">{{ props.item.rawMaterial?.description }}</p>
            </div>

            <div v-if="props.item.parameters && props.item.parameters.length > 0">
                <Divider align="center">
                    <span class="text-xs font-bold text-surface-400 uppercase tracking-widest">Parâmetros</span>
                </Divider>
                
                <div class="grid grid-cols-2 gap-3">
                    <div v-for="(param, index) in props.item.parameters" :key="index"
                        class="p-2 border border-surface-200 dark:border-surface-700 rounded-lg flex flex-col items-center text-center bg-white dark:bg-surface-800 shadow-sm">
                        
                        <span class="text-xs text-surface-500 mb-1 line-clamp-1" :title="param.parameterDescription">
                            {{ param.parameterDescription }}
                        </span>
                        
                        <span class="font-bold text-primary-600 text-lg">
                            {{ param.valueString }}
                        </span>
                    </div>
                </div>
            </div>
        </div>

        <template #footer>
            <Button label="Fechar" icon="pi pi-times" text severity="secondary" @click="emit('update:visible', false)" class="w-full" />
        </template>
    </Dialog>
</template>