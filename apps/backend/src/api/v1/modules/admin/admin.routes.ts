import { FastifyInstance } from 'fastify';
import { AdminService } from './admin.service.js';
import { firebaseAuthMiddleware } from '../../../../middleware/firebase-auth.middleware.js';

const adminService = new AdminService();

export async function adminRoutes(fastify: FastifyInstance) {
  // GET /api/v1/admin/tenants
  fastify.get('/api/v1/admin/tenants', {
    preHandler: [firebaseAuthMiddleware]
  }, async (request, reply) => {
    const tenants = await adminService.listAllTenants();
    return reply.status(200).send({
      status: 'SUCCESS',
      data: tenants
    });
  });

  // POST /api/v1/admin/tenants/:id/suspend
  fastify.post('/api/v1/admin/tenants/:id/suspend', {
    preHandler: [firebaseAuthMiddleware]
  }, async (request, reply) => {
    const { id } = request.params as { id: string };
    const suspended = await adminService.suspendTenant(id);
    return reply.status(200).send({
      status: 'SUCCESS',
      data: suspended
    });
  });
}
