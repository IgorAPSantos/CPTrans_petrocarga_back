CREATE TABLE dias_vaga (
    vaga_id UUID NOT NULL,
    dia_id UUID NOT NULL,
    CONSTRAINT pk_dias_vaga PRIMARY KEY (vaga_id, dia_id),
    CONSTRAINT fk_dias_vaga_vaga FOREIGN KEY (vaga_id) REFERENCES vaga(id) ON DELETE CASCADE,
    CONSTRAINT fk_dias_vaga_dia FOREIGN KEY (dia_id) REFERENCES dias_da_semana(id) ON DELETE CASCADE
);