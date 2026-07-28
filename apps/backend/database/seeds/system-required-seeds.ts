import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

async function main() {
  console.log('=== Seeding Base System Tenant Metadata ===');

  const defaultBusiness = await prisma.business.upsert({
    where: { id: 'b18a42f5-31a8-4e12-a720-0021c4ef99a1' },
    update: {},
    create: {
      id: 'b18a42f5-31a8-4e12-a720-0021c4ef99a1',
      name: 'Metro Bakery & Cafe',
      businessType: 'BAKERY',
      currencyCode: 'USD',
      currencySymbol: '$',
      decimalPrecision: 2,
      timeZone: 'UTC',
      isActive: true
    }
  });

  const defaultOutlet = await prisma.outlet.upsert({
    where: { id: 'o-9022-main' },
    update: {},
    create: {
      id: 'o-9022-main',
      businessId: defaultBusiness.id,
      name: 'Main Flagship Store',
      address: '128 Main Street',
      phone: '+1-555-0199',
      isActive: true
    }
  });

  console.log(`Default Business Tenant Seeded: ${defaultBusiness.name} (Outlet: ${defaultOutlet.name})`);
}

main()
  .catch((e) => {
    console.error('Seeding error:', e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
