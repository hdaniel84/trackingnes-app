<script setup>
import { ref } from 'vue';
import Tabs from 'primevue/tabs';
import TabList from 'primevue/tablist';
import Tab from 'primevue/tab';
import TabPanels from 'primevue/tabpanels';
import TabPanel from 'primevue/tabpanel';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import TrackingVidragemForm from '@/components/forms/tracking/vidragem/VidragemForm.vue';

// Paleta de colores
const colorPalette = [
    { border: 'border-blue-500', text: 'text-blue-600', bg: 'bg-blue-50 dark:bg-blue-900/20', badge: 'bg-blue-100 text-blue-700' },
    { border: 'border-emerald-500', text: 'text-emerald-600', bg: 'bg-emerald-50 dark:bg-emerald-900/20', badge: 'bg-emerald-100 text-emerald-700' },
    { border: 'border-amber-500', text: 'text-amber-600', bg: 'bg-amber-50 dark:bg-amber-900/20', badge: 'bg-amber-100 text-amber-700' },
    { border: 'border-violet-500', text: 'text-violet-600', bg: 'bg-violet-50 dark:bg-violet-900/20', badge: 'bg-violet-100 text-violet-700' },
    { border: 'border-rose-500', text: 'text-rose-600', bg: 'bg-rose-50 dark:bg-rose-900/20', badge: 'bg-rose-100 text-rose-700' },
    { border: 'border-cyan-500', text: 'text-cyan-600', bg: 'bg-cyan-50 dark:bg-cyan-900/20', badge: 'bg-cyan-100 text-cyan-700' }
];

const presses = ref([
    { id: Date.now(), name: 'Nova Produção', editing: false, theme: colorPalette[0] }
]);

const activeValue = ref(presses.value[0].id.toString());

const addPress = () => {
    const nextIndex = presses.value.length % colorPalette.length;
    const newPress = {
        id: Date.now(),
        name: `Registo ${presses.value.length + 1}`,
        editing: false,
        theme: colorPalette[nextIndex]
    };
    presses.value.push(newPress);
    setTimeout(() => { activeValue.value = newPress.id.toString(); }, 50);
};

const removePress = (id, event) => {
    if(event) event.stopPropagation();
    const indexToRemove = presses.value.findIndex(p => p.id === id);
    if (indexToRemove === -1) return;

    if (activeValue.value === id.toString() && presses.value.length > 1) {
        const nextActive = presses.value[indexToRemove - 1] || presses.value[indexToRemove + 1];
        if (nextActive) activeValue.value = nextActive.id.toString();
    }
    presses.value = presses.value.filter(p => p.id !== id);
    if (presses.value.length === 0) addPress();
};

const saveTitle = (press) => {
    if (!press.name.trim()) press.name = 'Sem Nome';
    press.editing = false;
};
</script>

<template>
    <div class="w-full flex flex-col h-full min-h-screen bg-surface-50 dark:bg-surface-950">
        
        <div class="flex flex-col flex-1 bg-surface-0 dark:bg-surface-900 shadow-sm border border-surface-200 dark:border-surface-700 m-2 rounded-xl overflow-hidden">
            
            <Tabs v-model:value="activeValue" scrollable>
                
                <div class="flex items-center border-b border-surface-200 dark:border-surface-700 bg-surface-50/50 dark:bg-surface-800/30 pl-4 pr-2 pt-1">
                    
                    <div class="flex items-center gap-2 mr-4 pb-1">
                        <i class="pi pi-slack text-primary-500"></i>
                        <span class="font-bold text-sm text-surface-700 dark:text-surface-200 uppercase tracking-wide whitespace-nowrap">
                            Produção Vidragem
                        </span>
                        <div class="h-4 w-px bg-surface-300 dark:bg-surface-600 mx-2"></div>
                    </div>

                    <TabList class="flex-1 border-0 bg-transparent w-0">
                        <Tab 
                            v-for="press in presses" 
                            :key="press.id" 
                            :value="press.id.toString()"
                            class="!bg-transparent !border-none !mr-1"
                        >
                            <div class="flex items-center gap-2 px-2 py-1.5 group min-w-[120px] max-w-[180px] justify-between rounded-t-lg transition-colors hover:bg-surface-100 dark:hover:bg-surface-700/50"
                                 :class="{ 'bg-white dark:bg-surface-900 shadow-sm text-primary-600': activeValue === press.id.toString() }">
                                
                                <div class="flex items-center gap-2 overflow-hidden">
                                    <span class="w-2 h-2 rounded-full flex-shrink-0" 
                                        :class="activeValue === press.id.toString() ? press.theme.bg.replace('bg-', 'bg-').replace('/20', '') : 'bg-surface-300 dark:bg-surface-600'">
                                    </span>

                                    <div class="truncate font-medium text-xs">
                                        <template v-if="press.editing">
                                            <InputText v-model="press.name" class="h-5 w-full text-xs p-0" @blur="saveTitle(press)" @keydown.enter="saveTitle(press)" autofocus />
                                        </template>
                                        <template v-else>
                                            <span @dblclick="press.editing = true" class="cursor-pointer">{{ press.name }}</span>
                                        </template>
                                    </div>
                                </div>

                                <button @click="removePress(press.id, $event)"
                                    class="w-4 h-4 rounded-full flex items-center justify-center hover:bg-surface-200 dark:hover:bg-surface-700 text-surface-400 hover:text-red-500 opacity-0 group-hover:opacity-100 transition-opacity"
                                    :class="{ 'opacity-100': activeValue === press.id.toString() }">
                                    <i class="pi pi-times text-[9px]"></i>
                                </button>
                            </div>
                            
                            <div v-if="activeValue === press.id.toString()" class="absolute bottom-0 left-0 right-0 h-[2px] bg-primary-500 z-10"></div>
                        </Tab>
                    </TabList>

                    <Button 
                        icon="pi pi-plus" 
                        text 
                        rounded 
                        severity="secondary" 
                        aria-label="Nova Aba"
                        class="ml-2 !w-8 !h-8"
                        @click="addPress" 
                    />
                </div>

                <TabPanels class="p-0 bg-transparent flex-1">
                    <TabPanel v-for="press in presses" :key="press.id" :value="press.id.toString()" class="h-full">
                        <div class="flex flex-col h-full">
                            <div class="px-6 py-2 border-b border-surface-100 dark:border-surface-800 bg-white dark:bg-surface-900 flex justify-between items-center"
                                 :class="press.theme.border + ' border-t-2'">
                                <div class="flex items-center gap-2">
                                    <span class="text-xs text-surface-400 font-mono">ID: {{ press.id }}</span>
                                    <span class="px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider" :class="press.theme.badge">
                                        {{ press.name }}
                                    </span>
                                </div>
                            </div>

                            <div class="p-6 bg-surface-50/30 dark:bg-surface-950/30 flex-1 overflow-y-auto">
                                <div class="max-w-7xl mx-auto">
                                    <TrackingVidragemForm :key="press.id" mode="create" class="animate-fade-in" />
                                </div>
                            </div>
                        </div>
                    </TabPanel>
                </TabPanels>
            </Tabs>
        </div>
    </div>
</template>

<style scoped>
.animate-fade-in {
    animation: fadeIn 0.3s ease-out forwards;
}
@keyframes fadeIn {
    from { opacity: 0; transform: translateY(5px); }
    to { opacity: 1; transform: translateY(0); }
}

/* Ajustes finos para PrimeVue Tabs */
:deep(.p-tablist-tab-list) {
    background: transparent;
    border: none;
}
</style>