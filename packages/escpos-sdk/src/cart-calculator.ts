export interface CartLineItem {
  id: string;
  productId: string;
  title: string;
  unitPrice: number;
  quantity: number;
  taxRatePercent?: number; // e.g. 10 for 10% VAT/GST
  discountAmount?: number; // Line item discount
  notes?: string;
}

export interface CartCalculationResult {
  subtotal: number;
  taxTotal: number;
  discountTotal: number;
  grossTotal: number;
  lineItems: Array<CartLineItem & { lineTotal: number }>;
}

export function roundHalfEven(value: number, decimals = 2): number {
  const factor = Math.pow(10, decimals);
  const temp = value * factor;
  const rounded = Math.round(temp);
  return Number((rounded / factor).toFixed(decimals));
}

export function calculateCart(
  items: CartLineItem[],
  globalDiscountAmount = 0,
  globalTaxPercent = 10
): CartCalculationResult {
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
