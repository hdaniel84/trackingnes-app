<script setup>
import { computed } from 'vue';
import AppMenuItem from './AppMenuItem.vue';
import { useAuthStore } from '@/stores/auth';

const auth = useAuthStore();

// Convertimos el modelo en computed para reactividad de permisos y badges
const model = computed(() => {
    return [
        {
            label: 'Home',
            items: [
                { label: 'Dashboard', icon: 'pi pi-fw pi-home', to: '/' }
            ]
        },
        {
            label: 'Produção',
            items: [
                { 
                    label: 'Prensas', 
                    icon: 'pi pi-fw pi-chart-scatter', 
                    to: '/prensas',
                    // 🔒 SEGURIDAD: Solo visible si tiene permiso
                    visible: auth.hasPrivilege('READ_PRENSAS') || auth.hasPrivilege('WRITE_PRENSAS'),
                    // 🔔 BADGE: Ejemplo de contador (conectarlo a un store de alertas)
                    badge: 0, 
                    badgeSeverity: 'danger' 
                },
                { 
                    label: 'Vidragem', 
                    icon: 'pi pi-fw pi-bullseye', 
                    to: '/vidragem', 
                    class: 'rotated-icon',
                    visible: auth.hasPrivilege('READ_VIDRAGEM') || auth.hasPrivilege('WRITE_VIDRAGEM') 
                },
                { label: 'KeraJet', icon: 'pi pi-fw pi-print', to: '/uikit/table' },
                { label: 'Forno Entrada', icon: 'pi pi-fw pi-gauge', to: '/forno-entrada' },
                { label: 'Forno Saída', icon: 'pi pi-fw pi-external-link', to: '/uikit/tree' },
                { label: 'Escolha', icon: 'pi pi-fw pi-eye', to: '/uikit/panel' },
                { label: 'Embalagem', icon: 'pi pi-fw pi-box', to: '/uikit/overlay' },
            ]
        },
        {
            label: 'Configuração',
            icon: 'pi pi-fw pi-briefcase',
            // 🔒 SEGURIDAD: Solo admins ven esta sección
            visible: auth.hasRole('ROLE_ADMIN'),
            items: [
                {
                    label: 'Administração',
                    icon: 'pi pi-fw pi-cog',
                    items: [
                        { label: 'Utilizadores', icon: 'pi pi-fw pi-users', to: '/admin/users' },
                        { label: 'Permissões', icon: 'pi pi-fw pi-lock', to: '/admin/permissions' }
                    ]
                }
            ]
        }
    ];
});
</script>

<template>
    <ul class="layout-menu">
        <template v-for="(item, i) in model">
            <app-menu-item v-if="!item.separator && item.visible !== false" :key="item.label || i" :item="item" :index="i"></app-menu-item>
            <li v-if="item.separator" class="menu-separator" :key="'separator-' + i"></li>
        </template>
    </ul>
</template>