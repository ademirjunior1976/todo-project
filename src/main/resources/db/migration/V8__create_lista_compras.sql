CREATE SEQUENCE IF NOT EXISTS seq_lista_compras START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_item_compra  START WITH 1 INCREMENT BY 1;

CREATE TABLE tb_lista_compras (
    cd_lista   BIGINT       NOT NULL DEFAULT nextval('seq_lista_compras'),
    nm_lista   VARCHAR(100) NOT NULL,
    cd_usuario BIGINT       REFERENCES tb_usuario(cd_usuario),
    dt_criacao TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_lista_compras PRIMARY KEY (cd_lista)
);

CREATE TABLE tb_item_compra (
    cd_item       BIGINT         NOT NULL DEFAULT nextval('seq_item_compra'),
    nm_produto    VARCHAR(100)   NOT NULL,
    qt_quantidade NUMERIC(10,3),
    ds_unidade    VARCHAR(20)    NOT NULL DEFAULT 'UNIDADE',
    ds_categoria  VARCHAR(20)    NOT NULL DEFAULT 'OUTROS',
    fl_comprado   BOOLEAN        NOT NULL DEFAULT false,
    cd_lista      BIGINT         NOT NULL REFERENCES tb_lista_compras(cd_lista) ON DELETE CASCADE,
    dt_criacao    TIMESTAMP      NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_item_compra PRIMARY KEY (cd_item)
);
