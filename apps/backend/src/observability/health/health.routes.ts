import { FastifyInstance } from 'fastify';

export async function healthRoutes(fastify: FastifyInstance) {
  fastify.get('/health', async (_request, reply) => {
    return reply.status(200).send({
      status: 'SUCCESS',
      data: {
        service: 'optix-backend-api',
        uptime: process.uptime(),
        timestamp: Date.now()
      }
    });
  });
}
