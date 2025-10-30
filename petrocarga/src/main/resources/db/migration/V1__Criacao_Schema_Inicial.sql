CREATE TABLE usuario (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    telefone VARCHAR(11),
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    permissao VARCHAR(40) NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN DEFAULT true,
    desativado_em TIMESTAMP WITH TIME ZONE
);

CREATE TABLE agente (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id uuid NOT NULL UNIQUE,
    matricula VARCHAR(50) UNIQUE NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE empresa (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id uuid NOT NULL UNIQUE,
    cnpj VARCHAR(14) UNIQUE NOT NULL,
    razao_social VARCHAR(255) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE motorista (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id uuid NOT NULL UNIQUE,
    tipo_cnh CHAR(1) NOT NULL,
    numero_cnh VARCHAR(9) UNIQUE,
    data_validade_cnh DATE NOT NULL,
    empresa_id uuid DEFAULT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);

CREATE TABLE endereco_vaga (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo_pmp VARCHAR(6) NOT NULL UNIQUE,
    logradouro VARCHAR(255) NOT NULL UNIQUE,
    bairro VARCHAR(100) NOT NULL
);

CREATE TABLE vaga (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    endereco_id uuid NOT NULL,
    area VARCHAR(20) NOT NULL,
    numero_endereco VARCHAR(10) NOT NULL,
    referencia_endereco TEXT NOT NULL,
    tipo_vaga VARCHAR(20) NOT NULL,
    comprimento INT NOT NULL CHECK (comprimento >= 5 AND comprimento <= 50),
    status VARCHAR(20) NOT NULL DEFAULT 'INDISPONIVEL',
    referencia_geo_inicio VARCHAR(100) NOT NULL UNIQUE,
    referencia_geo_fim VARCHAR(100) NOT NULL UNIQUE,
    FOREIGN KEY (endereco_id) REFERENCES endereco_vaga(id)
);

CREATE TABLE operacao_vaga (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    vaga_id uuid NOT NULL,
    dia_semana VARCHAR(10) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    FOREIGN KEY (vaga_id) REFERENCES vaga(id),
    UNIQUE (vaga_id, dia_semana)
);

CREATE TABLE disponibilidade_vaga (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    vaga_id uuid NOT NULL,
    inicio TIMESTAMP WITH TIME ZONE NOT NULL,
    fim TIMESTAMP WITH TIME ZONE NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    criado_por uuid NOT NULL, 
    FOREIGN KEY (vaga_id) REFERENCES vaga(id),
    FOREIGN KEY (criado_por) REFERENCES usuario(id)
);


CREATE TABLE veiculo (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    placa VARCHAR(7) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    comprimento INT NOT NULL CHECK (comprimento >= 5 AND comprimento <= 50),
    usuario_id uuid NOT NULL,
    cpf_proprietario VARCHAR(11),
    cnpj_proprietario VARCHAR(14),
    
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    
    UNIQUE (placa, usuario_id),
    
    CONSTRAINT chk_cpf_cnpj_proprietario CHECK (
        (cpf_proprietario IS NULL AND cnpj_proprietario IS NOT NULL) OR
        (cpf_proprietario IS NOT NULL AND cnpj_proprietario IS NULL)
    )
);

CREATE UNIQUE INDEX idx_unique_placa_cpf ON veiculo (placa, cpf_proprietario)
WHERE cpf_proprietario IS NOT NULL;

CREATE UNIQUE INDEX idx_unique_placa_cnpj ON veiculo (placa, cnpj_proprietario)
WHERE cnpj_proprietario IS NOT NULL;



CREATE TABLE veiculo_empresa_motorista (
    veiculo_id uuid NOT NULL,
    motorista_id uuid NOT NULL,
    PRIMARY KEY (veiculo_id, motorista_id),
    FOREIGN KEY (veiculo_id) REFERENCES veiculo(id) ON DELETE CASCADE,
    FOREIGN KEY (motorista_id) REFERENCES motorista(id) ON DELETE CASCADE
);

CREATE TABLE reserva (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    vaga_id uuid NOT NULL,
    motorista_id uuid NOT NULL,
    veiculo_id uuid NOT NULL,
    criado_por uuid NOT NULL, 
    cidade_origem VARCHAR(100) NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    inicio TIMESTAMP WITH TIME ZONE NOT NULL,
    fim TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVA',
    FOREIGN KEY (vaga_id) REFERENCES vaga(id),
    FOREIGN KEY (motorista_id) REFERENCES motorista(id),
    FOREIGN KEY (veiculo_id) REFERENCES veiculo(id),
    FOREIGN KEY (criado_por) REFERENCES usuario(id)
);

CREATE TABLE reserva_rapida (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    vaga_id uuid NOT NULL,
    agente_id uuid NOT NULL,
    tipo_veiculo VARCHAR(30) NOT NULL,
    placa VARCHAR(7) NOT NULL,
    inicio TIMESTAMP WITH TIME ZONE NOT NULL,
    fim TIMESTAMP WITH TIME ZONE NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVA',
    FOREIGN KEY (vaga_id) REFERENCES vaga(id),
    FOREIGN KEY (agente_id) REFERENCES agente(id)
);