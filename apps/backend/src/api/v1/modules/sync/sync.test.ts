import { buildApp } from '../../../../app.js';

describe('Milestone 7: Room Outbox Queue & Offline Sync Engine Verification', () => {
  const app = buildApp();

  afterAll(async () => {
    await app.close();
  });

  it('POST /api/v1/sync/push should process batch client outbox events and resolve conflicts', async () => {
    const response = await app.inject({
      method: 'POST',
      url: '/api/v1/sync/push',
      headers: { authorization: 'Bearer dev-token-secret', 'x-device-id': 'dev-device-100' },
      payload: {
        events: [
          {
            eventId: 'evt-101',
            eventType: 'PRODUCT_UPDATED',
            aggregateId: 'p-croissant',
            versionTimestamp: Date.now(),
            payloadJson: JSON.stringify({ title: 'Butter Croissant Premium', unitPrice: 4.80 })
          }
        ]
      }
    });

    expect(response.statusCode).toBe(200);
    const body = JSON.parse(response.payload);
    expect(body.status).toBe('SUCCESS');
    expect(body.data.processedEventIds).toContain('evt-101');
    expect(body.data.serverTimestamp).toBeDefined();
  });

  it('GET /api/v1/sync/pull should return delta updates for offline client catchup', async () => {
    const response = await app.inject({
      method: 'GET',
      url: '/api/v1/sync/pull?since_version_timestamp=0',
      headers: { authorization: 'Bearer dev-token-secret', 'x-device-id': 'dev-device-100' }
    });

    expect(response.statusCode).toBe(200);
    const body = JSON.parse(response.payload);
    expect(body.status).toBe('SUCCESS');
    expect(body.data.products).toBeDefined();
    expect(body.data.latestVersionTimestamp).toBeDefined();
  });
});
