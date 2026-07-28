import { FastifyInstance } from 'fastify';
import { z } from 'zod';
import { ShiftService } from './shift.service.js';
import { firebaseAuthMiddleware } from '../../../../middleware/firebase-auth.middleware.js';
import { tenantContextMiddleware } from '../../../../middleware/tenant-context.middleware.js';
import { requirePermission } from '../../../../middleware/rbac.middleware.js';

const shiftService = new ShiftService();

const OpenShiftSchema = z.object({
  opening_float: z.number().min(0)
});

const CloseShiftSchema = z.object({
  closing_count: z.number().min(0),
  notes: z.string().optional()
});

export async function shiftRoutes(fastify: FastifyInstance) {
  // GET /api/v1/shifts/current
  fastify.get('/api/v1/shifts/current', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const deviceId = request.tenantContext!.deviceId;
    const shift = await shiftService.getCurrentShift(deviceId);

    return reply.status(200).send({
      status: 'SUCCESS',
      data: shift
    });
  });

  // POST /api/v1/shifts/open
  fastify.post('/api/v1/shifts/open', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware, requirePermission('SHIFT_OPEN_CLOSE')]
  }, async (request, reply) => {
    const body = OpenShiftSchema.parse(request.body);
    const tenant = request.tenantContext!;

    const shift = await shiftService.openShift({
      businessId: tenant.businessId,
      outletId: tenant.outletId,
      deviceId: tenant.deviceId,
      staffId: request.user!.uid,
      openingFloat: body.opening_float
    });

    return reply.status(201).send({
      status: 'SUCCESS',
      data: shift
    });
  });

  // POST /api/v1/shifts/close
  fastify.post('/api/v1/shifts/close', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware, requirePermission('SHIFT_OPEN_CLOSE')]
  }, async (request, reply) => {
    const body = CloseShiftSchema.parse(request.body);
    const deviceId = request.tenantContext!.deviceId;

    const zReport = await shiftService.closeShift(deviceId, body.closing_count, body.notes);

    return reply.status(200).send({
      status: 'SUCCESS',
      data: zReport
    });
  });
}
