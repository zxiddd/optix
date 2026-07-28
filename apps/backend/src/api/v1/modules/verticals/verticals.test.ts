import { buildApp } from '../../../../app.js';

describe('Milestone 6: Specialized Vertical Extensions Verification', () => {
  const app = buildApp();

  afterAll(async () => {
    await app.close();
  });

  it('Restaurant Domain: Table Split and Table Merge Verification', async () => {
    const splitRes = await app.inject({
      method: 'POST',
      url: '/api/v1/verticals/restaurant/tables/split',
      headers: { authorization: 'Bearer dev-token-secret', 'x-device-id': 'dev-device-100' },
      payload: { table_id: 't-1', split_count: 2 }
    });

    expect(splitRes.statusCode).toBe(200);
    const splitBody = JSON.parse(splitRes.payload);
    expect(splitBody.data.splitTablesCreated.length).toBe(2);

    const mergeRes = await app.inject({
      method: 'POST',
      url: '/api/v1/verticals/restaurant/tables/merge',
      headers: { authorization: 'Bearer dev-token-secret', 'x-device-id': 'dev-device-100' },
      payload: { primary_table_id: 't-1', secondary_table_id: 't-2' }
    });

    expect(mergeRes.statusCode).toBe(200);
    const mergeBody = JSON.parse(mergeRes.payload);
    expect(mergeBody.data.status).toBe('MERGED_SUCCESSFULLY');
  });

  it('Fresh Produce Domain: Tare Weight & Dressing Yield Loss Calculations', async () => {
    const scaleRes = await app.inject({
      method: 'POST',
      url: '/api/v1/verticals/fresh-produce/scale',
      headers: { authorization: 'Bearer dev-token-secret', 'x-device-id': 'dev-device-100' },
      payload: { gross_weight_kg: 5.450, tare_weight_kg: 0.450, unit_price_per_kg: 10.00 }
    });

    expect(scaleRes.statusCode).toBe(200);
    const scaleBody = JSON.parse(scaleRes.payload);
    expect(scaleBody.data.netWeightKg).toBe(5.000);
    expect(scaleBody.data.totalPrice).toBe(50.00);

    const yieldRes = await app.inject({
      method: 'POST',
      url: '/api/v1/verticals/fresh-produce/yield-loss',
      headers: { authorization: 'Bearer dev-token-secret', 'x-device-id': 'dev-device-100' },
      payload: { live_weight_kg: 2.000, dressed_weight_kg: 1.300, cost_per_live_kg: 3.00 }
    });

    expect(yieldRes.statusCode).toBe(200);
    const yieldBody = JSON.parse(yieldRes.payload);
    expect(yieldBody.data.lossKg).toBe(0.700);
    expect(yieldBody.data.yieldPercentage).toBe(65.00);
  });

  it('Medical Domain: Expiry Safety Lock Enforcement', async () => {
    const expiredRes = await app.inject({
      method: 'POST',
      url: '/api/v1/verticals/medical/validate-batch',
      headers: { authorization: 'Bearer dev-token-secret', 'x-device-id': 'dev-device-100' },
      payload: { batch_number: 'BATCH-EXPIRED-99' }
    });

    expect(expiredRes.statusCode).toBe(422);
    const expiredBody = JSON.parse(expiredRes.payload);
    expect(expiredBody.status).toBe('ERROR');
    expect(expiredBody.error.code).toBe('EXPIRY_LOCK_VIOLATION');
  });
});
