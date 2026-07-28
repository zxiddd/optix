import { PrismaClient } from '@prisma/client';
import { calculateCart, CartLineItem } from '@optix/escpos-sdk';

const prisma = new PrismaClient();

export interface CheckoutInput {
  businessId: string;
  outletId: string;
  deviceId: string;
  staffId: string;
  customerId?: string;
  invoiceNumber: string;
  items: CartLineItem[];
  payments: Array<{ method: 'CASH' | 'CARD_TENDER' | 'DIGITAL_UPI_QR' | 'STORE_CREDIT_KHATA' | 'LOYALTY_POINTS'; amount: number }>;
  globalDiscountAmount?: number;
  globalTaxPercent?: number;
}

export class BillingService {
  async checkout(input: CheckoutInput) {
    // 1. Execute Cart Computation Engine (<50ms)
    const cart = calculateCart(input.items, input.globalDiscountAmount || 0, input.globalTaxPercent || 10);

    // 2. Perform Interactive Prisma Transaction ($transaction)
    return prisma.$transaction(async (tx) => {
      // Create Immutable Bill Record with Items and Payments
      const bill = await tx.bill.create({
        data: {
          businessId: input.businessId,
          outletId: input.outletId,
          deviceId: input.deviceId,
          invoiceNumber: input.invoiceNumber,
          staffId: input.staffId,
          customerId: input.customerId || null,
          subtotal: cart.subtotal,
          taxTotal: cart.taxTotal,
          discountTotal: cart.discountTotal,
          grossTotal: cart.grossTotal,
          status: 'FINALIZED',
          items: {
            create: cart.lineItems.map((item) => ({
              productId: item.productId,
              productNameSnapshot: item.title,
              skuSnapshot: item.id,
              unitPrice: item.unitPrice,
              quantity: item.quantity,
              lineTotal: item.lineTotal
            }))
          },
          payments: {
            create: input.payments.map((p) => ({
              paymentMethod: p.method,
              amount: p.amount
            }))
          }
        },
        include: {
          items: true,
          payments: true
        }
      });

      // Deduct Inventory Stock for Each Item
      for (const item of cart.lineItems) {
        if (item.productId) {
          await tx.product.update({
            where: { id: item.productId },
            data: {
              currentStock: {
                decrement: item.quantity
              }
            }
          });
        }
      }

      return {
        bill,
        cart
      };
    });
  }
}
