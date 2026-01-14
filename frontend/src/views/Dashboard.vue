<script setup>
import { ref, onMounted, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import Chart from 'primevue/chart';
import Timeline from 'primevue/timeline';
import Button from 'primevue/button';
import Avatar from 'primevue/avatar';
import Skeleton from 'primevue/skeleton';

// --- 1. Estado y Datos ---
const loading = ref(true);
const authStore = useAuthStore();
const userName = computed(() => authStore.user?.username || 'Utilizador'); 
const timeRange = ref('today');

// Datos simulados (KPIs)
const stats = ref([
    { 
        title: 'Output Real', 
        value: '12,450', 
        unit: 'un', 
        trend: '+12.5%', 
        trendUp: true, 
        icon: 'pi pi-box', 
        // Clases directas para colores
        bgClass: 'bg-indigo-50 dark:bg-indigo-500/10',
        textClass: 'text-indigo-600 dark:text-indigo-300'
    },
    { 
        title: 'Eficiência (OEE)', 
        value: '87.4', 
        unit: '%', 
        trend: '+2.1%', 
        trendUp: true, 
        icon: 'pi pi-bolt', 
        bgClass: 'bg-emerald-50 dark:bg-emerald-500/10',
        textClass: 'text-emerald-600 dark:text-emerald-300'
    },
    { 
        title: 'Taxa Rejeição', 
        value: '1.2', 
        unit: '%', 
        trend: '-0.4%', 
        trendUp: true, 
        negativeIsGood: true,
        icon: 'pi pi-exclamation-triangle', 
        bgClass: 'bg-amber-50 dark:bg-amber-500/10',
        textClass: 'text-amber-600 dark:text-amber-300'
    },
    { 
        title: 'Tempo Paragem', 
        value: '0:45', 
        unit: 'h', 
        trend: '+15m', 
        trendUp: false, 
        icon: 'pi pi-clock', 
        bgClass: 'bg-rose-50 dark:bg-rose-500/10',
        textClass: 'text-rose-600 dark:text-rose-300'
    },
]);

// Datos Timeline
const activities = ref([
    { status: 'Início Lote #4020', time: '14:30', icon: 'pi pi-play', color: '#10b981', desc: 'Produção iniciada na Prensa 04' },
    { status: 'Alerta Temperatura', time: '14:15', icon: 'pi pi-exclamation-circle', color: '#ef4444', desc: 'Forno B excedeu limite (980°C)' },
    { status: 'Mudança Turno', time: '14:00', icon: 'pi pi-users', color: '#3b82f6', desc: 'Equipa Tarde assumiu posto' },
    { status: 'Manutenção OK', time: '11:00', icon: 'pi pi-check', color: '#64748b', desc: 'Preventiva Prensa 01 finalizada' },
]);

// Datos Máquinas
const machines = ref([
    { name: 'Prensa 01', status: 98, state: 'active' },
    { name: 'Prensa 02', status: 85, state: 'active' },
    { name: 'Prensa 03', status: 0, state: 'stopped' },
    { name: 'Forno A', status: 100, state: 'active' },
    { name: 'Forno B', status: 45, state: 'warning' },
]);

// --- 2. Configuración Gráfico ---
const chartData = ref();
const chartOptions = ref();

const initChart = () => {
    const documentStyle = getComputedStyle(document.documentElement);
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');
    
    // Crear canvas temporal para gradiente
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');
    const gradient = ctx.createLinearGradient(0, 0, 0, 300);
    gradient.addColorStop(0, 'rgba(99, 102, 241, 0.35)'); 
    gradient.addColorStop(1, 'rgba(99, 102, 241, 0.0)');

    chartData.value = {
        labels: ['06:00', '08:00', '10:00', '12:00', '14:00', '16:00', '18:00'],
        datasets: [
            {
                label: 'Output Atual',
                data: [450, 590, 800, 810, 560, 950, 1200],
                fill: true,
                backgroundColor: gradient,
                borderColor: '#6366f1',
                borderWidth: 2,
                pointBackgroundColor: '#ffffff',
                pointBorderColor: '#6366f1',
                pointBorderWidth: 2,
                pointRadius: 4,
                tension: 0.4
            },
            {
                label: 'Meta',
                data: [700, 700, 700, 700, 700, 700, 700],
                fill: false,
                borderColor: '#94a3b8',
                borderDash: [5, 5],
                borderWidth: 1,
                pointRadius: 0,
                tension: 0
            }
        ]
    };

    chartOptions.value = {
        maintainAspectRatio: false,
        plugins: {
            legend: { display: false },
            tooltip: {
                backgroundColor: 'rgba(15, 23, 42, 0.9)',
                titleColor: '#fff',
                bodyColor: '#cbd5e1',
                padding: 10,
                cornerRadius: 8,
                displayColors: false,
            }
        },
        scales: {
            x: { 
                ticks: { color: '#64748b', font: { size: 11 } }, 
                grid: { display: false } 
            },
            y: { 
                ticks: { color: '#64748b', font: { size: 11 } }, 
                grid: { color: surfaceBorder, borderDash: [4, 4] },
                beginAtZero: true
            }
        },
        interaction: { mode: 'index', intersect: false }
    };
};

const currentDate = ref(new Date());
const dateStr = computed(() => currentDate.value.toLocaleDateString('pt-PT', { weekday: 'long', day: 'numeric', month: 'long' }));

onMounted(() => {
    initChart();
    // Simulamos carga
    setTimeout(() => { loading.value = false; }, 1200);
});
</script>

<template>
    <div class="w-full animate-fade-in p-4">
        
        <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-6">
            <div class="flex items-center gap-35">
                <div v-if="loading">
                    <Skeleton shape="circle" size="3rem" />
                </div>
                <Avatar v-else icon="pi pi-user" class="bg-indigo-50 text-indigo-600 dark:bg-indigo-500/20 dark:text-indigo-200" size="large" shape="circle" />
                
                <div>
                    <h1 v-if="loading" class="mb-1"><Skeleton width="10rem" height="1.5rem" /></h1>
                    <h1 v-else class="text-xl font-semibold text-surface-900 dark:text-surface-0 tracking-tight">
                        Olá, {{ userName }}
                    </h1>
                    
                    <p v-if="loading"><Skeleton width="6rem" height="1rem" /></p>
                    <p v-else class="text-xs text-surface-500 font-medium capitalize">
                        {{ dateStr }}
                    </p>
                </div>
            </div>

            <div class="flex gap-2">
                <Button icon="pi pi-bell" text rounded aria-label="Notificações" severity="secondary" />
                <Button icon="pi pi-refresh" text rounded aria-label="Atualizar" @click="loading = true; setTimeout(() => loading=false, 1000)" />
            </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
            <div v-for="(stat, index) in stats" :key="index" 
                class="bg-white dark:bg-surface-800 p-5 rounded-xl border border-surface-200 dark:border-surface-700 shadow-sm transition-all duration-200 hover:shadow-md hover:border-surface-300 dark:hover:border-surface-600 relative overflow-hidden group">
                
                <div v-if="loading" class="flex flex-col gap-2">
                    <div class="flex justify-between">
                        <Skeleton width="4rem" height="1rem" />
                        <Skeleton size="2rem" shape="circle" />
                    </div>
                    <Skeleton width="60%" height="2rem" />
                    <Skeleton width="30%" height="1rem" />
                </div>

                <div v-else>
                    <div class="flex justify-between items-start mb-3">
                        <span class="text-xs font-semibold text-surface-500 uppercase tracking-wide">{{ stat.title }}</span>
                        <div :class="`p-2 rounded-lg ${stat.bgClass} ${stat.textClass}`">
                            <i :class="stat.icon" class="text-lg"></i>
                        </div>
                    </div>
                    
                    <div class="flex items-baseline gap-2">
                        <span class="text-2xl font-bold text-surface-800 dark:text-surface-50">{{ stat.value }}</span>
                        <span class="text-xs font-medium text-surface-400">{{ stat.unit }}</span>
                    </div>

                    <div class="mt-2 flex items-center text-xs font-medium">
                        <span :class="stat.trendUp ? 'text-emerald-500' : 'text-rose-500'" class="flex items-center gap-1">
                            <i :class="stat.trendUp ? 'pi pi-arrow-up' : 'pi pi-arrow-down'" style="font-size: 0.7rem"></i>
                            {{ stat.trend }}
                        </span>
                        <span class="text-surface-400 ml-2">vs. período ant.</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="grid grid-cols-12 gap-6">
            
            <div class="col-span-12 lg:col-span-8">
                <div class="bg-white dark:bg-surface-800 p-5 rounded-xl border border-surface-200 dark:border-surface-700 shadow-sm h-full flex flex-col">
                    <div class="flex justify-between items-center mb-6">
                        <div>
                            <h3 class="text-base font-bold text-surface-800 dark:text-surface-100">Performance Produção</h3>
                            <p class="text-xs text-surface-500 mt-0.5">Comparativo Output vs Meta</p>
                        </div>
                        
                        <div class="bg-surface-100 dark:bg-surface-900 p-0.5 rounded-lg flex">
                            <button @click="timeRange = 'today'" :class="{'bg-white dark:bg-surface-700 shadow-sm text-surface-900': timeRange === 'today', 'text-surface-500': timeRange !== 'today'}" class="px-3 py-1 text-xs font-medium rounded-md transition-all">Hoje</button>
                            <button @click="timeRange = 'week'" :class="{'bg-white dark:bg-surface-700 shadow-sm text-surface-900': timeRange === 'week', 'text-surface-500': timeRange !== 'week'}" class="px-3 py-1 text-xs font-medium rounded-md transition-all">Semana</button>
                        </div>
                    </div>

                    <div class="flex-1 relative min-h-[300px]">
                        <Skeleton v-if="loading" width="100%" height="100%" />
                        <Chart v-else type="line" :data="chartData" :options="chartOptions" class="h-full w-full" />
                    </div>
                </div>
            </div>

            <div class="col-span-12 lg:col-span-4 flex flex-col gap-4">
                
                <div class="bg-white dark:bg-surface-800 p-5 rounded-xl border border-surface-200 dark:border-surface-700 shadow-sm">
                    <div class="flex justify-between items-center mb-4">
                        <h3 class="text-sm font-bold text-surface-800 dark:text-surface-100">Linhas & Prensas</h3>
                        <Button icon="pi pi-ellipsis-h" text rounded size="small" class="!p-0 !w-6 !h-6" />
                    </div>
                    
                    <div v-if="loading" class="space-y-3">
                         <Skeleton v-for="i in 4" :key="i" height="2rem" class="mb-2" />
                    </div>
                    
                    <div v-else class="space-y-3">
                        <div v-for="machine in machines" :key="machine.name" class="group cursor-pointer">
                            <div class="flex justify-between text-xs mb-1.5">
                                <span class="font-medium text-surface-700 dark:text-surface-200 flex items-center gap-2">
                                    <span class="w-2 h-2 rounded-full" :class="{
                                        'bg-emerald-500': machine.state === 'active',
                                        'bg-rose-500': machine.state === 'stopped',
                                        'bg-amber-500': machine.state === 'warning'
                                    }"></span>
                                    {{ machine.name }}
                                </span>
                                <span class="font-mono text-surface-500 group-hover:text-surface-900 transition-colors">{{ machine.status }}%</span>
                            </div>
                            <div class="h-1.5 w-full bg-surface-100 dark:bg-surface-800 rounded-full overflow-hidden">
                                <div class="h-full rounded-full transition-all duration-1000 ease-out"
                                    :class="{
                                        'bg-emerald-500': machine.state === 'active',
                                        'bg-rose-500': machine.state === 'stopped',
                                        'bg-amber-500': machine.state === 'warning'
                                    }"
                                    :style="{ width: machine.status + '%' }">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="bg-white dark:bg-surface-800 p-5 rounded-xl border border-surface-200 dark:border-surface-700 shadow-sm flex-1">
                    <h3 class="text-sm font-bold text-surface-800 dark:text-surface-100 mb-4">Live Feed</h3>
                    
                    <Skeleton v-if="loading" height="100%" />
                    
                    <Timeline v-else :value="activities" class="custom-timeline text-xs">
                        <template #marker="slotProps">
                            <span class="flex w-6 h-6 items-center justify-center rounded-full ring-1 ring-surface-200 dark:ring-surface-700 bg-surface-0 dark:bg-surface-800" 
                                :style="{ color: slotProps.item.color }">
                                <i :class="slotProps.item.icon" style="font-size: 0.6rem"></i>
                            </span>
                        </template>
                        <template #content="slotProps">
                            <div class="pb-5 pl-1">
                                <div class="flex justify-between items-start">
                                    <span class="font-semibold text-surface-700 dark:text-surface-200">{{ slotProps.item.status }}</span>
                                    <span class="text-[10px] text-surface-400 font-mono">{{ slotProps.item.time }}</span>
                                </div>
                                <p class="text-surface-500 mt-1 leading-tight line-clamp-2">{{ slotProps.item.desc }}</p>
                            </div>
                        </template>
                    </Timeline>
                </div>

            </div>
        </div>
    </div>
</template>

<style scoped>
/* Solo estilos CSS normales para el Timeline, sin @apply */
.animate-fade-in {
    animation: fadeIn 0.4s ease-out forwards;
}

@keyframes fadeIn {
    from { opacity: 0; transform: translateY(5px); }
    to { opacity: 1; transform: translateY(0); }
}

:deep(.custom-timeline .p-timeline-event-opposite) { 
    display: none; 
}
:deep(.custom-timeline .p-timeline-event-connector) { 
    background-color: var(--surface-200);
}
.dark :deep(.custom-timeline .p-timeline-event-connector) { 
    background-color: var(--surface-700);
}
</style>