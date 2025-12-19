// src/layout/composables/usePhase.js
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { PHASE_RULES } from '@/domain/phaseRules';

export function usePhase() {
    const route = useRoute();

    // 1. Core: ID de la Fase
    const phaseId = computed(() => {
        return route.meta.phaseId || null;
    });

    // 2. Core: Reglas de Negocio
    const currentRules = computed(() => {
        if (!phaseId.value) return {}; // Devolver objeto vacío para evitar errores de "cannot read X of null"
        return PHASE_RULES[phaseId.value] || {};
    });

    // 3. UI: Metadata agrupada (Aquí estaba el problema)
    // Agrupamos todo en un objeto para pasarlo limpio al GenericForm
    const phaseMetadata = computed(() => {
        return {
            title: route.meta.title || 'Produção',
            icon: route.meta.icon,
            slug: route.meta.slug || 1, // Para filterSection
            permissionSuffix: route.meta.permission || 'ADMIN', // Para v-can
            filterSection: route.meta.filterSection || route.meta.slug, // Fallback inteligente
            teamSlug: route.meta.teamSlug || null
        };
    });

    return {
        phaseId,
        currentRules,
        phaseMetadata
    };
}