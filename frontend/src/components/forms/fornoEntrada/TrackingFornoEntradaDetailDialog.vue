<script setup>
import { defineProps, defineEmits } from 'vue';
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

const emit = defineEmits(['update:visible', 'delete']);

</script>

<template>
    <Dialog :visible="props.visible" @update:visible="emit('update:visible', $event)" modal
        header="📋 Detalhes do Registo" :style="{ width: '400px' }">
        <div v-if="props.item" class="space-y-2">
            <!-- Header con carro y producto -->
            <div class="flex justify-between items-center bg-surface-100 dark:bg-surface-800 p-2 rounded-lg">
                <div>
                    <h2 class="text-lg font-semibold text-primary-600">
                        Vagona #{{ props.item.wagonId }}
                    </h2>
                    <p class="text-sm text-surface-500">{{ props.item.productDescription }}</p>
                </div>
                <Tag :value="`${props.item.quantity} Un.`" severity="success" class="text-sm font-bold" />
            </div>

            <div class="grid grid-cols-2 gap-2">
                <div class="p-2 border rounded-lg">
                    <p class="text-xs text-surface-400">Início</p>
                    <p class="font-medium">{{ formatDateTime(props.item.startTime) }}</p>
                </div>
                <div class="p-2 border rounded-lg">
                    <p class="text-xs text-surface-400">Carro na entrada</p>
                    <p class="font-medium">{{ props.item.logisticUnitInId }}</p>
                </div>
            </div>

            <div class="grid grid-cols-2 gap-2">

                <div class="p-2 border rounded-lg">
                    <p class="text-xs text-surface-400">Equipa</p>
                    <p class="font-medium">{{ props.item.teamDescription }} ({{ props.item.teamSectionDescription }})
                    </p>
                </div>
                <div class="p-2 border rounded-lg">
                    <p class="text-xs text-surface-400">Turno</p>
                    <p class="font-medium">{{ props.item.shiftDescription }}</p>
                </div>

                <div v-if="props.item.comments" class="p-2 border rounded-lg col-span-2">
                    <p class="text-xs text-surface-400">Observações</p>
                    <p class="font-medium">{{ props.item.comments }}</p>
                </div>
            </div>

            <!-- Parámetros -->
            <div v-if="props.item.parameters.length !== 0">
                <Divider align="center">
                    <span class="text-sm text-surface-500">Parâmetros</span>
                </Divider>
                <div class="grid grid-cols-2 gap-2">
                    <div v-for="(param, index) in props.item.parameters" :key="index"
                        class="p-2 border rounded-lg flex flex-col">
                        <span class="text-xs text-surface-400">{{ param.parameterDescription }}</span>
                        <span class="font-semibold text-primary-600">{{ param.value }}</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <template #footer>
            <Button label="Cerrar" icon="pi pi-times" text @click="emit('update:visible', false)" />
        </template>
    </Dialog>
</template>
