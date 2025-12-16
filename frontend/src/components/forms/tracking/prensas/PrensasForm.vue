<script setup>
import { useTrackingStore } from '@/stores/trackingStore';
import { useForm, useField } from 'vee-validate';
import { toTypedSchema } from '@vee-validate/zod';
import { trackingSchema } from '@/validation/trackingSchema';
import { onMounted, defineProps, watch, ref } from 'vue';

// Helpers & UI
import { useNotify } from '@/layout/composables/notify';
import { useErrorDialog } from '@/layout/composables/errorDialog';
import InputNumber from 'primevue/inputnumber';
import DatePicker from 'primevue/datepicker';
import Textarea from 'primevue/textarea';
import Button from 'primevue/button';

// Componentes Personalizados
import ProductSelect from '@/components/forms/global/ProductSelect.vue';
import TeamsSelect from '@/components/forms/global/TeamsSelect.vue';
import ShiftsSelect from '@/components/forms/global/ShiftsSelect.vue';
import EquipmentsSelect from '@/components/forms/global/EquipmentsSelect.vue';
import ParametersForm from '@/components/forms/global/ParametersForm.vue';

const props = defineProps({
  item: Object,
  mode: { type: String, default: 'create' }
});

const store = useTrackingStore();
const filterSectionVar = ref('prensas');
const { notifySuccess, notifyError } = useNotify();
const { showErrorDialog } = useErrorDialog();

// Vee-Validate
const { handleSubmit, errors, setValues } = useForm({
  validationSchema: toTypedSchema(trackingSchema)
});

// Campos
const { value: logisticUnit } = useField('logisticUnit');
const { value: startTime } = useField('startTime');
const { value: endTime } = useField('endTime');
const { value: comments } = useField('comments');
const { value: product } = useField('product');   // UI guarda el Objeto completo
const { value: team } = useField('team');         // UI guarda el Objeto completo
const { value: shift } = useField('shift');       // UI guarda el Objeto completo
const { value: equipment } = useField('equipment'); // UI guarda el Objeto completo
const { value: quantity } = useField('quantity');
const { value: parameters } = useField('parameters', { initialValue: [] });

// WATCH: Cargar datos al Editar (Mapping ResponseDTO -> Form State)
watch(() => props.item, (val) => {
  if (val) {
    setValues({
      logisticUnit: val.logisticUnit,
      startTime: new Date(val.startTime),
      endTime: val.endTime ? new Date(val.endTime) : null,
      comments: val.comments,
      quantity: val.quantity,

      // Como tu ResponseDTO devuelve objetos completos, los asignamos directo a los selects
      product: val.product || null,
      team: val.team || null,
      shift: val.shift || null,
      equipment: val.equipment || null,

      // Mapeamos los parámetros para asegurar estructura visual
      parameters: (val.parameters || []).map(p => ({
        id: p.id,
        parameterId: p.parameterId,
        valueString: p.valueString || '' 
      }))
    });
  }
}, { immediate: true });

onMounted(() => {
  if (props.mode === 'create') {
    startTime.value = new Date();
  }
});

// ONSUBMIT: Transformar Datos (Form State -> RequestDTO)
const onSubmit = handleSubmit(async (values) => {
  try {
    // CORRECCIÓN: Usamos (parameters.value || []) para evitar el error si es null
    const parametersPayload = (parameters.value || []).map(p => {
      const paramDto = {
        parameterId: p.parameterId,
        valueString: p.valueString
      };
      if (p.id) paramDto.id = p.id;
      return paramDto;
    });

    // 2. Construimos el Payload con IDs (Flat DTO)
    const payload = {
      logisticUnit: values.logisticUnit,
      startTime: values.startTime,
      endTime: values.endTime,
      comments: values.comments,
      quantity: values.quantity,

      // AQUÍ LA CLAVE: Extraemos solo los IDs de los objetos seleccionados
      productId: values.product?.id,
      teamId: values.team?.id,
      shiftId: values.shift?.id,
      equipmentId: values.equipment?.id,
      phaseId: 1, // Hardcodeado o dinámico según tu lógica

      // Agregamos los parámetros transformados
      parameters: parametersPayload
    };

    // 3. Enviamos al Store (Store llama al API)
    if (props.mode === 'create') {
      await store.create(payload);
      notifySuccess('Registo criado com sucesso');
    } else {
      await store.update(props.item.id, payload);
      notifySuccess('Registo atualizado com sucesso');
    }

    // Opcional: Cerrar diálogo o limpiar form
  } catch (err) {
    const message = err?.response?.data?.message || err?.message || 'Erro inesperado';
    notifyError('Erro', message);
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
            <EquipmentsSelect v-model="equipment" class="w-full" :filterSection="filterSectionVar" />
            <small v-if="errors.equipment" class="text-red-500 mt-1 block">{{ errors.equipment }}</small>
          </div>

          <div class="col-span-12 md:col-span-6">
            <ProductSelect v-model="product" class="w-full" />
            <small v-if="errors.product" class="text-red-500 mt-1 block">{{ errors.product }}</small>
          </div>

          <div class="col-span-12 md:col-span-6">
            <label class="block font-semibold mb-2">Unidade logística</label>
            <InputNumber v-model="logisticUnit" :useGrouping="false" fluid class="w-full" placeholder="Ex: 123" />
            <small v-if="errors.logisticUnit" class="text-red-500 mt-1 block">{{ errors.logisticUnit }}</small>
          </div>

          <div class="col-span-12 md:col-span-6">
            <label class="block font-semibold mb-2">Quantidade peças</label>
            <InputNumber v-model="quantity" :useGrouping="false" fluid class="w-full" placeholder="Ex: 360" />
            <small v-if="errors.quantity" class="text-red-500 mt-1 block">{{ errors.quantity }}</small>
          </div>

          <div class="col-span-12 md:col-span-6">
            <label class="block font-semibold mb-2">Inicio</label>
            <DatePicker v-model="startTime" showIcon showTime fluid class="w-full" />
            <small v-if="errors.startTime" class="text-red-500 mt-1 block">{{ errors.startTime }}</small>
          </div>

          <div class="col-span-12 md:col-span-6">
            <label class="block font-semibold mb-2">Fim</label>
            <DatePicker v-model="endTime" showIcon showTime fluid class="w-full" />
            <small v-if="errors.endTime" class="text-red-500 mt-1 block">{{ errors.endTime }}</small>
          </div>

          <div class="col-span-12 border-t border-surface-200 my-2"></div>

          <div class="col-span-12 md:col-span-3">
            <TeamsSelect v-model="team" :filterSection="filterSectionVar" class="w-full" />
            <small v-if="errors.team" class="text-red-500 mt-1 block">{{ errors.team }}</small>
          </div>

          <div class="col-span-12 md:col-span-3">
            <ShiftsSelect v-model="shift" class="w-full" />
            <small v-if="errors.shift" class="text-red-500 mt-1 block">{{ errors.shift }}</small>
          </div>

          <div class="col-span-12 mt-2 md:col-span-6">
            <label class="block font-semibold mb-2">Observações</label>
            <Textarea v-model="comments" rows="4" class="w-full" />
          </div>

          <div class="col-span-12 mt-2 md:col-span-6">
            <div class="block font-semibold mb-2">Parâmetros de Produção</div>
            <small v-if="errors.parameters" class="text-red-500 mt-1 block">{{ errors.parameters }}</small>
            <ParametersForm v-model="parameters" />
          </div>

          <div class="col-span-12 flex justify-end mt-6">
            <Button type="submit" label="Guardar Registo" icon="pi pi-check" class="w-full md:w-auto px-8" />
          </div>

        </div>
      </form>
    </div>
  </div>
</template>