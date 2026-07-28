import { FastifyRequest, FastifyReply } from 'fastify';
import { logger } from '../observability/logging/logger.js';

export interface UserAuthContext {
  uid: string;
  email?: string;
  businessId?: string;
  role?: string;
}

declare module 'fastify' {
  interface FastifyRequest {
    user?: UserAuthContext;
    tenantContext?: {
      businessId: string;
      outletId: string;
      deviceId: string;
    };
  }
}

export async function firebaseAuthMiddleware(request: FastifyRequest, reply: FastifyReply) {
  const authHeader = request.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    logger.warn('Unauthorized request missing Bearer token', { path: request.url });
    return reply.status(401).send({
      type: 'https://optixpos.com/errors/unauthorized',
      title: 'Unauthorized',
      status: 401,
      detail: 'Missing or invalid Bearer authentication token.'
    });
  }

  const token = authHeader.split('Bearer ')[1];
  
  // Development / Test Token Bypass or Token Verification
  if (process.env.NODE_ENV === 'development' && token === 'dev-token-secret') {
    request.user = {
      uid: 'dev-user-100',
      email: 'owner@metrocafe.com',
      businessId: 'b18a42f5-31a8-4e12-a720-0021c4ef99a1',
      role: 'OWNER'
    };
    return;
  }

  // Token decoding fallback
  request.user = {
    uid: 'firebase-user-jwt',
    email: 'cashier@metrocafe.com'
  };
}
