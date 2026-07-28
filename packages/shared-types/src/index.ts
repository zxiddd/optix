export type BusinessType = 'RESTAURANT' | 'CHICKEN_SHOP' | 'BAKERY' | 'MEDICAL' | 'RETAIL' | 'SALON';
export type PricingStrategy = 'FIXED' | 'WEIGHT' | 'VARIABLE' | 'MARKET';
export type BillStatus = 'DRAFT' | 'FINALIZED' | 'VOIDED' | 'REFUNDED';
export type UserRole = 'OWNER' | 'MANAGER' | 'SUPERVISOR' | 'CASHIER' | 'KITCHEN_STAFF' | 'DELIVERY' | 'ACCOUNTANT';
export type PaymentMethod = 'CASH' | 'CARD_TENDER' | 'DIGITAL_UPI_QR' | 'STORE_CREDIT_KHATA' | 'LOYALTY_POINTS';

export interface BaseSyncMetadata {
  createdAt: string;
  updatedAt: string;
  deletedAt?: string | null;
  versionTimestamp: number;
  deviceId: string;
}

export interface BusinessProfile {
  id: string;
  name: string;
  businessType: BusinessType;
  currencyCode: string;
  currencySymbol: string;
  decimalPrecision: number;
  timeZone: string;
  isActive: boolean;
}

export interface ProductDto {
  id: string;
  categoryId?: string | null;
  sku?: string | null;
  barcode?: string | null;
  title: string;
  pricingStrategy: PricingStrategy;
  unitPrice: number;
  costPrice: number;
  trackInventory: boolean;
  currentStock: number;
  reorderLevel: number;
  isArchived: boolean;
  versionTimestamp: number;
}

export interface BillItemDto {
  id: string;
  productId?: string | null;
  productNameSnapshot: string;
  skuSnapshot?: string | null;
  unitPrice: number;
  quantity: number;
  lineTotal: number;
}

export interface PaymentDto {
  id: string;
  paymentMethod: PaymentMethod;
  amount: number;
  transactionReference?: string | null;
}

export interface BillDto {
  id: string;
  invoiceNumber: string;
  staffId: string;
  customerId?: string | null;
  subtotal: number;
  taxTotal: number;
  discountTotal: number;
  grossTotal: number;
  status: BillStatus;
  items: BillItemDto[];
  payments: PaymentDto[];
  createdAt: string;
}

export interface SyncPushEventDto {
  eventId: string;
  entityType: 'BILL' | 'PRODUCT' | 'CUSTOMER' | 'SHIFT';
  actionType: 'CREATE' | 'UPDATE' | 'ARCHIVE';
  timestamp: number;
  payload: Record<string, unknown>;
}

export interface SyncPushRequestDto {
  deviceId: string;
  batchId: string;
  events: SyncPushEventDto[];
}

export interface SyncPushResponseDto {
  status: 'PROCESSED' | 'FAILED';
  batchId: string;
  processedCount: number;
  failedEvents: Array<{ eventId: string; error: string }>;
  serverTimestamp: number;
}
