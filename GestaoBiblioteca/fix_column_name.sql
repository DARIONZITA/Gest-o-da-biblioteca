-- Remove a coluna duplicada com erro de digitação
-- Execute este script no MySQL Workbench ou linha de comando

USE lib_db;

-- Remove a foreign key constraint com erro de digitação
ALTER TABLE tb_livros DROP FOREIGN KEY FKosc4f9ut8ghvfo41vdi5ov9c7;

-- Remove a coluna com erro de digitação (mantém apenas categoria_id)
ALTER TABLE tb_livros DROP COLUMN categroria_id;

-- Verifica a estrutura corrigida
DESCRIBE tb_livros;
