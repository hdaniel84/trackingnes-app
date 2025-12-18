<script setup>
import { ref, onMounted, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import Chart from 'primevue/chart';
import Timeline from 'primevue/timeline';
import Button from 'primevue/button';
import Avatar from 'primevue/avatar';
import Tag from 'primevue/tag';


// 1. Datos del Usuario
const authStore = useAuthStore();
const userName = computed(() => authStore.user?.username || 'Utilizador');
const userRole = computed(() => authStore.user?.roles?.[0]?.replace('ROLE_', '') || 'Operador');

// 2. Lógica de Fecha y Saludo
const currentDate = ref(new Date());
const greeting = computed(() => {
    const hour = currentDate.value.getHours();
    if (hour < 12) return 'Bom dia';
    if (hour < 18) return 'Boa tarde';
    return 'Boa noite';
});

const formattedDate = computed(() => {
    return new Intl.DateTimeFormat('pt-PT', { 
        weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' 
    }).format(currentDate.value);
});

// 3. Mock Data para KPIs (Conectarás esto a tu API de reportes luego)
const stats = ref([
    { title: 'Produção Hoje', value: '12,450', unit: 'un', icon: 'pi pi-box', color: 'text-blue-500', bg: 'bg-blue-100 dark:bg-blue-900/20' },
    { title: 'Eficiência (OEE)', value: '87%', unit: '', icon: 'pi pi-chart-line', color: 'text-green-500', bg: 'bg-green-100 dark:bg-green-900/20' },
    { title: 'Prensas Ativas', value: '8/10', unit: '', icon: 'pi pi-cog', color: 'text-orange-500', bg: 'bg-orange-100 dark:bg-orange-900/20' },
    { title: 'Alertas', value: '2', unit: 'Pendentes', icon: 'pi pi-exclamation-circle', color: 'text-red-500', bg: 'bg-red-100 dark:bg-red-900/20' },
]);

// 4. Configuración del Gráfico (Chart.js)
const chartData = ref();
const chartOptions = ref();

const initChart = () => {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');

    chartData.value = {
        labels: ['08:00', '09:00', '10:00', '11:00', '12:00', '13:00', '14:00'],
        datasets: [
            {
                label: 'Produção Real',
                data: [65, 59, 80, 81, 56, 55, 90],
                fill: true,
                borderColor: documentStyle.getPropertyValue('--p-primary-500'),
                backgroundColor: 'rgba(16, 185, 129, 0.1)', // Verde transparente
                tension: 0.4
            },
            {
                label: 'Meta',
                data: [70, 70, 70, 70, 70, 70, 70],
                fill: false,
                borderColor: documentStyle.getPropertyValue('--surface-400'),
                borderDash: [5, 5],
                tension: 0.4
            }
        ]
    };

    chartOptions.value = {
        maintainAspectRatio: false,
        plugins: {
            legend: { labels: { color: textColor } }
        },
        scales: {
            x: { ticks: { color: textColor }, grid: { color: surfaceBorder } },
            y: { ticks: { color: textColor }, grid: { color: surfaceBorder } }
        }
    };
};

// 5. Mock Data para Actividad Reciente (Timeline)
const activities = ref([
    { status: 'Registo Criado', date: '14:30', icon: 'pi pi-plus', color: '#10b981', description: 'Lote #4020 em Prensa 04' },
    { status: 'Paragem', date: '14:15', icon: 'pi pi-exclamation-triangle', color: '#ef4444', description: 'Avaria mecânica Prensa 02' },
    { status: 'Mudança Turno', date: '14:00', icon: 'pi pi-users', color: '#3b82f6', description: 'Início Turno Tarde' },
    { status: 'Registo Criado', date: '13:45', icon: 'pi pi-plus', color: '#10b981', description: 'Lote #4019 em Prensa 01' },
]);

onMounted(() => {
    initChart();
});
</script>

<template>
    <div class="grid grid-cols-12 gap-6">
        
        <div class="col-span-12">
            <div class="card relative overflow-hidden bg-surface-0 dark:bg-surface-800 p-6 rounded-2xl shadow-sm border border-surface-200 dark:border-surface-700 flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
                
                <div class="flex items-center gap-4 z-10">
                    <Avatar :label="userName.charAt(0).toUpperCase()" size="xlarge" shape="circle" class="bg-primary-100 text-primary-600 font-bold" />
                    <div>
                        <h2 class="text-2xl font-bold text-surface-900 dark:text-surface-0 m-0">
                            {{ greeting }}, {{ userName }}! 👋
                        </h2>
                        <p class="text-surface-500 dark:text-surface-400 m-0 text-sm mt-1 flex items-center gap-2">
                            <span>{{ formattedDate }}</span>
                            <span class="hidden md:inline">•</span>
                            <Tag severity="info" :value="userRole" class="text-xs"></Tag>
                        </p>
                    </div>
                </div>

                <div class="flex gap-2 z-10">
                    <Button label="Reportes" icon="pi pi-file" severity="secondary" outlined />
                    <Button label="Novo Registo" icon="pi pi-plus" /> 
                </div>

                <div class="absolute right-0 top-0 h-full w-48 bg-gradient-to-l from-surface-100 dark:from-surface-700/50 to-transparent pointer-events-none"></div>
            </div>
        </div>

        <div v-for="(stat, index) in stats" :key="index" class="col-span-12 md:col-span-6 lg:col-span-3">
            <div class="bg-surface-0 dark:bg-surface-800 p-4 rounded-xl shadow-sm border border-surface-200 dark:border-surface-700 h-full flex items-center justify-between transition-transform hover:-translate-y-1 duration-300">
                <div>
                    <span class="block text-surface-500 font-medium mb-1 text-sm uppercase tracking-wide">{{ stat.title }}</span>
                    <div class="text-2xl font-bold text-surface-900 dark:text-surface-0">
                        {{ stat.value }} <span class="text-sm font-normal text-surface-400">{{ stat.unit }}</span>
                    </div>
                </div>
                <div :class="`flex items-center justify-center w-12 h-12 rounded-full ${stat.bg}`">
                    <i :class="`${stat.icon} text-xl ${stat.color}`"></i>
                </div>
            </div>
        </div>

        <div class="col-span-12 lg:col-span-8">
            <div class="bg-surface-0 dark:bg-surface-800 p-6 rounded-xl shadow-sm border border-surface-200 dark:border-surface-700 h-full">
                <div class="flex items-center justify-between mb-4">
                    <h3 class="text-lg font-bold text-surface-900 dark:text-surface-0">Produtividade Hora-a-Hora</h3>
                    <Button icon="pi pi-refresh" text rounded aria-label="Atualizar" />
                </div>
                <Chart type="line" :data="chartData" :options="chartOptions" class="h-[300px]" />
            </div>
        </div>

        <div class="col-span-12 lg:col-span-4">
            <div class="bg-surface-0 dark:bg-surface-800 p-6 rounded-xl shadow-sm border border-surface-200 dark:border-surface-700 h-full">
                <h3 class="text-lg font-bold text-surface-900 dark:text-surface-0 mb-4">Atividade Recente</h3>
                <Timeline :value="activities" class="customized-timeline">
                    <template #marker="slotProps">
                        <span class="flex w-8 h-8 items-center justify-center rounded-full text-white shadow-sm" :style="{ backgroundColor: slotProps.item.color }">
                            <i :class="slotProps.item.icon + ' text-xs'"></i>
                        </span>
                    </template>
                    <template #content="slotProps">
                        <div class="flex flex-col mb-4">
                            <span class="text-sm font-semibold text-surface-700 dark:text-surface-200">{{ slotProps.item.status }}</span>
                            <span class="text-xs text-surface-500 mb-1">{{ slotProps.item.description }}</span>
                            <span class="text-xs text-surface-400">{{ slotProps.item.date }}</span>
                        </div>
                    </template>
                </Timeline>
                <Button label="Ver Tudo" link class="w-full mt-2 p-0 text-sm" />
            </div>
        </div>

    </div>
</template>

<style scoped>
/* Ajustes finos para la Timeline de PrimeVue */
:deep(.p-timeline-event-opposite) {
    display: none; /* Ocultamos el lado izquierdo vacío para hacerla compacta */
}
:deep(.p-timeline-event-content) {
    padding-bottom: 0;
}
</style>