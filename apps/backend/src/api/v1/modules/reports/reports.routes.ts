import { FastifyInstance } from 'fastify';
import { ReportsService } from './reports.service.js';
import { firebaseAuthMiddleware } from '../../../../middleware/firebase-auth.middleware.js';
import { tenantContextMiddleware } from '../../../../middleware/tenant-context.middleware.js';
import { requirePermission } from '../../../../middleware/rbac.middleware.js';

const reportsService = new ReportsService();

export async function reportsRoutes(fastify: FastifyInstance) {
  // GET /api/v1/reports/sales-summary
  fastify.get('/api/v1/reports/sales-summary', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware, requirePermission('REPORTS_VIEW')]
  }, async (request, reply) => {
    const businessId = request.tenantContext!.businessId;
    const summary = await reportsService.getSalesSummary(businessId);

    return reply.status(200).send({
      status: 'SUCCESS',
      data: summary
    });
  });

  // GET /api/v1/reports/tax-register
  fastify.get('/api/v1/reports/tax-register', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware, requirePermission('REPORTS_VIEW')]
  }, async (request, reply) => {
    const businessId = request.tenantContext!.businessId;
    const taxRegister = await reportsService.getTaxRegister(businessId);

    return reply.status(200).send({
      status: 'SUCCESS',
      data: taxRegister
    });
  });

  // GET /api/v1/reports/dashboard
  fastify.get('/api/v1/reports/dashboard', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware, requirePermission('REPORTS_VIEW')]
  }, async (request, reply) => {
    const businessId = request.tenantContext!.businessId;
    const dashboard = await reportsService.getDashboardAnalytics(businessId);

    return reply.status(200).send({
      status: 'SUCCESS',
      data: dashboard
    });
  });
}
