-- Atualiza a constraint CHECK da coluna status da tabela orders para incluir 'DESPACHADO'
-- Compatível com PostgreSQL

DO $$
DECLARE
    v_constraint_name text;
BEGIN
    -- Descobre o nome da constraint CHECK existente (se houver)
    SELECT con.conname INTO v_constraint_name
    FROM pg_constraint con
    JOIN pg_class rel ON rel.oid = con.conrelid
    JOIN pg_namespace nsp ON nsp.oid = rel.relnamespace
    WHERE rel.relname = 'orders'
      AND nsp.nspname = 'public'
      AND con.contype = 'c'
      AND pg_get_constraintdef(con.oid) ILIKE '%CHECK%status%';

    IF v_constraint_name IS NOT NULL THEN
        EXECUTE format('ALTER TABLE public.orders DROP CONSTRAINT %I', v_constraint_name);
    END IF;

    -- Cria nova constraint incluindo o novo valor
    ALTER TABLE public.orders
        ADD CONSTRAINT orders_status_check
        CHECK (status IN ('CREATED','PAID','CANCELED','DESPACHADO'));
END $$;