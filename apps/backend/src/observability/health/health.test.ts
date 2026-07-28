import { buildApp } from '../../app.js';

describe('Health Check Endpoint Integration Test', () => {
  const app = buildApp();

  afterAll(async () => {
    await app.close();
  });

  it('GET /health should return HTTP 200 OK and status SUCCESS', async () => {
    const response = await app.inject({
      method: 'GET',
      url: '/health'
    });

    expect(response.statusCode).toBe(200);
    const payload = JSON.parse(response.payload);
    expect(payload.status).toBe('SUCCESS');
    expect(payload.data.service).toBe('optix-backend-api');
    expect(payload.data.timestamp).toBeDefined();
  });
});
