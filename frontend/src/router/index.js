import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'dashboard',
            component: () => import('@/views/Dashboard.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/prensas',
            name: 'prensas',
            component: () => import('@/views/pages/tracking/prensas/PrensasMain.vue'),
            meta: { requiresAuth: true }
        },
        /*
        {
            path: '/prensas',
            name: 'prensas',
            component: () => import('@/views/pages/prensas/PrensasMain.vue'),
            meta: { requiresAuth: true } 
        },*/
        {
            path: '/vidragem',
            name: 'vidragem',
            component: () => import('@/views/pages/vidragem/VidragemMain.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/forno-entrada',
            name: 'forno-entrada',
            component: () => import('@/views/pages/fornoEntrada/FornoEntradaMain.vue'),
            meta: { requiresAuth: true }
        },
        // ... (el resto de rutas deben seguir este patrón)

        // ------------------------------------------------------------------------
        // 🛡️ RUTAS PÚBLICAS (No necesitan el AppLayout)
        // ------------------------------------------------------------------------

        {
            path: '/auth/login',
            name: 'login',
            component: () => import('@/views/pages/auth/Login.vue'),
            meta: { requiresAuth: false } // Pública
        },
        // ... (otras rutas públicas como landing, notfound) ...

        {
            path: '/:catchAll(.*)', // Ruta comodín para 404
            name: 'notfound',
            component: () => import('@/views/pages/NotFound.vue')
        }
    ]
});

export default router;