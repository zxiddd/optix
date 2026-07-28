import Fastify from 'fastify';
import cors from '@fastify/cors';
import helmet from '@fastify/helmet';
import rateLimit from '@fastify/rate-limit';
import { logger } from './observability/logging/logger.js';
import { healthRoutes } from './observability/health/health.routes.js';
import { authRoutes } from './api/v1/modules/auth/auth.routes.js';
import { productRoutes } from './api/v1/modules/products/product.routes.js';
import { billingRoutes } from './api/v1/modules/billing/billing.routes.js';
import { customerRoutes } from './api/v1/modules/customers/customer.routes.js';
import { shiftRoutes } from './api/v1/modules/shifts/shift.routes.js';
import { verticalRoutes } from './api/v1/modules/verticals/verticals.routes.js';
import { syncRoutes } from './api/v1/modules/sync/sync.routes.js';

export function buildApp() {
  const app = Fastify({
    logger: false
  });

  // Register Security Plugins
  app.register(helmet, { contentSecurityPolicy: false });
  app.register(cors, { origin: true });
  app.register(rateLimit, { max: 300, timeWindow: '1 minute' });

  // Request Correlation Logger
  app.addHook('onRequest', async (request) => {
    logger.info(`HTTP ${request.method} ${request.url}`, {
      ip: request.ip,
      userAgent: request.headers['user-agent']
    });
  });

  // Register Domain Route Modules
  app.register(healthRoutes);
  app.register(authRoutes);
  app.register(productRoutes);
  app.register(billingRoutes);
  app.register(customerRoutes);
  app.register(shiftRoutes);
  app.register(verticalRoutes);
  app.register(syncRoutes);

  return app;
}
