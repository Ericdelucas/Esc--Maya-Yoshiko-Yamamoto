-- Criar tabelas para Ferramentas de Saúde
-- Data: 2026-04-25

-- Tabela principal para ferramentas de saúde (IMC, gordura corporal, etc.)
CREATE TABLE IF NOT EXISTS health_tools (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    record_type VARCHAR(50) NOT NULL COMMENT 'bmi, body_fat, blood_pressure, etc.',
    value JSON NOT NULL COMMENT 'Dados estruturados em JSON',
    record_date DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_id (user_id),
    INDEX idx_record_type (record_type),
    INDEX idx_record_date (record_date),
    INDEX idx_user_record_type (user_id, record_type),
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabela para questionários de saúde
CREATE TABLE IF NOT EXISTS health_questionnaires (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    questionnaire_date DATETIME NOT NULL,
    total_score INT NOT NULL DEFAULT 0,
    max_score INT NOT NULL DEFAULT 0,
    risk_level VARCHAR(20) NOT NULL COMMENT 'Baixo, Médio, Alto',
    answers JSON NOT NULL COMMENT 'Respostas em JSON',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_id (user_id),
    INDEX idx_questionnaire_date (questionnaire_date),
    INDEX idx_risk_level (risk_level),
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Inserir dados de exemplo para teste
INSERT IGNORE INTO health_tools (user_id, record_type, value, record_date) VALUES
(1, 'bmi', '{"height": 1.75, "weight": 70, "bmi": 22.86, "category": "Sobrepeso"}', NOW()),
(1, 'body_fat', '{"height": 1.75, "weight": 70, "age": 25, "gender": "M", "body_fat_percentage": 15.2, "category": "Fitness"}', NOW()),
(2, 'bmi', '{"height": 1.65, "weight": 60, "bmi": 22.04, "category": "Sobrepeso"}', NOW());

INSERT IGNORE INTO health_questionnaires (user_id, questionnaire_date, total_score, max_score, risk_level, answers) VALUES
(1, NOW(), 15, 50, 'Baixo', '{"smoking": "no", "alcohol": "weekly", "exercise": "weekly", "sleep": "7-8h", "stress": "low", "chronic_diseases": "", "medications": ""}'),
(2, NOW(), 25, 50, 'Médio', '{"smoking": "no", "alcohol": "daily", "exercise": "rarely", "sleep": "5-6h", "stress": "medium", "chronic_diseases": "hipertensão", "medications": "losartana"}');
