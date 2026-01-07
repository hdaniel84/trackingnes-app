<script setup>
import { useTrackingStore } from '@/stores/trackingStore';
import { useForm, useField } from 'vee-validate';
import { toTypedSchema } from '@vee-validate/zod';
import { ref, computed, watch } from 'vue';
import { usePhase } from '@/layout/composables/usePhase';
import { useRawMaterialStore } from '@/stores/rawMaterialStore';
import { useParameterStore } from '@/stores/parameterStore';

// Helpers & UI
import { useNotify } from '@/layout/composables/notify';
import { useErrorDialog } from '@/layout/composables/errorDialog';
import DatePicker from 'primevue/datepicker';
import Textarea from 'primevue/textarea';
import Button from 'primevue/button';
import Divider from 'primevue/divider';

// Componentes Globales
import ProductSelect from '@/components/forms/global/ProductSelect.vue';
import TeamsSelect from '@/components/forms/global/TeamsSelect.vue';
import ShiftsSelect from '@/components/forms/global/ShiftsSelect.vue';
import EquipmentsSelect from '@/components/forms/global/EquipmentsSelect.vue';
import ParametersForm from '@/components/forms/global/ParametersForm.vue';
import RawMaterialsForm from '@/components/forms/global/RawMaterialsForm.vue';
import AuxiliaryEquipmentsSelect from '@/components/forms/global/AuxiliaryEquipmentsSelect.vue';
import TrackingSourceSelect from '@/components/forms/global/TrackingSourceSelect.vue';
import TrackingSuccessDialog from '@/components/forms/tracking/TrackingSuccessDialog.vue';

const props = defineProps({
  item: Object,
  mode: { type: String, default: 'create' },
  validationSchema: { type: Object, required: true }, // Schema Zod Inyectado
});

const emit = defineEmits(['success', 'cancel']);

// 1. CONTEXTO DINÁMICO (Router & Rules)
const { phaseId, currentRules, phaseMetadata } = usePhase();
const store = useTrackingStore();
const rawMaterialStore = useRawMaterialStore();
const parameterStore = useParameterStore();
const showSuccessDialog = ref(false);
const createdTrackingId = ref(null);
const { notifySuccess, notifyError } = useNotify();
const { showErrorDialog } = useErrorDialog();
const errorList = computed(() => {
  return Object.values(errors.value || {});
});


// 2. LOGICA VISUAL CONDICIONAL
const filterSectionVar = computed(() => phaseMetadata.value?.slug || 'producao');
const writePermission = computed(() => `WRITE_${phaseMetadata.value?.permissionSuffix || 'ADMIN'}`);

// ¿Mostramos Origem? (Sí, si la fase tiene fases permitidas definidas)
const showTrackingSource = computed(() => {
  return currentRules.value?.allowedSourcePhases && currentRules.value.allowedSourcePhases.length > 0;
});

// ¿Mostramos Materias Primas? (Sí, a menos que la regla diga explícitamente ocultarlo, ej: Forno)
const showRawMaterials = computed(() => {
  return !currentRules.value?.hideRawMaterials;
});

// 3. VEE-VALIDATE SETUP
const { handleSubmit, errors, setValues, isSubmitting, resetForm, meta, values: formValues, submitCount } = useForm({
  validationSchema: toTypedSchema(props.validationSchema),
  validateOnMount: false,
  initialValues: {
    startTime: new Date(),
    rawMaterials: [{ id: null, rawMaterialId: null, value: '' }],
    parameters: [],
    auxiliaryEquipmentIds: [],
    sourceTrackingIds: [],
    logisticUnits: []
  }
});

//inyeccion de rawMterials mandatory
const injectMandatoryRawMaterials = async () => {
  // 1. Cargar datos si faltan
  if (rawMaterialStore.items.length === 0) {
    await rawMaterialStore.fetchAll();
  }

  // 2. Filtrar obligatorios de la fase actual
  const currentPhaseId = Number(phaseId.value);
  const mandatoryItems = rawMaterialStore.items.filter(rm =>
    rm.mandatory === true &&
    rm.phase?.id === currentPhaseId
  );

  // Si no hay obligatorios, no tocamos nada (se queda la fila vacía si existía)
  if (mandatoryItems.length === 0) return;

  // 3. Preparar filas actuales
  let currentRows = formValues.rawMaterials || [];

  // 🚀 FIX: Si la única fila que existe está vacía (default), la marcamos para borrar
  // Esto evita: [ vacía, obligatoria1, obligatoria2 ]
  const isDefaultEmptyRow = currentRows.length === 1 && !currentRows[0].rawMaterialId && !currentRows[0].value;

  // Si es la fila por defecto, empezamos con un array limpio
  let mergedRows = isDefaultEmptyRow ? [] : [...currentRows];
  let hasChanges = isDefaultEmptyRow; // Si borramos la fila vacía, ya cuenta como cambio

  // 4. Inyectar obligatorios
  mandatoryItems.forEach(mandatory => {
    // Verificamos si ya está en el array que vamos a guardar
    const exists = mergedRows.some(row => row.rawMaterialId === mandatory.id);

    if (!exists) {
      mergedRows.push({
        id: null,
        rawMaterialId: mandatory.id,
        value: ''
      });
      hasChanges = true;
    }
  });

  // 5. Aplicar cambios al formulario
  if (hasChanges) {
    resetForm({
      values: {
        ...formValues,
        rawMaterials: mergedRows
      }
    });
  }
};

//Inyeccion de parametros:
const injectMandatoryParameters = async () => {
  // 1. Cargar catálogo si está vacío
  if (parameterStore.items.length === 0) {
    await parameterStore.fetchAll();
  }

  const currentPhaseId = Number(phaseId.value);

  // 2. Filtrar obligatorios de la fase actual
  const mandatoryItems = parameterStore.items.filter(p =>
    p.mandatory === true &&
    p.phase?.id === currentPhaseId
  );

  if (mandatoryItems.length === 0) return;

  let currentRows = formValues.parameters || [];

  // 3. Limpiar fila vacía por defecto (si existe y es la única)
  const isDefaultEmptyRow = currentRows.length === 1 && !currentRows[0].parameterId;

  let mergedRows = isDefaultEmptyRow ? [] : [...currentRows];
  let hasChanges = isDefaultEmptyRow;

  // 4. Fusionar
  mandatoryItems.forEach(mandatory => {
    // Verificamos si ya existe en el array
    const exists = mergedRows.some(row => row.parameterId === mandatory.id);

    if (!exists) {
      // Agregamos con la estructura completa del DTO inicializada en null
      mergedRows.push({
        id: null,
        parameterId: mandatory.id,
        valueString: '',
        valueNumber: null,
        valueBool: null,
        valueDate: null
      });
      hasChanges = true;
    }
  });

  // 5. Aplicar cambios
  if (hasChanges) {
    resetForm({
      values: {
        ...formValues, // Mantenemos lo que el usuario ya haya escrito en otros campos
        parameters: mergedRows
      }
    });
  }
};

// Definición de campos (para v-model manual si es necesario)
const { value: product } = useField('product');
const { value: sourceTrackingIds } = useField('sourceTrackingIds');
const { value: logisticUnits } = useField('logisticUnits');
const { value: startTime } = useField('startTime');
const { value: endTime } = useField('endTime');
const { value: comments } = useField('comments');
const { value: team } = useField('team');
const { value: shift } = useField('shift');
const { value: equipment } = useField('equipment');
const { value: quantity } = useField('quantity');
const { value: parameters } = useField('parameters');
const { value: rawMaterials } = useField('rawMaterials');
const { value: auxiliaryEquipmentIds } = useField('auxiliaryEquipmentIds');

// 4. LÓGICA DE ORIGEN (Tarjeta vs Select)
//const currentSourceDetails = ref(null);
const initialSourceItems = ref([]);
const showSourceSelect = ref(true);

// Calculamos el ID de referencia para el filtro (ej: ShapeId o ProductCode)
const targetReferenceId = computed(() => {
  if (currentRules.value?.filterType === 'PRODUCT_CODE') return product.value?.shapeId;
  return product.value?.id || null;
});

// Watcher para resetear si cambia producto padre
watch(product, (newVal, oldVal) => {
  if (oldVal && newVal?.id !== oldVal?.id) {
    sourceTrackingIds.value = []; // Reset array
    initialSourceItems.value = [];
  }
});

// 5. CARGA DE DATOS (EDICIÓN)
watch(() => props.item, async (val) => {
  if (val) {
    const sourceIds = (val.sourceTrackings || []).map(s => s.id);
    initialSourceItems.value = val.sourceTrackings || [];

    setValues({
      sourceTrackingIds: sourceIds,
      logisticUnits: val.logisticUnits || [],
      startTime: new Date(val.startTime),
      endTime: val.endTime ? new Date(val.endTime) : null,
      comments: val.comments,
      quantity: val.quantity,
      product: val.product || null,
      team: val.team || null,
      shift: val.shift || null,
      equipment: val.equipment || null,
      auxiliaryEquipmentIds: (val.auxiliaryEquipments || []).map(e => e.id),

      // Mapeos de arrays
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

    await injectMandatoryRawMaterials();
    await injectMandatoryParameters();
  }
}, { immediate: true });

// Caso A: Creación / Cambio de Fase
watch(phaseId, async (newId) => {
  if (newId) {
    // Inyectar obligatorios después del reset
    await injectMandatoryRawMaterials();
    await injectMandatoryParameters();
  }
}, { immediate: true });

// 6. SUBMIT
const onSubmit = handleSubmit(async (values) => {
  try {
    // Preparar Payloads Anidados
    const parametersPayload = (parameters.value || []).map(p => ({
      id: p.id,
      parameterId: p.parameterId,
      valueString: p.valueString,
      valueNumber: p.valueNumber,
      valueBool: p.valueBool,
      valueDate: p.valueDate ? new Date(p.valueDate).toISOString() : null
    }));

    const rawMaterialsPayload = (rawMaterials.value || [])
      .filter(r => r.rawMaterialId)
      .map(r => ({
        id: r.id,
        rawMaterialId: r.rawMaterialId,
        value: r.value
      }));

    const payload = {
      // Campos directos
      phaseId: phaseId.value, // 🚀 ID automático desde el contexto
      logisticUnits: values.logisticUnits,
      startTime: values.startTime,
      endTime: values.endTime,
      comments: values.comments,
      quantity: values.quantity,
      productId: values.product?.id,
      teamId: values.team?.id,
      shiftId: values.shift?.id,
      equipmentId: values.equipment?.id,
      auxiliaryEquipmentIds: values.auxiliaryEquipmentIds,
      sourceTrackingIds: values.sourceTrackingIds,
      // Arrays
      rawMaterials: rawMaterialsPayload,
      parameters: parametersPayload
    };

    if (props.mode === 'create') {
      const newRecord = await store.create(payload);
      createdTrackingId.value = newRecord.id;
      showSuccessDialog.value = true;

      // Reset inteligente (conserva contexto)
      resetForm({
        values: {
          equipment: values.equipment,
          product: values.product,
          team: values.team,
          shift: values.shift,
          startTime: new Date(),
          parameters: values.parameters, // Conserva params configurados
          rawMaterials: values.rawMaterials, // Conserva MPs configuradas
          auxiliaryEquipmentIds: values.auxiliaryEquipmentIds,
          // Limpia variables
          logisticUnits: [],
          quantity: null,
          comments: null,
          endTime: null,
          sourceTrackingIds: []
        }
      });
      // Forzar mostrar select de nuevo en create loop
      showSourceSelect.value = true;
      initialSourceItems.value = [];

    } else {
      await store.update(props.item.id, payload);
      notifySuccess('Registo atualizado com sucesso');
      emit('success');
    }
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
          <i class="pi pi-box"></i> Identificação da Produção ({{ phaseMetadata.title }})
        </div>

        <div class="grid grid-cols-1 md:grid-cols-12 gap-5">
          <div class="col-span-12 md:col-span-6">
            <EquipmentsSelect v-model="equipment" :filterSection="filterSectionVar" />
            <small v-if="errors.equipment" class="text-red-500 mt-1 flex items-center gap-1 text-xs">
              <i class="pi pi-exclamation-circle"></i> {{ errors.equipment }}
            </small>
          </div>

          <div class="col-span-12 md:col-span-6">
            <ProductSelect v-model="product" :prefixes="currentRules.productFilter" label="Produto de Saída" />
            <small v-if="errors.product" class="text-red-500 mt-1 flex items-center gap-1 text-xs">
              <i class="pi pi-exclamation-circle"></i> {{ errors.product }}
            </small>
          </div>

          <div v-if="showTrackingSource" class="col-span-12 md:col-span-6">
            <TrackingSourceSelect v-model="sourceTrackingIds" :initial-selection="initialSourceItems"
              :allowedPhases="currentRules.allowedSourcePhases" :target-reference-id="targetReferenceId"
              :match-reference="currentRules.matchReference" :filter-type="currentRules.filterType"
              label="Origens (Fase Anterior)" :disabled="!product" />

            <small v-if="errors.sourceTrackingIds" class="text-red-500 mt-1 text-xs">
              {{ errors.sourceTrackingIds }}
            </small>
            <small v-if="!product" class="text-surface-400 italic text-xs block mt-1">
              * Selecione o produto primeiro
            </small>
          </div>
          
          <div class="col-span-6 md:col-span-3">
             <LogisticUnitsInput 
                v-model="logisticUnits"
                :label="phaseId <= 4 ? 'Carros (Un. Logísticas)' : (phaseId <= 5 ? 'Vagões' : 'Paletes')"
                :error-message="errors.logisticUnits"
             />
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
              <TeamsSelect v-model="team" :filterSection="phaseMetadata.teamSlug || filterSectionVar" class="w-full" />
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

        <div v-if="showRawMaterials"
          class="border border-surface-200 dark:border-surface-700 rounded-xl p-4 bg-surface-50 dark:bg-surface-800/50">
          <div class="flex items-center justify-between mb-2">
            <span class="font-bold text-surface-700 dark:text-surface-200 text-sm flex items-center gap-2">
              <i class="pi pi-box"></i> Matérias Primas
            </span>
            <span class="text-xs text-red-500" v-if="errors.rawMaterials && meta.touched">* Obrigatório</span>
          </div>

          <RawMaterialsForm v-model="rawMaterials" :phaseId="phaseId" :show-validation="submitCount > 0" />

          <small v-if="errors.rawMaterials && meta.touched" class="text-red-500 mt-2 block text-xs">
            {{ errors.rawMaterials }}
          </small>
        </div>

        <div
          class="border border-surface-200 dark:border-surface-700 rounded-xl p-4 bg-surface-50 dark:bg-surface-800/50"
          :class="{ 'lg:col-span-2': !showRawMaterials }">
          <div class="flex items-center mb-2">
            <span class="font-bold text-surface-700 dark:text-surface-200 text-sm flex items-center gap-2">
              <i class="pi pi-sliders-h"></i> Parâmetros
            </span>
          </div>
          <ParametersForm v-model="parameters" :phaseId="phaseId" :show-validation="submitCount > 0" />
          <small v-if="errors.parameters" class="text-red-500 mt-2 block text-xs">{{ errors.parameters }}</small>
        </div>
      </div>

      <slot name="extra-fields" :values="formValues"></slot>

      <div class="col-span-12">
        <label class="block text-xs font-medium text-surface-500 mb-1 ml-1">Equipamentos Auxiliares</label>
        <AuxiliaryEquipmentsSelect v-model="auxiliaryEquipmentIds" :filterSection="filterSectionVar" />
      </div>

      <div>
        <label class="block text-xs font-medium text-surface-500 mb-1 ml-1">Observações / Comentários</label>
        <Textarea id="comments" v-model="comments" rows="3" class="w-full"
          placeholder="Informação adicional relevante..." />
      </div>

      <Divider />

      <div class="flex flex-col-reverse sm:flex-row justify-end gap-3 pb-2">
        <Button type="submit" label="Guardar Registo" icon="pi pi-check" :loading="isSubmitting"
          class="w-full sm:w-auto px-6 font-bold" v-can="writePermission" />
      </div>

      <div v-if="errorList.length"
        class="border border-red-300 bg-red-50 dark:bg-red-900/20 dark:border-red-800 rounded-lg p-4 mt-4">
        <div class="flex items-center gap-2 mb-2 text-red-700 dark:text-red-300 font-bold text-sm">
          <i class="pi pi-exclamation-triangle"></i>
          Foram encontrados erros no formulário:
        </div>

        <ul class="list-disc list-inside space-y-1 text-sm text-red-600 dark:text-red-300">
          <li v-for="(error, i) in errorList" :key="i">
            {{ error }}
          </li>
        </ul>
      </div>
      <Divider />
      <TrackingSuccessDialog v-model:visible="showSuccessDialog" :trackingId="createdTrackingId" />
    </form>
  </div>
</template>