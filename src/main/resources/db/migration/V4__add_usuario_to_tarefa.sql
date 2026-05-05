-- Ownership de tarefas
ALTER TABLE tb_tarefa ADD COLUMN IF NOT EXISTS cd_usuario BIGINT;

ALTER TABLE tb_tarefa
    ADD CONSTRAINT fk_tarefa_usuario
    FOREIGN KEY (cd_usuario) REFERENCES tb_usuario(cd_usuario);

UPDATE tb_tarefa
SET cd_usuario = (
    SELECT cd_usuario FROM tb_usuario WHERE ds_username = 'admin' LIMIT 1
)
WHERE cd_usuario IS NULL;

-- Flag de administrador
ALTER TABLE tb_usuario ADD COLUMN IF NOT EXISTS fl_admin BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE tb_usuario SET fl_admin = TRUE WHERE ds_username = 'admin';