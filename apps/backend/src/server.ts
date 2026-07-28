import { buildApp } from './app.js';
import { env } from './config/env.js';
import { logger } from './observability/logging/logger.js';

const app = buildApp();

async function start() {
  try {
    await app.listen({ port: env.PORT, host: env.HOST });
    logger.info(`Optix Fastify Core API running at http://${env.HOST}:${env.PORT}`);
  } catch (err) {
    logger.error('Failed to start Optix Fastify server', { error: err });
    process.exit(1);
  }
}

start();
