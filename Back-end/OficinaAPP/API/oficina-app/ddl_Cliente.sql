CREATE TABLE clientes
(
    id_cliente         INT AUTO_INCREMENT NOT NULL,
    nome               VARCHAR(150)       NOT NULL,
    telefone           VARCHAR(20)        NULL,
    cpf_cnpj           VARCHAR(18)        NOT NULL,
    email              VARCHAR(100)       NULL,
    endereco           VARCHAR(200)       NULL,
    inscricao_estadual VARCHAR(30)        NULL,
    CONSTRAINT pk_clientes PRIMARY KEY (id_cliente)
);

ALTER TABLE clientes
    ADD CONSTRAINT uc_clientes_cpf_cnpj UNIQUE (cpf_cnpj);