# 01 - Production PostgreSQL Database DDL & Schema Architecture

## Purpose
This document provides the production-grade PostgreSQL 16 database Data Definition Language (DDL) scripts, relational schemas, composite indexes, foreign key constraints, triggers, functions, and Row-Level Security (RLS) policies for the **Optix** central cloud database.

---

## Complete PostgreSQL 16 DDL Script

```sql
-- Optix Production Database DDL Schema Specification
-- Target: PostgreSQL 16+

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Enum Definitions
CREATE TYPE business_type_enum AS ENUM (
    'RESTAURANT', 'CHICKEN_SHOP', 'BAKERY', 'MEDICAL', 'RETAIL', 'SALON'
);

CREATE TYPE pricing_strategy_enum AS ENUM (
    'FIXED', 'WEIGHT', 'VARIABLE', 'MARKET'
);

CREATE TYPE bill_status_enum AS ENUM (
    'DRAFT', 'FINALIZED', 'VOIDED', 'REFUNDED'
);

CREATE TYPE user_role_enum AS ENUM (
    'OWNER', 'MANAGER', 'SUPERVISOR', 'CASHIER', 'KITCHEN_STAFF', 'DELIVERY', 'ACCOUNTANT'
);

-- Table: businesses (Tenants)
CREATE TABLE businesses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    business_type business_type_enum NOT NULL DEFAULT 'RETAIL',
    currency_code VARCHAR(3) NOT NULL DEFAULT 'USD',
    time_zone VARCHAR(50) NOT NULL DEFAULT 'UTC',
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: outlets (Store Branches)
CREATE TABLE outlets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_id UUID NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    phone VARCHAR(30),
    tax_number VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: users (Staff Accounts)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_id UUID NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
    firebase_uid VARCHAR(128) UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    role user_role_enum NOT NULL DEFAULT 'CASHIER',
    pin_hash VARCHAR(255) NOT NULL,
    is_archived BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: categories
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_id UUID NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    color_hex VARCHAR(7) DEFAULT '#4A90E2',
    display_order INT DEFAULT 0,
    is_archived BOOLEAN NOT NULL DEFAULT false,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: products
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_id UUID NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
    category_id UUID REFERENCES categories(id) ON DELETE SET NULL,
    sku VARCHAR(100),
    barcode VARCHAR(100),
    title VARCHAR(255) NOT NULL,
    pricing_strategy pricing_strategy_enum NOT NULL DEFAULT 'FIXED',
    unit_price NUMERIC(12, 4) NOT NULL DEFAULT 0.0000,
    cost_price NUMERIC(12, 4) NOT NULL DEFAULT 0.0000,
    track_inventory BOOLEAN NOT NULL DEFAULT true,
    current_stock NUMERIC(12, 3) NOT NULL DEFAULT 0.000,
    reorder_level NUMERIC(12, 3) NOT NULL DEFAULT 5.000,
    is_archived BOOLEAN NOT NULL DEFAULT false,
    version_timestamp BIGINT NOT NULL DEFAULT extract(epoch from now()) * 1000,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: customers
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_id UUID NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
    full_name VARCHAR(150) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(150),
    credit_limit NUMERIC(12, 4) NOT NULL DEFAULT 0.0000,
    current_balance NUMERIC(12, 4) NOT NULL DEFAULT 0.0000,
    loyalty_points INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: bills (Financial Header)
CREATE TABLE bills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    business_id UUID NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
    outlet_id UUID NOT NULL REFERENCES outlets(id) ON DELETE CASCADE,
    device_id VARCHAR(100) NOT NULL,
    invoice_number VARCHAR(50) NOT NULL,
    staff_id UUID NOT NULL REFERENCES users(id),
    customer_id UUID REFERENCES customers(id),
    subtotal NUMERIC(12, 4) NOT NULL,
    tax_total NUMERIC(12, 4) NOT NULL,
    discount_total NUMERIC(12, 4) NOT NULL DEFAULT 0.0000,
    gross_total NUMERIC(12, 4) NOT NULL,
    status bill_status_enum NOT NULL DEFAULT 'FINALIZED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_business_invoice UNIQUE(business_id, invoice_number)
);

-- Table: bill_items (Financial Line Items)
CREATE TABLE bill_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bill_id UUID NOT NULL REFERENCES bills(id) ON DELETE CASCADE,
    product_id UUID REFERENCES products(id),
    product_name_snapshot VARCHAR(255) NOT NULL,
    unit_price NUMERIC(12, 4) NOT NULL,
    quantity NUMERIC(12, 3) NOT NULL,
    line_total NUMERIC(12, 4) NOT NULL
);

-- Composite Indexes
CREATE INDEX idx_products_biz_sku ON products(business_id, sku);
CREATE INDEX idx_products_biz_barcode ON products(business_id, barcode);
CREATE INDEX idx_products_sync ON products(business_id, version_timestamp);
CREATE INDEX idx_bills_biz_created ON bills(business_id, created_at);
CREATE INDEX idx_customers_biz_phone ON customers(business_id, phone);

-- Immutability Trigger on Finalized Bills
CREATE OR REPLACE FUNCTION enforce_bill_immutability()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.status = 'FINALIZED' AND (NEW.gross_total <> OLD.gross_total OR NEW.subtotal <> OLD.subtotal) THEN
        RAISE EXCEPTION 'Finalized transaction headers are immutable.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_bill_immutability
BEFORE UPDATE ON bills
FOR EACH ROW EXECUTE FUNCTION enforce_bill_immutability();
```

---

## Row-Level Security (RLS) Policies

```sql
-- Enable Row-Level Security on multi-tenant tables
ALTER TABLE products ENABLE ROW LEVEL SECURITY;
ALTER TABLE bills ENABLE ROW LEVEL SECURITY;

CREATE POLICY product_tenant_isolation ON products
    FOR ALL
    USING (business_id = current_setting('app.current_business_id')::uuid);

CREATE POLICY bill_tenant_isolation ON bills
    FOR ALL
    USING (business_id = current_setting('app.current_business_id')::uuid);
```
