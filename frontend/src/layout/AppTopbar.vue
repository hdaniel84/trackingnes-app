<script setup>
import { ref, computed } from 'vue';
import { useLayout } from '@/layout/composables/layout';
import AppConfigurator from './AppConfigurator.vue';
import { useAuthStore } from '@/stores/auth';

// Imports de PrimeVue
import Menu from 'primevue/menu';
import Avatar from 'primevue/avatar';
import InputText from 'primevue/inputtext';
import IconField from 'primevue/iconfield';
import InputIcon from 'primevue/inputicon';

import myLogo from '@/assets/images/logo_webapps_noletters.png';

const { toggleMenu, toggleDarkMode, isDarkTheme } = useLayout();
const authStore = useAuthStore();

// 1. Datos del Usuario (Reactivos desde el Store)
const userInitials = computed(() => {
    const name = authStore.user?.username || 'U';
    return name.substring(0, 2).toUpperCase();
});
const userName = computed(() => authStore.user?.username || 'Utilizador');
const userRole = computed(() => authStore.user?.roles?.[0]?.replace('ROLE_', '') || 'User');

// 2. Menú de Usuario (Dropdown)
const menu = ref(null);

const userMenuItems = ref([
    {
        label: 'Conta',
        items: [
            { label: 'O meu Perfil', icon: 'pi pi-user' },
            { label: 'Definições', icon: 'pi pi-cog' }
        ]
    },
    {
        separator: true
    },
    {
        label: 'Sessão',
        items: [
            { 
                label: 'Terminar Sessão', 
                icon: 'pi pi-sign-out', 
                class: 'text-red-500',
                command: () => {
                    authStore.logout();
                } 
            }
        ]
    }
]);

const toggleUserMenu = (event) => {
    menu.value.toggle(event);
};
</script>

<template>
    <div class="layout-topbar fixed w-full z-50 transition-all duration-300
                bg-white/80 dark:bg-surface-900/80 backdrop-blur-md 
                border-b border-surface-200 dark:border-surface-700 
                px-4 lg:px-8 h-20 flex items-center justify-between gap-4 shadow-sm">

        <div class="flex items-center gap-4">
            <button class="layout-menu-button p-2 rounded-full hover:bg-surface-100 dark:hover:bg-surface-800 transition-colors text-surface-600 dark:text-surface-200 focus:outline-none focus:ring-2 focus:ring-primary-500" 
                    @click="toggleMenu">
                <i class="pi pi-bars text-xl"></i>
            </button>

            <router-link to="/" class="flex items-center gap-3 group">
                <img :src="myLogo" alt="Webapps" class="h-10 w-10 transition-transform group-hover:scale-110 duration-300" />
                <div class="flex flex-col">
                    <span class="font-bold text-lg text-surface-900 dark:text-surface-0 tracking-tight leading-none">TrackingNes</span>
                    <span class="text-[10px] uppercase font-bold text-primary-600 tracking-widest leading-none mt-1">MesaCer</span>
                </div>
            </router-link>
        </div>

        <div class="hidden md:flex flex-1 max-w-md mx-4">
            <IconField class="w-full">
                <InputIcon class="pi pi-search text-surface-400" />
                <InputText name="global_search"
                    placeholder="Pesquisar lotes, ordens, produtos..." 
                    class="w-full rounded-full bg-surface-100 dark:bg-surface-800 border-none px-10 py-2 focus:ring-2 focus:ring-primary-500 transition-all" 
                />
                <div class="absolute right-3 top-1/2 -translate-y-1/2 hidden lg:flex gap-1">
                    <span class="text-xs bg-white dark:bg-surface-700 px-1.5 py-0.5 rounded text-surface-400 border border-surface-200 dark:border-surface-600">⌘</span>
                    <span class="text-xs bg-white dark:bg-surface-700 px-1.5 py-0.5 rounded text-surface-400 border border-surface-200 dark:border-surface-600">K</span>
                </div>
            </IconField>
        </div>

        <div class="flex items-center gap-2 sm:gap-4">
            
            <button type="button" class="action-btn" @click="toggleDarkMode" v-tooltip.bottom="'Alternar Tema'">
                <i :class="['pi text-lg', { 'pi-moon': isDarkTheme, 'pi-sun': !isDarkTheme }]"></i>
            </button>

            <div class="relative">
                <button 
                    v-styleclass="{ selector: '@next', enterFromClass: 'hidden', enterActiveClass: 'animate-scalein', leaveToClass: 'hidden', leaveActiveClass: 'animate-fadeout', hideOnOutsideClick: true }"
                    type="button" class="action-btn">
                    <i class="pi pi-palette text-lg"></i>
                </button>
                <AppConfigurator />
            </div>

            <div class="h-8 w-px bg-surface-200 dark:bg-surface-700 mx-1 hidden sm:block"></div>

            <button 
                class="flex items-center gap-3 p-1 pr-3 rounded-full hover:bg-surface-100 dark:hover:bg-surface-800 transition-colors border border-transparent hover:border-surface-200 dark:hover:border-surface-700"
                @click="toggleUserMenu"
                aria-haspopup="true" 
                aria-controls="overlay_menu"
            >
                <Avatar :label="userInitials" class="bg-primary-100 text-primary-700 dark:bg-primary-900 dark:text-primary-200 font-bold" shape="circle" />
                <div class="hidden sm:flex flex-col items-start text-left">
                    <span class="text-sm font-semibold text-surface-800 dark:text-surface-100 leading-none">{{ userName }}</span>
                    <span class="text-[10px] text-surface-500 uppercase font-medium mt-1">{{ userRole }}</span>
                </div>
                <i class="pi pi-chevron-down text-xs text-surface-400 hidden sm:block"></i>
            </button>
            
            <Menu ref="menu" id="overlay_menu" :model="userMenuItems" :popup="true" class="w-48 mt-2" />

        </div>
    </div>
</template>

<style scoped>
/* Clase utilitaria para los botones circulares 
.action-btn {
    @apply w-10 h-10 rounded-full flex items-center justify-center text-surface-500 dark:text-surface-400 hover:bg-surface-100 dark:hover:bg-surface-800 hover:text-primary-600 dark:hover:text-primary-400 transition-all duration-200 focus:outline-none;
}
*/
/* Ajustes para el Glassmorphism en navegadores antiguos */
.layout-topbar {
    -webkit-backdrop-filter: blur(12px);
    backdrop-filter: blur(12px);
}
</style>