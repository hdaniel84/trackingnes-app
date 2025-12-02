<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import AppLayout from '@/layout/AppLayout.vue'; // La ruta a tu layout de Sakai

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
                <RouterView />
            </AppLayout>
        </template>

        <template v-else>
            <RouterView />
        </template>
    </div>
</template>
