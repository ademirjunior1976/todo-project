-- ============================================================
-- Script de criação da tabela TAREFAS no PostgreSQL
-- Banco: todolist | Porta: 5433
-- ============================================================

CREATE SEQUENCE seq_tarefa
    START WITH 1
    INCREMENT BY 1
    NO CACHE
    NO CYCLE;

CREATE TABLE tb_tarefa (
    cd_tarefa      BIGINT       DEFAULT nextval('seq_tarefa') NOT NULL,
    ds_tarefa      VARCHAR(200) NOT NULL,
    ds_texto       VARCHAR(1000),
    dt_inicio      DATE         NOT NULL,
    dt_fim         DATE         NOT NULL,
    ds_importancia VARCHAR(10)  DEFAULT 'NORMAL'   NOT NULL,
    ds_status      VARCHAR(15)  DEFAULT 'PENDENTE' NOT NULL,
    dt_criacao     TIMESTAMP    DEFAULT NOW()      NOT NULL,
    CONSTRAINT pk_tarefa              PRIMARY KEY (cd_tarefa),
    CONSTRAINT ck_importancia_tarefa  CHECK (ds_importancia IN ('NORMAL', 'URGENTE')),
    CONSTRAINT ck_status_tarefa       CHECK (ds_status IN ('PENDENTE', 'ANDAMENTO', 'CONCLUIDA'))
);

COMMENT ON TABLE  tb_tarefa               IS 'Tabela de tarefas do sistema Todo List';
COMMENT ON COLUMN tb_tarefa.cd_tarefa     IS 'Identificador único da tarefa';
COMMENT ON COLUMN tb_tarefa.ds_tarefa     IS 'Título da tarefa';
COMMENT ON COLUMN tb_tarefa.ds_texto      IS 'Descrição detalhada da tarefa';
COMMENT ON COLUMN tb_tarefa.dt_inicio     IS 'Data de início da tarefa';
COMMENT ON COLUMN tb_tarefa.dt_fim        IS 'Data de término previsto';
COMMENT ON COLUMN tb_tarefa.ds_importancia IS 'Importância: NORMAL ou URGENTE';
COMMENT ON COLUMN tb_tarefa.ds_status     IS 'Status: PENDENTE, ANDAMENTO ou CONCLUIDA';
COMMENT ON COLUMN tb_tarefa.dt_criacao    IS 'Data e hora de criação do registro';

CREATE INDEX idx_tarefa_status       ON tb_tarefa (ds_status);
CREATE INDEX idx_tarefa_importancia  ON tb_tarefa (ds_importancia);
CREATE INDEX idx_tarefa_nome         ON tb_tarefa (ds_tarefa);

-- Dados de exemplo
INSERT INTO tb_tarefa (ds_tarefa, ds_texto, dt_inicio, dt_fim, ds_importancia, ds_status)
VALUES ('Reunião de planejamento', 'Reunião trimestral com a equipe de projetos', CURRENT_DATE, CURRENT_DATE + 7, 'URGENTE', 'PENDENTE');

INSERT INTO tb_tarefa (ds_tarefa, ds_texto, dt_inicio, dt_fim, ds_importancia, ds_status)
VALUES ('Revisar documentação', 'Atualizar os manuais técnicos do sistema', CURRENT_DATE, CURRENT_DATE + 14, 'NORMAL', 'ANDAMENTO');

INSERT INTO tb_tarefa (ds_tarefa, ds_texto, dt_inicio, dt_fim, ds_importancia, ds_status)
VALUES ('Deploy produção', 'Publicar versão 2.0 no ambiente de produção', CURRENT_DATE, CURRENT_DATE + 3, 'URGENTE', 'CONCLUIDA');
