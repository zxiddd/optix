import { FastifyRequest, FastifyReply } from 'fastify';
import { UserRole } from '@prisma/client';

export type Permission =
  | 'BILL_CREATE'
  | 'BILL_VOID'
  | 'BILL_REFUND'
  | 'CATALOG_MANAGE'
  | 'SHIFT_OPEN_CLOSE'
  | 'CUSTOMER_MANAGE'
  | 'KHATA_REPAY'
  | 'REPORTS_VIEW'
  | 'SETTINGS_MANAGE';

const ROLE_PERMISSIONS: Record<UserRole, Permission[]> = {
  OWNER: [
    'BILL_CREATE', 'BILL_VOID', 'BILL_REFUND',
    'CATALOG_MANAGE', 'SHIFT_OPEN_CLOSE', 'CUSTOMER_MANAGE',
    'KHATA_REPAY', 'REPORTS_VIEW', 'SETTINGS_MANAGE'
  ],
  MANAGER: [
    'BILL_CREATE', 'BILL_VOID', 'BILL_REFUND',
    'CATALOG_MANAGE', 'SHIFT_OPEN_CLOSE', 'CUSTOMER_MANAGE',
    'KHATA_REPAY', 'REPORTS_VIEW'
  ],
  SUPERVISOR: [
    'BILL_CREATE', 'BILL_VOID', 'SHIFT_OPEN_CLOSE',
    'CUSTOMER_MANAGE', 'KHATA_REPAY'
  ],
  CASHIER: [
    'BILL_CREATE', 'SHIFT_OPEN_CLOSE', 'CUSTOMER_MANAGE'
  ],
  KITCHEN_STAFF: [],
  DELIVERY: [],
  ACCOUNTANT: [
    'REPORTS_VIEW', 'KHATA_REPAY'
  ]
};

export function requirePermission(requiredPermission: Permission) {
  return async (request: FastifyRequest, reply: FastifyReply) => {
    const userRole = (request.user?.role as UserRole) || 'CASHIER';
    const allowedPermissions = ROLE_PERMISSIONS[userRole] || [];

    if (!allowedPermissions.includes(requiredPermission)) {
      return reply.status(403).send({
        status: 'ERROR',
        error: {
          code: 'FORBIDDEN_PERMIT_DENIED',
          message: `User role '${userRole}' lacks permission '${requiredPermission}'.`
        }
      });
    }
  };
}
