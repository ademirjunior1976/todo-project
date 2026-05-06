ALTER TABLE tb_usuario
    ADD COLUMN ds_token_reset    VARCHAR(100),
    ADD COLUMN dt_token_expiracao TIMESTAMP;