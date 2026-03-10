-- init.sql
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'Patient',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email)
);

-- =========================================================
-- NOTIFICATIONS
-- =========================================================
CREATE TABLE IF NOT EXISTS notifications (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  channel VARCHAR(32) NOT NULL,
  title VARCHAR(120) NOT NULL,
  message TEXT NOT NULL,
  schedule_at DATETIME NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'queued',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_notifications_user_id (user_id),
  INDEX idx_notifications_status (status),
  INDEX idx_notifications_schedule_at (schedule_at)
);

-- =========================================================
-- CONSENT RECORDS (LGPD)
-- =========================================================
CREATE TABLE IF NOT EXISTS consent_records (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  consent_type VARCHAR(64) NOT NULL,  -- ex: 'ehr_processing'
  granted TINYINT(1) NOT NULL,
  granted_at TIMESTAMP NULL,
  revoked_at TIMESTAMP NULL,
  consent_data_encrypted LONGTEXT NULL,  -- Encrypted consent details
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_consent_user_type (user_id, consent_type),
  INDEX idx_consent_granted (granted)
);

-- =========================================================
-- MEDICAL RECORDS (EHR)
-- =========================================================
CREATE TABLE IF NOT EXISTS medical_records (
  id INT AUTO_INCREMENT PRIMARY KEY,
  patient_id INT NOT NULL,
  professional_id INT NOT NULL,
  notes TEXT NULL,  -- Legacy field for migration
  notes_encrypted LONGTEXT NULL,  -- Encrypted medical notes
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_medical_patient_id (patient_id),
  INDEX idx_medical_professional_id (professional_id),
  INDEX idx_medical_created_at (created_at)
);

-- =========================================================
-- EXERCISES
-- =========================================================
CREATE TABLE IF NOT EXISTS exercises (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(120) NOT NULL,
  description TEXT NOT NULL,
  tags_csv VARCHAR(512) NOT NULL DEFAULT '',
  media_path VARCHAR(512) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_exercises_created_at (created_at)
);

-- =========================================================
-- TRAINING PLANS
-- =========================================================
CREATE TABLE IF NOT EXISTS training_plans (
  id INT AUTO_INCREMENT PRIMARY KEY,
  patient_id INT NOT NULL,
  professional_id INT NOT NULL,
  title VARCHAR(120) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_training_plans_patient_id (patient_id),
  INDEX idx_training_plans_professional_id (professional_id),
  INDEX idx_training_plans_created_at (created_at)
);

-- =========================================================
-- TRAINING PLAN ITEMS (plan → exercises)
-- =========================================================
CREATE TABLE IF NOT EXISTS training_plan_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  plan_id INT NOT NULL,
  exercise_id INT NOT NULL,
  sets INT NOT NULL DEFAULT 0,
  reps INT NOT NULL DEFAULT 0,
  frequency_per_week INT NOT NULL DEFAULT 0,
  notes VARCHAR(512) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_plan_items_plan_id (plan_id),
  INDEX idx_plan_items_exercise_id (exercise_id)
);

-- =========================================================
-- TRAINING LOGS (patient execution)
-- =========================================================
CREATE TABLE IF NOT EXISTS training_logs (
  id INT AUTO_INCREMENT PRIMARY KEY,
  patient_id INT NOT NULL,
  plan_id INT NOT NULL,
  exercise_id INT NOT NULL,
  performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  perceived_effort INT NULL, -- 1-10
  pain_level INT NULL,       -- 0-10
  notes VARCHAR(512) NULL,
  notes_encrypted LONGTEXT NULL,  -- Encrypted patient observations
  INDEX idx_training_logs_patient_id (patient_id),
  INDEX idx_training_logs_plan_id (plan_id),
  INDEX idx_training_logs_exercise_id (exercise_id),
  INDEX idx_training_logs_performed_at (performed_at)
);
