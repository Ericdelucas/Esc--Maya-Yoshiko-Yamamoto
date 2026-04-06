CREATE TABLE IF NOT EXISTS appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    appointment_date DATETIME NOT NULL,
    time VARCHAR(10),  -- HH:MM format
    professional_id INT NOT NULL,
    patient_id INT,
    status VARCHAR(20) DEFAULT 'scheduled',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (professional_id) REFERENCES users(id),
    FOREIGN KEY (patient_id) REFERENCES users(id),
    INDEX idx_professional_date (professional_id, appointment_date),
    INDEX idx_date (appointment_date)
);
