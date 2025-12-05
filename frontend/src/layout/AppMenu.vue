<script setup>
import { ref } from 'vue';

import AppMenuItem from './AppMenuItem.vue';

const model = ref([
    {
        label: 'Home',
        items: [{ label: 'Dashboard', icon: 'pi pi-fw pi-home', to: '/' }]
    },
    {
        label: 'Fases',
        items: [
            { label: 'Prensas', icon: 'pi pi-fw pi-chart-scatter', to: '/prensas' },
            { label: 'Vidragem', icon: 'pi pi-fw pi-bullseye', to: '/vidragem', class: 'rotated-icon' },
            { label: 'KeraJet', icon: 'pi pi-fw pi-print', to: '/uikit/table' },
            { label: 'Forno Entrada', icon: 'pi pi-fw pi-gauge', to: '/uikit/list' },
            { label: 'Forno Saída', icon: 'pi pi-fw pi-external-link', to: '/uikit/tree' },
            { label: 'Escolha', icon: 'pi pi-fw pi-eye', to: '/uikit/panel' },
            { label: 'Embalagem', icon: 'pi pi-fw pi-box', to: '/uikit/overlay' },
            { label: 'Armazem', icon: 'pi pi-fw pi-warehouse', to: '/uikit/media' },
            { label: 'Logistica', icon: 'pi pi-fw pi-truck', to: '/uikit/menu' },
        ]
    },
    {
        label: 'Configuração',
        icon: 'pi pi-fw pi-briefcase',
        to: '/pages',
        items: [

            {
                label: 'Administração',
                icon: 'pi pi-fw pi-user',
                items: [
                    {
                        label: 'Utilizadores',
                        icon: 'pi pi-fw pi-sign-in',
                        to: '/auth/login'
                    },
                    {
                        label: 'Roles',
                        icon: 'pi pi-fw pi-times-circle',
                        to: '/auth/error'
                    },
                    {
                        label: 'Accessos',
                        icon: 'pi pi-fw pi-lock',
                        to: '/auth/access'
                    }
                ]
            }
        ]
    }

]);
</script>

<template>
    <ul class="layout-menu">
        <template v-for="(item, i) in model">
            <app-menu-item v-if="!item.separator" :key="item.label || i" :item="item" :index="i"></app-menu-item>
            <li v-if="item.separator" class="menu-separator" :key="'separator-' + i"></li>
        </template>
    </ul>
</template>

<style lang="scss" scoped></style>
