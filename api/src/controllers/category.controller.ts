import type { Request, Response } from 'express';
import { prisma } from '../lib/prisma.js';

export async function listCategories(_req: Request, res: Response) {
  const categories = await prisma.category.findMany({
    orderBy: { sortOrder: 'asc' },
    select: { id: true, name: true },
  });
  res.json(categories);
}
