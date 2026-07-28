import { FastifyInstance } from 'fastify';
import { z } from 'zod';
import { SubscriptionService, SubscriptionPlan } from './subscription.service.js';
import { firebaseAuthMiddleware } from '../../../../middleware/firebase-auth.middleware.js';
import { tenantContextMiddleware } from '../../../../middleware/tenant-context.middleware.js';

const subscriptionService = new SubscriptionService();

export async function subscriptionRoutes(fastify: FastifyInstance) {
  // GET /api/v1/subscriptions/status
  fastify.get('/api/v1/subscriptions/status', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const businessId = request.tenantContext!.businessId;
    const status = await subscriptionService.getSubscriptionStatus(businessId);

    return reply.status(200).send({
      status: 'SUCCESS',
      data: status
    });
  });

  // POST /api/v1/subscriptions/renew
  fastify.post('/api/v1/subscriptions/renew', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const { plan, months } = request.body as { plan: SubscriptionPlan; months?: number };
    const businessId = request.tenantContext!.businessId;

    const renewed = await subscriptionService.renewSubscription(businessId, plan || 'PRO_POS', months || 12);

    return reply.status(200).send({
      status: 'SUCCESS',
      data: renewed
    });
  });
}
