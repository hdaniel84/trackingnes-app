<script setup>
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';
import Tag from 'primevue/tag';
import Divider from 'primevue/divider';
import { useFormatters } from '@/layout/composables/useFormatters';

const { formatDateTime } = useFormatters();

// Helper para mostrar el valor correcto
const formatParamValue = (param) => {
    if (param.valueNumber !== null && param.valueNumber !== undefined) {
        return param.valueNumber;
    }
    if (param.valueBool !== null && param.valueBool !== undefined) {
        return param.valueBool ? 'Sim' : 'Não';
    }
    if (param.valueDate) {
        return formatDateTime(param.valueDate);
    }
    return param.valueString || '-';
};

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
        header="Detalhes da Produção" 
        :style="{ width: '90vw', maxWidth: '600px' }" 
        :breakpoints="{ '960px': '95vw' }"
        dismissableMask 
        class="p-0"
    >
        <div v-if="props.item" class="flex flex-col gap-6">

            <div class="bg-surface-50 dark:bg-surface-800 p-2 rounded-xl border border-surface-100 dark:border-surface-700 shadow-sm relative overflow-hidden">
                <div class="absolute top-0 right-0 bg-surface-200 dark:bg-surface-700 text-xl font-mono px-2 py-1 rounded-bl-lg text-surface-600 dark:text-surface-300">
                    ID #{{ props.item.id }}
                </div>

                <div class="flex flex-col gap-2 mt-1">
                    <div class="flex items-center gap-2">
                        <Tag severity="info" class="text-xs font-bold px-2">
                            {{ props.item.product?.productCode || 'S/C' }}
                        </Tag>
                        <Tag severity="success" class="text-xs font-bold px-2">
                            <i class="pi pi-box mr-1"></i> {{ props.item.quantity }} Peças
                        </Tag>
                    </div>

                    <h2 class="text-2xl font-bold text-primary-700 dark:text-primary-400 leading-tight">
                        {{ props.item.product?.description || 'Produto não identificado' }}
                    </h2>
                </div>
            </div>

            <div class="grid grid-cols-2 gap-4">
                <div class="flex flex-col bg-white dark:bg-surface-900 border border-surface-200 dark:border-surface-700 p-3 rounded-lg border-l-4 border-l-green-500">
                    <span class="text-xs uppercase text-surface-400 font-semibold mb-1">Início</span>
                    <span class="font-mono text-sm font-medium text-surface-900 dark:text-surface-100">
                        {{ formatDateTime(props.item.startTime) }}
                    </span>
                </div>
                <div class="flex flex-col bg-white dark:bg-surface-900 border border-surface-200 dark:border-surface-700 p-3 rounded-lg border-l-4 border-l-red-400">
                    <span class="text-xs uppercase text-surface-400 font-semibold mb-1">Fim</span>
                    <span class="font-mono text-sm font-medium text-surface-900 dark:text-surface-100">
                        {{ props.item.endTime ? formatDateTime(props.item.endTime) : '-- : --' }}
                    </span>
                </div>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-4 px-2">
                <div class="flex items-center gap-3">
                    <div class="w-8 h-8 rounded-full bg-blue-50 dark:bg-blue-900/30 flex items-center justify-center text-blue-600 dark:text-blue-400">
                        <i class="pi pi-cog"></i>
                    </div>
                    <div class="flex flex-col">
                        <span class="text-xs text-surface-500">Equipamento</span>
                        <span class="text-sm font-semibold">{{ props.item.equipment?.description }}</span>
                    </div>
                </div>

                <div class="flex items-center gap-3">
                    <div class="w-8 h-8 rounded-full bg-orange-50 dark:bg-orange-900/30 flex items-center justify-center text-orange-600 dark:text-orange-400">
                        <i class="pi pi-truck"></i>
                    </div>
                    <div class="flex flex-col">
                        <span class="text-xs text-surface-500">Un. Logistica (Carro/Vagão/Palete)</span>
                        <span class="text-sm font-semibold">{{ props.item.logisticUnit }}</span>
                    </div>
                </div>

                <div class="flex items-center gap-3">
                    <div class="w-8 h-8 rounded-full bg-purple-50 dark:bg-purple-900/30 flex items-center justify-center text-purple-600 dark:text-purple-400">
                        <i class="pi pi-clock"></i>
                    </div>
                    <div class="flex flex-col">
                        <span class="text-xs text-surface-500">Turno</span>
                        <span class="text-sm font-semibold">{{ props.item.shift?.description }}</span>
                    </div>
                </div>

                <div class="flex items-center gap-3">
                    <div class="w-8 h-8 rounded-full bg-indigo-50 dark:bg-indigo-900/30 flex items-center justify-center text-indigo-600 dark:text-indigo-400">
                        <i class="pi pi-users"></i>
                    </div>
                    <div class="flex flex-col">
                        <span class="text-xs text-surface-500">Equipa</span>
                        <span class="text-sm font-semibold">{{ props.item.team?.description }}</span>
                        <span class="text-[10px] text-surface-400 leading-none" v-if="props.item.team?.sectionDescription">
                            {{ props.item.team.sectionDescription }}
                        </span>
                    </div>
                </div>
            </div>

            <div v-if="props.item.trackingSourceId" class="mt-2">
                <div class="bg-cyan-50 dark:bg-cyan-900/20 border border-cyan-200 dark:border-cyan-800 rounded-lg p-3 flex items-center gap-4 relative overflow-hidden">
                    <div class="absolute -right-4 -top-4 text-cyan-100 dark:text-cyan-800/30 text-6xl">
                        <i class="pi pi-link"></i>
                    </div>
                    
                    <div class="z-10 w-10 h-10 rounded-full bg-white dark:bg-cyan-900 border border-cyan-100 dark:border-cyan-700 flex items-center justify-center text-cyan-600 dark:text-cyan-400 shadow-sm">
                        <i class="pi pi-link text-lg"></i>
                    </div>

                    <div class="z-10 flex flex-col">
                        <span class="text-[10px] uppercase font-bold text-cyan-600 dark:text-cyan-400 tracking-wider">Origem (Fase Anterior)</span>
                        <span class="text-sm font-bold text-surface-800 dark:text-surface-100">
                            {{ props.item.trackingSourceProductDescription || 'Produto da fase anterior' }}
                        </span>
                        <div class="flex items-center gap-2 mt-0.5">
                            <span class="text-xs text-surface-500 bg-white dark:bg-surface-800 px-1.5 py-0.5 rounded border border-surface-200 dark:border-surface-700 font-mono">
                                REF #{{ props.item.trackingSourceId }}
                            </span>
                            </div>
                    </div>
                </div>
            </div>

            <div v-if="props.item.rawMaterials && props.item.rawMaterials.length > 0">
                <Divider align="left">
                    <div class="inline-flex items-center gap-2 px-2 py-1 rounded-full bg-surface-100 dark:bg-surface-800 text-surface-600 dark:text-surface-300 text-xs font-bold uppercase tracking-wide">
                        <i class="pi pi-box"></i> Matérias Primas
                    </div>
                </Divider>

                <div class="flex flex-col gap-2">
                    <div v-for="(rm, index) in props.item.rawMaterials" :key="index"
                        class="flex justify-between items-center p-3 bg-surface-50 dark:bg-surface-800/50 rounded-lg border border-surface-200 dark:border-surface-700">
                        <div class="flex items-center gap-3">
                            <i class="pi pi-circle-fill text-[8px] text-surface-400"></i>
                            <span class="text-sm font-medium text-surface-700 dark:text-surface-200">
                                {{ rm.rawaMaterialDescription || rm.rawMaterialDescription || 'Material' }}
                            </span>
                        </div>
                        <div class="flex flex-col items-end">
                            <span class="text-[10px] text-surface-400 uppercase">Lote / Valor</span>
                            <span class="font-mono text-sm font-bold text-surface-900 dark:text-surface-100">
                                {{ rm.value }}
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            <div v-if="props.item.parameters && props.item.parameters.length > 0">
                <Divider align="left"></Divider>
                <div class="grid grid-cols-2 gap-3">
                    <div v-for="(param, index) in props.item.parameters" :key="index"
                        class="p-3 border border-surface-200 dark:border-surface-700 rounded-lg flex flex-col items-start bg-white dark:bg-surface-800 shadow-sm relative overflow-hidden">
                        
                        <div class="absolute top-0 right-0 p-1">
                            <i v-if="param.valueBool !== null" class="pi pi-check-circle text-xs text-surface-300"></i>
                            <i v-else-if="param.valueDate" class="pi pi-calendar text-xs text-surface-300"></i>
                            <i v-else-if="param.valueNumber !== null" class="pi pi-hashtag text-xs text-surface-300"></i>
                            <i v-else class="pi pi-align-left text-xs text-surface-300"></i>
                        </div>

                        <span class="text-xs text-surface-500 mb-1 block w-full truncate pr-4"
                            :title="param.parameterDescription || param.description">
                            {{ param.parameterDescription || param.description || 'Parâmetro' }}
                        </span>

                        <span class="font-bold text-primary-600 dark:text-primary-400 text-lg w-full truncate">
                            {{ formatParamValue(param) }}
                        </span>
                    </div>
                </div>
            </div>

            <div v-if="props.item.auxiliaryEquipments?.length" class="mt-2 flex flex-wrap gap-2">
                <span class="text-xs text-surface-500 w-full mb-1">Equipamentos Auxiliares:</span>
                <Tag v-for="aux in props.item.auxiliaryEquipments" :key="aux.id" :value="aux.description"
                    severity="secondary" rounded />
            </div>

            <div v-if="props.item.comments"
                class="bg-yellow-50 dark:bg-yellow-900/20 p-4 rounded-lg border border-yellow-200 dark:border-yellow-800/50 flex gap-3">
                <i class="pi pi-info-circle text-yellow-600 dark:text-yellow-500 mt-1"></i>
                <div class="flex flex-col">
                    <span class="text-xs font-bold text-yellow-700 dark:text-yellow-500 uppercase mb-1">Observações</span>
                    <p class="text-sm text-surface-700 dark:text-surface-200 leading-relaxed">
                        {{ props.item.comments }}
                    </p>
                </div>
            </div>

        </div>

        <template #footer>
            <div class="w-full flex justify-end pt-2">
                <Button label="Fechar" icon="pi pi-times" text severity="secondary"
                    @click="emit('update:visible', false)" class="px-6" />
            </div>
        </template>
    </Dialog>
</template>