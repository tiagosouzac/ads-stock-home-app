import type { Request, Response } from 'express';
import { prisma } from '../lib/prisma.js';
import { notFound } from '../lib/errors.js';

function publicUser(u: { id: string; name: string; email: string; initials: string; alertDays: number }) {
  return { id: u.id, name: u.name, email: u.email, initials: u.initials, alertDays: u.alertDays };
}

function initialsFromName(name: string): string {
  const parts = name.trim().split(/\s+/);
  const a = parts[0]?.[0] ?? '';
  const b = parts.length > 1 ? parts[parts.length - 1][0] : '';
  return (a + b).toUpperCase();
}

export async function getMe(req: Request, res: Response) {
  const user = await prisma.user.findUnique({ where: { id: req.userId } });
  if (!user) throw notFound('Usuário não encontrado.');
  res.json(publicUser(user));
}

export async function updateMe(req: Request, res: Response) {
  const { name, alertDays } = req.body;
  const user = await prisma.user.update({
    where: { id: req.userId },
    data: {
      ...(name !== undefined && { name, initials: initialsFromName(name) }),
      ...(alertDays !== undefined && { alertDays }),
    },
  });
  res.json(publicUser(user));
}
