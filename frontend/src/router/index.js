import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth'; // Importamos el store

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
            component: () => import('@/views/pages//tracking/TrackingMain.vue'),
            meta: {
                requiresAuth: true,
                title: 'Prensas',
                icon: 'pi pi-cog',
                permission: 'PRENSAS',
                phaseId: 1,
                slug: 1,
            }
        },
        {
            path: '/vidragem',
            name: 'vidragem',
            component: () => import('@/views/pages//tracking/TrackingMain.vue'),
            meta: {
                requiresAuth: true,
                phaseId: 3,
                title: 'Vidragem',         // Título para el Header
                icon: 'pi pi-sliders-h',
                slug: 3,                   // Para filtrar equipamentos por section (filterSection)
                permission: 'VIDRAGEM'     //Cada formulario maneja internamente el WRITE_ o READ_
            }
        },
        {
            path: '/forno-entrada',
            name: 'forno-entrada',
            component: () => import('@/views/pages//tracking/TrackingMain.vue'),
            meta: {
                requiresAuth: true,
                phaseId: 5,
                title: 'Forno (Entrada)',
                icon: 'pi pi-sign-in',
                slug: 5,
                permission: 'FORNO_ENTRADA',
                teamSlug: 3,    // Override para filtrar Equipos (Ej. Forno Entrada debe mostrar el equipo de Vidragem)
            }
        },
        {
            path: '/forno-saida',
            name: 'forno-saida',
            component: () => import('@/views/pages/tracking/TrackingMain.vue'),
            meta: {
                requiresAuth: true,
                phaseId: 6,
                title: 'Forno (Saída)',
                icon: 'pi pi-sign-out',
                slug: 5,
                permission: 'FORNO_SAIDA' //Cada formulario maneja internamente el WRITE_ o READ_
            }
        },
        {
            path: '/escolha',
            name: 'escolha',
            component: () => import('@/views/pages/tracking/TrackingMain.vue'),
            meta: {
                requiresAuth: true,
                phaseId: 7,
                title: 'Escolha',
                icon: 'pi pi-check-square',
                slug: 6,
                permission: 'ESCOLHA' //Cada formulario maneja internamente el WRITE_ o READ_
            }
        },
        {
            path: '/embalagem',
            name: 'embalagem',
            component: () => import('@/views/pages/tracking/TrackingMain.vue'),
            meta: {
                requiresAuth: true,
                phaseId: 9,
                title: 'Embalagem',
                icon: 'pi pi-box',
                slug: 7,
                permission: 'ESCOLHA' //Cada formulario maneja internamente el WRITE_ o READ_
            }
        },
        {
            path: '/trace',
            name: 'trace',
            component: () => import('@/views/pages/tracking/TrackingTraceMain.vue'),
            meta: {
                requiresAuth: true,
                phaseId: 9,
            }
        },


        // ... otras rutas protegidas ...

        // 🛡️ VISTA DE ACCESO DENEGADO (Necesaria para la redirección)
        {
            path: '/access-denied',
            name: 'access-denied',
            component: () => import('@/views/pages/auth/Access.vue'), // Asegúrate de crear este archivo
            meta: { requiresAuth: true }
        },

        // ------------------------------------------------------------------------
        // 🔓 RUTAS PÚBLICAS
        // ------------------------------------------------------------------------
        {
            path: '/auth/login',
            name: 'login',
            component: () => import('@/views/pages/auth/Login.vue'),
            meta: { requiresAuth: false }
        },
        {
            path: '/:catchAll(.*)',
            name: 'notfound',
            component: () => import('@/views/pages/NotFound.vue')
        }
    ]
});

// =============================================================================
// GLOBAL NAVIGATION GUARD
// Aquí es donde ocurre la magia de la seguridad
// =============================================================================
router.beforeEach((to, from, next) => {
    // 1. Instanciar el store DENTRO del guard para evitar errores de inicialización de Pinia
    const authStore = useAuthStore();

    // 2. Manejo de recarga de página (F5)
    // Si hay token en storage pero el store está vacío, intentamos restaurar la sesión
    if (!authStore.isLoggedIn && localStorage.getItem('jwtToken')) {
        authStore.initializeStore();
    }

    // 3. Lógica de Rutas Públicas (Login, 404, etc)
    // Si la ruta NO requiere auth (requiresAuth: false), dejamos pasar
    if (to.meta.requiresAuth === false) {
        // UX: Si ya está logueado e intenta ir al Login, lo mandamos al Dashboard
        if (to.name === 'login' && authStore.isLoggedIn) {
            return next({ name: 'dashboard' });
        }
        return next();
    }

    // 4. Verificar Autenticación Básica
    // Si la ruta requiere auth y NO estamos logueados -> Login
    if (!authStore.isLoggedIn) {
        // Guardamos la ruta a la que quería ir (redirect) para devolverlo allí tras el login
        return next({
            name: 'login',
            query: { redirect: to.fullPath }
        });
    }

    // 5. Verificar PRIVILEGIOS (Granularidad Fina)
    if (to.meta.requiredPrivilege) {
        if (!authStore.hasPrivilege(to.meta.requiredPrivilege)) {
            console.warn(`Acceso denegado: Usuario ${authStore.user?.username} no tiene el privilegio ${to.meta.requiredPrivilege}`);
            return next({ name: 'access-denied' });
        }
    }

    // 6. Verificar ROLES (Granularidad Gruesa)
    if (to.meta.requiredRole) {
        if (!authStore.hasRole(to.meta.requiredRole)) {
            console.warn(`Acceso denegado: Falta el rol ${to.meta.requiredRole}`);
            return next({ name: 'access-denied' });
        }
    }

    // Si pasa todas las validaciones, permitimos la navegación
    next();
});

export default router;