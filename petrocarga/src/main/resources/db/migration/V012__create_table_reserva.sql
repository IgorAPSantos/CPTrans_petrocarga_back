CREATE TABLE reserva (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cidade_origem VARCHAR(100),
    data_solicitacao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    data_permissao TIMESTAMP WITH TIME ZONE,
    horario_inicio_reserva TIME,
    horario_fim_reserva TIME,
    status VARCHAR(50),
    usuario_id UUID NOT NULL,
    motorista_id UUID NOT NULL,
    vaga_id UUID NOT NULL,
    CONSTRAINT fk_reserva_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_reserva_motorista FOREIGN KEY (motorista_id) REFERENCES motorista(id),
    CONSTRAINT fk_reserva_vaga FOREIGN KEY (vaga_id) REFERENCES vaga(id)
);