<script setup>
import { ref, onMounted, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import Chart from 'primevue/chart';
import Timeline from 'primevue/timeline';
import Button from 'primevue/button';
import Tag from 'primevue/tag';
import ProgressBar from 'primevue/progressbar';
import Menu from 'primevue/menu';

// 1. Datos del Usuario
const authStore = useAuthStore();
const userName = computed(() => authStore.user?.username || 'Utilizador');

// 2. Lógica de Fecha
const currentDate = ref(new Date());
const formattedDate = computed(() => {
    return new Intl.DateTimeFormat('pt-PT', { 
        weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' 
    }).format(currentDate.value);
});
// Hora actual simple
const currentTime = computed(() => {
    return currentDate.value.toLocaleTimeString('pt-PT', { hour: '2-digit', minute: '2-digit' });
});

// 3. KPIs con "Tendencias" (Simulado para efecto visual)
const stats = ref([
    { title: 'Produção Total', value: '12,450', unit: 'un', trend: '+12%', trendUp: true, subtext: 'vs. ontem' },
    { title: 'OEE Global', value: '87.4', unit: '%', trend: '+2.1%', trendUp: true, subtext: 'vs. meta' },
    { title: 'Rejeições', value: '1.2', unit: '%', trend: '-0.4%', trendUp: false, subtext: 'dentro do limite', negativeIsGood: true }, // negativeIsGood para pintar verde si baja
    { title: 'Tempo Paragem', value: '45', unit: 'min', trend: '+15m', trendUp: true, subtext: 'acima da média', negativeIsGood: false },
]);

// 4. Configuración del Gráfico (Chart.js con Gradiente)
const chartData = ref();
const chartOptions = ref();

const initChart = () => {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border'); // Color sutil para bordes
    
    // Crear gradiente para el gráfico
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');
    const gradient = ctx.createLinearGradient(0, 0, 0, 400);
    gradient.addColorStop(0, 'rgba(99, 102, 241, 0.5)'); // Indigo
    gradient.addColorStop(1, 'rgba(99, 102, 241, 0.0)');

    chartData.value = {
        labels: ['08:00', '09:00', '10:00', '11:00', '12:00', '13:00', '14:00'],
        datasets: [
            {
                label: 'Output Real',
                data: [65, 59, 80, 81, 56, 55, 90],
                fill: true,
                backgroundColor: gradient,
                borderColor: '#6366f1', // Indigo 500
                pointBackgroundColor: '#ffffff',
                pointBorderColor: '#6366f1',
                pointBorderWidth: 2,
                pointRadius: 4,
                pointHoverRadius: 6,
                tension: 0.4
            }
        ]
    };

    chartOptions.value = {
        maintainAspectRatio: false,
        plugins: {
            legend: { display: false }, // Ocultamos leyenda para look minimalista
            tooltip: {
                mode: 'index',
                intersect: false,
                backgroundColor: 'rgba(255, 255, 255, 0.9)',
                titleColor: '#1e293b',
                bodyColor: '#475569',
                borderColor: '#e2e8f0',
                borderWidth: 1,
            }
        },
        scales: {
            x: { 
                ticks: { color: textColor, font: { size: 11 } }, 
                grid: { display: false } // Sin grid vertical
            },
            y: { 
                ticks: { color: textColor, font: { size: 11 } }, 
                grid: { color: surfaceBorder,  borderDash: [5, 5] }, // Grid horizontal sutil
                beginAtZero: true
            }
        },
        interaction: {
            mode: 'nearest',
            axis: 'x',
            intersect: false
        }
    };
};

// 5. Timeline compacta
const activities = ref([
    { status: 'Lote #4020', date: '14:30', icon: 'pi pi-check-circle', color: '#10b981', description: 'Iniciado em Prensa 04' },
    { status: 'Alerta Crítico', date: '14:15', icon: 'pi pi-exclamation-circle', color: '#ef4444', description: 'Temperatura elevada Forno B' },
    { status: 'Manutenção', date: '11:00', icon: 'pi pi-wrench', color: '#f59e0b', description: 'Preventiva Prensa 01 concluída' },
]);

// 6. Estado de las Máquinas (Visual extra)
const machines = ref([
    { name: 'Prensa 01', status: 85, state: 'active' },
    { name: 'Prensa 02', status: 92, state: 'active' },
    { name: 'Prensa 03', status: 0, state: 'stopped' },
    { name: 'Prensa 04', status: 45, state: 'warning' },
]);

const getSeverity = (state) => {
    if (state === 'active') return 'bg-emerald-500';
    if (state === 'warning') return 'bg-amber-500';
    return 'bg-rose-500';
};

onMounted(() => {
    initChart();
});
</script>

<template>
    <div class="w-full animate-fade-in">
        
        <div class="mb-8 flex flex-col md:flex-row md:items-end justify-between gap-4 border-b border-surface-200 dark:border-surface-700 pb-5">
            <div>
                <h1 class="text-3xl font-light text-surface-900 dark:text-surface-0 tracking-tight">
                    Dashboard <span class="font-bold text-primary-600">MesaCer</span>
                </h1>
                <p class="text-surface-500 dark:text-surface-400 mt-1 text-sm font-medium uppercase tracking-wider">
                    {{ formattedDate }} <span class="mx-2 text-surface-300">|</span> {{ currentTime }}
                </p>
            </div>
            <div class="flex gap-3">
                <Button label="Exportar Dados" icon="pi pi-download" severity="secondary" text size="small"/>
                <Button label="Relatório Diário" icon="pi pi-file-pdf" size="small" outlined />
            </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <div v-for="(stat, index) in stats" :key="index" 
                class="bg-surface-0 dark:bg-surface-800 p-5 rounded-lg shadow-sm border border-surface-200 dark:border-surface-700 hover:shadow-md transition-shadow duration-300">
                
                <div class="flex justify-between items-start mb-2">
                    <span class="text-xs font-bold text-surface-400 uppercase tracking-widest">{{ stat.title }}</span>
                    <span class="text-xs font-bold px-2 py-1 rounded bg-surface-100 dark:bg-surface-700"
                        :class="{
                            'text-emerald-600': (stat.trendUp && !stat.negativeIsGood) || (!stat.trendUp && stat.negativeIsGood),
                            'text-rose-600': (!stat.trendUp && !stat.negativeIsGood) || (stat.trendUp && stat.negativeIsGood)
                        }">
                        <i :class="stat.trendUp ? 'pi pi-arrow-up text-[10px]' : 'pi pi-arrow-down text-[10px]'"></i>
                        {{ stat.trend }}
                    </span>
                </div>
                
                <div class="flex items-baseline gap-1">
                    <span class="text-3xl font-bold text-surface-800 dark:text-surface-0 tracking-tight">{{ stat.value }}</span>
                    <span class="text-sm font-medium text-surface-500">{{ stat.unit }}</span>
                </div>
                <p class="text-xs text-surface-400 mt-2">{{ stat.subtext }}</p>
            </div>
        </div>

        <div class="grid grid-cols-12 gap-6">
            
            <div class="col-span-12 lg:col-span-8">
                <div class="bg-surface-0 dark:bg-surface-800 p-6 rounded-lg shadow-sm border border-surface-200 dark:border-surface-700 h-full">
                    <div class="flex justify-between items-center mb-6">
                        <div>
                            <h3 class="text-lg font-bold text-surface-800 dark:text-surface-100">Performance de Produção</h3>
                            <p class="text-xs text-surface-500">Output horário vs Capacidade instalada</p>
                        </div>
                        <div class="flex bg-surface-100 dark:bg-surface-900 rounded p-1">
                            <button class="px-3 py-1 text-xs font-bold rounded bg-white dark:bg-surface-700 shadow-sm text-surface-900">Hoje</button>
                            <button class="px-3 py-1 text-xs font-medium rounded text-surface-500 hover:text-surface-900 transition-colors">Semana</button>
                        </div>
                    </div>
                    <div class="relative h-[320px] w-full">
                        <Chart type="line" :data="chartData" :options="chartOptions" class="h-full w-full" />
                    </div>
                </div>
            </div>

            <div class="col-span-12 lg:col-span-4 flex flex-col gap-6">
                
                <div class="bg-surface-0 dark:bg-surface-800 p-6 rounded-lg shadow-sm border border-surface-200 dark:border-surface-700">
                    <h3 class="text-sm font-bold text-surface-800 dark:text-surface-100 mb-4 uppercase tracking-wide">Status Equipamentos</h3>
                    <div class="space-y-4">
                        <div v-for="machine in machines" :key="machine.name">
                            <div class="flex justify-between text-xs mb-1">
                                <span class="font-medium text-surface-600 dark:text-surface-300">{{ machine.name }}</span>
                                <span class="font-mono text-surface-500">{{ machine.status }}%</span>
                            </div>
                            <div class="h-1.5 w-full bg-surface-100 dark:bg-surface-700 rounded-full overflow-hidden">
                                <div class="h-full rounded-full transition-all duration-500" 
                                    :class="getSeverity(machine.state)"
                                    :style="{ width: machine.status + '%' }">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="bg-surface-0 dark:bg-surface-800 p-6 rounded-lg shadow-sm border border-surface-200 dark:border-surface-700 flex-1">
                    <h3 class="text-sm font-bold text-surface-800 dark:text-surface-100 mb-4 uppercase tracking-wide">Live Feed</h3>
                    <Timeline :value="activities" class="custom-timeline">
                        <template #marker="slotProps">
                            <span class="w-2 h-2 rounded-full ring-2 ring-offset-2 ring-offset-surface-0 dark:ring-offset-surface-800" 
                                :style="{ backgroundColor: slotProps.item.color, ringColor: slotProps.item.color }"></span>
                        </template>
                        <template #content="slotProps">
                            <div class="pb-6 pl-2">
                                <div class="flex justify-between items-start">
                                    <span class="text-sm font-semibold text-surface-700 dark:text-surface-200 block">{{ slotProps.item.status }}</span>
                                    <span class="text-[10px] text-surface-400 font-mono">{{ slotProps.item.date }}</span>
                                </div>
                                <p class="text-xs text-surface-500 mt-0.5 leading-relaxed">{{ slotProps.item.description }}</p>
                            </div>
                        </template>
                    </Timeline>
                </div>

            </div>
        </div>
    </div>
</template>

<style scoped>
/* Animación de entrada suave */
.animate-fade-in {
    animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
    from { opacity: 0; transform: translateY(10px); }
    to { opacity: 1; transform: translateY(0); }
}

/* Reset de Timeline para look minimalista */
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