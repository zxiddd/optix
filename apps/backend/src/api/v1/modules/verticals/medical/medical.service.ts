export interface MedicineBatch {
  batchNumber: string;
  genericSalt: string;
  brandName: string;
  expiryDate: Date;
  stockCount: number;
}

export class MedicalService {
  private inventory: MedicineBatch[] = [
    {
      batchNumber: 'BATCH-2024-A',
      genericSalt: 'Paracetamol 500mg',
      brandName: 'Crocin Pain Relief',
      expiryDate: new Date('2026-12-31'),
      stockCount: 150
    },
    {
      batchNumber: 'BATCH-EXPIRED-99',
      genericSalt: 'Amoxicillin 250mg',
      brandName: 'Mox 250 Capsule',
      expiryDate: new Date('2023-01-01'), // Expired batch
      stockCount: 20
    }
  ];

  async searchBySalt(genericSaltQuery: string) {
    return this.inventory.filter(item =>
      item.genericSalt.toLowerCase().includes(genericSaltQuery.toLowerCase())
    );
  }

  async validateBatchForCheckout(batchNumber: string) {
    const batch = this.inventory.find(b => b.batchNumber === batchNumber);
    if (!batch) throw new Error('Medicine batch not found');

    const now = new Date();
    if (batch.expiryDate < now) {
      throw new Error(`SAFETY LOCK: Batch '${batch.batchNumber}' expired on ${batch.expiryDate.toISOString().substring(0, 10)}. Checkout is strictly forbidden.`);
    }

    return {
      isValid: true,
      batch
    };
  }
}
