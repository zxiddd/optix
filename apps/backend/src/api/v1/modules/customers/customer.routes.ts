import { FastifyInstance } from 'fastify';
import { z } from 'zod';
import { CustomerService } from './customer.service.js';
import { firebaseAuthMiddleware } from '../../../../middleware/firebase-auth.middleware.js';
import { tenantContextMiddleware } from '../../../../middleware/tenant-context.middleware.js';
import { requirePermission } from '../../../../middleware/rbac.middleware.js';

const customerService = new CustomerService();

const CreateCustomerSchema = z.object({
  full_name: z.string().min(1),
  phone: z.string().min(5),
  email: z.string().email().optional(),
  credit_limit: z.number().min(0).optional()
});

const KhataRepaySchema = z.object({
  amount: z.number().positive(),
  payment_method: z.enum(['CASH', 'CARD_TENDER', 'DIGITAL_UPI_QR'])
});

export async function customerRoutes(fastify: FastifyInstance) {
  // GET /api/v1/customers
  fastify.get('/api/v1/customers', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware, requirePermission('CUSTOMER_MANAGE')]
  }, async (request, reply) => {
    const { search } = request.query as { search?: string };
    const businessId = request.tenantContext!.businessId;

    const customers = await customerService.listCustomers(businessId, search);

    return reply.status(200).send({
      status: 'SUCCESS',
      data: customers
    });
  });

  // POST /api/v1/customers
  fastify.post('/api/v1/customers', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware, requirePermission('CUSTOMER_MANAGE')]
  }, async (request, reply) => {
    const body = CreateCustomerSchema.parse(request.body);
    const businessId = request.tenantContext!.businessId;

    const customer = await customerService.createCustomer({
      businessId,
      fullName: body.full_name,
      phone: body.phone,
      email: body.email,
      creditLimit: body.credit_limit
    });

    return reply.status(201).send({
      status: 'SUCCESS',
      data: customer
    });
  });

  // POST /api/v1/customers/:id/khata-repay
  fastify.post('/api/v1/customers/:id/khata-repay', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware, requirePermission('KHATA_REPAY')]
  }, async (request, reply) => {
    const { id } = request.params as { id: string };
    const body = KhataRepaySchema.parse(request.body);
    const businessId = request.tenantContext!.businessId;

    const customer = await customerService.repayKhata(businessId, id, body.amount);

    return reply.status(200).send({
      status: 'SUCCESS',
      data: customer
    });
  });
}
