import sqlite3
import os

# Remove banco existente
if os.path.exists("smartsaude.db"):
    os.remove("smartsaude.db")

conn = sqlite3.connect("smartsaude.db")
cursor = conn.cursor()

# Cria tabelas básicas
cursor.execute('''
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'Patient',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
''')

cursor.execute('''
CREATE TABLE notifications (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    channel VARCHAR(32) NOT NULL,
    title VARCHAR(120) NOT NULL,
    message TEXT NOT NULL,
    schedule_at TEXT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'queued',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
''')

cursor.execute('''
CREATE TABLE consent_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    consent_type VARCHAR(64) NOT NULL,
    granted INTEGER NOT NULL,
    granted_at TIMESTAMP NULL,
    revoked_at TIMESTAMP NULL,
    consent_data_encrypted TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
''')

cursor.execute('''
CREATE TABLE medical_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    patient_id INTEGER NOT NULL,
    professional_id INTEGER NOT NULL,
    notes TEXT NULL,
    notes_encrypted TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
''')

cursor.execute('''
CREATE TABLE exercises (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(120) NOT NULL,
    description TEXT NOT NULL,
    tags_csv VARCHAR(512) NOT NULL DEFAULT '',
    media_path VARCHAR(512) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
''')

cursor.execute('''
CREATE TABLE training_plans (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    patient_id INTEGER NOT NULL,
    professional_id INTEGER NOT NULL,
    title VARCHAR(120) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
''')

cursor.execute('''
CREATE TABLE training_plan_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    plan_id INTEGER NOT NULL,
    exercise_id INTEGER NOT NULL,
    sets INTEGER NOT NULL DEFAULT 0,
    reps INTEGER NOT NULL DEFAULT 0,
    frequency_per_week INTEGER NOT NULL DEFAULT 0,
    notes VARCHAR(512) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
''')

cursor.execute('''
CREATE TABLE training_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    patient_id INTEGER NOT NULL,
    plan_id INTEGER NOT NULL,
    exercise_id INTEGER NOT NULL,
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    perceived_effort INTEGER NULL,
    pain_level INTEGER NULL,
    notes VARCHAR(512) NULL,
    notes_encrypted TEXT NULL
)
''')

conn.commit()
conn.close()

print("Banco SQLite criado com sucesso!")
