-- Adiciona constraint de unicidade para (endereco_id, numero_endereco)
ALTER TABLE vaga
ADD CONSTRAINT uq_vaga_endereco_numero UNIQUE (endereco_id, numero_endereco);

-- Adiciona constraint de unicidade para (referencia_geo_inicio, referencia_geo_fim)
ALTER TABLE vaga
ADD CONSTRAINT uq_vaga_referencia_geo UNIQUE (referencia_geo_inicio, referencia_geo_fim);
