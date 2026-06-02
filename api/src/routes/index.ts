import { Router } from 'express';
import { asyncHandler } from '../lib/asyncHandler.js';
import { authRequired } from '../middleware/auth.js';
import { validate } from '../middleware/validate.js';
import {
  adjustQtySchema,
  createProductSchema,
  idParamSchema,
  listProductsQuerySchema,
  loginSchema,
  registerSchema,
  updateMeSchema,
  updateProductSchema,
} from '../schemas.js';
import { login, register } from '../controllers/auth.controller.js';
import { getMe, updateMe } from '../controllers/user.controller.js';
import { listCategories } from '../controllers/category.controller.js';
import {
  adjustQuantity,
  createProduct,
  deleteProduct,
  getProduct,
  listProducts,
  updateProduct,
} from '../controllers/product.controller.js';
import { getAlerts, getSummary } from '../controllers/dashboard.controller.js';

export const router = Router();

// ── Auth ──────────────────────────────────────────────────────
router.post('/auth/register', validate(registerSchema), asyncHandler(register));
router.post('/auth/login', validate(loginSchema), asyncHandler(login));

// ── Profile ───────────────────────────────────────────────────
router.get('/me', authRequired, asyncHandler(getMe));
router.patch('/me', authRequired, validate(updateMeSchema), asyncHandler(updateMe));

// ── Categories ────────────────────────────────────────────────
router.get('/categories', authRequired, asyncHandler(listCategories));

// ── Dashboard / Alerts ────────────────────────────────────────
router.get('/dashboard/summary', authRequired, asyncHandler(getSummary));
router.get('/alerts', authRequired, asyncHandler(getAlerts));

// ── Products ──────────────────────────────────────────────────
router.get('/products', authRequired, validate(listProductsQuerySchema, 'query'), asyncHandler(listProducts));
router.post('/products', authRequired, validate(createProductSchema), asyncHandler(createProduct));
router.get('/products/:id', authRequired, validate(idParamSchema, 'params'), asyncHandler(getProduct));
router.patch(
  '/products/:id',
  authRequired,
  validate(idParamSchema, 'params'),
  validate(updateProductSchema),
  asyncHandler(updateProduct),
);
router.patch(
  '/products/:id/quantity',
  authRequired,
  validate(idParamSchema, 'params'),
  validate(adjustQtySchema),
  asyncHandler(adjustQuantity),
);
router.delete('/products/:id', authRequired, validate(idParamSchema, 'params'), asyncHandler(deleteProduct));
