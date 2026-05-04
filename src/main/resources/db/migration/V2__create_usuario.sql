CREATE SEQUENCE IF NOT EXISTS seq_usuario START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS tb_usuario (
    cd_usuario  BIGINT       NOT NULL DEFAULT nextval('seq_usuario') PRIMARY KEY,
    ds_username VARCHAR(50)  NOT NULL UNIQUE,
    ds_password VARCHAR(100) NOT NULL,
    ds_nome     VARCHAR(100) NOT NULL,
    fl_ativo    BOOLEAN      NOT NULL DEFAULT TRUE,
    dt_criacao  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Usuário padrão: admin / admin
INSERT INTO tb_usuario (ds_username, ds_password, ds_nome)
VALUES ('admin', 'admin', 'Administrador');