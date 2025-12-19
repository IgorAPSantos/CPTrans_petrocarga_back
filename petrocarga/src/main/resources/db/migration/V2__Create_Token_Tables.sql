-- Create token tables for account confirmation and password recovery

CREATE TABLE token_confirmacao (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    token uuid NOT NULL UNIQUE,
    data_expiracao TIMESTAMP WITH TIME ZONE NOT NULL,
    usuario_id uuid NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE token_recuperacao (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo VARCHAR(6) NOT NULL,
    data_expiracao TIMESTAMP WITH TIME ZONE NOT NULL,
    usuario_id uuid NOT NULL,
    utilizado BOOLEAN DEFAULT false,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
