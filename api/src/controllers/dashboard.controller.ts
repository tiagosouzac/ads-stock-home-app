import type { Request, Response } from 'express';
import type { Product } from '@prisma/client';
import { prisma } from '../lib/prisma.js';
import { daysUntil, today, serializeProduct, statusItem } from '../lib/status.js';

type ProductWithCategory = Product & { category: { id: string; name: string } };

async function loadContext(userId: string) {
  const user = await prisma.user.findUnique({ where: { id: userId }, select: { alertDays: true } });
  const alertDays = user?.alertDays ?? 7;
  const products = (await prisma.product.findMany({
    where: { userId },
    include: { category: { select: { id: true, name: true } } },
    orderBy: { name: 'asc' },
  })) as ProductWithCategory[];
  return { alertDays, products };
}

/**
 * Dashboard summary (app Home → resumo()):
 *  total item count, low-stock items and items expiring within the window.
 */
export async function getSummary(req: Request, res: Response) {
  const { alertDays, products } = await loadContext(req.userId!);
  const base = today();

  const low = products.filter((p) => statusItem(p, alertDays, base).type === 'low');
  const expiring = products.filter((p) => {
    const d = daysUntil(p.expiresAt, base);
    return d !== null && d >= 0 && d <= alertDays;
  });
  const expired = products.filter((p) => statusItem(p, alertDays, base).type === 'expired');

  res.json({
    total: products.length,
    counters: {
      low: low.length,
      expiring: expiring.length,
      expired: expired.length,
    },
    low: low.map((p) => serializeProduct(p, alertDays)),
    expiring: expiring.map((p) => serializeProduct(p, alertDays)),
  });
}

/**
 * Alerts screen: every item that needs attention
 * (low, expiring or expired), grouped by type.
 */
export async function getAlerts(req: Request, res: Response) {
  const { alertDays, products } = await loadContext(req.userId!);

  const serialized = products
    .map((p) => serializeProduct(p, alertDays))
    .filter((p) => p.status.type !== 'ok');

  res.json({
    expired: serialized.filter((p) => p.status.type === 'expired'),
    low: serialized.filter((p) => p.status.type === 'low'),
    expiring: serialized.filter((p) => p.status.type === 'expiring'),
    total: serialized.length,
  });
}
