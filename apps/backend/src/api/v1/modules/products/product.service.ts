import { PrismaClient, PricingStrategy } from '@prisma/client';

const prisma = new PrismaClient();

export interface CreateProductInput {
  businessId: string;
  categoryId?: string;
  sku?: string;
  barcode?: string;
  title: string;
  pricingStrategy?: PricingStrategy;
  unitPrice: number;
  costPrice?: number;
  trackInventory?: boolean;
  currentStock?: number;
  reorderLevel?: number;
}

export class ProductService {
  async listProducts(businessId: string, categoryId?: string, search?: string, limit = 50) {
    return prisma.product.findMany({
      where: {
        businessId,
        isArchived: false,
        ...(categoryId ? { categoryId } : {}),
        ...(search
          ? {
              OR: [
                { title: { contains: search, mode: 'insensitive' } },
                { barcode: { contains: search } },
                { sku: { contains: search } }
              ]
            }
          : {})
      },
      include: {
        category: true
      },
      orderBy: { title: 'asc' },
      take: limit
    });
  }

  async createProduct(input: CreateProductInput) {
    const versionTimestamp = BigInt(Date.now());

    return prisma.product.create({
      data: {
        businessId: input.businessId,
        categoryId: input.categoryId || null,
        sku: input.sku || null,
        barcode: input.barcode || null,
        title: input.title,
        pricingStrategy: input.pricingStrategy || 'FIXED',
        unitPrice: input.unitPrice,
        costPrice: input.costPrice || 0,
        trackInventory: input.trackInventory ?? true,
        currentStock: input.currentStock || 0,
        reorderLevel: input.reorderLevel || 5,
        versionTimestamp
      },
      include: {
        category: true
      }
    });
  }

  async listCategories(businessId: string) {
    return prisma.category.findMany({
      where: {
        businessId,
        isArchived: false
      },
      orderBy: { displayOrder: 'asc' }
    });
  }

  async createCategory(businessId: string, name: string, colorHex?: string) {
    return prisma.category.create({
      data: {
        businessId,
        name,
        colorHex: colorHex || '#4A90E2'
      }
    });
  }
}
