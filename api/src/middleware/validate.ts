import type { NextFunction, Request, Response } from 'express';
import type { ZodSchema } from 'zod';
import { badRequest } from '../lib/errors.js';

type Source = 'body' | 'query' | 'params';

/** Validates and replaces req[source] with the data already parsed by Zod. */
export function validate(schema: ZodSchema, source: Source = 'body') {
  return (req: Request, _res: Response, next: NextFunction) => {
    const result = schema.safeParse(req[source]);
    if (!result.success) {
      return next(
        badRequest('Dados inválidos.', result.error.issues.map((i) => ({
          field: i.path.join('.'),
          message: i.message,
        }))),
      );
    }
    // query/params are read-only in Express 5; use Object.assign when possible.
    if (source === 'body') {
      req.body = result.data;
    } else {
      Object.assign(req[source], result.data);
    }
    next();
  };
}
