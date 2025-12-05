<script setup>
import { ref } from 'vue';
import Tabs from 'primevue/tabs';
import TabList from 'primevue/tablist';
import Tab from 'primevue/tab';
import TabPanels from 'primevue/tabpanels';
import TabPanel from 'primevue/tabpanel';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import TrackingVidragemForm from '@/components/forms/vidragem/TrackingVidragemForm.vue';

// Lista de colores Tailwind (puedes personalizar)
const colors = [
    '',
    'bg-blue-300',
    'bg-green-300',
    'bg-yellow-300',
    'bg-purple-300',
    'bg-red-300'
];

// Estado dinámico de vidragem
const presses = ref([
    { id: Date.now(), name: 'Registo 1', editing: false, color: colors[0] }
]);

// Tab activo
const activeValue = ref(presses.value[0].id.toString());

// Añadir nueva prensa
const addPress = () => {
    const newPress = {
        id: Date.now(),
        name: `Registo ${presses.value.length + 1}`,
        editing: false,
        color: colors[presses.value.length % colors.length] // asigna color cíclico
    };
    presses.value.push(newPress);
    activeValue.value = newPress.id.toString();
};

// Eliminar prensa
const removePress = (id) => {
    presses.value = presses.value.filter(p => p.id !== id);
    if (presses.value.length > 0) {
        activeValue.value = presses.value[0].id.toString();
    } else {
        addPress();
    }
};

// Guardar título editado
const saveTitle = (press) => {
    press.editing = false;
};
</script>

<template>
    <div
        class="bg-surface-0 dark:bg-surface-900 p-2 rounded-xl shadow-lg border border-surface-200 dark:border-surface-700">
        <Tabs v-model:value="activeValue" scrollable>
            <TabList>
                <div class="px-4 py-2 w-auto">
                    <Button label="Adicionar" icon="pi pi-plus" @click="addPress" />
                </div>
                <Tab v-for="press in presses" :key="press.id" :value="press.id.toString()">
                    <div class="flex items-center gap-2" >
                        <template v-if="press.editing">
                            <InputText v-model="press.name" class="w-32" @blur="saveTitle(press)" />
                        </template>
                        <template v-else>
                            <span @dblclick="press.editing = true">{{ press.name }}</span>
                        </template>
                        <Button icon="pi pi-times" severity="danger" text @click.stop="removePress(press.id)" />
                    </div>
                </Tab>
            </TabList>

            <TabPanels>
                <TabPanel v-for="press in presses" :key="press.id" :value="press.id.toString()">
                    <!-- 👇 Aquí aplicamos el color dinámico -->
                    <div class="p-4 rounded-lg" :class="press.color">
                        <div class="mb-5 ml-2">
                            <h3 class="text-2xl font-bold text-surface-900 dark:text-surface-0">
                                <i class="pi pi-bullseye mr-2" style="font-size: 2rem"></i>
                                Vidragem - Registo de produção ({{ press.name }})
                            </h3>
                        </div>

                        <TrackingVidragemForm :pressName="press.name" />
                    </div>
                </TabPanel>
            </TabPanels>
        </Tabs>
    </div>
</template>
