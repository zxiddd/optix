export type KdsStatus = 'NEW' | 'IN_PREPARATION' | 'READY_FOR_PICKUP' | 'SERVED';
export type TableStatus = 'VACANT' | 'OCCUPIED' | 'RESERVED';

export interface RestaurantTable {
  id: string;
  tableName: string;
  capacity: number;
  status: TableStatus;
  activeBillId?: string;
}

export interface KdsTicket {
  ticketId: string;
  orderNumber: string;
  tableName?: string;
  items: Array<{ title: string; quantity: number; notes?: string }>;
  status: KdsStatus;
  createdAt: Date;
}

export class RestaurantService {
  private tables = new Map<string, RestaurantTable>([
    ['t-1', { id: 't-1', tableName: 'Table 1 (Patio)', capacity: 4, status: 'VACANT' }],
    ['t-2', { id: 't-2', tableName: 'Table 2 (Main Hall)', capacity: 2, status: 'VACANT' }],
    ['t-3', { id: 't-3', tableName: 'Table 3 (Booth)', capacity: 6, status: 'VACANT' }]
  ]);

  private kdsTickets: KdsTicket[] = [];

  async listTables() {
    return Array.from(this.tables.values());
  }

  async occupyTable(tableId: string, activeBillId: string) {
    const table = this.tables.get(tableId);
    if (!table) throw new Error('Table not found');
    table.status = 'OCCUPIED';
    table.activeBillId = activeBillId;
    return table;
  }

  async splitTable(tableId: string, splitCount: number) {
    const table = this.tables.get(tableId);
    if (!table) throw new Error('Table not found');
    return {
      originalTableId: tableId,
      splitTablesCreated: Array.from({ length: splitCount }, (_, i) => ({
        subTableId: `${tableId}-sub-${i + 1}`,
        name: `${table.tableName} (Seat ${i + 1})`
      }))
    };
  }

  async mergeTables(primaryTableId: string, secondaryTableId: string) {
    const primary = this.tables.get(primaryTableId);
    const secondary = this.tables.get(secondaryTableId);
    if (!primary || !secondary) throw new Error('One or both tables not found');

    secondary.status = 'VACANT';
    secondary.activeBillId = undefined;
    primary.status = 'OCCUPIED';

    return {
      mergedTableId: primaryTableId,
      status: 'MERGED_SUCCESSFULLY',
      message: `Merged ${secondary.tableName} into ${primary.tableName}`
    };
  }

  async createKdsTicket(orderNumber: string, tableName: string | undefined, items: Array<{ title: string; quantity: number; notes?: string }>) {
    const ticket: KdsTicket = {
      ticketId: `kds-${Date.now()}`,
      orderNumber,
      tableName,
      items,
      status: 'NEW',
      createdAt: new Date()
    };
    this.kdsTickets.push(ticket);
    return ticket;
  }

  async updateKdsStatus(ticketId: string, status: KdsStatus) {
    const ticket = this.kdsTickets.find(t => t.ticketId === ticketId);
    if (!ticket) throw new Error('KDS Ticket not found');
    ticket.status = status;
    return ticket;
  }

  async listActiveKdsTickets() {
    return this.kdsTickets.filter(t => t.status !== 'SERVED');
  }
}
