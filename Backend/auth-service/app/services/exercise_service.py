from sqlalchemy.orm import Session
from app.models.orm.task_orm import TaskORM
from typing import List, Optional, Dict, Any
from datetime import date, datetime

class ExerciseService:
    def __init__(self):
        pass
    
    def get_exercises_by_patient(self, patient_id: int, db: Session) -> List[Dict[str, Any]]:
        """Obter exercícios de um paciente do banco de dados"""
        exercises = db.query(TaskORM).filter(
            TaskORM.patient_id == patient_id,
            TaskORM.is_active == True
        ).all()
        
        result = []
        for exercise in exercises:
            result.append({
                "id": exercise.exercise_id or exercise.id,
                "title": exercise.title,
                "description": exercise.description,
                "points_value": exercise.points_value,
                "frequency_per_week": exercise.frequency_per_week,
                "is_active": exercise.is_active,
                "created_at": exercise.created_at.isoformat() if exercise.created_at else datetime.now().isoformat(),
                "patient_id": exercise.patient_id,
                "assigned_by": f"Profissional {exercise.professional_id}",
                "assigned_at": exercise.created_at.isoformat() if exercise.created_at else "Desconhecido",
                "can_delete": True
            })
        
        return result
    
    def delete_exercise(self, exercise_id: int, db: Session) -> bool:
        """Excluir exercício do banco de dados"""
        # Tentar encontrar por exercise_id primeiro
        exercise = db.query(TaskORM).filter(
            TaskORM.exercise_id == exercise_id
        ).first()
        
        # Se não encontrar, tentar por id
        if not exercise:
            exercise = db.query(TaskORM).filter(
                TaskORM.id == exercise_id
            ).first()
        
        if exercise:
            db.delete(exercise)
            db.commit()
            return True
        
        return False
    
    def get_all_exercises(self, db: Session) -> List[Dict[str, Any]]:
        """Obter todos os exercícios de todos os pacientes"""
        exercises = db.query(TaskORM).filter(
            TaskORM.is_active == True
        ).all()
        
        print(f"🔍 DEBUG: Encontrados {len(exercises)} exercícios no banco")
        
        result = []
        for exercise in exercises:
            exercise_data = {
                "id": exercise.exercise_id or exercise.id,
                "title": exercise.title,
                "description": exercise.description,
                "points_value": exercise.points_value,
                "frequency_per_week": exercise.frequency_per_week,
                "is_active": exercise.is_active,
                "created_at": exercise.created_at.isoformat() if exercise.created_at else datetime.now().isoformat(),
                "patient_id": exercise.patient_id,
                "assigned_by": f"Profissional {exercise.professional_id}",
                "assigned_at": exercise.created_at.isoformat() if exercise.created_at else "Desconhecido",
                "can_delete": True
            }
            print(f"   - Exercício: {exercise_data['id']} - {exercise_data['title']}")
            result.append(exercise_data)
        
        return result
    
    def initialize_mock_data(self, db: Session):
        """Inicializar dados mockados no banco se não existirem"""
        # Verificar se já existem exercícios com os IDs específicos
        existing_ids = db.query(TaskORM.exercise_id).filter(
            TaskORM.exercise_id.in_([1001, 1002, 2001, 2002, 3001])
        ).all()
        
        if len(existing_ids) >= 5:
            return  # Já existem todos os dados mockados
        
        # Dados mockados iniciais
        mock_exercises = [
            # Paciente 1 - Edgar
            {
                "professional_id": 1,
                "patient_id": 1,
                "exercise_id": 1001,
                "title": "Rotação de ombro",
                "description": "Movimentos circulares suaves para fortalecer ombro",
                "points_value": 15,
                "frequency_per_week": 3,
                "start_date": date.today()
            },
            {
                "professional_id": 1,
                "patient_id": 1,
                "exercise_id": 1002,
                "title": "Elevação lateral",
                "description": "Levantar braços lateralmente até altura dos ombros",
                "points_value": 20,
                "frequency_per_week": 2,
                "start_date": date.today()
            },
            # Paciente 2 - Vinícius
            {
                "professional_id": 1,
                "patient_id": 2,
                "exercise_id": 2001,
                "title": "Agachamento parcial",
                "description": "Agachar até 45 graus para fortalecer quadríceps",
                "points_value": 25,
                "frequency_per_week": 4,
                "start_date": date.today()
            },
            {
                "professional_id": 1,
                "patient_id": 2,
                "exercise_id": 2002,
                "title": "Elevação de panturrilha",
                "description": "Levantar-se na ponta dos pés para fortalecer panturrilhas",
                "points_value": 15,
                "frequency_per_week": 3,
                "start_date": date.today()
            },
            # Paciente 3 - Teste
            {
                "professional_id": 1,
                "patient_id": 3,
                "exercise_id": 3001,
                "title": "Caminhada leve",
                "description": "Caminhar por 15 minutos para aquecimento",
                "points_value": 10,
                "frequency_per_week": 5,
                "start_date": date.today()
            }
        ]
        
        # Inserir no banco
        for exercise_data in mock_exercises:
            exercise = TaskORM(**exercise_data)
            db.add(exercise)
        
        db.commit()
        print(f"✅ {len(mock_exercises)} exercícios mockados criados no banco de dados")
