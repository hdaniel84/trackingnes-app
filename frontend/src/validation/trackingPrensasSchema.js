import { z } from 'zod';

export const trackingPrensasSchema = z.object({
  logisticUnit: z
    .any()
    .refine(val => val !== null && val !== undefined, {
      error: 'Unidade logística é obrigatória'
    }),

  quantity: z
    .any()
    .refine(val => val !== null && val !== undefined, {
      error: 'Quantidade é obrigatória'
    }),

  lote: z
    .string({
      error: (val) => val.input === undefined ? "Lote é obrigatório" : "Lote não é valido."
    })
    .min(1, {
      error: 'Lote é obrigatório'
    }),

  startTime: z
    .any()
    .refine(val => val instanceof Date, {
      error: 'Data de início é obrigatória'
    }),

  endTime: z
    .any()
    .optional()
    .refine(val => !val || val instanceof Date, {
      error: 'Data de fim inválida'
    }),

  comments: z.string().optional().nullable(),

  product: z
    .any()
    .refine(val => val && val.id, {
      error: 'Produto é obrigatório'
    }),

  team: z
    .any()
    .refine(val => val && val.id, {
      error: 'Equipa é obrigatória'
    }),

  shift: z
    .any()
    .refine(val => val && val.id, {
      error: 'Turno é obrigatório'
    }),

  equipment: z
    .any()
    .refine(val => val && val.id, {
      error: 'Equipamento é obrigatório'
    }),

  rawMaterial: z
    .any()
    .refine(val => val && val.id, {
      error: 'Matéria prima é obrigatória'
    }),

  parameters: z
    .array(
      z.object({
        parameterId: z.number({
          required_error: 'Parâmetro é obrigatório',
          error: 'Tipo de parâmetro é obrigatório'
        }),
        value: z.string().min(1, {
          error: 'Valor do parâmetro é obrigatório'
        })
      })
    )
    .optional()
    .refine(
      params => {
        if (!params) return true; // si es opcional y no hay parámetros, pasa
        const ids = params.map(p => p.parameterId);
        return new Set(ids).size === ids.length; // no duplicados
      },
      {
        error: 'Não pode haver parâmetros duplicados'
      }
    )
})
  // VALIDACIÓN CRUZADA: startTime debe ser menor que endTime
  .refine((data) => {
    const start = data.startTime;
    const end = data.endTime;

    if (start instanceof Date && end instanceof Date) {
      return start < end;
    }

    return true;
  }, {
    error: 'A data de fim deve ser posterior à data de início.',
    path: ['endTime'],
  });