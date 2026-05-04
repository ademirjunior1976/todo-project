-- Atualiza senhas em texto simples para hash BCrypt (custo 10)
-- Hash gerado para a senha 'admin'
UPDATE tb_usuario
SET ds_password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
WHERE ds_username = 'admin'
  AND LENGTH(ds_password) < 60;