import type { NextFunction, Request, Response } from 'express';
import { verifyToken } from '../lib/jwt.js';
import { unauthorized } from '../lib/errors.js';

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Express {
    interface Request {
      userId?: string;
    }
  }
}

export function authRequired(req: Request, _res: Response, next: NextFunction) {
  const header = req.headers.authorization;
  if (!header?.startsWith('Bearer ')) {
    return next(unauthorized('Token ausente. Envie o header Authorization: Bearer <token>.'));
  }
  try {
    const payload = verifyToken(header.slice('Bearer '.length));
    req.userId = payload.sub;
    next();
  } catch {
    next(unauthorized('Token inválido ou expirado.'));
  }
}
