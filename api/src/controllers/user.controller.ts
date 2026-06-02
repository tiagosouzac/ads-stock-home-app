import type { Request, Response } from 'express';
import bcrypt from 'bcryptjs';
import { prisma } from '../lib/prisma.js';
import { notFound, unauthorized } from '../lib/errors.js';

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

export async function changePassword(req: Request, res: Response) {
  const { currentPassword, newPassword } = req.body;

  const user = await prisma.user.findUnique({ where: { id: req.userId } });
  if (!user) throw notFound('Usuário não encontrado.');

  const ok = await bcrypt.compare(currentPassword, user.password);
  if (!ok) throw unauthorized('Senha atual incorreta.');

  const hash = await bcrypt.hash(newPassword, 10);
  await prisma.user.update({ where: { id: user.id }, data: { password: hash } });

  res.json({ ok: true });
}
