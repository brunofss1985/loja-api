-- Força recriação da CHECK constraint para incluir DESPACHADO
-- Baseline-on-migrate pode ter marcado V1 como aplicada em bancos já existentes

ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_status_check;

ALTER TABLE orders
  ADD CONSTRAINT orders_status_check
  CHECK (status IN ('CREATED','PAID','CANCELED','DESPACHADO'));
