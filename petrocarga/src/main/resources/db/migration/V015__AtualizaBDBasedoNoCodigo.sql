-- V015: (Sintaxe Corrigida) Ajusta as tabelas endereco_vaga e vaga para refletir as entidades Java.

-- Ajustes na tabela: endereco_vaga
ALTER TABLE endereco_vaga
    DROP COLUMN cidade,
    DROP COLUMN estado,
    DROP COLUMN cep;

ALTER TABLE endereco_vaga
    ALTER COLUMN logradouro TYPE VARCHAR(100),
    ALTER COLUMN logradouro SET NOT NULL,
    ADD CONSTRAINT uk_endereco_vaga_logradouro UNIQUE (logradouro);

-- Adiciona a constraint de unicidade para codigo_pmp que já existe
ALTER TABLE endereco_vaga ADD CONSTRAINT uk_endereco_vaga_codigo_pmp UNIQUE (codigo_pmp);

ALTER TABLE endereco_vaga
    ALTER COLUMN bairro TYPE VARCHAR(50),
    ALTER COLUMN bairro SET NOT NULL;


-- Ajustes na tabela: vaga
ALTER TABLE vaga ADD COLUMN comprimento INTEGER NOT NULL;
ALTER TABLE vaga DROP COLUMN autorizacao_id;
ALTER TABLE vaga RENAME COLUMN localizacao_descricao TO localizacao;
ALTER TABLE vaga RENAME COLUMN max_veiculos TO max_eixos;

ALTER TABLE vaga
    ALTER COLUMN localizacao TYPE VARCHAR(50),
    ALTER COLUMN localizacao SET NOT NULL,
    ADD CONSTRAINT uk_vaga_localizacao UNIQUE (localizacao);

ALTER TABLE vaga
    ALTER COLUMN horario_inicio SET NOT NULL,
    ALTER COLUMN horario_fim SET NOT NULL,
    ALTER COLUMN max_eixos SET NOT NULL,
    ALTER COLUMN status SET NOT NULL;


-- Criação da tabela para o relacionamento @ElementCollection de dias da semana
CREATE TABLE vaga_dias_semana (
    vaga_id UUID NOT NULL,
    dia_semana VARCHAR(255) NOT NULL,
    CONSTRAINT fk_vaga_dias_semana_vaga FOREIGN KEY (vaga_id) REFERENCES vaga (id)
);