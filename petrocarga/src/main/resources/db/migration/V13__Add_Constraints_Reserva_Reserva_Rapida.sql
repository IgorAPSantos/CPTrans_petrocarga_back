CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE reserva
DROP CONSTRAINT IF EXISTS chk_reserva_intervalo_valido;

ALTER TABLE reserva
DROP CONSTRAINT IF EXISTS no_overlap_veiculo_ativo;

ALTER TABLE reserva
DROP CONSTRAINT IF EXISTS no_overlap_motorista_ativo;

ALTER TABLE reserva_rapida
DROP CONSTRAINT IF EXISTS no_overlap_placa_ativa;


ALTER TABLE reserva
ADD CONSTRAINT chk_reserva_intervalo_valido
CHECK (fim > inicio);


ALTER TABLE reserva
ADD CONSTRAINT no_overlap_veiculo_ativo
EXCLUDE USING gist (
    veiculo_id WITH =,
    tstzrange(inicio, fim, '[)') WITH &&
)
WHERE (status IN ('ATIVA', 'RESERVADA'));


ALTER TABLE reserva
ADD CONSTRAINT no_overlap_motorista_ativo
EXCLUDE USING gist (
    motorista_id WITH =,
    tstzrange(inicio, fim, '[)') WITH &&
)
WHERE (status IN ('ATIVA', 'RESERVADA'));


ALTER TABLE reserva_rapida
ADD CONSTRAINT no_overlap_placa_ativa
EXCLUDE USING gist (
    placa WITH =,
    tstzrange(inicio, fim, '[)') WITH &&
)
WHERE (status IN ('ATIVA', 'RESERVADA'));