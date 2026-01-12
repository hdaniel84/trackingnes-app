<script setup>
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';
import Tag from 'primevue/tag';
import Divider from 'primevue/divider';
import { computed } from 'vue';
import { useFormatters } from '@/layout/composables/useFormatters';

const { formatDateTime } = useFormatters();

const props = defineProps({
    visible: Boolean,
    item: Object
});

const emit = defineEmits(['update:visible']);

// FUNCIÓN DE IMPRESIÓN
const printContent = () => {
    window.print();
};

// 1. FORMATTERS
const formatParamValue = (param) => {
    if (param.valueNumber !== null && param.valueNumber !== undefined) return param.valueNumber;
    if (param.valueBool !== null && param.valueBool !== undefined) return param.valueBool ? 'Sim' : 'Não';
    if (param.valueDate) return formatDateTime(param.valueDate);
    return param.valueString || '-';
};

// 2. CÁLCULOS DE BALANCE DE MASA (Super Pro Feature)
const productionStats = computed(() => {
    if (!props.item) return null;

    const qtyOk = props.item.quantity || 0;
    const qtyScrap = props.item.quantityScrap || 0;
    const totalOutput = qtyOk + qtyScrap;

    // Calcular entrada total desde Sources (si existen)
    const sources = props.item.sources || [];

    const totalInput = sources.reduce((acc, s) => acc + (s.quantityUsed || 0), 0);

    const diff = totalInput - totalOutput;
    const hasLoss = totalInput > 0 && diff > 0;

    return {
        qtyOk,
        qtyScrap,
        totalOutput,
        totalInput,
        diff,
        hasLoss,
        sources
    };
});
</script>

<template>
    <Dialog :visible="props.visible" @update:visible="emit('update:visible', $event)" modal
        header="Ficha Técnica de Produção" :style="{ width: '90vw', maxWidth: '700px' }"
        :breakpoints="{ '960px': '95vw' }" dismissableMask class="p-0"
        :pt="{ header: { class: 'border-b border-surface-200 dark:border-surface-700 bg-surface-50 dark:bg-surface-900' } }">

        <div id="printable-content" v-if="props.item"
            class="flex flex-col gap-6 py-4 px-1 bg-white dark:bg-surface-900">

            <div
                class="relative bg-white dark:bg-surface-800 rounded-xl border border-surface-200 dark:border-surface-700 shadow-sm overflow-hidden">
                <div class="absolute left-0 top-0 bottom-0 w-1.5"
                    :class="props.item.endTime ? 'bg-green-500' : 'bg-blue-500'"></div>

                <div class="p-5 pl-7 flex flex-col md:flex-row justify-between gap-4">
                    <div class="flex-1">
                        <div class="flex items-center gap-2 mb-2">
                            <span class="text-[10px] font-bold uppercase tracking-wider text-surface-400">
                                ID #{{ props.item.id }}
                            </span>
                            <Tag :value="props.item.phase?.description" severity="secondary"
                                class="uppercase text-[10px] font-bold"></Tag>
                        </div>
                        <h2 class="text-xl font-bold text-surface-900 dark:text-surface-0 leading-tight">
                            {{ props.item.product?.description || 'Produto Desconhecido' }}
                        </h2>
                        <span class="text-sm font-mono text-surface-500 mt-1 block">
                            Código SAP: {{ props.item.product?.productCode || 'N/A' }}
                        </span>
                    </div>

                    <div
                        class="flex items-center gap-4 bg-surface-50 dark:bg-surface-900/50 p-2 rounded-lg border border-surface-100 dark:border-surface-700/50">
                        <div class="text-center px-2">
                            <span class="block text-[10px] uppercase font-bold text-green-600 mb-0.5">OK</span>
                            <span class="text-xl font-bold text-surface-800 dark:text-surface-100">{{
                                productionStats.qtyOk }}</span>
                        </div>
                        <div class="w-px h-8 bg-surface-200 dark:bg-surface-700"></div>
                        <div class="text-center px-2">
                            <span class="block text-[10px] uppercase font-bold text-red-500 mb-0.5">NOK</span>
                            <span class="text-xl font-bold text-red-600 dark:text-red-400">{{ productionStats.qtyScrap
                            }}</span>
                        </div>
                        <div class="w-px h-8 bg-surface-200 dark:bg-surface-700"></div>
                        <div class="text-center px-2">
                            <span class="block text-[10px] uppercase font-bold text-surface-500 mb-0.5">TOTAL</span>
                            <span class="text-xl font-bold text-surface-900 dark:text-surface-0">{{
                                productionStats.totalOutput }}</span>
                        </div>
                    </div>
                </div>
            </div>

            <div v-if="productionStats.hasLoss"
                class="bg-amber-50 dark:bg-amber-900/20 border border-amber-200 dark:border-amber-800/50 rounded-lg p-3 flex items-center gap-4">
                <div
                    class="w-10 h-10 rounded-full bg-amber-100 dark:bg-amber-800/40 flex items-center justify-center shrink-0">
                    <i class="pi pi-chart-pie text-amber-600 dark:text-amber-400 text-lg"></i>
                </div>
                <div class="flex-1">
                    <p class="text-sm font-bold text-amber-800 dark:text-amber-200 uppercase tracking-wide">Relatório de
                        Eficiência</p>
                    <p class="text-xs text-amber-700 dark:text-amber-300 mt-0.5">
                        Entrada Total: <strong>{{ productionStats.totalInput }}</strong> |
                        Saída Total: <strong>{{ productionStats.totalOutput }}</strong>
                    </p>
                </div>
                <div class="text-right">
                    <span class="block text-[10px] uppercase font-bold text-amber-600 dark:text-amber-400">Perda
                        Técnica</span>
                    <span class="text-lg font-bold text-amber-700 dark:text-amber-300">- {{ productionStats.diff }}
                        un</span>
                </div>
            </div>

            <div v-if="productionStats.sources.length > 0">
                <div class="flex items-center gap-2 mb-3 px-1">
                    <i class="pi pi-link text-primary-500"></i>
                    <span class="text-xs font-bold text-surface-500 uppercase tracking-wider">Origem dos Materiais
                        (Lotes Pai)</span>
                </div>

                <div class="border border-surface-200 dark:border-surface-700 rounded-lg overflow-hidden">
                    <table class="w-full text-sm text-left">
                        <thead
                            class="bg-surface-50 dark:bg-surface-800 text-xs text-surface-500 uppercase font-bold border-b border-surface-200 dark:border-surface-700">
                            <tr>
                                <th class="px-3 py-2">Lote #Ref</th>
                                <th class="px-3 py-2">Peça</th>
                                <th class="px-3 py-2 text-right">Qtd. Usada</th>
                            </tr>
                        </thead>
                        <tbody class="divide-y divide-surface-100 dark:divide-surface-700">
                            <tr v-for="source in productionStats.sources" :key="source.id || source.trackingId"
                                class="hover:bg-surface-50 dark:hover:bg-surface-800/50 transition-colors">
                                <td class="px-3 py-2 font-mono font-bold text-primary-600">
                                    #{{ source.trackingId || source.parent?.id || source.id }}
                                </td>
                                <td class="px-3 py-2 text-surface-700 dark:text-surface-300">
                                    {{ source.productCode }} - {{ source.productDescription ||
                                        source.parent?.product?.description || '---' }}
                                </td>
                                <td class="px-3 py-2 text-right font-bold text-surface-900 dark:text-surface-100">
                                    {{ source.quantityUsed }} <span
                                        class="text-[10px] font-normal text-surface-400">un</span>
                                </td>
                            </tr>
                        </tbody>
                        <tfoot class="bg-surface-50/50 dark:bg-surface-800/30 font-bold text-surface-600">
                            <tr>
                                <td colspan="2" class="px-3 py-2 text-right uppercase">Total Entrada:</td>
                                <td class="px-3 py-2 text-right text-primary-600">{{ productionStats.totalInput }} un
                                </td>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mt-2">

                <div class="flex flex-col gap-4">
                    <div
                        class="bg-surface-50 dark:bg-surface-800 p-3 rounded-lg border border-surface-100 dark:border-surface-700">
                        <span class="text-[10px] font-bold text-surface-400 uppercase mb-2 block">Tempos de
                            Processo</span>
                        <div class="flex justify-between items-center mb-2">
                            <span class="text-xs text-surface-600 dark:text-surface-300 flex items-center gap-2">
                                <i class="pi pi-play text-green-500 text-[10px]"></i> Início
                            </span>
                            <span class="font-mono text-sm font-bold">{{ formatDateTime(props.item.startTime) }}</span>
                        </div>
                        <div class="flex justify-between items-center">
                            <span class="text-xs text-surface-600 dark:text-surface-300 flex items-center gap-2">
                                <i class="pi pi-stop text-red-500 text-[10px]"></i> Fim
                            </span>
                            <span class="font-mono text-sm font-bold">{{ props.item.endTime ?
                                formatDateTime(props.item.endTime) : '---' }}</span>
                        </div>
                    </div>

                    <div
                        class="flex items-center gap-3 p-3 border border-surface-200 dark:border-surface-700 rounded-lg">
                        <div
                            class="w-8 h-8 rounded bg-blue-50 dark:bg-blue-900/20 flex items-center justify-center text-blue-600">
                            <i class="pi pi-cog"></i>
                        </div>
                        <div>
                            <span class="text-[10px] font-bold text-surface-400 uppercase block">Equipamento</span>
                            <span class="text-sm font-bold text-surface-800 dark:text-surface-200">{{
                                props.item.equipment?.description }}</span>
                        </div>
                    </div>
                </div>

                <div class="flex flex-col gap-4">
                    <div
                        class="flex items-center gap-3 p-3 border border-surface-200 dark:border-surface-700 rounded-lg">
                        <div
                            class="w-8 h-8 rounded bg-indigo-50 dark:bg-indigo-900/20 flex items-center justify-center text-indigo-600">
                            <i class="pi pi-users"></i>
                        </div>
                        <div>
                            <span class="text-[10px] font-bold text-surface-400 uppercase block">Equipa</span>
                            <span class="text-sm font-bold text-surface-800 dark:text-surface-200">{{
                                props.item.team?.description }}</span>
                        </div>
                    </div>

                    <div
                        class="flex items-center gap-3 p-3 border border-surface-200 dark:border-surface-700 rounded-lg">
                        <div
                            class="w-8 h-8 rounded bg-purple-50 dark:bg-purple-900/20 flex items-center justify-center text-purple-600">
                            <i class="pi pi-clock"></i>
                        </div>
                        <div>
                            <span class="text-[10px] font-bold text-surface-400 uppercase block">Turno</span>
                            <span class="text-sm font-bold text-surface-800 dark:text-surface-200">{{
                                props.item.shift?.description }}</span>
                        </div>
                    </div>
                </div>
            </div>

            <div v-if="props.item.rawMaterials && props.item.rawMaterials.length > 0">
                <Divider />
                <span class="text-xs font-bold text-surface-500 uppercase tracking-wider block mb-3">
                    <i class="pi pi-box mr-1"></i> Consumíveis / Matérias Primas
                </span>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-2">
                    <div v-for="(rm, index) in props.item.rawMaterials" :key="index"
                        class="flex justify-between items-center p-2 rounded bg-surface-50 dark:bg-surface-800 border border-surface-100 dark:border-surface-700">
                        <span class="text-xs font-medium text-surface-700 dark:text-surface-200 truncate pr-2">
                            {{ rm.rawMaterialDescription || rm.description || 'Material' }}
                        </span>
                        <span
                            class="font-mono text-xs font-bold bg-white dark:bg-surface-700 px-1.5 py-0.5 rounded border border-surface-200 dark:border-surface-600">
                            {{ rm.value }}
                        </span>
                    </div>
                </div>
            </div>

            <div v-if="props.item.parameters && props.item.parameters.length > 0">
                <Divider />
                <span class="text-xs font-bold text-surface-500 uppercase tracking-wider block mb-3">
                    <i class="pi pi-sliders-h mr-1"></i> Parâmetros de Processo
                </span>
                <div class="flex flex-wrap gap-2">
                    <div v-for="(param, index) in props.item.parameters" :key="index"
                        class="inline-flex flex-col border border-surface-200 dark:border-surface-700 rounded px-3 py-1.5 bg-white dark:bg-surface-800 min-w-[120px]">
                        <span class="text-[9px] uppercase font-bold text-surface-400 mb-0.5 truncate max-w-[150px]"
                            :title="param.parameterDescription">
                            {{ param.parameterDescription }}
                        </span>
                        <span class="text-sm font-bold text-primary-700 dark:text-primary-300">
                            {{ formatParamValue(param) }}
                        </span>
                    </div>
                </div>
            </div>

            <div v-if="props.item.comments"
                class="mt-2 bg-yellow-50 dark:bg-yellow-900/10 p-3 rounded-lg border-l-4 border-yellow-400 text-sm text-surface-700 dark:text-surface-200 italic">
                <span
                    class="not-italic font-bold text-xs block text-yellow-700 dark:text-yellow-500 mb-1 not-sr-only">Obs:</span>
                "{{ props.item.comments }}"
            </div>

        </div>

        <template #footer>
            <div
                class="flex justify-between p-2 gap-2 bg-surface-50 dark:bg-surface-900 border-t border-surface-200 dark:border-surface-700 rounded-b-lg no-print">
                <Button label="Imprimir" icon="pi pi-print" severity="help" text @click="printContent" />
                <Button label="Fechar" icon="pi pi-times" text severity="secondary"
                    @click="emit('update:visible', false)" />
            </div>
        </template>
    </Dialog>
</template>

<style>
@media print {

    /* 1. Ocultamos todo y colapsamos la altura del body para evitar páginas fantasma */
    body,
    html {
        height: 0;
        overflow: hidden;
        margin: 0;
        padding: 0;
    }

    body * {
        visibility: hidden;
    }

    /* 2. Hacemos visible nuestro contenido */
    #printable-content,
    #printable-content * {
        visibility: visible;
    }

    /* 3. Posicionamos de forma absoluta para 'sacarlo' del flujo colapsado del body */
    #printable-content {
        left: 0;
        top: 0;
        width: 100%;
        margin: 0;
        padding: 20px;
        background: white !important;
        color: black !important;
        z-index: 99999;
        /* Restablecemos el overflow para que si el contenido es largo, se imprima en varias páginas correctamente */
        height: auto;
        overflow: visible;
    }

    /* Ocultar elementos marcados como no-print */
    .no-print {
        display: none !important;
    }

    /* Asegurar bordes nítidos */
    * {
        -webkit-print-color-adjust: exact !important;
        print-color-adjust: exact !important;
    }
}
</style>