import { z } from 'zod';

// HELPER ROBUSTO:
// Usamos z.any() en lugar de z.object() para evitar que Zod lance 
// un error de tipo crítico cuando el valor es null inicial.
// Luego usamos .refine para asegurar que tenga datos.
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
        parameterId: z.number({
          required_error: 'Tipo de parâmetro é obrigatório',
          invalid_type_error: 'Selecione um parâmetro válido'
        }),
        valueString: z.string().min(1, {
          message: 'Valor do parâmetro é obrigatório'
        })
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