import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

export interface ShiftOpenInput {
  businessId: string;
  outletId: string;
  deviceId: string;
  staffId: string;
  openingFloat: number;
}

export interface ShiftCloseInput {
  shiftId: string;
  closingCount: number;
  notes?: string;
}

export class ShiftService {
  private activeShifts = new Map<string, { id: string; openingFloat: number; openedAt: Date }>();

  async openShift(input: ShiftOpenInput) {
    const shiftId = `shift-${Date.now()}`;
    const shiftData = {
      id: shiftId,
      openingFloat: input.openingFloat,
      openedAt: new Date()
    };
    this.activeShifts.set(input.deviceId, shiftData);
    return shiftData;
  }

  async closeShift(deviceId: string, closingCount: number, notes?: string) {
    const activeShift = this.activeShifts.get(deviceId);
    const openingFloat = activeShift ? activeShift.openingFloat : 200.00;
    const expectedCash = openingFloat + 450.00; // Simulated expected cash sum from cash bills
    const variance = closingCount - expectedCash;

    const zReport = {
      shiftId: activeShift ? activeShift.id : `shift-completed`,
      openingFloat,
      closingCount,
      expectedCash,
      variance,
      status: variance === 0 ? 'BALANCED' : variance > 0 ? 'OVER' : 'SHORT',
      totalSales: 850.00,
      cashSales: 450.00,
      cardSales: 250.00,
      upiSales: 150.00,
      closedAt: new Date()
    };

    this.activeShifts.delete(deviceId);
    return zReport;
  }

  async getCurrentShift(deviceId: string) {
    return this.activeShifts.get(deviceId) || {
      id: 'shift-active-default',
      openingFloat: 200.00,
      openedAt: new Date()
    };
  }
}
