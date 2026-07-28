"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.roundHalfEven = roundHalfEven;
exports.calculateCart = calculateCart;
function roundHalfEven(value, decimals = 2) {
    const factor = Math.pow(10, decimals);
    const temp = value * factor;
    const rounded = Math.round(temp);
    return Number((rounded / factor).toFixed(decimals));
}
function calculateCart(items, globalDiscountAmount = 0, globalTaxPercent = 10) {
    let subtotal = 0;
    let lineTaxSum = 0;
    let lineDiscountSum = 0;
    const processedItems = items.map((item) => {
        const rawLineSubtotal = item.unitPrice * item.quantity;
        const discount = item.discountAmount || 0;
        const lineAfterDiscount = Math.max(0, rawLineSubtotal - discount);
        const taxRate = item.taxRatePercent !== undefined ? item.taxRatePercent : globalTaxPercent;
        const lineTax = lineAfterDiscount * (taxRate / 100);
        const lineTotal = roundHalfEven(lineAfterDiscount + lineTax);
        subtotal += rawLineSubtotal;
        lineDiscountSum += discount;
        lineTaxSum += lineTax;
        return {
            ...item,
            lineTotal
        };
    });
    const totalDiscount = roundHalfEven(lineDiscountSum + globalDiscountAmount);
    const taxTotal = roundHalfEven(lineTaxSum);
    const grossTotal = roundHalfEven(Math.max(0, subtotal - totalDiscount + taxTotal));
    return {
        subtotal: roundHalfEven(subtotal),
        taxTotal,
        discountTotal: totalDiscount,
        grossTotal,
        lineItems: processedItems
    };
}
