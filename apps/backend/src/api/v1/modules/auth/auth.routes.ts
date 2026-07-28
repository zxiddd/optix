import { FastifyInstance } from 'fastify';
import { z } from 'zod';
import { firebaseAuthMiddleware } from '../../../../middleware/firebase-auth.middleware.js';
import { tenantContextMiddleware } from '../../../../middleware/tenant-context.middleware.js';

const DeviceRegisterSchema = z.object({
  device_hardware_uuid: z.string().min(5),
  device_name: z.string().min(2),
  app_version: z.string().min(1)
});

const StaffPinSchema = z.object({
  pin: z.string().min(4).max(6)
});

export async function authRoutes(fastify: FastifyInstance) {
  // POST /api/v1/auth/device-register
  fastify.post('/api/v1/auth/device-register', {
    preHandler: [firebaseAuthMiddleware]
  }, async (request, reply) => {
    const body = DeviceRegisterSchema.parse(request.body);

    return reply.status(200).send({
      status: 'SUCCESS',
      data: {
        device_id: `dev-${body.device_hardware_uuid}`,
        business_id: 'b18a42f5-31a8-4e12-a720-0021c4ef99a1',
        outlet_id: 'o-9022-main',
        business_name: 'Metro Bakery & Cafe',
        currency_symbol: '$',
        currency_code: 'USD',
        decimal_precision: 2,
        device_token: 'signed_device_jwt_token_optix_v1'
      }
    });
  });

  // POST /api/v1/auth/staff-pin
  fastify.post('/api/v1/auth/staff-pin', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const body = StaffPinSchema.parse(request.body);

    // Mock staff PIN validation (Default PIN "1234")
    if (body.pin !== '1234') {
      return reply.status(401).send({
        type: 'https://optixpos.com/errors/invalid-pin',
        title: 'Invalid Staff PIN',
        status: 401,
        detail: 'The entered staff PIN is incorrect.'
      });
    }

    return reply.status(200).send({
      status: 'SUCCESS',
      data: {
        staff_id: 'usr-cashier-01',
        full_name: 'John Cashier',
        role: 'CASHIER',
        authorized_at: Date.now()
      }
    });
  });
}
