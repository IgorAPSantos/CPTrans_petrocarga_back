CREATE EXTENSION IF NOT EXISTS "pgcrypto";



CREATE TYPE permissoes_enum AS ENUM (
    'AGENTE',
    'GESTOR',
    'MOTORISTA',
    'EMPRESA',
    'ADMIN'
);

CREATE TYPE tipo_cnh_enum AS ENUM (
    'A', 'B', 'C', 'D', 'E', 'AB', 'AC', 'AD', 'AE'
);

CREATE TYPE tipo_veiculo_enum AS ENUM (
    'VUC', 'CAMINHAO_TOCO', 'CAMINHAO_TRUCK', 'CARRETA', 'MOTO', 'CARRO'
);

CREATE TYPE status_vaga_enum AS ENUM (
    'ATIVA', 'INATIVA', 'MANUTENCAO'
);

CREATE TYPE area_vaga_enum AS ENUM (
    'AZUL', 'BRANCA', 'CARGA_DESCARGA'
);

CREATE TYPE tipo_vaga_carga_enum AS ENUM (
    'CARGA_DESCARGA', 'ESTACIONAMENTO_ROTATIVO'
);

CREATE TYPE dia_semana_enum AS ENUM (
    'SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO', 'DOMINGO'
);

CREATE TYPE status_reserva_enum AS ENUM (
    'PENDENTE', 'CONFIRMADA', 'CANCELADA', 'CONCLUIDA', 'EM_USO'
);



CREATE TABLE Usuario (
    UniqueID uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    telefone VARCHAR(20),
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    permissao permissoes_enum NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN DEFAULT true,
    desativado_em TIMESTAMP WITH TIME ZONE
);

CREATE TABLE Agente (
    UniqueID uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id uuid NOT NULL UNIQUE,
    matricula VARCHAR(50) UNIQUE NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES Usuario(UniqueID)
);

CREATE TABLE Empresa (
    UniqueID uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id uuid NOT NULL UNIQUE,
    cnpj VARCHAR(14) UNIQUE NOT NULL,
    razao_social VARCHAR(255) NOT NULL
);

CREATE TABLE Motorista (
    UniqueID uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id uuid NOT NULL UNIQUE,
    tipo_CNH tipo_cnh_enum,
    numero_CNH VARCHAR(20) UNIQUE,
    data_validade_CNH DATE,
    empresa_id uuid,
    FOREIGN KEY (usuario_id) REFERENCES Usuario(UniqueID),
    FOREIGN KEY (empresa_id) REFERENCES Empresa(UniqueID)
);

CREATE TABLE Endereco_vaga (
    UniqueID uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo_pmp VARCHAR(50) UNIQUE,
    logradouro VARCHAR(255) NOT NULL,
    bairro VARCHAR(100)
);

CREATE TABLE Vaga (
    UniqueID uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    endereco_id uuid NOT NULL,
    area area_vaga_enum,
    numero_endereco VARCHAR(20),
    referencia_endereco TEXT,
    tipo_vaga tipo_vaga_carga_enum,
    comprimento DECIMAL(5, 2),
    status status_vaga_enum NOT NULL DEFAULT 'INATIVA',
    referencia_geo_inicio VARCHAR(100),
    referencia_geo_fim VARCHAR(100),
    FOREIGN KEY (endereco_id) REFERENCES Endereco_vaga(UniqueID)
);

CREATE TABLE Operacao_vaga (
    operacao_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    vaga_id uuid NOT NULL,
    dia_semana dia_semana_enum NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    FOREIGN KEY (vaga_id) REFERENCES Vaga(UniqueID),
    UNIQUE (vaga_id, dia_semana, hora_inicio)
);

CREATE TABLE disponibilidade_vaga (
    UniqueID uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    vaga_id uuid NOT NULL,
    inicio TIMESTAMP WITH TIME ZONE NOT NULL,
    fim TIMESTAMP WITH TIME ZONE NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    criado_por uuid NOT NULL, 
    FOREIGN KEY (vaga_id) REFERENCES Vaga(UniqueID),
    FOREIGN KEY (criado_por) REFERENCES Usuario(UniqueID)
);


CREATE TABLE Veiculo (
    UniqueID uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    placa VARCHAR(10) NOT NULL,
    marca VARCHAR(100),
    modelo VARCHAR(100),
    tipo tipo_veiculo_enum,
    comprimento DECIMAL(5, 2),
    usuario_id uuid NOT NULL,
    cpf_proprietario VARCHAR(11),
    cnpj_proprietario VARCHAR(14),
    
    FOREIGN KEY (usuario_id) REFERENCES Usuario(UniqueID),
    
    UNIQUE (placa, usuario_id),
    
    CONSTRAINT chk_cpf_cnpj_proprietario CHECK (
        (cpf_proprietario IS NULL AND cnpj_proprietario IS NOT NULL) OR
        (cpf_proprietario IS NOT NULL AND cnpj_proprietario IS NULL)
    )
);

CREATE UNIQUE INDEX idx_unique_placa_cpf ON Veiculo (placa, cpf_proprietario)
WHERE cpf_proprietario IS NOT NULL;

CREATE UNIQUE INDEX idx_unique_placa_cnpj ON Veiculo (placa, cnpj_proprietario)
WHERE cnpj_proprietario IS NOT NULL;



CREATE TABLE veiculo_motorista_associacao (
    veiculo_id uuid NOT NULL,
    motorista_id uuid NOT NULL,
    PRIMARY KEY (veiculo_id, motorista_id),
    FOREIGN KEY (veiculo_id) REFERENCES Veiculo(UniqueID) ON DELETE CASCADE,
    FOREIGN KEY (motorista_id) REFERENCES Motorista(UniqueID) ON DELETE CASCADE
);

CREATE TABLE Reserva (
    UniqueID uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    vaga_id uuid NOT NULL,
    motorista_id uuid NOT NULL,
    veiculo_id uuid NOT NULL,
    criado_por uuid NOT NULL, 
    cidade_origem VARCHAR(100),
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    inicio TIMESTAMP WITH TIME ZONE NOT NULL,
    fim TIMESTAMP WITH TIME ZONE NOT NULL,
    status status_reserva_enum NOT NULL DEFAULT 'PENDENTE',
    FOREIGN KEY (vaga_id) REFERENCES Vaga(UniqueID),
    FOREIGN KEY (motorista_id) REFERENCES Motorista(UniqueID),
    FOREIGN KEY (veiculo_id) REFERENCES Veiculo(UniqueID),
    FOREIGN KEY (criado_por) REFERENCES Usuario(UniqueID)
);

CREATE TABLE Reserva_Rapida (
    UniqueID uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    vaga_id uuid NOT NULL,
    agente_id uuid NOT NULL,
    tipo_veiculo tipo_veiculo_enum,
    placa VARCHAR(10),
    inicio TIMESTAMP WITH TIME ZONE NOT NULL,
    fim TIMESTAMP WITH TIME ZONE NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vaga_id) REFERENCES Vaga(UniqueID),
    FOREIGN KEY (agente_id) REFERENCES Agente(UniqueID)
);
