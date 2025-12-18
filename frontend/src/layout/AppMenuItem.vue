<script setup>
import { useLayout } from '@/layout/composables/layout';
import { onBeforeMount, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import Badge from 'primevue/badge'; // Importamos Badge

const route = useRoute();
const { layoutState, setActiveMenuItem, toggleMenu } = useLayout();

const props = defineProps({
    item: {
        type: Object,
        default: () => ({})
    },
    index: {
        type: Number,
        default: 0
    },
    root: {
        type: Boolean,
        default: true
    },
    parentItemKey: {
        type: String,
        default: null
    }
});

const isActiveMenu = ref(false);
const itemKey = ref(null);

onBeforeMount(() => {
    itemKey.value = props.parentItemKey ? props.parentItemKey + '-' + props.index : String(props.index);
    const activeItem = layoutState.activeMenuItem;
    isActiveMenu.value = activeItem === itemKey.value || activeItem ? activeItem.startsWith(itemKey.value + '-') : false;
});

watch(
    () => layoutState.activeMenuItem,
    (newVal) => {
        isActiveMenu.value = newVal === itemKey.value || newVal.startsWith(itemKey.value + '-');
    }
);

function itemClick(event, item) {
    if (item.disabled) {
        event.preventDefault();
        return;
    }
    if ((item.to || item.url) && (layoutState.staticMenuMobileActive || layoutState.overlayMenuActive)) {
        toggleMenu();
    }
    if (item.command) {
        item.command({ originalEvent: event, item: item });
    }
    const foundItemKey = item.items ? (isActiveMenu.value ? props.parentItemKey : itemKey) : itemKey.value;
    setActiveMenuItem(foundItemKey);
}

function checkActiveRoute(item) {
    return route.path === item.to;
}
</script>

<template>
    <li :class="{ 'layout-root-menuitem': root, 'active-menuitem': isActiveMenu }">
        
        <div v-if="root && item.visible !== false" class="layout-menuitem-root-text">
            {{ item.label }}
        </div>

        <a v-if="(!item.to || item.items) && item.visible !== false" 
           :href="item.url" 
           @click="itemClick($event, item, index)" 
           :class="item.class" 
           :target="item.target" 
           tabindex="0">
            <i :class="item.icon" class="layout-menuitem-icon"></i>
            <span class="layout-menuitem-text">{{ item.label }}</span>
            
            <Badge v-if="item.badge" :value="item.badge" :severity="item.badgeSeverity || 'info'" class="ml-auto mr-2" />
            
            <i class="pi pi-fw pi-angle-down layout-submenu-toggler" v-if="item.items"></i>
        </a>

        <router-link 
            v-if="item.to && !item.items && item.visible !== false" 
            @click="itemClick($event, item, index)" 
            :class="[item.class, { 'active-route': checkActiveRoute(item) }]" 
            tabindex="0" 
            :to="item.to">
            
            <i :class="item.icon" class="layout-menuitem-icon"></i>
            <span class="layout-menuitem-text">{{ item.label }}</span>
            
            <Badge v-if="item.badge" :value="item.badge" :severity="item.badgeSeverity || 'info'" class="ml-auto mr-2" />
            
            <i class="pi pi-fw pi-angle-down layout-submenu-toggler" v-if="item.items"></i>
        </router-link>

        <Transition v-if="item.items && item.visible !== false" name="layout-submenu">
            <ul v-show="root ? true : isActiveMenu" class="layout-submenu">
                <app-menu-item v-for="(child, i) in item.items" :key="child" :index="i" :item="child" :parentItemKey="itemKey" :root="false"></app-menu-item>
            </ul>
        </Transition>
    </li>
</template>

<style scoped lang="scss">
/* 🎨 ESTILOS PRO 
   Añadimos feedback visual mejorado para la ruta activa
*/

/* Cuando el item está activo (ruta actual) */
.active-route {
    font-weight: 700 !important;
    color: var(--primary-color) !important;
    background-color: var(--surface-100); 
    border-radius: 10px;
    
    /* Pequeña barra lateral para indicar selección */
    border-left: 4px solid var(--primary-color);
    padding-left: 0.8rem !important; /* Ajuste por el borde */
}

/* Modo oscuro */
:deep(.dark) .active-route {
    background-color: rgba(var(--primary-color-rgb), 0.15);
    color: var(--primary-400) !important;
}

/* Badge Styling Tweaks */
:deep(.p-badge) {
    min-width: 1.25rem;
    height: 1.25rem;
    line-height: 1.25rem;
    font-size: 0.75rem;
}
</style>