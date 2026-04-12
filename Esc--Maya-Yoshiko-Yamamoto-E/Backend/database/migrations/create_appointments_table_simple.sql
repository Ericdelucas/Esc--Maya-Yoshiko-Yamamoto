CREATE TABLE IF NOT EXISTS appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    appointment_date DATETIME NOT NULL,
    time VARCHAR(10),
    professional_id INT NOT NULL,
    patient_id INT,
    status VARCHAR(20) DEFAULT 'scheduled',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_professional_date (professional_id, appointment_date),
    INDEX idx_date (appointment_date)
);
