import { buildApp } from '../../../../app.js';

describe('Milestone 9: SaaS Subscriptions & Admin Web Portal Verification', () => {
  const app = buildApp();

  afterAll(async () => {
    await app.close();
  });

  it('GET /api/v1/subscriptions/status should return subscription status and remaining offline grace days', async () => {
    const response = await app.inject({
      method: 'GET',
      url: '/api/v1/subscriptions/status',
      headers: { authorization: 'Bearer dev-token-secret', 'x-device-id': 'dev-device-100' }
    });

    expect(response.statusCode).toBe(200);
    const body = JSON.parse(response.payload);
    expect(body.status).toBe('SUCCESS');
    expect(body.data.offlineGraceDaysRemaining).toBeDefined();
    expect(body.data.canProcessTransactions).toBe(true);
  });

  it('POST /api/v1/subscriptions/renew should extend subscription validity', async () => {
    const response = await app.inject({
      method: 'POST',
      url: '/api/v1/subscriptions/renew',
      headers: { authorization: 'Bearer dev-token-secret', 'x-device-id': 'dev-device-100' },
      payload: { plan: 'ENTERPRISE_CHAIN', months: 12 }
    });

    expect(response.statusCode).toBe(200);
    const body = JSON.parse(response.payload);
    expect(body.status).toBe('SUCCESS');
    expect(body.data.plan).toBe('ENTERPRISE_CHAIN');
  });

  it('GET /api/v1/admin/tenants should return all business tenants fleet summary', async () => {
    const response = await app.inject({
      method: 'GET',
      url: '/api/v1/admin/tenants',
      headers: { authorization: 'Bearer dev-token-secret' }
    });

    expect(response.statusCode).toBe(200);
    const body = JSON.parse(response.payload);
    expect(body.status).toBe('SUCCESS');
    expect(body.data.length).toBeGreaterThan(0);
  });
});
