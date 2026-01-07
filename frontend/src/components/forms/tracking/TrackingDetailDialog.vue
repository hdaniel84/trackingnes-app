<script setup>
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';
import Tag from 'primevue/tag';
import Divider from 'primevue/divider';
import { useFormatters } from '@/layout/composables/useFormatters';

const { formatDateTime } = useFormatters();

// Helper para mostrar el valor correcto de parámetros
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
    <Dialog :visible="props.visible" @update:visible="emit('update:visible', $event)" modal
        header="Detalhes da Produção" :style="{ width: '90vw', maxWidth: '650px' }" :breakpoints="{ '960px': '95vw' }"
        dismissableMask class="p-0" :pt="{ header: { class: 'border-b border-surface-200 dark:border-surface-700' } }">
        
        <div v-if="props.item" class="flex flex-col gap-6 py-2">

            <div class="bg-surface-50 dark:bg-surface-800 p-4 rounded-xl border border-surface-100 dark:border-surface-700 shadow-sm relative overflow-hidden">
                <div class="absolute top-0 right-0 bg-surface-200 dark:bg-surface-700 text-sm font-mono px-3 py-1 rounded-bl-lg text-surface-600 dark:text-surface-300 font-bold">
                    ID #{{ props.item.id }}
                </div>

                <div class="flex flex-col gap-2 mt-2">
                    <div class="flex flex-wrap items-center gap-2">
                        <Tag severity="info" class="font-bold">
                            {{ props.item.product?.productCode || 'S/C' }}
                        </Tag>
                        <Tag severity="success" class="font-bold">
                            <i class="pi pi-box mr-1 text-xs"></i> {{ props.item.quantity }} un.
                        </Tag>
                        <Tag :value="props.item.phase?.description" severity="secondary" class="uppercase text-[10px] tracking-wider font-bold"></Tag>
                    </div>

                    <h2 class="text-2xl font-bold text-surface-900 dark:text-surface-0 leading-tight mt-1">
                        {{ props.item.product?.description || 'Produto não identificado' }}
                    </h2>
                </div>
            </div>

            <div class="grid grid-cols-2 gap-4">
                <div class="flex flex-col bg-white dark:bg-surface-900 border border-surface-200 dark:border-surface-700 p-3 rounded-lg border-l-4 border-l-green-500 shadow-sm">
                    <div class="flex items-center gap-2 mb-1">
                        <i class="pi pi-play text-green-500 text-xs"></i>
                        <span class="text-xs uppercase text-surface-500 font-bold">Início</span>
                    </div>
                    <span class="font-mono text-sm font-bold text-surface-900 dark:text-surface-100">
                        {{ formatDateTime(props.item.startTime) }}
                    </span>
                </div>
                <div class="flex flex-col bg-white dark:bg-surface-900 border border-surface-200 dark:border-surface-700 p-3 rounded-lg border-l-4 border-l-red-400 shadow-sm">
                    <div class="flex items-center gap-2 mb-1">
                        <i class="pi pi-stop text-red-400 text-xs"></i>
                        <span class="text-xs uppercase text-surface-500 font-bold">Fim</span>
                    </div>
                    <span class="font-mono text-sm font-bold text-surface-900 dark:text-surface-100">
                        {{ props.item.endTime ? formatDateTime(props.item.endTime) : '-- : --' }}
                    </span>
                </div>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-6 px-1">
                
                <div class="flex items-start gap-3">
                    <div class="mt-0.5 w-8 h-8 rounded-full bg-blue-50 dark:bg-blue-900/30 flex items-center justify-center text-blue-600 dark:text-blue-400 shrink-0">
                        <i class="pi pi-cog"></i>
                    </div>
                    <div class="flex flex-col">
                        <span class="text-xs text-surface-500 font-medium uppercase tracking-wide">Equipamento</span>
                        <span class="text-sm font-bold text-surface-800 dark:text-surface-200">
                            {{ props.item.equipment?.description }}
                        </span>
                    </div>
                </div>

                <div class="flex items-start gap-3">
                    <div class="mt-0.5 w-8 h-8 rounded-full bg-orange-50 dark:bg-orange-900/30 flex items-center justify-center text-orange-600 dark:text-orange-400 shrink-0">
                        <i class="pi pi-truck"></i>
                    </div>
                    <div class="flex flex-col w-full">
                        <span class="text-xs text-surface-500 font-medium uppercase tracking-wide mb-1">Un. Logísticas</span>
                        
                        <div v-if="props.item.logisticUnits && props.item.logisticUnits.length > 0" class="flex flex-wrap gap-1.5">
                            <span v-for="unit in props.item.logisticUnits" :key="unit" 
                                class="inline-flex items-center px-2 py-0.5 rounded bg-surface-100 dark:bg-surface-800 text-surface-700 dark:text-surface-300 text-xs font-mono font-bold border border-surface-200 dark:border-surface-700">
                                #{{ unit }}
                            </span>
                        </div>
                        <span v-else class="text-sm text-surface-400 italic">Sem informação de carro</span>
                    </div>
                </div>

                <div class="flex items-start gap-3">
                    <div class="mt-0.5 w-8 h-8 rounded-full bg-purple-50 dark:bg-purple-900/30 flex items-center justify-center text-purple-600 dark:text-purple-400 shrink-0">
                        <i class="pi pi-clock"></i>
                    </div>
                    <div class="flex flex-col">
                        <span class="text-xs text-surface-500 font-medium uppercase tracking-wide">Turno</span>
                        <span class="text-sm font-bold text-surface-800 dark:text-surface-200">
                            {{ props.item.shift?.description }}
                        </span>
                    </div>
                </div>

                <div class="flex items-start gap-3">
                    <div class="mt-0.5 w-8 h-8 rounded-full bg-indigo-50 dark:bg-indigo-900/30 flex items-center justify-center text-indigo-600 dark:text-indigo-400 shrink-0">
                        <i class="pi pi-users"></i>
                    </div>
                    <div class="flex flex-col">
                        <span class="text-xs text-surface-500 font-medium uppercase tracking-wide">Equipa</span>
                        <span class="text-sm font-bold text-surface-800 dark:text-surface-200">
                            {{ props.item.team?.description }}
                        </span>
                        <span class="text-[10px] text-surface-400 leading-none mt-0.5" v-if="props.item.team?.sectionDescription">
                            {{ props.item.team.sectionDescription }}
                        </span>
                    </div>
                </div>
            </div>

            <div v-if="props.item.sourceTrackings && props.item.sourceTrackings.length > 0">
                <Divider align="left" type="solid" class="my-4">
                    <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-surface-100 dark:bg-surface-800 text-surface-600 dark:text-surface-300 text-xs font-bold uppercase tracking-wide border border-surface-200 dark:border-surface-700">
                        <i class="pi pi-link"></i> Origens ({{ props.item.sourceTrackings.length }})
                    </div>
                </Divider>

                <div class="flex flex-col gap-3">
                    <div v-for="source in props.item.sourceTrackings" :key="source.id"
                        class="bg-cyan-50 dark:bg-cyan-900/10 border border-cyan-200 dark:border-cyan-800/50 rounded-lg p-3 flex items-center gap-3 relative overflow-hidden group hover:border-cyan-300 transition-colors">
                        
                        <div class="absolute -right-2 -top-2 text-cyan-100 dark:text-cyan-800/20 text-5xl group-hover:scale-110 transition-transform">
                            <i class="pi pi-link"></i>
                        </div>

                        <div class="flex flex-col items-center justify-center min-w-[50px] bg-white dark:bg-surface-900 rounded border border-cyan-100 dark:border-cyan-800 p-1 z-10">
                            <span class="text-[9px] text-surface-400 uppercase font-bold">REF</span>
                            <span class="text-sm font-bold text-cyan-700 dark:text-cyan-400">#{{ source.id }}</span>
                        </div>

                        <div class="z-10 flex flex-col flex-1">
                            <span class="text-sm font-bold text-surface-800 dark:text-surface-100 leading-tight">
                                {{ source.productDescription || 'Produto Anterior' }}
                            </span>
                            
                            <div v-if="source.logisticUnits && source.logisticUnits.length > 0" class="flex flex-wrap gap-1 mt-1.5">
                                <span v-for="u in source.logisticUnits" :key="u" class="text-[10px] bg-cyan-100 dark:bg-cyan-800 text-cyan-800 dark:text-cyan-200 px-1.5 py-0.5 rounded font-mono">
                                    {{ u }}
                                </span>
                            </div>
                            <span v-else class="text-[10px] text-surface-400 mt-1">Sem unidades logísticas</span>
                        </div>
                    </div>
                </div>
            </div>

            <div v-if="props.item.rawMaterials && props.item.rawMaterials.length > 0">
                <Divider align="left" class="my-4">
                    <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-surface-100 dark:bg-surface-800 text-surface-600 dark:text-surface-300 text-xs font-bold uppercase tracking-wide border border-surface-200 dark:border-surface-700">
                        <i class="pi pi-box"></i> Matérias Primas
                    </div>
                </Divider>

                <div class="flex flex-col gap-2">
                    <div v-for="(rm, index) in props.item.rawMaterials" :key="index"
                        class="flex justify-between items-center p-2.5 bg-surface-50 dark:bg-surface-800/50 rounded-lg border border-surface-200 dark:border-surface-700 hover:bg-surface-100 dark:hover:bg-surface-800 transition-colors">
                        <div class="flex items-center gap-3">
                            <div class="w-1.5 h-1.5 rounded-full bg-surface-400"></div>
                            <span class="text-sm font-medium text-surface-700 dark:text-surface-200">
                                {{ rm.rawaMaterialDescription || rm.rawMaterialDescription || 'Material' }}
                            </span>
                        </div>
                        <div class="flex flex-col items-end">
                            <span class="text-[9px] text-surface-400 uppercase tracking-wider font-bold">Lote / Valor</span>
                            <span class="font-mono text-sm font-bold text-surface-900 dark:text-surface-100">
                                {{ rm.value }}
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            <div v-if="props.item.parameters && props.item.parameters.length > 0">
                <Divider align="left" class="my-4">
                    <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-surface-100 dark:bg-surface-800 text-surface-600 dark:text-surface-300 text-xs font-bold uppercase tracking-wide border border-surface-200 dark:border-surface-700">
                        <i class="pi pi-sliders-h"></i> Parâmetros
                    </div>
                </Divider>
                <div class="grid grid-cols-2 gap-3">
                    <div v-for="(param, index) in props.item.parameters" :key="index"
                        class="p-3 border border-surface-200 dark:border-surface-700 rounded-lg flex flex-col items-start bg-white dark:bg-surface-800 shadow-sm relative overflow-hidden group hover:border-primary-200 dark:hover:border-primary-700/50 transition-colors">

                        <div class="absolute top-2 right-2 opacity-50">
                            <i v-if="param.valueBool !== null" class="pi pi-check-circle text-xs text-surface-300"></i>
                            <i v-else-if="param.valueDate" class="pi pi-calendar text-xs text-surface-300"></i>
                            <i v-else-if="param.valueNumber !== null" class="pi pi-sort-numeric-up text-xs text-surface-300"></i>
                            <i v-else class="pi pi-align-left text-xs text-surface-300"></i>
                        </div>

                        <span class="text-[10px] uppercase font-bold text-surface-400 mb-1 block w-full truncate pr-4"
                            :title="param.parameterDescription || param.description">
                            {{ param.parameterDescription || param.description || 'Parâmetro' }}
                        </span>

                        <span class="font-bold text-surface-700 dark:text-surface-200 text-base w-full truncate">
                            {{ formatParamValue(param) }}
                        </span>
                    </div>
                </div>
            </div>

            <div v-if="props.item.auxiliaryEquipments?.length" class="bg-surface-50 dark:bg-surface-800/50 p-3 rounded-lg border border-surface-100 dark:border-surface-700">
                <span class="text-xs font-bold text-surface-500 uppercase tracking-wide block mb-2">Equipamentos Auxiliares</span>
                <div class="flex flex-wrap gap-2">
                    <Tag v-for="aux in props.item.auxiliaryEquipments" :key="aux.id" :value="aux.description" severity="secondary" class="!font-medium" rounded />
                </div>
            </div>

            <div v-if="props.item.comments"
                class="bg-yellow-50 dark:bg-yellow-900/10 p-4 rounded-lg border border-yellow-200 dark:border-yellow-800/30 flex gap-3">
                <i class="pi pi-info-circle text-yellow-600 dark:text-yellow-500 mt-0.5 shrink-0"></i>
                <div class="flex flex-col">
                    <span class="text-xs font-bold text-yellow-700 dark:text-yellow-500 uppercase mb-1">Observações</span>
                    <p class="text-sm text-surface-700 dark:text-surface-200 leading-relaxed italic">
                        "{{ props.item.comments }}"
                    </p>
                </div>
            </div>

        </div>

        <template #footer>
            <div class="w-full flex justify-end gap-2 pt-2 border-t border-surface-100 dark:border-surface-700 mt-2">
                <Button label="Fechar" icon="pi pi-times" severity="secondary" @click="emit('update:visible', false)" class="w-full sm:w-auto" />
            </div>
        </template>
    </Dialog>
</template>