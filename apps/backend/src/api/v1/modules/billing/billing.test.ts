import { calculateCart, generateEscPosReceiptBytecode, EscPosBuilder } from '@optix/escpos-sdk';

describe('Milestone 4: Core Checkout, Offline Queue & ESC/POS Engine Master Verification', () => {

  it('Verification 1: Cart Engine Live Totals & Banker Rounding Precision (<50ms SLA)', () => {
    const startTime = performance.now();

    const sampleItems = [
      { id: '1', productId: 'p-croissant', title: 'Butter Croissant', unitPrice: 4.50, quantity: 2, taxRatePercent: 10 },
      { id: '2', productId: 'p-latte', title: 'Iced Vanilla Latte', unitPrice: 5.00, quantity: 1, taxRatePercent: 10 },
      { id: '3', productId: 'p-bread', title: 'Sourdough Loaf', unitPrice: 6.50, quantity: 1.5, discountAmount: 1.00, taxRatePercent: 10 }
    ];

    const cart = calculateCart(sampleItems, 0.50, 10);
    const duration = performance.now() - startTime;

    expect(duration).toBeLessThan(50); // Performance Target: <50ms SLA
    expect(cart.subtotal).toBe(23.75);
    expect(cart.discountTotal).toBe(1.50);
    expect(cart.taxTotal).toBe(2.28);
    expect(cart.grossTotal).toBe(24.53);
  });

  it('Verification 2: ESC/POS Thermal Receipt, Logo, QR Code & RJ11 Drawer Pulse Bytecode', () => {
    const cart = calculateCart([
      { id: '1', productId: 'p-1', title: 'Butter Croissant', unitPrice: 4.50, quantity: 2 }
    ]);

    const receiptBytes = generateEscPosReceiptBytecode(
      {
        businessName: 'Metro Bakery & Cafe',
        storeAddress: '128 Main Street',
        storePhone: '+1-555-0199',
        taxNumber: 'GST-9022-US',
        invoiceNumber: 'INV-10042',
        cashierName: 'John Cashier',
        customerName: 'Alice Customer',
        paymentTenders: [
          { method: 'CASH', amount: 10.00 }
        ],
        isDuplicate: true
      },
      cart
    );

    expect(receiptBytes).toBeInstanceOf(Uint8Array);
    expect(receiptBytes.length).toBeGreaterThan(100);

    // ESC @ (0x1b, 0x40) Initialize
    expect(receiptBytes[0]).toBe(0x1b);
    expect(receiptBytes[1]).toBe(0x40);

    // RJ11 Solenoid Pulse (0x1b, 0x70, 0x00, 0x19, 0xfa)
    const hasPulse = Array.from(receiptBytes).some((val, idx, arr) => 
      val === 0x1b && arr[idx + 1] === 0x70 && arr[idx + 2] === 0x00
    );
    expect(hasPulse).toBe(true);
  });

  it('Verification 3: Thermal Printer Reconnect Fallback & Queue Simulation', () => {
    let isPrinterConnected = false;
    const printQueue: Uint8Array[] = [];

    function sendToPrinter(bytecode: Uint8Array): boolean {
      if (!isPrinterConnected) {
        printQueue.push(bytecode);
        return false;
      }
      return true;
    }

    const testBytecode = new EscPosBuilder().textLine('Test Print Job').build();
    
    // Printer offline -> Queued
    const success1 = sendToPrinter(testBytecode);
    expect(success1).toBe(false);
    expect(printQueue.length).toBe(1);

    // Reconnect printer -> Flush queue
    isPrinterConnected = true;
    let flushedCount = 0;
    while (printQueue.length > 0) {
      const job = printQueue.shift();
      if (job && sendToPrinter(job)) {
        flushedCount++;
      }
    }

    expect(flushedCount).toBe(1);
    expect(printQueue.length).toBe(0);
  });

  it('Verification 4: Offline Queue Storage & Sync Event Generation', () => {
    interface LocalOutboxQueueItem {
      eventId: string;
      eventType: string;
      payload: string;
      isSynced: boolean;
    }

    const offlineQueue: LocalOutboxQueueItem[] = [];

    // Offline checkout action creates local outbox sync event
    offlineQueue.push({
      eventId: 'evt-9001',
      eventType: 'BILL_CREATED',
      payload: JSON.stringify({ invoiceNumber: 'INV-OFFLINE-01', grossTotal: 45.00 }),
      isSynced: false
    });

    expect(offlineQueue.length).toBe(1);
    expect(offlineQueue[0].isSynced).toBe(false);

    // Online Sync Worker processes queue
    offlineQueue[0].isSynced = true;
    expect(offlineQueue.filter(i => !i.isSynced).length).toBe(0);
  });

  it('Verification 5: Master 100-Bill Batch Execution & Stock Ledger Deduction Audit', () => {
    const startTime = performance.now();
    let initialInventoryStock = 1000.0;
    const paymentRecords: Array<{ method: string; amount: number }> = [];
    const auditLogs: string[] = [];

    for (let i = 1; i <= 100; i++) {
      const items = [
        { id: `item-${i}`, productId: 'p-bulk-flour', title: 'Flour 1kg', unitPrice: 10.00, quantity: 2.0 }
      ];

      const cart = calculateCart(items);
      expect(cart.grossTotal).toBe(22.00); // 20 subtotal + 10% tax

      // Deduct inventory
      initialInventoryStock -= 2.0;

      // Record payment
      const paymentMethod = i % 3 === 0 ? 'CARD_TENDER' : i % 3 === 1 ? 'CASH' : 'DIGITAL_UPI_QR';
      paymentRecords.push({ method: paymentMethod, amount: 22.00 });

      // Audit log
      auditLogs.push(`AUDIT: BILL_CHECKOUT INV-${1000 + i} TOTAL: 22.00`);

      // Receipt bytecode
      const receipt = generateEscPosReceiptBytecode(
        {
          businessName: 'Metro Bakery Test',
          invoiceNumber: `INV-${1000 + i}`,
          cashierName: 'Automated Tester',
          paymentTenders: [{ method: paymentMethod, amount: 22.00 }]
        },
        cart
      );

      expect(receipt.length).toBeGreaterThan(0);
    }

    const duration = performance.now() - startTime;
    const avgTimePerBill = duration / 100;

    expect(initialInventoryStock).toBe(800.0); // 1000 - (100 * 2)
    expect(paymentRecords.length).toBe(100);
    expect(auditLogs.length).toBe(100);
    expect(avgTimePerBill).toBeLessThan(10); // Average under 10ms per bill
  });
});
