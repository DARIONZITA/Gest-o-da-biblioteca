-- ============================================================================
-- LIMPEZA TOTAL DA BASE DE DADOS
-- Executar ANTES de reiniciar o backend para o DemoSeedRunner criar os dados.
-- ============================================================================
-- O DemoSeedRunner.java (Spring Boot) cria automaticamente todos os dados
-- de demonstração ao iniciar, incluindo utilizadores com senha correta (BCrypt).
--
-- PASSO 1: Executar este SQL no MySQL Workbench
-- PASSO 2: Reiniciar o backend (./mvnw spring-boot:run)
-- PASSO 3: O backend cria tudo automaticamente!
--
-- Credenciais após o seed:
--   Admin:   20260001@isptec.co.ao / admin123
--   Membros: 20260101@isptec.co.ao até 20260108@isptec.co.ao / senha123
-- ============================================================================

SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM tb_reserva;
DELETE FROM tb_emprestimo;
DELETE FROM tb_livros;
DELETE FROM editoras;
DELETE FROM tb_autor;
DELETE FROM tb_categoria;
DELETE FROM tb_usuario;
DELETE FROM ocr_logs;

SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

SELECT 'Base de dados limpa! Reinicie o backend para criar os dados demo.' AS resultado;
