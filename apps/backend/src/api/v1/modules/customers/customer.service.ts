import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

export interface CreateCustomerInput {
  businessId: string;
  fullName: string;
  phone: string;
  email?: string;
  creditLimit?: number;
}

export class CustomerService {
  async listCustomers(businessId: string, search?: string) {
    return prisma.customer.findMany({
      where: {
        businessId,
        ...(search
          ? {
              OR: [
                { fullName: { contains: search, mode: 'insensitive' } },
                { phone: { contains: search } }
              ]
            }
          : {})
      },
      orderBy: { fullName: 'asc' }
    });
  }

  async createCustomer(input: CreateCustomerInput) {
    return prisma.customer.create({
      data: {
        businessId: input.businessId,
        fullName: input.fullName,
        phone: input.phone,
        email: input.email || null,
        creditLimit: input.creditLimit || 0.00
      }
    });
  }

  async repayKhata(businessId: string, customerId: string, amount: number) {
    return prisma.customer.update({
      where: { id: customerId },
      data: {
        currentBalance: {
          decrement: amount
        },
        loyaltyPoints: {
          increment: Math.floor(amount)
        }
      }
    });
  }
}
