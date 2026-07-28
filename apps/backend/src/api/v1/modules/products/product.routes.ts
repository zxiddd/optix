import { FastifyInstance } from 'fastify';
import { z } from 'zod';
import { ProductService } from './product.service.js';
import { firebaseAuthMiddleware } from '../../../../middleware/firebase-auth.middleware.js';
import { tenantContextMiddleware } from '../../../../middleware/tenant-context.middleware.js';

const productService = new ProductService();

const CreateProductSchema = z.object({
  category_id: z.string().optional(),
  sku: z.string().optional(),
  barcode: z.string().optional(),
  title: z.string().min(1),
  pricing_strategy: z.enum(['FIXED', 'WEIGHT', 'VARIABLE', 'MARKET']).optional(),
  unit_price: z.number().min(0),
  cost_price: z.number().min(0).optional(),
  track_inventory: z.boolean().optional(),
  current_stock: z.number().optional(),
  reorder_level: z.number().optional()
});

const CreateCategorySchema = z.object({
  name: z.string().min(1),
  color_hex: z.string().optional()
});

export async function productRoutes(fastify: FastifyInstance) {
  // GET /api/v1/products
  fastify.get('/api/v1/products', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const { category_id, search, limit } = request.query as { category_id?: string; search?: string; limit?: string };
    const businessId = request.tenantContext!.businessId;

    const products = await productService.listProducts(
      businessId,
      category_id,
      search,
      limit ? parseInt(limit, 10) : 50
    );

    return reply.status(200).send({
      status: 'SUCCESS',
      data: products
    });
  });

  // POST /api/v1/products
  fastify.post('/api/v1/products', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const body = CreateProductSchema.parse(request.body);
    const businessId = request.tenantContext!.businessId;

    const product = await productService.createProduct({
      businessId,
      categoryId: body.category_id,
      sku: body.sku,
      barcode: body.barcode,
      title: body.title,
      pricingStrategy: body.pricing_strategy,
      unitPrice: body.unit_price,
      costPrice: body.cost_price,
      trackInventory: body.track_inventory,
      currentStock: body.current_stock,
      reorderLevel: body.reorder_level
    });

    return reply.status(201).send({
      status: 'SUCCESS',
      data: product
    });
  });

  // GET /api/v1/categories
  fastify.get('/api/v1/categories', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const businessId = request.tenantContext!.businessId;
    const categories = await productService.listCategories(businessId);

    return reply.status(200).send({
      status: 'SUCCESS',
      data: categories
    });
  });

  // POST /api/v1/categories
  fastify.post('/api/v1/categories', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const body = CreateCategorySchema.parse(request.body);
    const businessId = request.tenantContext!.businessId;
    const category = await productService.createCategory(businessId, body.name, body.color_hex);

    return reply.status(201).send({
      status: 'SUCCESS',
      data: category
    });
  });
}
