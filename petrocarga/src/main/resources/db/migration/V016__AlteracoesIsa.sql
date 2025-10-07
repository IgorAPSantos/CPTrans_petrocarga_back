

CREATE TYPE dia_da_semana AS ENUM (
    'SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO', 'DOMINGO'
);

CREATE TYPE categoria_cnh AS ENUM (
    'A', 'B', 'C', 'D', 'E', 'AB', 'AC', 'AD', 'AE'
);



ALTER TABLE vaga
    ADD COLUMN tipo_vaga VARCHAR(255),
    ADD COLUMN referencia_endereco VARCHAR(255),
    ADD COLUMN numero_endereco VARCHAR(255),
    ADD COLUMN referencia_geo_inicio VARCHAR(255),
    ADD COLUMN referencia_geo_fim VARCHAR(255);

ALTER TABLE vaga
    DROP COLUMN horario_inicio,
    DROP COLUMN horario_fim,
    DROP COLUMN localizacao;



CREATE TABLE operacao_vaga (
    operacao_id UUID NOT NULL DEFAULT gen_random_uuid(),
    vaga_id UUID,
    dia_semana dia_da_semana,
    hora_inicio TIME WITHOUT TIME ZONE,
    hora_fim TIME WITHOUT TIME ZONE,
    CONSTRAINT pk_operacao_vaga PRIMARY KEY (operacao_id)
);

ALTER TABLE operacao_vaga
    ADD CONSTRAINT FK_OPERACAO_VAGA_ON_VAGA
    FOREIGN KEY (vaga_id)
    REFERENCES vaga (id);



ALTER TABLE motorista
    ADD COLUMN tipo_CNH categoria_cnh,
    ADD COLUMN data_validade_CNH DATE,
    ADD COLUMN gerente_id UUID;

ALTER TABLE motorista
    ADD CONSTRAINT FK_MOTORISTA_ON_GERENTE
    FOREIGN KEY (gerente_id)
    REFERENCES usuario (id); 
    
ALTER TABLE motorista RENAME COLUMN cnh TO numero_CNH;


ALTER TABLE agente
    ADD COLUMN matricula VARCHAR(255) UNIQUE;


ALTER TABLE reserva
    ADD COLUMN veiculo_id UUID;

ALTER TABLE reserva
    ADD CONSTRAINT FK_RESERVA_ON_VEICULO
    FOREIGN KEY (veiculo_id)
    REFERENCES veiculo (id);

ALTER TABLE reserva
    RENAME COLUMN data_solicitacao TO data_emissao;
ALTER TABLE reserva
    RENAME COLUMN data_permissao TO data_reservada;
ALTER TABLE reserva
    RENAME COLUMN horario_inicio_reserva TO horario_inicio;
ALTER TABLE reserva
    RENAME COLUMN horario_fim_reserva TO horario_fim;



ALTER TABLE veiculo
    ADD COLUMN eixos INT;



ALTER TABLE usuario
    RENAME COLUMN permissoes TO permissao;



DROP TABLE IF EXISTS vaga_dias_semana;
DROP TABLE IF EXISTS dias_vaga;
DROP TABLE IF EXISTS dias_da_semana;