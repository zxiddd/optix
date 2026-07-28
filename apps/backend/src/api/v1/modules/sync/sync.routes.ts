import { FastifyInstance } from 'fastify';
import { z } from 'zod';
import { SyncService } from './sync.service.js';
import { firebaseAuthMiddleware } from '../../../../middleware/firebase-auth.middleware.js';
import { tenantContextMiddleware } from '../../../../middleware/tenant-context.middleware.js';

const syncService = new SyncService();

const SyncEventSchema = z.object({
  eventId: z.string(),
  eventType: z.enum(['BILL_CREATED', 'PRODUCT_UPDATED', 'CUSTOMER_UPDATED']),
  aggregateId: z.string(),
  versionTimestamp: z.number(),
  payloadJson: z.string()
});

const PushSyncSchema = z.object({
  events: z.array(SyncEventSchema)
});

export async function syncRoutes(fastify: FastifyInstance) {
  // POST /api/v1/sync/push
  fastify.post('/api/v1/sync/push', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const body = PushSyncSchema.parse(request.body);
    const tenant = request.tenantContext!;

    const result = await syncService.pushSyncEvents(
      tenant.businessId,
      tenant.outletId,
      tenant.deviceId,
      body.events
    );

    return reply.status(200).send({
      status: 'SUCCESS',
      data: result
    });
  });

  // GET /api/v1/sync/pull
  fastify.get('/api/v1/sync/pull', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const { since_version_timestamp } = request.query as { since_version_timestamp?: string };
    const businessId = request.tenantContext!.businessId;

    const delta = await syncService.pullDeltaUpdates(
      businessId,
      since_version_timestamp ? parseInt(since_version_timestamp, 10) : 0
    );

    return reply.status(200).send({
      status: 'SUCCESS',
      data: delta
    });
  });
}
