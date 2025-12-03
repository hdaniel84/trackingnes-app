import { z } from 'zod';

export const trackingPrensasSchema = z.object({
  logisticUnit: z
    .any()
    .refine(val => val !== null && val !== undefined, {
      message: 'Unidade logística é obrigatória'
    }),

  quantity: z
    .any()
    .refine(val => val !== null && val !== undefined, {
      message: 'Quantidade é obrigatória'
    }),

  lote: z
    .string()
    .min(1, { message: 'Lote é obrigatório' }),

  startTime: z
    .any()
    .refine(val => val instanceof Date, {
      message: 'Data de início é obrigatória'
    }),

  endTime: z
    .any()
    .optional()
    .refine(val => !val || val instanceof Date, {
      message: 'Data de fim inválida'
    }),

  comments: z.string().optional(),

  product: z
    .any()
    .refine(val => val && val.id, {
      message: 'Produto é obrigatório'
    }),

  team: z
    .any()
    .refine(val => val && val.id, {
      message: 'Equipa é obrigatória'
    }),

  shift: z
    .any()
    .refine(val => val && val.id, {
      message: 'Turno é obrigatório'
    }),

  equipment: z
    .any()
    .refine(val => val && val.id, {
      message: 'Equipamento é obrigatório'
    }),

  rawMaterial: z
    .any()
    .refine(val => val && val.id, {
      message: 'Matéria prima é obrigatória'
    })
});
