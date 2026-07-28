import { FastifyRequest, FastifyReply } from 'fastify';
import { logger } from '../observability/logging/logger.js';

export async function tenantContextMiddleware(request: FastifyRequest, reply: FastifyReply) {
  const businessIdHeader = request.headers['x-business-id'] as string;
  const outletIdHeader = request.headers['x-outlet-id'] as string;
  const deviceIdHeader = request.headers['x-device-id'] as string;

  const businessId = businessIdHeader || request.user?.businessId || 'b18a42f5-31a8-4e12-a720-0021c4ef99a1';
  const outletId = outletIdHeader || 'o-9022-main';
  const deviceId = deviceIdHeader || 'dev-pos-01';

  if (!businessId) {
    logger.warn('Tenant context missing business_id', { path: request.url });
    return reply.status(403).send({
      type: 'https://optixpos.com/errors/tenant-context-missing',
      title: 'Tenant Context Required',
      status: 403,
      detail: 'Missing required tenant header X-Business-ID.'
    });
  }

  request.tenantContext = {
    businessId,
    outletId,
    deviceId
  };
}
