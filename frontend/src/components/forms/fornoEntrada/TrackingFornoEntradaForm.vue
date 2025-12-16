<script setup>

import { useTrackingFornoEntradaStore } from '@/stores/trackingFornoEntradaStore';
import { useForm, useField } from 'vee-validate';
import { toTypedSchema } from '@vee-validate/zod';
import { trackingFornoEntradaSchema } from '@/validation/trackingFornoEntradaSchema';
import { onMounted, defineProps, watch, ref } from 'vue';

// ✅ Importar helpers
import { useNotify } from '@/layout/composables/notify';
import { useErrorDialog } from '@/layout/composables/errorDialog';

// PrimeVue
import InputNumber from 'primevue/inputnumber';
import DatePicker from 'primevue/datepicker';
import Textarea from 'primevue/textarea';
import Button from 'primevue/button';

// Dropdowns
import ProductSelect from '@/components/forms/global/ProductSelect.vue';
import TeamsSelect from '@/components/forms/global/TeamsSelect.vue';
import ShiftsSelect from '@/components/forms/global/ShiftsSelect.vue';
import ParametersForm from '@/components/forms/global/ParametersForm.vue';

const filterSectionVar = ref('vidragem');
const props = defineProps({
  item: Object, // registro a editar
  mode: { type: String, default: 'create' } // 'create' o 'edit'
});

const store = useTrackingFornoEntradaStore();

const { notifySuccess } = useNotify();
const { showErrorDialog } = useErrorDialog();

const { handleSubmit, errors, setValues } = useForm({
  validationSchema: toTypedSchema(trackingFornoEntradaSchema)
});

const { value: logisticUnitInId } = useField('logisticUnitInId');
const { value: wagonId } = useField('wagonId');
const { value: startTime } = useField('startTime');
const { value: comments } = useField('comments');
const { value: product } = useField('product');
const { value: team } = useField('team');
const { value: shift } = useField('shift');
const { value: quantity } = useField('quantity');
const { value: parameters } = useField('parameters', {
  initialValue: []
});

watch(() => props.item, (val) => {
  if (val) {
    setValues({
      logisticUnitInId: val.logisticUnitInId ?? null,
      wagonId: val.wagonId,
      startTime: new Date(val.startTime),
      comments: val.comments,
      product: { id: val.productId, description: val.productDescription },
      team: { id: val.teamId, description: val.teamDescription },
      shift: { id: val.shiftId, description: val.shiftDescription },
      quantity: val.quantity,
      parameters: val.parameters
    });
  }
}, { immediate: true });

onMounted(() => {
  if (props.mode === 'create') {
    startTime.value = new Date();
  }
});


const onSubmit = handleSubmit(async (values) => {

  try {
    const payload = {
      logisticUnitInId: Number(values.logisticUnitInId),
      wagonId: Number(values.wagonId),
      lote: values.lote,
      startTime: values.startTime,
      comments: values.comments,
      productId: values.product.id,
      teamId: values.team.id,
      shiftId: values.shift.id,
      quantity: values.quantity,
      parameters: parameters.value
    };

    if (props.mode === 'create') {
      await store.create(payload);
      notifySuccess('Registo criado com sucesso');
    } else {
      await store.update(props.item.id, payload);
      notifySuccess('Registo atualizado com sucesso');
    }

  } catch (err) {
    showErrorDialog(err);
  }
});
</script>


<template>
  <div class="w-full max-w-7xl mx-auto">
    <div
      class="bg-surface-0 dark:bg-surface-900 p-8 rounded-xl shadow-lg border border-surface-200 dark:border-surface-700">
      <form @submit.prevent="onSubmit">
        <div class="grid grid-cols-1 md:grid-cols-12 gap-6">

          <div class="col-span-12 md:col-span-6">
            <ProductSelect v-model="product" class="w-full" />
            <small v-if="errors.product" class="text-red-500 mt-1 block">{{ errors.product }}</small>
          </div>

          <div class="col-span-12 md:col-span-6">
            <PrensasCarSelect inputId="logisticUnitinSelect" v-model="logisticUnitInId"
              :filterProduct="product ? product.shapeId : null" class="w-full" />
            <small v-if="errors.logisticUnitInId" class="text-red-500 mt-1 block">
              {{ errors.logisticUnitInId }}
            </small>
          </div>

          <div class="col-span-12 md:col-span-4">
            <label for="car2" class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">Nro. Vagona</label>
            <InputNumber v-model="wagonId" inputId="car2" :useGrouping="false" fluid class="w-full"
              placeholder="Ex: 456" />
            <small v-if="errors.wagonId" class="text-red-500 mt-1 block">
              {{ errors.wagonId }}
            </small>
          </div>

          <div class="col-span-12 md:col-span-4">
            <label for="quantity" class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">Quantidade
              peças</label>
            <InputNumber v-model="quantity" inputId="quantity" :useGrouping="false" fluid class="w-full"
              placeholder="Ex: 360" />
            <small v-if="errors.quantity" class="text-red-500 mt-1 block">
              {{ errors.quantity }}
            </small>
          </div>

          <div class="col-span-12 md:col-span-4">
            <label for="startTimePicker" class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">Hora enforna</label>
            <DatePicker inputId="startTimePicker" v-model="startTime" showIcon showButtonBar showTime fluid
              class="w-full" />
            <small v-if="errors.startTime" class="text-red-500 mt-1 block">
              {{ errors.startTime }}
            </small>
          </div>

          <div class="col-span-12 md:col-span-4">
            <TeamsSelect v-model="team" class="w-full" :filterSection="filterSectionVar"/>
            <small v-if="errors.team" class="text-red-500 mt-1 block">{{ errors.team }}</small>
          </div>

          <div class="col-span-12 md:col-span-4">
            <ShiftsSelect v-model="shift" class="w-full" />
            <small v-if="errors.shift" class="text-red-500 mt-1 block">{{ errors.shift }}</small>
          </div>

          <div class="col-span-12 border-t border-surface-100 dark:border-surface-800 my-2"></div>

          <div class="col-span-12 mt-2 md:col-span-7">
            <label for="comments" class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">Comentários /
              Observações</label>
            <Textarea id="comments" v-model="comments" rows="4" class="w-full"
              placeholder="Escreva observações adicionais aqui..." />
          </div>

          <div class="col-span-12 mt-2 md:col-span-5">
            <div class="block font-semibold text-surface-700 dark:text-surface-200 mb-2">
              Parâmetros de Produção</div>
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