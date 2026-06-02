import type { Request, Response } from 'express';
import bcrypt from 'bcryptjs';
import { prisma } from '../lib/prisma.js';
import { signToken } from '../lib/jwt.js';
import { conflict, unauthorized } from '../lib/errors.js';

/** Builds the initials from a name (e.g. "Marina Alves" → "MA"). */
function initialsFromName(name: string): string {
  const parts = name.trim().split(/\s+/);
  const a = parts[0]?.[0] ?? '';
  const b = parts.length > 1 ? parts[parts.length - 1][0] : '';
  return (a + b).toUpperCase();
}

function publicUser(u: { id: string; name: string; email: string; initials: string; alertDays: number }) {
  return { id: u.id, name: u.name, email: u.email, initials: u.initials, alertDays: u.alertDays };
}

export async function register(req: Request, res: Response) {
  const { name, email, password, alertDays } = req.body;

  const existing = await prisma.user.findUnique({ where: { email } });
  if (existing) throw conflict('Já existe uma conta com este e-mail.');

  const hash = await bcrypt.hash(password, 10);
  const user = await prisma.user.create({
    data: {
      name,
      email,
      password: hash,
      initials: initialsFromName(name),
      alertDays: alertDays ?? 7,
    },
  });

  const token = signToken({ sub: user.id, email: user.email });
  res.status(201).json({ token, user: publicUser(user) });
}

export async function login(req: Request, res: Response) {
  const { email, password } = req.body;

  const user = await prisma.user.findUnique({ where: { email } });
  if (!user) throw unauthorized('E-mail ou senha incorretos.');

  const ok = await bcrypt.compare(password, user.password);
  if (!ok) throw unauthorized('E-mail ou senha incorretos.');

  const token = signToken({ sub: user.id, email: user.email });
  res.json({ token, user: publicUser(user) });
}
