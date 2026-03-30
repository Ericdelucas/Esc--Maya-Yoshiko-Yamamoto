-- Health Features Database Schema
-- SmartSaúde Project

-- Tabela de métricas de saúde (IMC, gordura corporal, peso, altura)
CREATE TABLE IF NOT EXISTS health_metrics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    metric_type ENUM('imc', 'body_fat', 'weight', 'height', 'waist_circumference') NOT NULL,
    value DECIMAL(10,2) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    classification VARCHAR(50),
    measured_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_metric (user_id, metric_type, measured_at),
    INDEX idx_user_date (user_id, measured_at)
);

-- Tabela de questionários de saúde
CREATE TABLE IF NOT EXISTS health_questionnaires (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    questionnaire_data JSON NOT NULL,
    version VARCHAR(10) DEFAULT '1.0',
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_questionnaire (user_id, completed_at),
    INDEX idx_version (version)
);

-- Tabela de metas de saúde
CREATE TABLE IF NOT EXISTS health_goals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    goal_type ENUM('weight_loss', 'weight_gain', 'muscle_gain', 'body_fat_reduction', 'endurance', 'strength', 'flexibility', 'custom') NOT NULL,
    target_value DECIMAL(10,2) NOT NULL,
    current_value DECIMAL(10,2) DEFAULT 0.0,
    target_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    achieved BOOLEAN DEFAULT FALSE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_goals (user_id, is_active),
    INDEX idx_goal_type (goal_type),
    INDEX idx_target_date (target_date)
);

-- Tabela de progresso das metas
CREATE TABLE IF NOT EXISTS health_progress (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    goal_id INT NOT NULL,
    progress_value DECIMAL(10,2) NOT NULL,
    progress_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (goal_id) REFERENCES health_goals(id) ON DELETE CASCADE,
    INDEX idx_goal_progress (goal_id, progress_date),
    INDEX idx_user_progress (user_id, progress_date)
);

-- Tabela de avaliações físicas completas
CREATE TABLE IF NOT EXISTS physical_assessments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    weight DECIMAL(5,2),
    height DECIMAL(3,2),
    body_fat_percentage DECIMAL(5,2),
    muscle_mass DECIMAL(5,2),
    waist_circumference DECIMAL(5,2),
    chest_circumference DECIMAL(5,2),
    arm_circumference DECIMAL(5,2),
    thigh_circumference DECIMAL(5,2),
    imc_calculated DECIMAL(5,2),
    imc_classification VARCHAR(50),
    assessment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_assessments (user_id, assessment_date)
);

-- Índices adicionais para performance
CREATE INDEX idx_health_metrics_user_created ON health_metrics(user_id, created_at);
CREATE INDEX idx_health_questionnaires_user_created ON health_questionnaires(user_id, created_at);
CREATE INDEX idx_health_goals_user_active ON health_goals(user_id, is_active);
CREATE INDEX idx_health_progress_goal_date ON health_progress(goal_id, progress_date);
CREATE INDEX idx_physical_assessments_user_date ON physical_assessments(user_id, assessment_date);
