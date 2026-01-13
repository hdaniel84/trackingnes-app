<!-- GenericTrackingForm.vue -->
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
import { useConfirm } from "primevue/useconfirm";

import DatePicker from 'primevue/datepicker';
import Textarea from 'primevue/textarea';
import Button from 'primevue/button';
import Divider from 'primevue/divider';
import ConfirmDialog from 'primevue/confirmdialog';

// Componentes Globales
import ProductSelect from '@/components/forms/global/ProductSelect.vue';
import TeamsSelect from '@/components/forms/global/TeamsSelect.vue';
import ShiftsSelect from '@/components/forms/global/ShiftsSelect.vue';
//import EquipmentsSelect from '@/components/forms/global/EquipmentsSelect.vue';
import ParametersForm from '@/components/forms/global/ParametersForm.vue';
import RawMaterialsForm from '@/components/forms/global/RawMaterialsForm.vue';
import AuxiliaryEquipmentsSelect from '@/components/forms/global/AuxiliaryEquipmentsSelect.vue';
import TrackingSuccessDialog from '@/components/forms/tracking/TrackingSuccessDialog.vue';
import TrackingSourceManager from '@/components/forms/global/TrackingSourceManager.vue';
import DialogInfo from '@/components/forms/global/DialogInfo.vue';

const props = defineProps({
  item: Object,
  mode: { type: String, default: 'create' },
  validationSchema: { type: Object, required: true },
});

const emit = defineEmits(['success', 'cancel']);

const instanceId = Math.random().toString(36).substring(2, 9); //Generamos ID único para cada form
const confirmGroup = `trackingForm_${instanceId}`;

// 1. CONTEXTO DINÁMICO
const { phaseId, currentRules, phaseMetadata } = usePhase();
const store = useTrackingStore();
const rawMaterialStore = useRawMaterialStore();
const parameterStore = useParameterStore();
const showSuccessDialog = ref(false);
const createdTrackingId = ref(null);
const { notifySuccess, notifyError } = useNotify();
const { showErrorDialog } = useErrorDialog();
const confirm = useConfirm();

const errorList = computed(() => {
  return Object.values(errors.value || {});
});

// 2. LOGICA VISUAL CONDICIONAL
const filterSectionVar = computed(() => phaseMetadata.value?.slug || 'producao');
const writePermission = computed(() => `WRITE_${phaseMetadata.value?.permissionSuffix || 'ADMIN'}`);

const showTrackingSource = computed(() => {
  return currentRules.value?.allowedSourcePhases && currentRules.value.allowedSourcePhases.length > 0;
});

const showRawMaterials = computed(() => {
  return !currentRules.value?.hideRawMaterials;
});

// 3. VEE-VALIDATE SETUP
const { handleSubmit, errors, setValues, isSubmitting, resetForm, meta, values: formValues, submitCount } = useForm({
  validationSchema: toTypedSchema(props.validationSchema),
  validateOnMount: false,
  validateOnBlur: false,
  validateOnChange: false,
  validateOnInput: false,
  validateOnModelUpdate: false,

  initialValues: {
    startTime: new Date(),
    rawMaterials: [{ id: null, rawMaterialId: null, value: '' }],
    parameters: [],
    auxiliaryEquipmentIds: [],
    sources: [],
    product: null,
    quantity: null,
    quantityScrap: null
  }
});

// ... (FUNCIONES DE INYECCIÓN DE MATERIAS PRIMAS Y PARÁMETROS) ...
const injectMandatoryRawMaterials = async () => {
  if (rawMaterialStore.items.length === 0) await rawMaterialStore.fetchAll();
  const currentPhaseId = Number(phaseId.value);
  const mandatoryItems = rawMaterialStore.items.filter(rm => rm.mandatory === true && rm.phase?.id === currentPhaseId);
  if (mandatoryItems.length === 0) return;
  let currentRows = formValues.rawMaterials || [];
  const isDefaultEmptyRow = currentRows.length === 1 && !currentRows[0].rawMaterialId && !currentRows[0].value;
  let mergedRows = isDefaultEmptyRow ? [] : [...currentRows];
  let hasChanges = isDefaultEmptyRow;
  mandatoryItems.forEach(mandatory => {
    const exists = mergedRows.some(row => row.rawMaterialId === mandatory.id);
    if (!exists) {
      mergedRows.push({ id: null, rawMaterialId: mandatory.id, value: '' });
      hasChanges = true;
    }
  });
  if (hasChanges) {
    resetForm({ values: { ...formValues, rawMaterials: mergedRows } });
  }
};

const injectMandatoryParameters = async () => {
  if (parameterStore.items.length === 0) await parameterStore.fetchAll();
  const currentPhaseId = Number(phaseId.value);
  const mandatoryItems = parameterStore.items.filter(p => p.mandatory === true && p.phase?.id === currentPhaseId);
  if (mandatoryItems.length === 0) return;
  let currentRows = formValues.parameters || [];
  const isDefaultEmptyRow = currentRows.length === 1 && !currentRows[0].parameterId;
  let mergedRows = isDefaultEmptyRow ? [] : [...currentRows];
  let hasChanges = isDefaultEmptyRow;
  mandatoryItems.forEach(mandatory => {
    const exists = mergedRows.some(row => row.parameterId === mandatory.id);
    if (!exists) {
      mergedRows.push({ id: null, parameterId: mandatory.id, valueString: '', valueNumber: null, valueBool: null, valueDate: null });
      hasChanges = true;
    }
  });
  if (hasChanges) {
    resetForm({ values: { ...formValues, parameters: mergedRows } });
  }
};

// Definición de campos
const { value: product } = useField('product');
const { value: sources } = useField('sources');
const { value: startTime } = useField('startTime');
const { value: endTime } = useField('endTime');
const { value: comments } = useField('comments');
const { value: team } = useField('team');
const { value: shift } = useField('shift');
//const { value: equipment } = useField('equipment');
const { value: quantity } = useField('quantity');
const { value: quantityScrap } = useField('quantityScrap');
const { value: parameters } = useField('parameters');
const { value: rawMaterials } = useField('rawMaterials');
const { value: auxiliaryEquipmentIds } = useField('auxiliaryEquipmentIds');

// 4. LÓGICA DE ORIGEN
const initialSourceItems = ref([]);
const showSourceSelect = ref(true);
const isLoadingData = ref(false); // Bandera para proteger carga

const targetReferenceId = computed(() => {
  const p = product.value;
  if (!p) return null;
  const filterType = currentRules.value?.filterType;
  if (filterType === 'PRODUCT_CODE' || filterType === 'SHAPE') {
    return p.shapeId || p.shape?.id || p.productCode;
  }
  return p.id;
});

watch(product, (newVal, oldVal) => {
  if (isLoadingData.value) return;
  if (oldVal && newVal?.id !== oldVal?.id) {
    sources.value = [];
    initialSourceItems.value = [];
  }
});

watch(() => props.item, async (val) => {
  if (val) {
    isLoadingData.value = true;
    try {
      const incomingSources = (val.sources || []).map(s => ({ ...s, remainingQuantity: s.remainingQuantity }));
      initialSourceItems.value = incomingSources;
      const formSources = incomingSources.map(s => ({ trackingId: s.trackingId, quantityUsed: s.quantityUsed }));

      setValues({
        sources: formSources,
        startTime: new Date(val.startTime),
        endTime: val.endTime ? new Date(val.endTime) : null,
        comments: val.comments,
        quantity: val.quantity,
        quantityScrap: val.quantityScrap,
        product: val.product || null,
        team: val.team || null,
        shift: val.shift || null,
        //equipment: val.equipment || null,
        auxiliaryEquipmentIds: (val.auxiliaryEquipments || []).map(e => e.id),
        rawMaterials: (val.rawMaterials || []).map(r => ({ id: r.id, rawMaterialId: r.rawMaterialId, value: r.value || '' })),
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
    } finally {
      setTimeout(() => { isLoadingData.value = false; }, 100);
    }
  }
}, { immediate: true });

watch(phaseId, async (newId) => {
  if (newId) {
    await injectMandatoryRawMaterials();
    await injectMandatoryParameters();
  }
}, { immediate: true });

// Validaciones de cantidades
const totalProduced = computed(() => (quantity.value || 0) + (quantityScrap.value || 0));
const totalInput = computed(() => (sources.value || []).reduce((acc, item) => acc + (Number(item.quantityUsed) || 0), 0));

const massBalanceAlert = computed(() => {
  if (!showTrackingSource.value || totalInput.value <= 0) return null;
  const diff = totalProduced.value - totalInput.value;

  if (diff > 0) return { severity: 'error', icon: 'pi pi-exclamation-triangle', title: 'Inconsistência', message: `A produção (${totalProduced.value}) excede a entrada (${totalInput.value}) em ${diff} un.` };
  if (diff === 0) return { severity: 'success', icon: 'pi pi-check-circle', title: 'Balanço Perfeito', message: `Entrada e saída coincidem (${totalInput.value} un).` };

  // CASO WARNING (Merma)
  return { severity: 'secondary', icon: 'pi pi-info-circle', title: 'Perda Técnica', message: `Consumo: ${totalInput.value} | Perda: ${Math.abs(diff)} un.` };
});


// 5. LÓGICA DE GUARDADO (Separada para poder llamarla desde Confirm)
const executeSave = async (values) => {
  try {
    const parametersPayload = (parameters.value || []).map(p => ({
      id: p.id, parameterId: p.parameterId, valueString: p.valueString, valueNumber: p.valueNumber, valueBool: p.valueBool, valueDate: p.valueDate ? new Date(p.valueDate).toISOString() : null
    }));

    const rawMaterialsPayload = (rawMaterials.value || []).filter(r => r.rawMaterialId).map(r => ({
      id: r.id, rawMaterialId: r.rawMaterialId, value: r.value
    }));

    const payload = {
      phaseId: phaseId.value,
      startTime: values.startTime,
      endTime: values.endTime,
      comments: values.comments,
      quantity: values.quantity,
      productId: values.product?.id,
      teamId: values.team?.id,
      shiftId: values.shift?.id,
      //equipmentId: values.equipment?.id,
      auxiliaryEquipmentIds: values.auxiliaryEquipmentIds,
      sources: values.sources,
      quantityScrap: values.quantityScrap ?? 0,
      rawMaterials: rawMaterialsPayload,
      parameters: parametersPayload
    };

    if (props.mode === 'create') {
      const newRecord = await store.create(payload);
      createdTrackingId.value = newRecord.id;
      showSuccessDialog.value = true;
      resetForm({
        values: {
          //equipment: values.equipment,
          product: null,
          team: values.team,
          shift: values.shift,
          startTime: new Date(),
          parameters: [],
          rawMaterials: values.rawMaterials,
          auxiliaryEquipmentIds: [],
          quantity: null, comments: null, endTime: null, sources: [], quantityScrap: null
        }
      });
      showSourceSelect.value = true;
      initialSourceItems.value = [];
    } else {
      //-- Modo Edición --
      await store.update(props.item.id, payload);
      notifySuccess('Registo atualizado com sucesso');
      emit('success');
    }
  } catch (err) {
    showErrorDialog(err, 'error');
    notifyError('Erro', err.message);
  }
};


// 6. SUBMIT INTERCEPTADO
const onSubmit = handleSubmit(async (values) => {

  // A. ERROR BLOQUEANTE: Producción mayor que entrada
  if (massBalanceAlert.value && massBalanceAlert.value.severity === 'error') {
    const diff = totalProduced.value - totalInput.value;
    notifyError(
      'Bloqueio de Inconsistência',
      `A produção (${totalProduced.value}) não pode ser maior que a entrada (${totalInput.value}). Diferença: ${diff}`
    );
    return; // Stop
  }

  // B. RECOLECTAR ADVERTENCIAS
  let warningMessage = '';
  let hasWarning = false;

  // Advertencia 1: Perda Técnica (Entrada > Salida)
  if (massBalanceAlert.value && massBalanceAlert.value.severity === 'secondary') {
    const perda = Math.abs(totalProduced.value - totalInput.value);
    warningMessage += `<p class="mb-2"><strong>Perda Técnica:</strong> Está a registar uma perda de <strong>${perda}</strong> unidades.</p>`;
    hasWarning = true;
  }

  // Advertencia 2: Origens com quantidade ZERO
  // Verificamos si hay orígenes seleccionados pero con quantityUsed 0 o null
  const zeroQuantitySources = (values.sources || []).filter(s => !s.quantityUsed || s.quantityUsed <= 0);
  if (zeroQuantitySources.length > 0) {
    warningMessage += `<p><strong>Atenção:</strong> Existem <strong>${zeroQuantitySources.length}</strong> lotes de origem selecionados com quantidade 0.</p>`;
    hasWarning = true;
  }

  // C. DECISIÓN: CONFIRMAR O GUARDAR
  if (hasWarning) {
    confirm.require({
      group: confirmGroup,
      message: warningMessage + '<br/>Deseja guardar o registo mesmo assim?',
      header: 'Confirmação Necessária',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sim, Guardar',
      rejectLabel: 'Não, Cancelar',
      acceptClass: 'p-button-warning',
      rejectClass: 'p-button-text',
      accept: () => {
        executeSave(values);
      },
      reject: () => {
        // No hacemos nada, el usuario canceló
      }
    });
  } else {
    // Si no hay advertencias, guardamos directo
    await executeSave(values);
  }
});
</script>

<template>
  <div class="w-full">
    <ConfirmDialog :group="confirmGroup">
      <template #message="slotProps">
        <div class="flex flex-col items-start w-full">
          <div v-html="slotProps.message.message" class="text-surface-600 dark:text-surface-300"></div>
        </div>
      </template>
    </ConfirmDialog>

    <form @submit.prevent="onSubmit" class="flex flex-col gap-6">

      <div class="bg-surface-50 dark:bg-surface-800 p-5 rounded-xl border border-surface-200 dark:border-surface-700">
        <div
          class="flex items-center gap-2 mb-4 text-primary-600 dark:text-primary-400 font-bold uppercase text-xs tracking-wider">
          <i class="pi pi-box"></i> Identificação da Produção ({{ phaseMetadata.title }})
        </div>

        <div class="grid grid-cols-1 md:grid-cols-12 gap-5">
          <!--
          <div class="col-span-12 md:col-span-6">
            <EquipmentsSelect v-model="equipment" :filterSection="filterSectionVar" />
            <small v-if="errors.equipment" class="text-red-500 mt-1 flex items-center gap-1 text-xs">
              <i class="pi pi-exclamation-circle"></i> {{ errors.equipment }}
            </small>
          </div>
          -->
          <div class="col-span-12 md:col-span-6">
            <AuxiliaryEquipmentsSelect v-model="auxiliaryEquipmentIds" :filterSection="filterSectionVar" />
            <small v-if="errors.auxiliaryEquipmentIds" class="text-red-500 mt-1 flex items-center gap-1 text-xs">
              <i class="pi pi-exclamation-circle"></i> {{ errors.auxiliaryEquipmentIds }}
            </small>
          </div>

          <div class="col-span-12 md:col-span-6">
            <ProductSelect v-model="product" :prefixes="currentRules.productFilter" label="Produto de Saída" />
            <small v-if="errors.product" class="text-red-500 mt-1 flex items-center gap-1 text-xs">
              <i class="pi pi-exclamation-circle"></i> {{ errors.product }}
            </small>
          </div>

          <div v-if="showTrackingSource" class="col-span-12">
            <TrackingSourceManager v-model="sources" :initial-data="initialSourceItems"
              :allowedPhases="currentRules.allowedSourcePhases" :target-reference-id="targetReferenceId"
              :match-reference="currentRules.matchReference" :filter-type="currentRules.filterType"
              :disabled="!product" />
            <small v-if="errors.sources && submitCount > 0" class="text-red-500 mt-1 text-xs">{{ errors.sources
            }}</small>
          </div>


          <!--
          <div class="col-span-6 md:col-span-3">
             <LogisticUnitsInput 
                v-model="logisticUnits"
                :label="phaseId <= 4 ? 'Carros (Un. Logísticas)' : (phaseId <= 5 ? 'Vagões' : 'Paletes')"
                :error-message="errors.logisticUnits"
             />
          </div>
          -->

          <div class="col-span-12 md:col-span-12">
            <div
              class="bg-white dark:bg-surface-900 border border-surface-200 dark:border-surface-700 rounded-xl p-4 shadow-sm h-full flex flex-col">

              <div class="flex items-center justify-between mb-3">
                <span class="text-xs font-bold text-surface-500 uppercase tracking-wider flex items-center gap-2">
                  <i class="pi pi-chart-bar"></i> Resultados da Produção
                </span>
              </div>

              <div class="grid grid-cols-3 gap-4 mb-4">

                <div class="col-span-1">
                  <label class="block text-[10px] uppercase font-bold text-green-600 mb-1.5 ml-1">
                    <i class="pi pi-check-circle"></i> Qtd. OK (Boa)
                  </label>
                  <InputNumber v-model="quantity" fluid :useGrouping="false" placeholder="0" suffix=" un."
                    inputClass="font-bold text-green-700 dark:text-green-400" class="w-full" />
                </div>

                <div class="col-span-1">
                  <label class="block text-[10px] uppercase font-bold text-red-500 mb-1.5 ml-1">
                    <i class="pi pi-times-circle"></i> NOK (Quebras)
                  </label>
                  <InputNumber v-model="quantityScrap" fluid :useGrouping="false" placeholder="0" :min="0" suffix=" un."
                    inputClass="text-red-600 dark:text-red-400 font-bold" class="w-full" />
                </div>
                <div class="col-span-1">
                  <label class="block text-[10px] uppercase font-bold  mb-1.5 ml-1">
                    <i class="pi pi-chart-pie"></i> Total
                  </label>
                  <InputNumber v-model="totalProduced" fluid :useGrouping="false" placeholder="0" suffix=" un." disabled
                    inputClass="text-red-600 dark:text-red-400 font-bold" class="w-full" />
                </div>
              </div>

              <div v-if="errors.quantity" class="-mt-2 mb-2">
                <small class="text-red-500 text-[10px] flex items-center gap-1">
                  <i class="pi pi-exclamation-triangle"></i> {{ errors.quantity }}
                </small>
              </div>

              <div class="mt-auto">
                <div v-if="massBalanceAlert"
                  class="text-xs p-2.5 rounded-lg border flex items-start gap-3 transition-all duration-300" :class="{
                    'bg-red-50 border-red-100 text-red-800 dark:bg-red-900/10 dark:border-red-800/50 dark:text-red-300': massBalanceAlert.severity === 'error',
                    'bg-green-50 border-green-100 text-green-800 dark:bg-green-900/10 dark:border-green-800/50 dark:text-green-300': massBalanceAlert.severity === 'success',
                    'bg-surface-50 border-surface-100 text-surface-600 dark:bg-surface-800 dark:border-surface-700/50 dark:text-surface-400': massBalanceAlert.severity === 'secondary'
                  }">

                  <div
                    class="shrink-0 w-6 h-6 rounded-full flex items-center justify-center bg-white/50 dark:bg-black/20">
                    <i :class="massBalanceAlert.icon" class="text-xs"></i>
                  </div>

                  <div class="flex flex-col">
                    <span class="font-bold uppercase text-[9px] tracking-wider mb-0.5 opacity-80">
                      {{ massBalanceAlert.title }}
                    </span>
                    <span class="text-[11px] font-medium leading-tight">
                      {{ massBalanceAlert.message }}
                    </span>
                  </div>
                </div>

                <div v-else-if="showTrackingSource"
                  class="h-[50px] border border-dashed border-surface-100 dark:border-surface-800 rounded-lg flex items-center justify-center text-surface-300 text-[10px] italic">
                  Aguardando dados de entrada...
                </div>
              </div>

            </div>
          </div>
          <!-- POR AHORA NO PEDIR MOTIVO DE QUEBRAS
          <div v-if="quantityScrap > 0" class="col-span-12">
            <label class="block text-xs font-medium text-surface-500 mb-1 ml-1">Motivo do Descarte</label>
            <Textarea v-model="scrapReason" rows="1" autoResize class="w-full" placeholder="Ex: Quebrado, Sujo..." />
          </div>
          -->
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

      <transition name="p-message-fade">
        <div v-if="errorList.length && submitCount > 0"
          class="relative mt-6 overflow-hidden rounded-xl border-l-4 border-red-500 bg-white shadow-md dark:bg-surface-900 transition-all duration-300">

          <div class="absolute inset-0 bg-gradient-to-r from-red-500/5 to-transparent pointer-events-none"></div>

          <div class="relative p-4 flex items-start gap-4">
            <div
              class="flex-shrink-0 flex items-center justify-center w-10 h-10 rounded-full bg-red-100 dark:bg-red-900/30">
              <i class="pi pi-exclamation-circle text-red-600 dark:text-red-400 text-xl animate-pulse"></i>
            </div>

            <div class="flex-1 min-w-0">
              <div class="flex items-center justify-between mb-2">
                <h5 class="text-sm font-bold text-red-800 dark:text-red-300 uppercase tracking-wide">
                  Atenção: {{ errorList.length }} {{ errorList.length > 1 ? 'erros pendentes' : 'erro pendente' }}
                </h5>
                <span
                  class="text-[10px] bg-red-100 dark:bg-red-900/50 text-red-600 dark:text-red-400 px-2 py-0.5 rounded-full font-bold uppercase">
                  Ação necessária
                </span>
              </div>

              <div class="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-2">
                <div v-for="(error, i) in errorList" :key="i"
                  class="flex items-center gap-2 text-xs text-surface-600 dark:text-surface-400 group">
                  <i
                    class="pi pi-angle-right text-[10px] text-red-400 group-hover:translate-x-1 transition-transform"></i>
                  <span class="leading-tight">{{ error }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="h-1 bg-red-500/20 w-full">
            <div class="h-full bg-red-500 w-1/3 animate-shimmer"></div>
          </div>
        </div>
      </transition>
      <Divider />
      <TrackingSuccessDialog v-model:visible="showSuccessDialog" :trackingId="createdTrackingId" />
      <DialogInfo />
    </form>
  </div>
</template>
