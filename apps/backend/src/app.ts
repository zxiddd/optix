import Fastify from 'fastify';
import cors from '@fastify/cors';
import helmet from '@fastify/helmet';
import rateLimit from '@fastify/rate-limit';
import { logger } from './observability/logging/logger.js';
import { healthRoutes } from './observability/health/health.routes.js';

export function buildApp() {
  const app = Fastify({
    logger: false // Winston is used for structured logging
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

  // Register Routes
  app.register(healthRoutes);

  return app;
}
