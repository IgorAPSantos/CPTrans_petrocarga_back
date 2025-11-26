-- Adicionar campos de check-in na tabela reserva
ALTER TABLE reserva 
ADD COLUMN checked_in BOOLEAN DEFAULT false NOT NULL,
ADD COLUMN check_in_em TIMESTAMP WITH TIME ZONE;

-- Criar índice para otimizar queries do job de no-show
CREATE INDEX idx_reserva_noshow ON reserva (status, checked_in, inicio) WHERE status = 'ATIVA' AND checked_in = false;
