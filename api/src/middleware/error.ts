import type { NextFunction, Request, Response } from 'express';
import { Prisma } from '@prisma/client';
import { HttpError } from '../lib/errors.js';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
export function errorHandler(err: unknown, _req: Request, res: Response, _next: NextFunction) {
  if (err instanceof HttpError) {
    return res.status(err.status).json({ error: err.message, details: err.details });
  }

  if (err instanceof Prisma.PrismaClientKnownRequestError) {
    if (err.code === 'P2002') {
      return res.status(409).json({ error: 'Registro já existe (violação de unicidade).' });
    }
    if (err.code === 'P2025') {
      return res.status(404).json({ error: 'Recurso não encontrado.' });
    }
    if (err.code === 'P2003') {
      return res.status(400).json({ error: 'Referência inválida (categoria inexistente?).' });
    }
  }

  console.error('[unhandled error]', err);
  return res.status(500).json({ error: 'Erro interno do servidor.' });
}

export function notFoundHandler(_req: Request, res: Response) {
  res.status(404).json({ error: 'Rota não encontrada.' });
}
