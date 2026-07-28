export interface ScaleTareInput {
  grossWeightKg: number;
  tareWeightKg: number;
  unitPricePerKg: number;
}

export interface YieldLossInput {
  liveWeightKg: number;
  dressedWeightKg: number;
  costPerLiveKg: number;
}

export class FreshProduceService {
  calculateScaleWeight(input: ScaleTareInput) {
    const netWeightKg = Math.max(0, input.grossWeightKg - input.tareWeightKg);
    const totalPrice = Number((netWeightKg * input.unitPricePerKg).toFixed(2));

    return {
      grossWeightKg: input.grossWeightKg,
      tareWeightKg: input.tareWeightKg,
      netWeightKg: Number(netWeightKg.toFixed(3)),
      unitPricePerKg: input.unitPricePerKg,
      totalPrice
    };
  }

  calculateYieldLoss(input: YieldLossInput) {
    const lossKg = Math.max(0, input.liveWeightKg - input.dressedWeightKg);
    const yieldPercentage = (input.dressedWeightKg / input.liveWeightKg) * 100;
    const totalLiveCost = input.liveWeightKg * input.costPerLiveKg;
    const adjustedCostPerDressedKg = totalLiveCost / input.dressedWeightKg;

    return {
      liveWeightKg: input.liveWeightKg,
      dressedWeightKg: input.dressedWeightKg,
      lossKg: Number(lossKg.toFixed(3)),
      yieldPercentage: Number(yieldPercentage.toFixed(2)),
      adjustedCostPerDressedKg: Number(adjustedCostPerDressedKg.toFixed(2))
    };
  }
}
