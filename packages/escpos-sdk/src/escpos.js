"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.EscPosBuilder = void 0;
exports.generateEscPosReceiptBytecode = generateEscPosReceiptBytecode;
class EscPosBuilder {
    buffer = [];
    constructor() {
        this.reset();
    }
    reset() {
        this.buffer = [0x1b, 0x40]; // ESC @ Initialize Printer
        return this;
    }
    alignCenter() {
        this.buffer.push(0x1b, 0x61, 0x01); // ESC a 1
        return this;
    }
    alignLeft() {
        this.buffer.push(0x1b, 0x61, 0x00); // ESC a 0
        return this;
    }
    alignRight() {
        this.buffer.push(0x1b, 0x61, 0x02); // ESC a 2
        return this;
    }
    bold(enable) {
        this.buffer.push(0x1b, 0x45, enable ? 0x01 : 0x00); // ESC E n
        return this;
    }
    text(str) {
        const bytes = Array.from(Buffer.from(str, 'utf-8'));
        this.buffer.push(...bytes);
        return this;
    }
    textLine(str) {
        this.text(str);
        this.buffer.push(0x0a); // LF
        return this;
    }
    feed(lines = 1) {
        for (let i = 0; i < lines; i++) {
            this.buffer.push(0x0a);
        }
        return this;
    }
    divider(char = '-') {
        this.textLine(char.repeat(48));
        return this;
    }
    pulseCashDrawer() {
        this.buffer.push(0x1b, 0x70, 0x00, 0x19, 0xfa); // ESC p 0 25 250
        return this;
    }
    cutPaper() {
        this.buffer.push(0x1d, 0x56, 0x41, 0x00); // GS V A 0
        return this;
    }
    build() {
        return new Uint8Array(this.buffer);
    }
}
exports.EscPosBuilder = EscPosBuilder;
function generateEscPosReceiptBytecode(meta, cart) {
    const builder = new EscPosBuilder();
    // Header & Logo
    builder.alignCenter().bold(true).textLine(meta.businessName);
    builder.bold(false);
    if (meta.storeAddress)
        builder.textLine(meta.storeAddress);
    if (meta.storePhone)
        builder.textLine(`Tel: ${meta.storePhone}`);
    if (meta.taxNumber)
        builder.textLine(`Tax ID: ${meta.taxNumber}`);
    if (meta.isDuplicate) {
        builder.feed(1).bold(true).textLine("*** DUPLICATE COPY ***").bold(false);
    }
    builder.divider();
    // Invoice Details
    builder.alignLeft();
    builder.textLine(`Invoice #: ${meta.invoiceNumber}`);
    builder.textLine(`Cashier:   ${meta.cashierName}`);
    if (meta.customerName)
        builder.textLine(`Customer:  ${meta.customerName}`);
    builder.textLine(`Date:      ${new Date().toISOString().replace('T', ' ').substring(0, 19)}`);
    builder.divider();
    // Items Header
    builder.textLine("QTY  ITEM DESCRIPTION                  PRICE");
    builder.divider();
    // Itemized Rows
    cart.lineItems.forEach((item) => {
        const qtyStr = item.quantity.toString().padEnd(4);
        const titleStr = item.title.substring(0, 28).padEnd(28);
        const priceStr = `$${item.lineTotal.toFixed(2)}`.padStart(8);
        builder.textLine(`${qtyStr} ${titleStr} ${priceStr}`);
        if (item.notes) {
            builder.textLine(`     Note: ${item.notes}`);
        }
    });
    builder.divider();
    // Totals Section
    builder.alignRight();
    builder.textLine(`Subtotal:    $${cart.subtotal.toFixed(2)}`);
    if (cart.discountTotal > 0) {
        builder.textLine(`Discount:   -$${cart.discountTotal.toFixed(2)}`);
    }
    builder.textLine(`Tax (VAT/GST): $${cart.taxTotal.toFixed(2)}`);
    builder.bold(true).textLine(`TOTAL:       $${cart.grossTotal.toFixed(2)}`).bold(false);
    builder.divider();
    // Payment Tenders
    builder.alignLeft();
    let totalTendered = 0;
    meta.paymentTenders.forEach((tender) => {
        totalTendered += tender.amount;
        builder.textLine(`Tender (${tender.method}): $${tender.amount.toFixed(2)}`);
    });
    const changeDue = Math.max(0, totalTendered - cart.grossTotal);
    if (changeDue > 0) {
        builder.bold(true).textLine(`CHANGE DUE:     $${changeDue.toFixed(2)}`).bold(false);
    }
    builder.divider();
    // Footer & Cash Drawer Pulse
    builder.alignCenter();
    builder.textLine("Thank you for your business!");
    builder.textLine("Powered by Optix POS");
    builder.feed(3);
    builder.pulseCashDrawer();
    builder.cutPaper();
    return builder.build();
}
