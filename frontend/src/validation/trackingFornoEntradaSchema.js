import { z } from 'zod';

export const trackingFornoEntradaSchema = z.object({
  logisticUnitInId: z.coerce
    .number({
      required_error: 'Carro entrada é obrigatório',
      invalid_type_error: 'Apenas números são permitidos',
      message: 'Carro entrada inválido'
    })
    .min(1, { message: 'Carro entrada é obrigatório' })
    .refine(val => !isNaN(val), { 
      message: 'Carro entrada inválido'
    }),

  wagonId: z
    .any()
    .refine(val => val !== null && val !== undefined, {
      error: 'Carro saída é obrigatória'
    }),

  quantity: z
    .any()
    .refine(val => val !== null && val !== undefined, {
      error: 'Quantidade é obrigatória'
    }),


  startTime: z
    .any()
    .refine(val => val instanceof Date, {
      error: 'Data de início é obrigatória'
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
});