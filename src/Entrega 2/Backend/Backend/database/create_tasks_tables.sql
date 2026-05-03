-- =========================================================
-- TASKS AND POINTS SYSTEM
-- =========================================================

-- Tarefas criadas por profissionais para pacientes
CREATE TABLE IF NOT EXISTS tasks (
  id INT AUTO_INCREMENT PRIMARY KEY,
  professional_id INT NOT NULL,
  patient_id INT NOT NULL,
  title VARCHAR(120) NOT NULL,
  description TEXT NOT NULL,
  points_value INT NOT NULL DEFAULT 10,
  exercise_id INT NULL,  -- Associada a um exercício específico (opcional)
  frequency_per_week INT NOT NULL DEFAULT 1,  -- Quantas vezes por semana
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  start_date DATE NOT NULL,
  end_date DATE NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_tasks_professional_id (professional_id),
  INDEX idx_tasks_patient_id (patient_id),
  INDEX idx_tasks_exercise_id (exercise_id),
  INDEX idx_tasks_active (is_active),
  INDEX idx_tasks_date_range (start_date, end_date)
);

-- Conclusão de tarefas pelos pacientes
CREATE TABLE IF NOT EXISTS task_completions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  task_id INT NOT NULL,
  patient_id INT NOT NULL,
  completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  points_earned INT NOT NULL,
  completion_notes VARCHAR(512) NULL,
  verified_by_professional TINYINT(1) NOT NULL DEFAULT 0,
  verified_at TIMESTAMP NULL,
  INDEX idx_task_completions_task_id (task_id),
  INDEX idx_task_completions_patient_id (patient_id),
  INDEX idx_task_completions_completed_at (completed_at),
  INDEX idx_task_completions_verified (verified_by_professional),
  UNIQUE KEY unique_task_completion_per_day (task_id, patient_id, DATE(completed_at))
);

-- Acumulado de pontos por usuário
CREATE TABLE IF NOT EXISTS user_points (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL UNIQUE,
  total_points INT NOT NULL DEFAULT 0,
  weekly_points INT NOT NULL DEFAULT 0,
  monthly_points INT NOT NULL DEFAULT 0,
  current_streak INT NOT NULL DEFAULT 0,  -- Dias seguidos completando tarefas
  longest_streak INT NOT NULL DEFAULT 0,
  last_completion_date DATE NULL,
  rank_position INT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_user_points_user_id (user_id),
  INDEX idx_user_points_total_points (total_points),
  INDEX idx_user_points_rank (rank_position)
);

-- Histórico de alterações de pontos (para auditoria)
CREATE TABLE IF NOT EXISTS points_history (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  points_change INT NOT NULL,  -- Positivo para ganho, negativo para perda
  change_type VARCHAR(32) NOT NULL,  -- 'task_completion', 'bonus', 'penalty', 'adjustment'
  reference_id INT NULL,  -- ID da tarefa, bônus, etc.
  description VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_points_history_user_id (user_id),
  INDEX idx_points_history_type (change_type),
  INDEX idx_points_history_created_at (created_at)
);

-- Badges/Conquistas por usuário
CREATE TABLE IF NOT EXISTS user_badges (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  badge_type VARCHAR(64) NOT NULL,  -- 'first_task', 'week_streak', 'month_warrior', etc.
  badge_name VARCHAR(120) NOT NULL,
  badge_description TEXT NULL,
  badge_icon VARCHAR(255) NULL,
  points_reward INT NOT NULL DEFAULT 0,
  earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user_badges_user_id (user_id),
  INDEX idx_user_badges_type (badge_type),
  UNIQUE KEY unique_user_badge (user_id, badge_type)
);

-- Configuração de desafios globais (criados por admin)
CREATE TABLE IF NOT EXISTS global_challenges (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(120) NOT NULL,
  description TEXT NOT NULL,
  points_reward INT NOT NULL DEFAULT 100,
  requirement_type VARCHAR(64) NOT NULL,  -- 'total_tasks', 'weekly_tasks', 'streak_days'
  requirement_value INT NOT NULL,  -- Quantidade necessária
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_by INT NOT NULL,  -- Admin que criou
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_global_challenges_active (is_active),
  INDEX idx_global_challenges_date_range (start_date, end_date)
);

-- Participação em desafios globais
CREATE TABLE IF NOT EXISTS challenge_participations (
  id INT AUTO_INCREMENT PRIMARY KEY,
  challenge_id INT NOT NULL,
  user_id INT NOT NULL,
  current_progress INT NOT NULL DEFAULT 0,
  is_completed TINYINT(1) NOT NULL DEFAULT 0,
  completed_at TIMESTAMP NULL,
  reward_claimed TINYINT(1) NOT NULL DEFAULT 0,
  claimed_at TIMESTAMP NULL,
  joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_challenge_participations_challenge_id (challenge_id),
  INDEX idx_challenge_participations_user_id (user_id),
  INDEX idx_challenge_participations_completed (is_completed),
  UNIQUE KEY unique_challenge_user (challenge_id, user_id)
);

-- Inserir dados iniciais para testes
INSERT IGNORE INTO user_points (user_id, total_points, weekly_points, monthly_points) 
SELECT id, 0, 0, 0 FROM users WHERE role IN ('Patient', 'patient');

-- Criar alguns desafios globais iniciais
INSERT IGNORE INTO global_challenges (
  title, description, points_reward, requirement_type, requirement_value, 
  start_date, end_date, created_by
) VALUES 
(
  'Desafio da Semana', 
  'Complete 7 tarefas esta semana para ganhar pontos extras!', 
  150, 
  'weekly_tasks', 
  7, 
  CURDATE(), 
  DATE_ADD(CURDATE(), INTERVAL 7 DAY), 
  1
),
(
  'Guerreiro Mensal', 
  'Complete 30 tarefas neste mês e conquiste o título!', 
  500, 
  'monthly_tasks', 
  30, 
  CURDATE(), 
  DATE_ADD(CURDATE(), INTERVAL 30 DAY), 
  1
),
(
  'Mestre da Sequência', 
  'Mantenha uma sequência de 7 dias consecutivos completando tarefas!', 
  300, 
  'streak_days', 
  7, 
  CURDATE(), 
  DATE_ADD(CURDATE(), INTERVAL 14 DAY), 
  1
);
