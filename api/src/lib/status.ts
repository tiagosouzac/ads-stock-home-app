import type { Product } from '@prisma/client';

/**
 * Replicates the app's status helpers (data/MockData.kt → statusItem).
 *
 * Rules (in order):
 *  - expired:  expiry date is in the past (days < 0)
 *  - low:      quantity below the minimum OR zero
 *  - expiring: expires within the `alertDays` window
 *  - ok:       otherwise
 *
 * Status labels stay in pt-BR because they are display text for the app's UI.
 */
export type StatusType = 'ok' | 'low' | 'expiring' | 'expired';

export interface Status {
  type: StatusType;
  label: string;
}

const LABELS: Record<StatusType, string> = {
  ok: 'OK',
  low: 'Estoque baixo',
  expiring: 'Vence em breve',
  expired: 'Vencido',
};

/** "Today" normalized to UTC midnight, comparable to expiresAt (DATE). */
export function today(): Date {
  const now = new Date();
  return new Date(Date.UTC(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate()));
}

/** Days between today and the expiry date (negative = already expired). null if no expiry. */
export function daysUntil(expiresAt: Date | null, base: Date = today()): number | null {
  if (!expiresAt) return null;
  const e = new Date(Date.UTC(expiresAt.getUTCFullYear(), expiresAt.getUTCMonth(), expiresAt.getUTCDate()));
  const ms = e.getTime() - base.getTime();
  return Math.round(ms / (1000 * 60 * 60 * 24));
}

export function statusItem(p: Product, alertDays: number, base: Date = today()): Status {
  const d = daysUntil(p.expiresAt, base);
  const low = p.quantity < p.minQuantity || p.quantity === 0;
  if (d !== null && d < 0) return { type: 'expired', label: LABELS.expired };
  if (low) return { type: 'low', label: LABELS.low };
  if (d !== null && d <= alertDays) return { type: 'expiring', label: LABELS.expiring };
  return { type: 'ok', label: LABELS.ok };
}

/** Serializes a product for the API response, including status and daysUntilExpiry. */
export function serializeProduct(
  p: Product & { category?: { id: string; name: string } },
  alertDays: number,
) {
  const base = today();
  return {
    id: p.id,
    name: p.name,
    categoryId: p.categoryId,
    category: p.category ? { id: p.category.id, name: p.category.name } : undefined,
    quantity: p.quantity,
    minQuantity: p.minQuantity,
    unit: p.unit,
    expiresAt: p.expiresAt ? p.expiresAt.toISOString().slice(0, 10) : null,
    lastUpdated: p.lastUpdated.toISOString().slice(0, 10),
    daysUntilExpiry: daysUntil(p.expiresAt, base),
    status: statusItem(p, alertDays, base),
  };
}
