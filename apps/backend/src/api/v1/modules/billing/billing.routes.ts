import { FastifyInstance } from 'fastify';
import { z } from 'zod';
import { BillingService } from './billing.service.js';
import { firebaseAuthMiddleware } from '../../../../middleware/firebase-auth.middleware.js';
import { tenantContextMiddleware } from '../../../../middleware/tenant-context.middleware.js';

const billingService = new BillingService();

const CartItemSchema = z.object({
  id: z.string(),
  productId: z.string(),
  title: z.string(),
  unitPrice: z.number().min(0),
  quantity: z.number().min(0.001),
  taxRatePercent: z.number().optional(),
  discountAmount: z.number().optional(),
  notes: z.string().optional()
});

const PaymentSchema = z.object({
  method: z.enum(['CASH', 'CARD_TENDER', 'DIGITAL_UPI_QR', 'STORE_CREDIT_KHATA', 'LOYALTY_POINTS']),
  amount: z.number().min(0)
});

const CheckoutSchema = z.object({
  invoice_number: z.string().min(3),
  staff_id: z.string(),
  customer_id: z.string().optional(),
  items: z.array(CartItemSchema).min(1),
  payments: z.array(PaymentSchema).min(1),
  global_discount_amount: z.number().optional(),
  global_tax_percent: z.number().optional()
});

export async function billingRoutes(fastify: FastifyInstance) {
  // POST /api/v1/bills/checkout
  fastify.post('/api/v1/bills/checkout', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const body = CheckoutSchema.parse(request.body);
    const tenant = request.tenantContext!;

    const result = await billingService.checkout({
      businessId: tenant.businessId,
      outletId: tenant.outletId,
      deviceId: tenant.deviceId,
      staffId: body.staff_id,
      customerId: body.customer_id,
      invoiceNumber: body.invoice_number,
      items: body.items,
      payments: body.payments,
      globalDiscountAmount: body.global_discount_amount,
      globalTaxPercent: body.global_tax_percent
    });

    return reply.status(201).send({
      status: 'SUCCESS',
      data: result
    });
  });
}
