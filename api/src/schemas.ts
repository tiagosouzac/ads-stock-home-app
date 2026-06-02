import { z } from 'zod';

// Field names are English; validation messages stay in pt-BR (user-facing).

// ── Auth ──────────────────────────────────────────────────────
export const registerSchema = z.object({
  name: z.string().trim().min(2, 'Informe o nome completo.'),
  email: z.string().trim().toLowerCase().email('E-mail inválido.'),
  password: z.string().min(6, 'Use ao menos 6 caracteres.'),
  alertDays: z.coerce.number().int().min(1).max(90).optional(),
});

export const loginSchema = z.object({
  email: z.string().trim().toLowerCase().email('E-mail inválido.'),
  password: z.string().min(1, 'Informe a senha.'),
});

// ── Profile ───────────────────────────────────────────────────
export const updateMeSchema = z
  .object({
    name: z.string().trim().min(2).optional(),
    alertDays: z.coerce.number().int().min(1).max(90).optional(),
  })
  .refine((d) => Object.keys(d).length > 0, { message: 'Nada para atualizar.' });

// ── Products ──────────────────────────────────────────────────
// expiresAt accepts "YYYY-MM-DD" or null
const expiresAtSchema = z
  .string()
  .regex(/^\d{4}-\d{2}-\d{2}$/, 'Use o formato YYYY-MM-DD.')
  .nullable()
  .optional();

export const createProductSchema = z.object({
  name: z.string().trim().min(1, 'Informe o nome do produto.'),
  categoryId: z.string().trim().min(1, 'Informe a categoria.'),
  quantity: z.coerce.number().int().min(0).default(0),
  minQuantity: z.coerce.number().int().min(0).default(1),
  unit: z.string().trim().min(1).default('un'),
  expiresAt: expiresAtSchema,
});

export const updateProductSchema = z
  .object({
    name: z.string().trim().min(1).optional(),
    categoryId: z.string().trim().min(1).optional(),
    quantity: z.coerce.number().int().min(0).optional(),
    minQuantity: z.coerce.number().int().min(0).optional(),
    unit: z.string().trim().min(1).optional(),
    expiresAt: expiresAtSchema,
  })
  .refine((d) => Object.keys(d).length > 0, { message: 'Nada para atualizar.' });

// Quick quantity adjustment (app steppers: +1 / -1 or absolute value)
export const adjustQtySchema = z
  .object({
    delta: z.coerce.number().int().optional(),
    quantity: z.coerce.number().int().min(0).optional(),
  })
  .refine((d) => d.delta !== undefined || d.quantity !== undefined, {
    message: 'Informe "delta" ou "quantity".',
  });

// ── List filters ──────────────────────────────────────────────
export const listProductsQuerySchema = z.object({
  search: z.string().trim().optional(),
  category: z.string().trim().optional(),
  status: z.enum(['ok', 'low', 'expiring', 'expired']).optional(),
  sort: z.enum(['name', 'expiresAt', 'lastUpdated', 'quantity']).default('name'),
});

export const idParamSchema = z.object({
  id: z.coerce.number().int().positive('ID inválido.'),
});
