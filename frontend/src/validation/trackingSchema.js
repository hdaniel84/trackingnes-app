import { z } from 'zod';

// HELPER ROBUSTO:
const requiredObject = (message) =>
  z.any()
    .refine((val) => val && val.id, {
      message: message
    });

export const trackingSchema = z.object({
  // 1. Números
  logisticUnit: z
    .number({
      required_error: 'Unidade logística é obrigatória',
      invalid_type_error: 'Unidade logística é obrigatória',
      error: 'Unidade logística é obrigatória'
    })
    .min(1, 'Unidade logística deve ser positiva'),

  quantity: z
    .number({
      required_error: 'Quantidade é obrigatória',
      invalid_type_error: 'Quantidade é obrigatória',
      error: 'Quantidade é obrigatória'
    })
    .min(1, 'Quantidade deve ser maior que zero'),

  // 2. Fechas
  startTime: z
    .date({
      required_error: 'Data de início é obrigatória',
      invalid_type_error: 'Data de início inválida'
    }),

  endTime: z
    .date()
    .nullable()
    .optional(),

  comments: z.string().optional().nullable(),

  // 3. Objetos (Selects) - Usando el helper robusto
  product: requiredObject('Produto é obrigatório'),
  team: requiredObject('Equipa é obrigatória'),
  shift: requiredObject('Turno é obrigatório'),
  equipment: requiredObject('Equipamento é obrigatório'),

  auxiliaryEquipmentIds: z
    .array(z.number())
    .optional(),

  rawMaterials: z
    .array(
      z.object({
        rawMaterialId: z.number({
          required_error: 'Matéria prima é obrigatória',
          invalid_type_error: 'Selecione uma matéria prima válida',
          error: 'Selecione uma matéria prima válida'
        }),
        value: z.string().min(1, {
          message: 'O valor/lote é obrigatório'
        })
      })
    )
    .min(1, { message: 'É obrigatório adicionar pelo menos uma matéria prima' }) // CAMBIO AQUÍ
    .refine(
      (items) => {
        // ... validación de duplicados ...
        const ids = items.map((i) => i.rawMaterialId);
        return new Set(ids).size === ids.length;
      },
      { message: 'Não pode repetir a mesma matéria prima' }
    ),

  // 4. Parámetros
  parameters: z
    .array(
      z.object({
        parameterId: z.number({ required_error: 'Selecione um parâmetro' }),

        // Hacemos opcionales todos los campos individuales
        valueString: z.string().optional().nullable(),
        valueNumber: z.number().optional().nullable(),
        valueBool: z.boolean().optional().nullable(),
        valueDate: z.date().optional().nullable().or(z.string().optional().nullable())
      })
        // SUPER REFINE: Validamos que AL MENOS UNO tenga valor
        .refine((data) => {
          // Si es booleano, true o false son valores validos.
          if (data.valueBool !== undefined && data.valueBool !== null) return true;
          // Si es numero
          if (data.valueNumber !== undefined && data.valueNumber !== null) return true;
          // Si es fecha
          if (data.valueDate) return true;
          // Si es string
          if (data.valueString && data.valueString.trim().length > 0) return true;

          return false;
        }, {
          message: "O valor é obrigatório",
          path: ['parameterId'] // Mostramos el error cerca del select si falla
        })
    )
    .optional()
    // IMPORTANTE: Sin .default([]) para no chocar con VeeValidate
    .refine(
      (params) => {
        if (!params || params.length === 0) return true;
        const ids = params.map((p) => p.parameterId);
        return new Set(ids).size === ids.length;
      },
      {
        message: 'Não pode haver parâmetros duplicados'
      }
    )
})
  // 5. Validación Cruzada (Fechas)
  .refine((data) => {
    // Verificamos que ambos existan antes de comparar
    if (data.startTime && data.endTime) {
      return data.endTime > data.startTime;
    }
    return true;
  }, {
    message: 'A data de fim deve ser posterior à data de início',
    path: ['endTime'],
  });