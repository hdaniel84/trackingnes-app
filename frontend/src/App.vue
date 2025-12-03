<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import Toast from 'primevue/toast';
import AppLayout from '@/layout/AppLayout.vue'; 

const authStore = useAuthStore();
const route = useRoute();

const requiresAppLayout = computed(() => {
    // Solo renderizamos el layout si el usuario está logueado Y no está en la página de login
    return authStore.isLoggedIn && route.name !== 'login';
});
</script>

<template>
    <div>
        <template v-if="requiresAppLayout">
            <AppLayout>
                <Toast position="top-right" />
                <RouterView />
            </AppLayout>
        </template>

        <template v-else>
            <Toast position="top-right" />
            <RouterView />
        </template>
    </div>
</template>
