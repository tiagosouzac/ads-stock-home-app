import type { Request, Response } from 'express';
import type { Prisma, Product } from '@prisma/client';
import { prisma } from '../lib/prisma.js';
import { notFound } from '../lib/errors.js';
import { serializeProduct } from '../lib/status.js';

type ProductWithCategory = Product & { category: { id: string; name: string } };

/** Loads the logged-in user's alertDays (default 7). */
async function getAlertDays(userId: string): Promise<number> {
  const u = await prisma.user.findUnique({ where: { id: userId }, select: { alertDays: true } });
  return u?.alertDays ?? 7;
}

function parseExpiresAt(v: string | null | undefined): Date | null | undefined {
  if (v === undefined) return undefined;
  if (v === null) return null;
  // "YYYY-MM-DD" → UTC Date (column is @db.Date)
  return new Date(`${v}T00:00:00.000Z`);
}

export async function listProducts(req: Request, res: Response) {
  const userId = req.userId!;
  const { search, category, status, sort } = req.query as {
    search?: string;
    category?: string;
    status?: 'ok' | 'low' | 'expiring' | 'expired';
    sort?: 'name' | 'expiresAt' | 'lastUpdated' | 'quantity';
  };

  const where: Prisma.ProductWhereInput = { userId };
  if (category) where.categoryId = category;
  if (search) where.name = { contains: search, mode: 'insensitive' };

  const orderBy: Prisma.ProductOrderByWithRelationInput =
    sort === 'expiresAt'
      ? { expiresAt: 'asc' }
      : sort === 'lastUpdated'
        ? { lastUpdated: 'desc' }
        : sort === 'quantity'
          ? { quantity: 'asc' }
          : { name: 'asc' };

  const alertDays = await getAlertDays(userId);
  const products = (await prisma.product.findMany({
    where,
    orderBy,
    include: { category: { select: { id: true, name: true } } },
  })) as ProductWithCategory[];

  let serialized = products.map((p) => serializeProduct(p, alertDays));
  // status is computed in memory → filter after serializing
  if (status) serialized = serialized.filter((p) => p.status.type === status);

  res.json(serialized);
}

export async function getProduct(req: Request, res: Response) {
  const userId = req.userId!;
  const id = Number(req.params.id);
  const alertDays = await getAlertDays(userId);

  const product = (await prisma.product.findFirst({
    where: { id, userId },
    include: { category: { select: { id: true, name: true } } },
  })) as ProductWithCategory | null;
  if (!product) throw notFound('Produto não encontrado.');

  res.json(serializeProduct(product, alertDays));
}

export async function createProduct(req: Request, res: Response) {
  const userId = req.userId!;
  const { name, categoryId, quantity, minQuantity, unit, expiresAt } = req.body;
  const alertDays = await getAlertDays(userId);

  const product = (await prisma.product.create({
    data: {
      name,
      categoryId,
      quantity,
      minQuantity,
      unit,
      expiresAt: parseExpiresAt(expiresAt) ?? null,
      lastUpdated: new Date(),
      userId,
    },
    include: { category: { select: { id: true, name: true } } },
  })) as ProductWithCategory;

  res.status(201).json(serializeProduct(product, alertDays));
}

export async function updateProduct(req: Request, res: Response) {
  const userId = req.userId!;
  const id = Number(req.params.id);
  const alertDays = await getAlertDays(userId);

  // make sure the product belongs to the user before updating
  const existing = await prisma.product.findFirst({ where: { id, userId } });
  if (!existing) throw notFound('Produto não encontrado.');

  const { name, categoryId, quantity, minQuantity, unit, expiresAt } = req.body;
  const expiresAtParsed = parseExpiresAt(expiresAt);

  const product = (await prisma.product.update({
    where: { id },
    data: {
      ...(name !== undefined && { name }),
      ...(categoryId !== undefined && { categoryId }),
      ...(quantity !== undefined && { quantity }),
      ...(minQuantity !== undefined && { minQuantity }),
      ...(unit !== undefined && { unit }),
      ...(expiresAtParsed !== undefined && { expiresAt: expiresAtParsed }),
      lastUpdated: new Date(),
    },
    include: { category: { select: { id: true, name: true } } },
  })) as ProductWithCategory;

  res.json(serializeProduct(product, alertDays));
}

/** Adjusts the quantity (app steppers): by delta or absolute value. */
export async function adjustQuantity(req: Request, res: Response) {
  const userId = req.userId!;
  const id = Number(req.params.id);
  const { delta, quantity } = req.body as { delta?: number; quantity?: number };
  const alertDays = await getAlertDays(userId);

  const existing = await prisma.product.findFirst({ where: { id, userId } });
  if (!existing) throw notFound('Produto não encontrado.');

  const next = quantity !== undefined ? quantity : Math.max(0, existing.quantity + (delta ?? 0));

  const product = (await prisma.product.update({
    where: { id },
    data: { quantity: next, lastUpdated: new Date() },
    include: { category: { select: { id: true, name: true } } },
  })) as ProductWithCategory;

  res.json(serializeProduct(product, alertDays));
}

export async function deleteProduct(req: Request, res: Response) {
  const userId = req.userId!;
  const id = Number(req.params.id);

  const existing = await prisma.product.findFirst({ where: { id, userId } });
  if (!existing) throw notFound('Produto não encontrado.');

  await prisma.product.delete({ where: { id } });
  res.status(204).send();
}
