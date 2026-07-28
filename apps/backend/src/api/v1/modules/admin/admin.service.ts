export interface TenantFleetSummary {
  businessId: string;
  businessName: string;
  plan: string;
  activeOutletsCount: number;
  registeredDevicesCount: number;
  status: 'ACTIVE' | 'SUSPENDED';
}

export class AdminService {
  private tenants = new Map<string, TenantFleetSummary>([
    [
      'b18a42f5-31a8-4e12-a720-0021c4ef99a1',
      {
        businessId: 'b18a42f5-31a8-4e12-a720-0021c4ef99a1',
        businessName: 'Metro Cafe & Bakery Chain',
        plan: 'PRO_POS',
        activeOutletsCount: 4,
        registeredDevicesCount: 12,
        status: 'ACTIVE'
      }
    ]
  ]);

  async listAllTenants() {
    return Array.from(this.tenants.values());
  }

  async suspendTenant(businessId: string) {
    const tenant = this.tenants.get(businessId);
    if (!tenant) throw new Error('Tenant not found');
    tenant.status = 'SUSPENDED';
    return tenant;
  }
}
