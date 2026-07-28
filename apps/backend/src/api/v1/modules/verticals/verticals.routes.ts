import { FastifyInstance } from 'fastify';
import { z } from 'zod';
import { RestaurantService } from './restaurant/restaurant.service.js';
import { FreshProduceService } from './fresh-produce/fresh-produce.service.js';
import { MedicalService } from './medical/medical.service.js';
import { firebaseAuthMiddleware } from '../../../../middleware/firebase-auth.middleware.js';
import { tenantContextMiddleware } from '../../../../middleware/tenant-context.middleware.js';

const restaurantService = new RestaurantService();
const freshProduceService = new FreshProduceService();
const medicalService = new MedicalService();

export async function verticalRoutes(fastify: FastifyInstance) {
  // Restaurant Table Management
  fastify.get('/api/v1/verticals/restaurant/tables', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const tables = await restaurantService.listTables();
    return reply.status(200).send({ status: 'SUCCESS', data: tables });
  });

  fastify.post('/api/v1/verticals/restaurant/tables/split', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const { table_id, split_count } = request.body as { table_id: string; split_count: number };
    const result = await restaurantService.splitTable(table_id, split_count || 2);
    return reply.status(200).send({ status: 'SUCCESS', data: result });
  });

  fastify.post('/api/v1/verticals/restaurant/tables/merge', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const { primary_table_id, secondary_table_id } = request.body as { primary_table_id: string; secondary_table_id: string };
    const result = await restaurantService.mergeTables(primary_table_id, secondary_table_id);
    return reply.status(200).send({ status: 'SUCCESS', data: result });
  });

  // Fresh Produce & Chicken Shop
  fastify.post('/api/v1/verticals/fresh-produce/scale', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const { gross_weight_kg, tare_weight_kg, unit_price_per_kg } = request.body as any;
    const result = freshProduceService.calculateScaleWeight({
      grossWeightKg: gross_weight_kg,
      tareWeightKg: tare_weight_kg,
      unitPricePerKg: unit_price_per_kg
    });
    return reply.status(200).send({ status: 'SUCCESS', data: result });
  });

  fastify.post('/api/v1/verticals/fresh-produce/yield-loss', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const { live_weight_kg, dressed_weight_kg, cost_per_live_kg } = request.body as any;
    const result = freshProduceService.calculateYieldLoss({
      liveWeightKg: live_weight_kg,
      dressedWeightKg: dressed_weight_kg,
      costPerLiveKg: cost_per_live_kg
    });
    return reply.status(200).send({ status: 'SUCCESS', data: result });
  });

  // Medical Pharmacy Expiry Lock & Generic Salt Search
  fastify.get('/api/v1/verticals/medical/salt-search', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const { salt } = request.query as { salt?: string };
    const results = await medicalService.searchBySalt(salt || 'Paracetamol');
    return reply.status(200).send({ status: 'SUCCESS', data: results });
  });

  fastify.post('/api/v1/verticals/medical/validate-batch', {
    preHandler: [firebaseAuthMiddleware, tenantContextMiddleware]
  }, async (request, reply) => {
    const { batch_number } = request.body as { batch_number: string };
    try {
      const validation = await medicalService.validateBatchForCheckout(batch_number);
      return reply.status(200).send({ status: 'SUCCESS', data: validation });
    } catch (err: any) {
      return reply.status(422).send({
        status: 'ERROR',
        error: { code: 'EXPIRY_LOCK_VIOLATION', message: err.message }
      });
    }
  });
}
