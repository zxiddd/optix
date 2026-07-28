import { FastifyRequest, FastifyReply } from 'fastify';

declare module 'fastify' {
  interface FastifyRequest {
    tenantContext?: {
      businessId: string;
      outletId: string;
      deviceId: string;
    };
  }
}

export async function tenantContextMiddleware(
  request: FastifyRequest,
  reply: FastifyReply
) {
  const businessIdHeader = request.headers['x-business-id'] as string;
  const outletIdHeader = request.headers['x-outlet-id'] as string;
  const deviceIdHeader = request.headers['x-device-id'] as string;

  const businessId = businessIdHeader || request.user?.businessId || 'b18a42f5-31a8-4e12-a720-0021c4ef99a1';
  const outletId = outletIdHeader || 'o-9022-main';
  const deviceId = deviceIdHeader || 'dev-device-100';

  request.tenantContext = {
    businessId,
    outletId,
    deviceId
  };
}
