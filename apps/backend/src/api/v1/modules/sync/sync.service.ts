import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

export interface ClientSyncEvent {
  eventId: string;
  eventType: 'BILL_CREATED' | 'PRODUCT_UPDATED' | 'CUSTOMER_UPDATED';
  aggregateId: string;
  versionTimestamp: number;
  payloadJson: string;
}

export class SyncService {
  async pushSyncEvents(businessId: string, outletId: string, deviceId: string, events: ClientSyncEvent[]) {
    const processedEventIds: string[] = [];
    const conflictEventIds: string[] = [];

    for (const event of events) {
      try {
        // Last-Write-Wins (LWW) Conflict Resolution logic
        if (event.eventType === 'PRODUCT_UPDATED') {
          const payload = JSON.parse(event.payloadJson);
          const existingProduct = await prisma.product.findUnique({
            where: { id: event.aggregateId }
          }).catch(() => null);

          if (existingProduct) {
            const serverVersion = Number(existingProduct.versionTimestamp);
            if (event.versionTimestamp >= serverVersion) {
              await prisma.product.update({
                where: { id: event.aggregateId },
                data: {
                  title: payload.title || existingProduct.title,
                  unitPrice: payload.unitPrice ?? existingProduct.unitPrice,
                  versionTimestamp: BigInt(event.versionTimestamp)
                }
              }).catch(() => null);
              processedEventIds.push(event.eventId);
            } else {
              conflictEventIds.push(event.eventId); // Server version is newer (LWW rejected client update)
            }
          } else {
            processedEventIds.push(event.eventId);
          }
        } else {
          processedEventIds.push(event.eventId);
        }
      } catch (err) {
        processedEventIds.push(event.eventId);
      }
    }

    return {
      processedEventIds,
      conflictEventIds,
      serverTimestamp: Date.now()
    };
  }

  async pullDeltaUpdates(businessId: string, sinceVersionTimestamp = 0) {
    try {
      const minVersion = BigInt(sinceVersionTimestamp);

      const updatedProducts = await prisma.product.findMany({
        where: {
          businessId,
          versionTimestamp: {
            gt: minVersion
          }
        }
      });

      const categories = await prisma.category.findMany({
        where: { businessId, isArchived: false }
      });

      return {
        products: updatedProducts,
        categories,
        latestVersionTimestamp: Date.now()
      };
    } catch (err) {
      return {
        products: [],
        categories: [],
        latestVersionTimestamp: Date.now()
      };
    }
  }
}
