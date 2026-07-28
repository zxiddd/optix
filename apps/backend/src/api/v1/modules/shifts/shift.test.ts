import { buildApp } from '../../../../app.js';

describe('Milestone 5: Shift Closing, RBAC & Customer CRM Domain Verification', () => {
  const app = buildApp();

  afterAll(async () => {
    await app.close();
  });

  it('GET /api/v1/shifts/current should return active register shift float', async () => {
    const response = await app.inject({
      method: 'GET',
      url: '/api/v1/shifts/current',
      headers: {
        authorization: 'Bearer dev-token-secret',
        'x-device-id': 'dev-device-100'
      }
    });

    expect(response.statusCode).toBe(200);
    const body = JSON.parse(response.payload);
    expect(body.status).toBe('SUCCESS');
    expect(body.data.openingFloat).toBeDefined();
  });

  it('POST /api/v1/shifts/open should open shift with float amount', async () => {
    const response = await app.inject({
      method: 'POST',
      url: '/api/v1/shifts/open',
      headers: {
        authorization: 'Bearer dev-token-secret',
        'x-device-id': 'dev-device-100'
      },
      payload: {
        opening_float: 250.00
      }
    });

    expect(response.statusCode).toBe(201);
    const body = JSON.parse(response.payload);
    expect(body.status).toBe('SUCCESS');
    expect(body.data.openingFloat).toBe(250.00);
  });

  it('POST /api/v1/shifts/close should calculate cash variance & return Z-Report', async () => {
    const response = await app.inject({
      method: 'POST',
      url: '/api/v1/shifts/close',
      headers: {
        authorization: 'Bearer dev-token-secret',
        'x-device-id': 'dev-device-100'
      },
      payload: {
        closing_count: 700.00,
        notes: 'Shift balanced cleanly'
      }
    });

    expect(response.statusCode).toBe(200);
    const body = JSON.parse(response.payload);
    expect(body.status).toBe('SUCCESS');
    expect(body.data.expectedCash).toBe(700.00);
    expect(body.data.variance).toBe(0);
    expect(body.data.status).toBe('BALANCED');
  });
});
