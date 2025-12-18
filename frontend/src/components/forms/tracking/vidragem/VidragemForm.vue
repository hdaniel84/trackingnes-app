<script setup>
import { useTrackingStore } from '@/stores/trackingStore';
import { useForm, useField } from 'vee-validate';
import { toTypedSchema } from '@vee-validate/zod';
import { trackingVidragemFormSchema } from '@/validation/trackingVidragemFormSchema';
import { onMounted, defineProps, watch, ref } from 'vue';

// Helpers & UI
import { useNotify } from '@/layout/composables/notify';
import { useErrorDialog } from '@/layout/composables/errorDialog';
import InputNumber from 'primevue/inputnumber';
import DatePicker from 'primevue/datepicker';
import Textarea from 'primevue/textarea';
import Button from 'primevue/button';
import Divider from 'primevue/divider'; // Importante para separar secciones

// Componentes Personalizados
import ProductSelect from '@/components/forms/global/ProductSelect.vue';
import TeamsSelect from '@/components/forms/global/TeamsSelect.vue';
import ShiftsSelect from '@/components/forms/global/ShiftsSelect.vue';
import EquipmentsSelect from '@/components/forms/global/EquipmentsSelect.vue';
import ParametersForm from '@/components/forms/global/ParametersForm.vue';
import RawMaterialsForm from '@/components/forms/global/RawMaterialsForm.vue';
import AuxiliaryEquipmentsSelect from '@/components/forms/global/AuxiliaryEquipmentsSelect.vue';
import TrackingSourceSelect from '@/components/forms/global/TrackingSourceSelect.vue';

const props = defineProps({
  item: Object,
  mode: { type: String, default: 'create' }
});

const emit = defineEmits(['success', 'cancel']);
const CURRENT_PHASE_ID = 3; // 2 = Vidragem
const SOURCE_PHASE_ID = 1; // Fase de donde debería mostrarse los items de la fase anterior

const store = useTrackingStore();
const filterSectionVar = ref('Vidragem');
const { notifySuccess, notifyError } = useNotify();
const { showErrorDialog } = useErrorDialog();

// Vee-Validate
const { handleSubmit, errors, setValues, isSubmitting, resetForm, meta } = useForm({
  validationSchema: toTypedSchema(trackingVidragemFormSchema),
  validateOnMount: false,
  initialValues: {
    startTime: new Date(),
    rawMaterials: [{ id: null, rawMaterialId: null, value: '' }],
    parameters: [],
    auxiliaryEquipmentIds: []
  }
});

// Campos
const { value: trackingSourceId } = useField('trackingSourceId');
const { value: logisticUnit } = useField('logisticUnit');
const { value: startTime } = useField('startTime');
const { value: endTime } = useField('endTime');
const { value: comments } = useField('comments');
const { value: product } = useField('product');
const { value: team } = useField('team');
const { value: shift } = useField('shift');
const { value: equipment } = useField('equipment');
const { value: quantity } = useField('quantity');
const { value: parameters } = useField('parameters');
const { value: rawMaterials } = useField('rawMaterials');
const { value: auxiliaryEquipmentIds } = useField('auxiliaryEquipmentIds');


// WATCH: Cargar datos al Editar
watch(() => props.item, (val) => {
  if (val) {
    setValues({
      trackingSourceId: val.trackingSourceId,
      logisticUnit: val.logisticUnit,
      startTime: new Date(val.startTime),
      endTime: val.endTime ? new Date(val.endTime) : null,
      comments: val.comments,
      quantity: val.quantity,
      product: val.product || null,
      team: val.team || null,
      shift: val.shift || null,
      equipment: val.equipment || null,
      auxiliaryEquipmentIds: (val.auxiliaryEquipments || []).map(e => e.id),

      rawMaterials: (val.rawMaterials || []).map(r => ({
        id: r.id,
        rawMaterialId: r.rawMaterialId,
        value: r.value || ''
      })),

      parameters: (val.parameters || []).map(p => ({
        id: p.id,
        parameterId: p.parameterId,
        valueString: p.valueString || '',
        valueNumber: p.valueNumber,
        valueBool: p.valueBool,
        valueDate: p.valueDate ? new Date(p.valueDate) : null
      }))
    });
  }
}, { immediate: true });

onMounted(() => {
});

const onSubmit = handleSubmit(async (values) => {
  try {
    const parametersPayload = (parameters.value || []).map(p => {
      // 1. Clonamos el objeto básico
      const paramDto = {
        parameterId: p.parameterId,
        // Enviamos todos los campos, el backend sabrá cual usar o estarán null
        valueString: p.valueString,
        valueNumber: p.valueNumber,
        valueBool: p.valueBool,
        // 2. Formateamos fecha si existe (necesitamos enviarla como ISO string)
        valueDate: p.valueDate ? new Date(p.valueDate).toISOString() : null
      };

      if (p.id) paramDto.id = p.id;
      return paramDto;
    });

    const rawMaterialsPayload = (rawMaterials.value || []).map(r => {
      const dto = { rawMaterialId: r.rawMaterialId, value: r.value };
      if (r.id) dto.id = r.id;
      return dto;
    });

    const payload = {
      trackingSourceId: values.trackingSourceId,
      logisticUnit: values.logisticUnit,
      startTime: values.startTime,
      endTime: values.endTime,
      comments: values.comments,
      quantity: values.quantity,
      productId: values.product?.id,
      teamId: values.team?.id,
      shiftId: values.shift?.id,
      equipmentId: values.equipment?.id,
      auxiliaryEquipmentIds: values.auxiliaryEquipmentIds,
      phaseId: CURRENT_PHASE_ID,
      rawMaterials: rawMaterialsPayload,
      parameters: parametersPayload
    };

    if (props.mode === 'create') {
      await store.create(payload);
      notifySuccess('Registo criado com sucesso');

      resetForm({
        values: {
          // 1. CAMPOS QUE CONSERVAMOS (Contexto)
          // El usuario suele seguir produciendo lo mismo en el mismo turno
          equipment: values.equipment,
          product: values.product,
          team: values.team,
          shift: values.shift,

          // 2. CAMPOS QUE LIMPIAMOS
          logisticUnit: null, // Limpia el InputNumber
          quantity: null,     // Limpia el InputNumber
          comments: null,     // Limpia el Textarea
          endTime: null,      // Limpia el DatePicker

          // 3. CAMPOS QUE ACTUALIZAMOS
          startTime: new Date(),
          parameters: values.parameters,
          rawMaterials: values.rawMaterials,
          auxiliaryEquipmentIds: values.auxiliaryEquipmentIds
        }
      });
    } else {
      await store.update(props.item.id, payload);
      notifySuccess('Registo atualizado com sucesso');
      emit('success');
    }
    //emit('success'); // Avisar al padre para cerrar el modal
  } catch (err) {
    const message = err?.response?.data?.message || err?.message || 'Erro inesperado';
    notifyError('Erro', message);
    showErrorDialog(err);
  }
});
</script>

<template>
  <div class="w-full">
    <form @submit.prevent="onSubmit" class="flex flex-col gap-6">

      <div class="bg-surface-50 dark:bg-surface-800 p-5 rounded-xl border border-surface-200 dark:border-surface-700">
        <div
          class="flex items-center gap-2 mb-4 text-primary-600 dark:text-primary-400 font-bold uppercase text-xs tracking-wider">
          <i class="pi pi-box"></i> Identificação da Produção
        </div>

        <div class="grid grid-cols-1 md:grid-cols-12 gap-5">
          <div class="col-span-12 md:col-span-6">
            <EquipmentsSelect v-model="equipment" :filterSection="filterSectionVar" />
            <small v-if="errors.equipment" class="text-red-500 mt-1 flex items-center gap-1 text-xs">
              <i class="pi pi-exclamation-circle"></i> {{ errors.equipment }}
            </small>
          </div>

          <div class="col-span-12 md:col-span-6">
            <ProductSelect v-model="product" />
            <small v-if="errors.product" class="text-red-500 mt-1 flex items-center gap-1 text-xs">
              <i class="pi pi-exclamation-circle"></i> {{ errors.product }}
            </small>
          </div>

          <div class="col-span-12 md:col-span-6">
            <TrackingSourceSelect v-model="trackingSourceId" :sourcePhaseId="SOURCE_PHASE_ID"
              :filterProductId="product?.shapeId" label="Peças de Prensas (Origem)" />
            <!--
              <TrackingSourceSelect v-model="trackingSourceId" :sourcePhaseId="SOURCE_PHASE_ID" :filterProductId="product?.id"
              label="Peças de Prensas (Origem)" /> ---- ASI ES COMO DEBERÁN SER LAS DEMAS FASES, BUSCAR POR EL ID DEL PROPIO PRODUCTO
            -->
            <small v-if="errors.trackingSourceId" class="text-red-500 mt-1 text-xs">
              {{ errors.trackingSourceId }}
            </small>
          </div>



          <div class="col-span-6 md:col-span-3">
            <label class="block text-xs font-medium text-surface-500 mb-1 ml-1">Carro (Un. Logística)</label>
            <InputNumber v-model="logisticUnit" :useGrouping="false" fluid class="w-full" placeholder="Ex: 123" />
            <small v-if="errors.logisticUnit" class="text-red-500 mt-1 text-xs">{{ errors.logisticUnit }}</small>
          </div>

          <div class="col-span-6 md:col-span-3">
            <label class="block text-xs font-medium text-surface-500 mb-1 ml-1">Quantidade</label>
            <InputNumber v-model="quantity" :useGrouping="false" fluid class="w-full" placeholder="Ex: 360"
              suffix=" un." />
            <small v-if="errors.quantity" class="text-red-500 mt-1 text-xs">{{ errors.quantity }}</small>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">

        <div
          class="bg-white dark:bg-surface-900 p-4 rounded-xl border border-surface-200 dark:border-surface-700 shadow-sm">
          <div class="flex items-center gap-2 mb-3 text-surface-500 font-bold uppercase text-xs tracking-wider">
            <i class="pi pi-clock"></i> Tempos
          </div>
          <div class="flex flex-col gap-4">
            <div>
              <label class="block text-xs font-medium text-surface-500 mb-1">Início</label>
              <DatePicker v-model="startTime" showIcon showTime fluid class="w-full" />
              <small v-if="errors.startTime" class="text-red-500 mt-1 text-xs">{{ errors.startTime }}</small>
            </div>
            <div>
              <label class="block text-xs font-medium text-surface-500 mb-1">Fim</label>
              <DatePicker v-model="endTime" showIcon showTime fluid class="w-full" />
              <small v-if="errors.endTime" class="text-red-500 mt-1 text-xs">{{ errors.endTime }}</small>
            </div>
          </div>
        </div>

        <div
          class="bg-white dark:bg-surface-900 p-4 rounded-xl border border-surface-200 dark:border-surface-700 shadow-sm">
          <div class="flex items-center gap-2 mb-3 text-surface-500 font-bold uppercase text-xs tracking-wider">
            <i class="pi pi-users"></i> Equipa & Turno
          </div>
          <div class="flex flex-col gap-4">
            <div>
              <TeamsSelect v-model="team" :filterSection="filterSectionVar" class="w-full" />
              <small v-if="errors.team" class="text-red-500 mt-1 text-xs">{{ errors.team }}</small>
            </div>
            <div>
              <ShiftsSelect v-model="shift" class="w-full" />
              <small v-if="errors.shift" class="text-red-500 mt-1 text-xs">{{ errors.shift }}</small>
            </div>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">

        <div
          class="border border-surface-200 dark:border-surface-700 rounded-xl p-4 bg-surface-50 dark:bg-surface-800/50">
          <div class="flex items-center justify-between mb-2">
            <span class="font-bold text-surface-700 dark:text-surface-200 text-sm flex items-center gap-2">
              <i class="pi pi-box"></i> Matérias Primas
            </span>
            <span class="text-xs text-red-500" v-if="errors.rawMaterials && meta.touched">* Obrigatório</span>
          </div>

          <RawMaterialsForm v-model="rawMaterials" :phaseId="CURRENT_PHASE_ID" />

          <small v-if="errors.rawMaterials && meta.touched" class="text-red-500 mt-2 block text-xs">
            {{ errors.rawMaterials }}
          </small>
        </div>

        <div
          class="border border-surface-200 dark:border-surface-700 rounded-xl p-4 bg-surface-50 dark:bg-surface-800/50">
          <div class="flex items-center mb-2">
            <span class="font-bold text-surface-700 dark:text-surface-200 text-sm flex items-center gap-2">
              <i class="pi pi-sliders-h"></i> Parâmetros
            </span>
          </div>
          <ParametersForm v-model="parameters" :phaseId="CURRENT_PHASE_ID" />
          <small v-if="errors.parameters" class="text-red-500 mt-2 block text-xs">{{ errors.parameters }}</small>
        </div>
      </div>

      <div class="col-span-12">
        <label class="block text-xs font-medium text-surface-500 mb-1 ml-1">Equipamentos Auxiliares</label>
        <AuxiliaryEquipmentsSelect v-model="auxiliaryEquipmentIds" :filterSection="filterSectionVar" />
      </div>

      <div>
        <label class="block text-xs font-medium text-surface-500 mb-1 ml-1">Observações / Comentários</label>
        <Textarea v-model="comments" rows="3" class="w-full" placeholder="Informação adicional relevante..." />
      </div>

      <Divider />

      <div class="flex flex-col-reverse sm:flex-row justify-end gap-3 pb-2">
        <Button label="Cancelar" icon="pi pi-times" severity="secondary" text @click="$emit('cancel')"
          class="w-full sm:w-auto" />
        <Button type="submit" label="Guardar Registo" icon="pi pi-check" :loading="isSubmitting"
          class="w-full sm:w-auto px-6 font-bold" />
      </div>

    </form>
  </div>
</template>