<script setup>

import { useTrackingPrensasStore } from '@/stores/trackingPrensasStore';
import { useForm, useField } from 'vee-validate';
import { toTypedSchema } from '@vee-validate/zod';
import { trackingPrensasSchema } from '@/validation/trackingPrensasSchema';
import { onMounted } from 'vue';

// ✅ Importar helpers
import { useNotify } from '@/layout/composables/notify';
import { useErrorDialog } from '@/layout/composables/errorDialog';

// PrimeVue
import InputText from 'primevue/inputtext';
import InputNumber from 'primevue/inputnumber';
import DatePicker from 'primevue/datepicker';
import Textarea from 'primevue/textarea';
import Button from 'primevue/button';

// Dropdowns
import ProductSelect from '@/components/forms/global/ProductSelect.vue';
import TeamsSelect from '@/components/forms/global/TeamsSelect.vue';
import ShiftsSelect from '@/components/forms/global/ShiftsSelect.vue';
import EquipmentsSelect from '@/components/forms/global/EquipmentsSelect.vue';
import RawMaterialSelect from '@/components/forms/global/RawMaterialSelect.vue';
import ParametersForm from '@/components/forms/global/ParametersForm.vue';

const store = useTrackingPrensasStore();

const { notifySuccess, notifyError } = useNotify();
const { showErrorDialog } = useErrorDialog();

const { handleSubmit, errors } = useForm({
  validationSchema: toTypedSchema(trackingPrensasSchema)
});

const { value: logisticUnit } = useField('logisticUnit');
const { value: lote } = useField('lote');
const { value: startTime } = useField('startTime');
const { value: endTime } = useField('endTime');
const { value: comments } = useField('comments');
const { value: product } = useField('product');
const { value: team } = useField('team');
const { value: shift } = useField('shift');
const { value: equipment } = useField('equipment');
const { value: rawMaterial } = useField('rawMaterial');
const { value: quantity } = useField('quantity');
const { value: parameters } = useField('parameters', {
  initialValue: []
});


onMounted(() => {
  startTime.value = new Date();
});


const onSubmit = handleSubmit(async (values) => {
  try {
    const payload = {
      logisticUnit: values.logisticUnit,
      lote: values.lote,
      startTime: values.startTime,
      endTime: values.endTime,
      comments: values.comments,
      productId: values.product.id,
      teamId: values.team.id,
      shiftId: values.shift.id,
      equipmentId: values.equipment.id,
      rawMaterialId: values.rawMaterial.id,
      quantity: values.quantity,
      parameters: parameters.value
    };

    await store.create(payload);

    // ✅ Usar helper global
    notifySuccess('Registo criado com sucesso');
  } catch (err) {
    const backendError = err;
    const message = backendError?.message || 'Erro inesperado';
    const detail = backendError?.detail || '';

    // ✅ Opción 1: Toast rápido
    notifyError(detail, message);

    // ✅ Opción 2: Dialog con detalle
    showErrorDialog(backendError);
  }
});
</script>


<template>
  <div class="w-full max-w-7xl mx-auto">
    <div
      class="bg-surface-0 dark:bg-surface-900 p-8 rounded-xl shadow-lg border border-surface-200 dark:border-surface-700">
      <form @submit.prevent="onSubmit">
        <div class="grid grid-cols-1 md:grid-cols-12 gap-6">

          <div class="col-span-6">
            <label for="car" class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">Unidade logística
              (Nro. Carro)</label>
            <InputNumber v-model="logisticUnit" inputId="car" :useGrouping="false" fluid class="w-full"
              placeholder="Ex: 123" />
            <small v-if="errors.logisticUnit" class="text-red-500 mt-1 block">
              {{ errors.logisticUnit }}
            </small>
          </div>

          <div class="col-span-6">
            <label for="car" class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">Quantidade
              peças</label>
            <InputNumber v-model="quantity" inputId="quantity" :useGrouping="false" fluid class="w-full"
              placeholder="Ex: 360" />
            <small v-if="errors.quantity" class="text-red-500 mt-1 block">
              {{ errors.quantity }}
            </small>
          </div>

          <div class="col-span-12 md:col-span-6">
            <label for="startTime" class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">Inicio da
              produção</label>
            <DatePicker v-model="startTime" showIcon showButtonBar showTime fluid class="w-full" />
            <small v-if="errors.startTime" class="text-red-500 mt-1 block">
              {{ errors.startTime }}
            </small>
          </div>

          <div class="col-span-12 md:col-span-6">
            <label for="endTime" class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">Fim da
              produção</label>
            <DatePicker v-model="endTime" showIcon showButtonBar showTime fluid class="w-full" />
          </div>

          <div class="col-span-12 border-t border-surface-100 dark:border-surface-800 my-2"></div>

          <div class="col-span-12 md:col-span-6">
            <ProductSelect v-model="product" class="w-full" />
            <small v-if="errors.product" class="text-red-500 mt-1 block">{{ errors.product }}</small>
          </div>

          <div class="col-span-12 md:col-span-3">
            <RawMaterialSelect v-model="rawMaterial" class="w-full" />
            <small v-if="errors.rawMaterial" class="text-red-500 mt-1 block">{{ errors.rawMaterial }}</small>
          </div>

          <div class="col-span-12 md:col-span-3">
            <label for="lote" class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">Lote MP</label>
            <InputText v-model="lote" inputId="lote" fluid class="w-full" />
            <small v-if="errors.lote" class="text-red-500 mt-1 block">{{ errors.lote }}</small>
          </div>

          <div class="col-span-12 md:col-span-4">
            <TeamsSelect v-model="team" class="w-full" />
            <small v-if="errors.team" class="text-red-500 mt-1 block">{{ errors.team }}</small>
          </div>

          <div class="col-span-12 md:col-span-4">
            <ShiftsSelect v-model="shift" class="w-full" />
            <small v-if="errors.shift" class="text-red-500 mt-1 block">{{ errors.shift }}</small>
          </div>

          <div class="col-span-12 md:col-span-4">
            <EquipmentsSelect v-model="equipment" class="w-full" />
            <small v-if="errors.equipment" class="text-red-500 mt-1 block">{{ errors.equipment }}</small>
          </div>

          <div class="col-span-12 mt-2 md:col-span-7">
            <label for="comments" class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">Comentários /
              Observações</label>
            <Textarea id="comments" v-model="comments" rows="4" class="w-full"
              placeholder="Escreva observações adicionais aqui..." />
          </div>
          
            <div class="col-span-12 mt-2 md:col-span-5">
              <label class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">Parâmetros de
                Produção</label>
              <small v-if="errors.parameters" class="text-red-500 mt-1 block">{{ errors.parameters }}</small>
              <ParametersForm v-model="parameters" />
            </div>
          

          <div class="col-span-12 flex justify-end mt-6">
            <Button type="submit" label="Guardar Registo" icon="pi pi-check" class="w-full md:w-auto px-8" />
          </div>

        </div>
      </form>
      <DialogInfo />
    </div>
  </div>
</template>