CREATE SEQUENCE IF NOT EXISTS seq_tarefa START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS tb_tarefa (
    cd_tarefa     BIGINT       NOT NULL DEFAULT nextval('seq_tarefa') PRIMARY KEY,
    ds_tarefa     VARCHAR(200) NOT NULL,
    ds_texto      VARCHAR(1000),
    dt_inicio     DATE         NOT NULL,
    dt_fim        DATE         NOT NULL,
    ds_importancia VARCHAR(10) NOT NULL DEFAULT 'NORMAL',
    ds_status     VARCHAR(15)  NOT NULL DEFAULT 'PENDENTE',
    dt_criacao    TIMESTAMP    NOT NULL
);