import { PrismaClient } from '@prisma/client';
import bcrypt from 'bcryptjs';

const prisma = new PrismaClient();

// Mirrors CATEGORIAS and PRODUTOS from data/MockData.kt.
// Display values (category and product names) stay in pt-BR — they are data.
const CATEGORIES = [
  { id: 'alimentos', name: 'Alimentos', sortOrder: 0 },
  { id: 'bebidas', name: 'Bebidas', sortOrder: 1 },
  { id: 'limpeza', name: 'Limpeza', sortOrder: 2 },
  { id: 'higiene', name: 'Higiene', sortOrder: 3 },
];

const d = (s: string | null) => (s ? new Date(`${s}T00:00:00.000Z`) : null);

const PRODUCTS = [
  { name: 'Arroz branco 5kg', cat: 'alimentos', qty: 2, min: 1, unit: 'pacotes', expiresAt: '2026-11-10', lastUpdated: '2026-05-28' },
  { name: 'Feijão carioca 1kg', cat: 'alimentos', qty: 1, min: 2, unit: 'pacotes', expiresAt: '2026-09-01', lastUpdated: '2026-05-30' },
  { name: 'Café torrado 500g', cat: 'alimentos', qty: 1, min: 1, unit: 'pacote', expiresAt: '2026-06-05', lastUpdated: '2026-05-20' },
  { name: 'Leite integral 1L', cat: 'bebidas', qty: 6, min: 4, unit: 'caixas', expiresAt: '2026-06-04', lastUpdated: '2026-06-01' },
  { name: 'Açúcar refinado 1kg', cat: 'alimentos', qty: 0, min: 1, unit: 'pacotes', expiresAt: '2026-12-02', lastUpdated: '2026-05-25' },
  { name: 'Óleo de soja 900ml', cat: 'alimentos', qty: 3, min: 1, unit: 'frascos', expiresAt: '2026-10-15', lastUpdated: '2026-05-18' },
  { name: 'Macarrão espaguete 500g', cat: 'alimentos', qty: 4, min: 2, unit: 'pacotes', expiresAt: '2027-02-10', lastUpdated: '2026-05-12' },
  { name: 'Iogurte natural 170g', cat: 'bebidas', qty: 4, min: 2, unit: 'potes', expiresAt: '2026-06-06', lastUpdated: '2026-05-31' },
  { name: 'Detergente neutro 500ml', cat: 'limpeza', qty: 1, min: 2, unit: 'frascos', expiresAt: null, lastUpdated: '2026-05-22' },
  { name: 'Sabão em pó 1kg', cat: 'limpeza', qty: 2, min: 1, unit: 'caixas', expiresAt: null, lastUpdated: '2026-05-10' },
  { name: 'Amaciante 2L', cat: 'limpeza', qty: 0, min: 1, unit: 'frascos', expiresAt: null, lastUpdated: '2026-05-27' },
  { name: 'Papel higiênico 12un', cat: 'higiene', qty: 1, min: 1, unit: 'pacotes', expiresAt: null, lastUpdated: '2026-05-16' },
  { name: 'Creme dental 90g', cat: 'higiene', qty: 3, min: 1, unit: 'tubos', expiresAt: '2027-03-01', lastUpdated: '2026-05-08' },
  { name: 'Sabonete 85g', cat: 'higiene', qty: 5, min: 2, unit: 'barras', expiresAt: null, lastUpdated: '2026-04-30' },
  { name: 'Farinha de trigo 1kg', cat: 'alimentos', qty: 1, min: 1, unit: 'pacotes', expiresAt: '2026-08-20', lastUpdated: '2026-05-05' },
];

async function main() {
  console.log('🌱 Seeding...');

  // Categories (idempotent)
  for (const c of CATEGORIES) {
    await prisma.category.upsert({
      where: { id: c.id },
      update: { name: c.name, sortOrder: c.sortOrder },
      create: c,
    });
  }
  console.log(`   ${CATEGORIES.length} categories`);

  // Demo user — Marina Alves (data/MockData.kt → USUARIO)
  const passwordHash = await bcrypt.hash('123456', 10);
  const marina = await prisma.user.upsert({
    where: { email: 'marina.alves@email.com' },
    update: {},
    create: {
      name: 'Marina Alves',
      email: 'marina.alves@email.com',
      password: passwordHash,
      initials: 'MA',
      alertDays: 7,
    },
  });
  console.log(`   user: ${marina.email} (password: 123456)`);

  // Recreate Marina's products
  await prisma.product.deleteMany({ where: { userId: marina.id } });
  for (const p of PRODUCTS) {
    await prisma.product.create({
      data: {
        name: p.name,
        categoryId: p.cat,
        quantity: p.qty,
        minQuantity: p.min,
        unit: p.unit,
        expiresAt: d(p.expiresAt),
        lastUpdated: d(p.lastUpdated)!,
        userId: marina.id,
      },
    });
  }
  console.log(`   ${PRODUCTS.length} products`);
  console.log('✅ Seed complete.');
}

main()
  .catch((e) => {
    console.error('❌ Seed error:', e);
    process.exit(1);
  })
  .finally(() => prisma.$disconnect());
