import { buildApp } from '../../../../app.js';

describe('Milestone 8: Reports, Analytics & Financial Registers Verification', () => {
  const app = buildApp();

  afterAll(async () => {
    await app.close();
  });

  it('GET /api/v1/reports/sales-summary should return sales summary and payment breakdown', async () => {
    const response = await app.inject({
      method: 'GET',
      url: '/api/v1/reports/sales-summary',
      headers: { authorization: 'Bearer dev-token-secret', 'x-device-id': 'dev-device-100' }
    });

    expect(response.statusCode).toBe(200);
    const body = JSON.parse(response.payload);
    expect(body.status).toBe('SUCCESS');
    expect(body.data.grossSales).toBeDefined();
    expect(body.data.paymentBreakdown).toBeDefined();
  });

  it('GET /api/v1/reports/tax-register should return GST/VAT tax breakdown audit register', async () => {
    const response = await app.inject({
      method: 'GET',
      url: '/api/v1/reports/tax-register',
      headers: { authorization: 'Bearer dev-token-secret', 'x-device-id': 'dev-device-100' }
    });

    expect(response.statusCode).toBe(200);
    const body = JSON.parse(response.payload);
    expect(body.status).toBe('SUCCESS');
    expect(body.data.vatGstRatePercent).toBe(10.0);
    expect(body.data.taxAuthority).toBe('State Revenue Office');
  });

  it('GET /api/v1/reports/dashboard should return executive BI analytics metrics', async () => {
    const response = await app.inject({
      method: 'GET',
      url: '/api/v1/reports/dashboard',
      headers: { authorization: 'Bearer dev-token-secret', 'x-device-id': 'dev-device-100' }
    });

    expect(response.statusCode).toBe(200);
    const body = JSON.parse(response.payload);
    expect(body.status).toBe('SUCCESS');
    expect(body.data.topSellingItems).toBeDefined();
    expect(body.data.hourlySalesVelocity).toBeDefined();
  });
});
