import { z } from 'zod';

// --- 1. HELPERS REUTILIZABLES ---

const requiredObject = (message) =>
  z.any().refine((val) => val && val.id, { message: message });

// Función de validación de fechas para no repetirla
const dateValidation = (data) => {
  if (data.startTime && data.endTime) {
    return data.endTime > data.startTime;
  }
  return true;
};

const dateValidationOptions = {
  message: 'A data de fim deve ser posterior à data de início',
  path: ['endTime'],
};

// --- 2. BASE OBJECT (La estructura común) ---
// Nota: No lo exportamos directamente porque le falta la validación de fechas
const trackingBaseObject = z.object({
  // A. Números
  logisticUnit: z
    .number({
      required_error: 'Unidade logística é obrigatória',
      invalid_type_error: 'Unidade logística é obrigatória',
    })
    .min(1, 'Unidade logística deve ser positiva'),

  trackingSourceId: z.number({
    required_error: 'É obrigatório selecionar lote da fase anterior',
    invalid_type_error: 'Origem inválida',
  }),

  quantity: z
    .number({
      required_error: 'Quantidade é obrigatória',
      invalid_type_error: 'Quantidade é obrigatória',
    })
    .min(1, 'Quantidade deve ser maior que zero'),

  // B. Fechas
  startTime: z.date({
    required_error: 'Data de início é obrigatória',
    invalid_type_error: 'Data de início inválida',
  }),

  endTime: z.date().nullable().optional(),

  comments: z.string().optional().nullable(),

  // C. Objetos (Selects)
  product: requiredObject('Produto é obrigatório'),
  team: requiredObject('Equipa é obrigatória'),
  shift: requiredObject('Turno é obrigatório'),
  equipment: requiredObject('Equipamento é obrigatório'),

  auxiliaryEquipmentIds: z.array(z.number()).optional(),

  // D. Arrays Complejos (Raw Materials)
  rawMaterials: z
    .array(
      z.object({
        rawMaterialId: z.number({
          required_error: 'Matéria prima é obrigatória',
          invalid_type_error: 'Selecione uma matéria prima válida',
        }),
        value: z.string().min(1, { message: 'O valor/lote é obrigatório' }),
      })
    )
    .min(1, { message: 'É obrigatório adicionar pelo menos uma matéria prima' })
    .refine(
      (items) => {
        const ids = items.map((i) => i.rawMaterialId);
        return new Set(ids).size === ids.length;
      },
      { message: 'Não pode repetir a mesma matéria prima' }
    ),

  // E. Parámetros
  parameters: z
    .array(
      z.object({
        parameterId: z.number({ required_error: 'Selecione um parâmetro', error: 'Selecione um parâmetro' }),
        valueString: z.string().optional().nullable(),
        valueNumber: z.number().optional().nullable(),
        valueBool: z.boolean().optional().nullable(),
        valueDate: z
          .date()
          .optional()
          .nullable()
          .or(z.string().optional().nullable()),
      })
        .refine(
          (data) => {
            if (data.valueBool !== undefined && data.valueBool !== null) return true;
            if (data.valueNumber !== undefined && data.valueNumber !== null) return true;
            if (data.valueDate) return true;
            if (data.valueString && data.valueString.trim().length > 0) return true;
            return false;
          },
          {
            message: 'O valor é obrigatório',
            error: 'O valor é obrigatório',
            path: ['parameterId'],
          }
        )
    )
    .optional()
    .refine(
      (params) => {
        if (!params || params.length === 0) return true;
        const ids = params.map((p) => p.parameterId);
        return new Set(ids).size === ids.length;
      },
      { message: 'Não pode haver parâmetros duplicados' }
    ),
});

// --- 3. ESQUEMAS FINALES (Exportables) ---

/**
 * Esquema Genérico (Para Prensas, etc.)
 * Solo aplica la validación de fechas sobre la base.
 */
export const trackingSchema = trackingBaseObject.refine(
  dateValidation,
  dateValidationOptions
);

/****************************************************
 * Esquemas (Heredados)
 * 1. Extiende la base agregando trackingSourceId.
 * 2. Aplica la validación de fechas al final.
 * Usado para Vidragem, Forno
 */

export const trackingPrensasFormSchema = trackingBaseObject
  .omit({
    trackingSourceId: true, // AQUÍ ELIMINAMOS SU VALIDACIÓN
  })
  .refine(dateValidation, dateValidationOptions);

export const trackingVidragemFormSchema = trackingBaseObject
  .refine(dateValidation, dateValidationOptions);

export const trackingFornoFormSchema = trackingBaseObject
  .omit({
    rawMaterials: true,
  })
  .refine(dateValidation, dateValidationOptions);

export const trackingEscolhaFormSchema = trackingBaseObject
  .omit({
    rawMaterials: true,
  })
  .refine(dateValidation, dateValidationOptions);

export const trackingEmbalagemFormSchema = trackingBaseObject
  .refine(dateValidation, dateValidationOptions);