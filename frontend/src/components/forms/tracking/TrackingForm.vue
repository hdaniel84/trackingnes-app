<script setup>
import { computed } from 'vue';
import GenericTrackingForm from '@/components/forms/global/GenericTrackingForm.vue';
import { trackingSchema, trackingPrensasFormSchema, trackingVidragemFormSchema, trackingFornoFormSchema, trackingEscolhaFormSchema, trackingEmbalagemFormSchema } from '@/validation/trackingSchema';
import { usePhase } from '@/layout/composables/usePhase';

const { phaseId } = usePhase();

const props = defineProps({
    item: Object, // El registro a editar (o null si es crear)
    mode: { type: String, default: 'create' }
});

const emit = defineEmits(['success', 'cancel']);

// 2. Manejo de Éxito
// Cuando el GenericForm termine de guardar en el Store, emitirá 'success'
const onFormSuccess = () => {
    emit('success'); // Avisamos al padre (ProductionView) para que cierre el modal
};

// SELECCIÓN DINÁMICA DEL ESQUEMA: Dependiendo de la fase en la que estamos (leída del Router), usamos un esquema u otro.
const currentValidationSchema = computed(() => {
    switch (phaseId.value) {
        case 1: return trackingPrensasFormSchema;
        case 3: return trackingVidragemFormSchema;
        case 5: return trackingFornoFormSchema;
        case 6: return trackingFornoFormSchema;
        case 7: return trackingEscolhaFormSchema;
        case 9: return trackingEmbalagemFormSchema;
        default: return trackingSchema; // Prensas (1) y otros por defecto
    }
});

</script>

<template>
    <div class="w-full">
        <GenericTrackingForm :item="props.item" :mode="props.mode" :validation-schema="currentValidationSchema"
            @success="onFormSuccess" @cancel="$emit('cancel')" :key="phaseId">
        </GenericTrackingForm>
    </div>
</template>