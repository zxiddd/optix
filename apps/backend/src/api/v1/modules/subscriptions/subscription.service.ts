export type SubscriptionPlan = 'BASIC_STARTER' | 'PRO_POS' | 'ENTERPRISE_CHAIN';

export interface SubscriptionRecord {
  businessId: string;
  plan: SubscriptionPlan;
  status: 'ACTIVE' | 'PAST_DUE' | 'EXPIRED' | 'SUSPENDED';
  validUntil: Date;
  lastOnlineSyncAt: Date;
  maxAllowedDevices: number;
}

export class SubscriptionService {
  private subscriptions = new Map<string, SubscriptionRecord>([
    [
      'b18a42f5-31a8-4e12-a720-0021c4ef99a1',
      {
        businessId: 'b18a42f5-31a8-4e12-a720-0021c4ef99a1',
        plan: 'PRO_POS',
        status: 'ACTIVE',
        validUntil: new Date('2027-12-31'),
        lastOnlineSyncAt: new Date(),
        maxAllowedDevices: 10
      }
    ]
  ]);

  async getSubscriptionStatus(businessId: string) {
    const sub = this.subscriptions.get(businessId) || {
      businessId,
      plan: 'PRO_POS' as SubscriptionPlan,
      status: 'ACTIVE' as const,
      validUntil: new Date('2027-12-31'),
      lastOnlineSyncAt: new Date(),
      maxAllowedDevices: 5
    };

    const now = new Date();
    const daysSinceLastSync = Math.floor((now.getTime() - sub.lastOnlineSyncAt.getTime()) / (1000 * 60 * 60 * 24));
    const offlineGraceDaysRemaining = Math.max(0, 7 - daysSinceLastSync);

    return {
      subscription: sub,
      offlineGraceDaysRemaining,
      isOfflineGraceExpired: offlineGraceDaysRemaining <= 0,
      canProcessTransactions: sub.status === 'ACTIVE' && offlineGraceDaysRemaining > 0
    };
  }

  async renewSubscription(businessId: string, plan: SubscriptionPlan, monthsToAdd = 12) {
    const sub = await this.getSubscriptionStatus(businessId);
    const updatedValidUntil = new Date(sub.subscription.validUntil);
    updatedValidUntil.setMonth(updatedValidUntil.getMonth() + monthsToAdd);

    const updatedRecord: SubscriptionRecord = {
      ...sub.subscription,
      plan,
      status: 'ACTIVE',
      validUntil: updatedValidUntil,
      lastOnlineSyncAt: new Date()
    };

    this.subscriptions.set(businessId, updatedRecord);
    return updatedRecord;
  }
}
