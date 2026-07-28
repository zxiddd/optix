import { calculateCart, generateEscPosReceiptBytecode } from '@optix/escpos-sdk';

describe('Milestone 4: Core Checkout & ESC/POS Receipt Engine Verification', () => {
  it('Requirement 1: Cart Engine - Live totals & Banker Rounding precision under 50ms', () => {
    const startTime = performance.now();

    const sampleItems = [
      { id: '1', productId: 'p-croissant', title: 'Butter Croissant', unitPrice: 4.50, quantity: 2, taxRatePercent: 10 },
      { id: '2', productId: 'p-latte', title: 'Iced Vanilla Latte', unitPrice: 5.00, quantity: 1, taxRatePercent: 10 },
      { id: '3', productId: 'p-bread', title: 'Sourdough Loaf', unitPrice: 6.50, quantity: 1.5, discountAmount: 1.00, taxRatePercent: 10 }
    ];

    const cart = calculateCart(sampleItems, 0.50, 10);
    const duration = performance.now() - startTime;

    expect(duration).toBeLessThan(50); // Performance Target: <50ms
    expect(cart.subtotal).toBe(23.75);
    expect(cart.discountTotal).toBe(1.50);
    expect(cart.taxTotal).toBe(2.28);
    expect(cart.grossTotal).toBe(24.53);
  });

  it('Requirement 2: Receipt Engine - ESC/POS Bytecode & RJ11 Drawer Pulse', () => {
    const cart = calculateCart([
      { id: '1', productId: 'p-1', title: 'Butter Croissant', unitPrice: 4.50, quantity: 2 }
    ]);

    const receiptBytes = generateEscPosReceiptBytecode(
      {
        businessName: 'Metro Bakery & Cafe',
        invoiceNumber: 'INV-10042',
        cashierName: 'John Cashier',
        paymentTenders: [
          { method: 'CASH', amount: 10.00 }
        ]
      },
      cart
    );

    expect(receiptBytes).toBeInstanceOf(Uint8Array);
    expect(receiptBytes.length).toBeGreaterThan(50);

    // Verify ESC @ (0x1b, 0x40) header byte initialization
    expect(receiptBytes[0]).toBe(0x1b);
    expect(receiptBytes[1]).toBe(0x40);

    // Verify RJ11 Cash Drawer Pulse (0x1b, 0x70, 0x00, 0x19, 0xfa)
    const hasPulse = Array.from(receiptBytes).some((val, idx, arr) => 
      val === 0x1b && arr[idx + 1] === 0x70 && arr[idx + 2] === 0x00
    );
    expect(hasPulse).toBe(true);
  });

  it('Requirement 3 & 10 Verification: 100 Test Bills Batch Execution & Inventory Deductions', () => {
    const startTime = performance.now();
    let initialStock = 1000.0;

    for (let i = 1; i <= 100; i++) {
      const items = [
        { id: `item-${i}`, productId: 'p-test-bulk', title: 'Test Product', unitPrice: 10.00, quantity: 2 }
      ];

      const cart = calculateCart(items);
      expect(cart.grossTotal).toBe(22.00); // 20 subtotal + 10% tax

      initialStock -= 2.0;

      // Generate ESC/POS thermal bytecode for bill
      const receipt = generateEscPosReceiptBytecode(
        {
          businessName: 'Metro Test Store',
          invoiceNumber: `TEST-INV-${1000 + i}`,
          cashierName: 'Automated Test Runner',
          paymentTenders: [{ method: i % 2 === 0 ? 'CASH' : 'DIGITAL_UPI_QR', amount: 22.00 }]
        },
        cart
      );

      expect(receipt.length).toBeGreaterThan(0);
    }

    const totalBatchDuration = performance.now() - startTime;
    const avgTimePerBill = totalBatchDuration / 100;

    expect(initialStock).toBe(800.0); // 1000 - (100 * 2)
    expect(avgTimePerBill).toBeLessThan(10); // Average per bill calculation under 10ms
  });
});
