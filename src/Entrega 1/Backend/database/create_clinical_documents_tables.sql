-- Criar tabelas para documentos clínicos no ehr-service

-- Tabela de pastas de documentos dos pacientes
CREATE TABLE IF NOT EXISTS patient_document_folders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_by INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabela de documentos dos pacientes
CREATE TABLE IF NOT EXISTS patient_documents (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    folder_id INT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_size BIGINT DEFAULT 0,
    uploaded_by INT NOT NULL,
    notes TEXT NULL,
    is_archived BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (folder_id) REFERENCES patient_document_folders(id) ON DELETE SET NULL,
    FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE CASCADE
);

-- Inserir pastas padrão para pacientes existentes
INSERT IGNORE INTO patient_document_folders (patient_id, name, created_by) 
SELECT u.id, 'Exames de Sangue', u.id 
FROM users u 
WHERE u.role = 'Patient' AND u.id NOT IN (
    SELECT DISTINCT pdf.patient_id 
    FROM patient_document_folders pdf 
    WHERE pdf.name = 'Exames de Sangue'
);

INSERT IGNORE INTO patient_document_folders (patient_id, name, created_by) 
SELECT u.id, 'Radiografias', u.id 
FROM users u 
WHERE u.role = 'Patient' AND u.id NOT IN (
    SELECT DISTINCT pdf.patient_id 
    FROM patient_document_folders pdf 
    WHERE pdf.name = 'Radiografias'
);

INSERT IGNORE INTO patient_document_folders (patient_id, name, created_by) 
SELECT u.id, 'Laudos Médicos', u.id 
FROM users u 
WHERE u.role = 'Patient' AND u.id NOT IN (
    SELECT DISTINCT pdf.patient_id 
    FROM patient_document_folders pdf 
    WHERE pdf.name = 'Laudos Médicos'
);

-- Criar índices para melhor performance
CREATE INDEX idx_patient_document_folders_patient_id ON patient_document_folders(patient_id);
CREATE INDEX idx_patient_documents_patient_id ON patient_documents(patient_id);
CREATE INDEX idx_patient_documents_folder_id ON patient_documents(folder_id);
CREATE INDEX idx_patient_documents_uploaded_by ON patient_documents(uploaded_by);
CREATE INDEX idx_patient_documents_is_archived ON patient_documents(is_archived);
