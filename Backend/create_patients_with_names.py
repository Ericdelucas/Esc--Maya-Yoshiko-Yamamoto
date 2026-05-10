#!/usr/bin/env python3
"""
Script para criar/atualizar pacientes com nomes reais
"""
import pymysql

# Configuração do banco de dados (via Docker)
DB_CONFIG = {
    'host': 'localhost',
    'user': 'smartuser',
    'password': 'smartpass',
    'database': 'smartsaude',
    'port': 3306
}

def create_patients_with_names():
    """Cria/atualiza pacientes com nomes reais"""
    
    pacientes = [
        {
            'id': 1,
            'email': 'edgar@paciente.com',
            'role': 'patient',
            'full_name': 'Edgar Ferreira'
        },
        {
            'id': 2,
            'email': 'vinicius@paciente.com',
            'role': 'patient',
            'full_name': 'Vinícius Santos'
        },
        {
            'id': 3,
            'email': 'ana@paciente.com',
            'role': 'patient',
            'full_name': 'Ana Carolina'
        },
        {
            'id': 4,
            'email': 'carlos@paciente.com',
            'role': 'patient',
            'full_name': 'Carlos Alberto'
        },
        {
            'id': 5,
            'email': 'juliana@paciente.com',
            'role': 'patient',
            'full_name': 'Juliana Mendes'
        },
        {
            'id': 6,
            'email': 'roberto@paciente.com',
            'role': 'patient',
            'full_name': 'Roberto Silva'
        }
    ]
    
    try:
        connection = pymysql.connect(**DB_CONFIG)
        cursor = connection.cursor()
        
        for paciente in pacientes:
            patient_id = paciente['id']
            email = paciente['email']
            role = paciente['role']
            full_name = paciente['full_name']
            
            # Verificar se paciente já existe
            cursor.execute("SELECT id, full_name FROM users WHERE id = %s", (patient_id,))
            existing_patient = cursor.fetchone()
            
            if existing_patient:
                current_name = existing_patient[1]
                print(f"Paciente ID {patient_id} já existe. Nome atual: '{current_name}'")
                
                # Atualizar se não tiver nome completo
                if not current_name or current_name == f"Paciente {patient_id}":
                    print(f"Atualizando nome para: '{full_name}'")
                    cursor.execute(
                        "UPDATE users SET email = %s, role = %s, full_name = %s WHERE id = %s",
                        (email, role, full_name, patient_id)
                    )
                else:
                    print(f"Mantendo nome existente: '{current_name}'")
            else:
                print(f"Criando paciente ID {patient_id}: '{full_name}'")
                # Usar uma senha padrão para novos pacientes
                password_hash = "dummy_hash_for_new_patient"
                cursor.execute(
                    "INSERT INTO users (id, email, password_hash, role, full_name) VALUES (%s, %s, %s, %s, %s)",
                    (patient_id, email, password_hash, role, full_name)
                )
        
        connection.commit()
        print("\n✅ Pacientes criados/atualizados com sucesso!")
        
        # Verificar resultado
        print("\n📋 Pacientes no banco:")
        print("=" * 50)
        cursor.execute("SELECT id, email, full_name FROM users WHERE role = 'patient' ORDER BY id")
        patients = cursor.fetchall()
        for patient in patients:
            print(f"ID: {patient[0]} | Email: {patient[1]} | Nome: {patient[2]}")
        
    except Exception as e:
        print(f"❌ Erro: {e}")
    finally:
        if 'connection' in locals():
            connection.close()

if __name__ == "__main__":
    create_patients_with_names()
