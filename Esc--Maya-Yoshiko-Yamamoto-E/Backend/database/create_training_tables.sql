-- Criar tabelas para o training-service

-- Tabela de progresso dos pacientes
CREATE TABLE IF NOT EXISTS patient_progress (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL UNIQUE,
    total_sessions INT DEFAULT 0,
    weekly_sessions INT DEFAULT 0,
    streak_days INT DEFAULT 0,
    progress_percentage FLOAT DEFAULT 0.0,
    total_points INT DEFAULT 0,
    level INT DEFAULT 1,
    last_session_date DATETIME NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabela de pontos dos pacientes
CREATE TABLE IF NOT EXISTS patient_points (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL UNIQUE,
    total_points INT DEFAULT 0,
    weekly_points INT DEFAULT 0,
    monthly_points INT DEFAULT 0,
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabela de metas dos pacientes
CREATE TABLE IF NOT EXISTS patient_goals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    goal_type VARCHAR(50) NOT NULL,
    target_value INT NOT NULL,
    current_value INT DEFAULT 0,
    deadline DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabela de desafios
CREATE TABLE IF NOT EXISTS challenges (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    reward_points INT NOT NULL,
    target_sessions INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de desafios dos pacientes
CREATE TABLE IF NOT EXISTS patient_challenges (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    challenge_id INT NOT NULL,
    joined BOOLEAN DEFAULT FALSE,
    progress_sessions INT DEFAULT 0,
    completed BOOLEAN DEFAULT FALSE,
    completed_at DATETIME NULL,
    joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE,
    UNIQUE KEY unique_patient_challenge (patient_id, challenge_id)
);

-- Inserir alguns desafios de exemplo
INSERT IGNORE INTO challenges (title, description, reward_points, target_sessions, start_date, end_date) VALUES
('Desafio da Semana', 'Complete 5 sessões esta semana', 150, 5, CURDATE() - INTERVAL 1 DAY, CURDATE() + INTERVAL 6 DAY),
('Desafio do Mês', 'Complete 20 sessões este mês', 500, 20, CURDATE() - INTERVAL 1 DAY, CURDATE() + INTERVAL 30 DAY),
('Desafio de Streak', 'Complete exercícios por 7 dias seguidos', 200, 7, CURDATE() - INTERVAL 1 DAY, CURDATE() + INTERVAL 6 DAY);

-- Criar índices para melhor performance
CREATE INDEX idx_patient_progress_patient_id ON patient_progress(patient_id);
CREATE INDEX idx_patient_points_patient_id ON patient_points(patient_id);
CREATE INDEX idx_patient_goals_patient_id ON patient_goals(patient_id);
CREATE INDEX idx_patient_challenges_patient_id ON patient_challenges(patient_id);
CREATE INDEX idx_challenges_active ON challenges(is_active, start_date, end_date);
