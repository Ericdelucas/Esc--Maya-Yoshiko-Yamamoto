from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from typing import List, Optional
from datetime import date

from app.storage.database.db import get_session
from app.services.task_service import TaskService
from app.core.dependencies import get_current_user
from app.models.schemas.user_schema import UserOut

# 🔥 **SISTEMA DE PONTOS EM MEMÓRIA**
# Simula banco de dados para soma real de pontos
user_points_db = {}
user_tasks_db = {}

# 🔥 **SISTEMA DE CONTROLE DE REPETIÇÃO DIÁRIA**
# Impede que usuário complete o mesmo exercício várias vezes no mesmo dia
daily_completed_tasks = {}  # {user_id: {date: [task_ids]}}

# 🔥 **SISTEMA DE EXERCÍCIOS POR PACIENTE**
patient_exercises_db = {}  # {patient_id: [exercises]}

# 🔥 **EXERCÍCIOS INICIAIS POR PACIENTE**
patient_exercises_db = {
    1: [  # Paciente Edgar - Problema de ombro
        {
            "id": 1001,
            "title": "Rotação de ombro",
            "description": "Movimentos circulares suaves para fortalecer ombro",
            "points_value": 15,
            "frequency_per_week": 3,
            "is_active": True,
            "created_at": "2026-04-24T00:00:00"
        },
        {
            "id": 1002,
            "title": "Elevação lateral",
            "description": "Levantar braços lateralmente até altura dos ombros",
            "points_value": 20,
            "frequency_per_week": 2,
            "is_active": True,
            "created_at": "2026-04-24T00:00:00"
        }
    ],
    2: [  # Paciente Vinícius - Problema de perna
        {
            "id": 2001,
            "title": "Agachamento parcial",
            "description": "Agachar até 45 graus para fortalecer quadríceps",
            "points_value": 25,
            "frequency_per_week": 4,
            "is_active": True,
            "created_at": "2026-04-24T00:00:00"
        },
        {
            "id": 2002,
            "title": "Elevação de panturrilha",
            "description": "Levantar-se na ponta dos pés para fortalecer panturrilhas",
            "points_value": 15,
            "frequency_per_week": 3,
            "is_active": True,
            "created_at": "2026-04-24T00:00:00"
        }
    ],
    3: [  # Paciente teste - Exercícios gerais
        {
            "id": 3001,
            "title": "Caminhada leve",
            "description": "Caminhar por 15 minutos para aquecimento",
            "points_value": 10,
            "frequency_per_week": 5,
            "is_active": True,
            "created_at": "2026-04-24T00:00:00"
        }
    ]
}

# 🔥 **FUNÇÕES DE GERENCIAMENTO DE PONTOS COM RESET DIÁRIO**
def should_reset_daily(user_id: int):
    """Verifica se deve resetar os pontos do usuário diariamente"""
    if user_id not in user_points_db:
        return True  # Primeiro acesso do dia
    
    last_reset = user_points_db[user_id].get("last_reset_date")
    today = date.today().isoformat()
    
    # Reseta se for um novo dia
    return last_reset != today

def reset_daily_points(user_id: int):
    """Reseta os pontos do usuário para o novo dia"""
    today = date.today().isoformat()
    
    # Se não existe, cria com reset
    if user_id not in user_points_db:
        user_points_db[user_id] = {
            "total_points": 0,
            "tasks_completed": 0,
            "current_streak": 0,
            "weekly_points": 0,
            "monthly_points": 0,
            "level": "Nível 1",
            "next_level_points": 50,
            "badges": ["Iniciante"],
            "last_reset_date": today
        }
        return
    
    # Se existe, reseta mantendo streak semanal
    current_streak = user_points_db[user_id]["current_streak"]
    weekly_points = user_points_db[user_id]["weekly_points"]
    
    user_points_db[user_id] = {
        "total_points": 0,           # 🔥 RESETA PARA 0
        "tasks_completed": 0,         # 🔥 RESETA PARA 0
        "current_streak": current_streak, # Mantém streak semanal
        "weekly_points": weekly_points,    # Mantém pontos semanais
        "monthly_points": 0,           # Reseta mensal
        "level": "Nível 1",           # 🔥 VOLTA AO NÍVEL 1
        "next_level_points": 50,       # 🔥 REINICIA PROGRESSÃO
        "badges": ["Iniciante"],      # 🔥 APENAS BADGE INICIAL
        "last_reset_date": today        # 🔥 REGISTRA DATA DO RESET
    }

def initialize_user_points(user_id: int):
    """Inicializa pontos do usuário se não existir"""
    if user_id not in user_points_db:
        reset_daily_points(user_id)  # 🔥 Usa reset diário

def add_points_to_user(user_id: int, points: int):
    """Adiciona pontos ao usuário com verificação diária"""
    # 🔥 **VERIFICA RESET ANTES DE SOMAR**
    if should_reset_daily(user_id):
        reset_daily_points(user_id)
    
    # 🔥 **SOMA PONTOS NORMALMENTE**
    user_points_db[user_id]["total_points"] += points
    user_points_db[user_id]["tasks_completed"] += 1
    user_points_db[user_id]["current_streak"] += 1
    user_points_db[user_id]["weekly_points"] += points
    user_points_db[user_id]["monthly_points"] += points
    
    # 🔥 **VERIFICA SUBIDA DE NÍVEL**
    current_points = user_points_db[user_id]["total_points"]
    if current_points >= 200:
        user_points_db[user_id]["level"] = "Nível 4"
        user_points_db[user_id]["next_level_points"] = 300
        if "Mestre" not in user_points_db[user_id]["badges"]:
            user_points_db[user_id]["badges"].append("Mestre")
    elif current_points >= 100:
        user_points_db[user_id]["level"] = "Nível 3"
        user_points_db[user_id]["next_level_points"] = 200
        if "Pontuoso" not in user_points_db[user_id]["badges"]:
            user_points_db[user_id]["badges"].append("Pontuoso")
    elif current_points >= 50:
        user_points_db[user_id]["level"] = "Nível 2"
        user_points_db[user_id]["next_level_points"] = 100
        if "Dedicado" not in user_points_db[user_id]["badges"]:
            user_points_db[user_id]["badges"].append("Dedicado")

def get_user_points_data(user_id: int):
    """Retorna dados de pontos do usuário com reset diário"""
    # 🔥 **VERIFICA SE PRECISA RESETAR**
    if should_reset_daily(user_id):
        reset_daily_points(user_id)
    
    return user_points_db[user_id]

# 🔥 **SISTEMA DE PROGRESSO DIÁRIO**
def get_daily_progress_percentage(user_id: int):
    """Calcula porcentagem de progresso diário"""
    today = date.today().isoformat()
    
    # 🔥 **OBTER EXERCÍCIOS DISPONÍVEIS HOJE - TOTAL REAL DO PACIENTE**
    if user_id in patient_exercises_db:
        total_daily_exercises = len(patient_exercises_db[user_id])
        print(f"   - Total de exercícios do paciente {user_id}: {total_daily_exercises}")
    else:
        total_daily_exercises = 1  # Mínimo para evitar divisão por zero
        print(f"   - Paciente {user_id} não tem exercícios, usando padrão: {total_daily_exercises}")
    
    # 🔥 **OBTER EXERCÍCIOS COMPLETADOS HOJE**
    if user_id not in daily_completed_tasks or today not in daily_completed_tasks[user_id]:
        completed_today = 0
    else:
        completed_today = len(daily_completed_tasks[user_id][today])
    
    # 🔥 **CALCULAR PORCENTAGEM COM TOTAL REAL**
    if total_daily_exercises == 0:
        progress_percentage = 0.0
    else:
        progress_percentage = (completed_today / total_daily_exercises) * 100
    
    return {
        "user_id": user_id,
        "date": today,
        "total_daily_exercises": total_daily_exercises,
        "completed_today": completed_today,
        "remaining_today": total_daily_exercises - completed_today,
        "progress_percentage": round(progress_percentage, 1),  # Arredondar para 1 casa decimal
        "progress_fraction": f"{completed_today}/{total_daily_exercises}",
        "is_complete": completed_today >= total_daily_exercises,
        "status_message": get_progress_status_message(completed_today, total_daily_exercises)
    }

def get_progress_status_message(completed: int, total: int) -> str:
    """Gera mensagem de status baseada no progresso"""
    percentage = (completed / total) * 100
    
    if completed == 0:
        return "Comece seus exercícios hoje! 💪"
    elif percentage < 25:
        return "Bom começo! Continue assim! 🌱"
    elif percentage < 50:
        return "Você está no caminho certo! 🚶‍♂️"
    elif percentage < 75:
        return "Ótimo progresso! Continue firme! 💪"
    elif percentage < 100:
        return "Quase lá! Você consegue! 🔥"
    else:
        return "Parabéns! Meta diária alcançada! 🎉"

def get_weekly_summary(user_id: int):
    """Obter resumo semanal"""
    # Simplificado - pode ser expandido depois
    return {
        "week_start": "2026-04-21",
        "week_end": "2026-04-27",
        "total_exercises_this_week": 12,
        "total_points_this_week": 180,
        "best_day": "2026-04-23",
        "exercises_on_best_day": 5
    }

def get_recent_achievements(user_id: int):
    """Obter conquistas recentes"""
    # Simplificado - pode ser expandido depois
    return [
        {
            "achievement": "Primeira Semana",
            "date": "2026-04-23",
            "icon": "🏆"
        },
        {
            "achievement": "5 Exercícios em um Dia",
            "date": "2026-04-22",
            "icon": "💪"
        }
    ]

# 🔥 **SISTEMA DE CONTROLE DE REPETIÇÃO**
def can_complete_task_today(user_id: int, task_id: int):
    """Verifica se usuário pode completar tarefa hoje"""
    today = date.today().isoformat()
    
    # 🔥 **LOG DETALHADO PARA DEBUG**
    print(f"🔍 VERIFICANDO TAREFA {task_id} para usuário {user_id}")
    print(f"   - Data de hoje: {today}")
    
    # Inicializa registro do dia
    if user_id not in daily_completed_tasks:
        daily_completed_tasks[user_id] = {}
        print(f"   - Criado registro para usuário {user_id}")
    
    if today not in daily_completed_tasks[user_id]:
        daily_completed_tasks[user_id][today] = []
        print(f"   - Criado registro para dia {today}")
    
    # 🔥 **VERIFICA SE TAREFA JÁ FOI COMPLETADA HOJE**
    completed_today = daily_completed_tasks[user_id][today]
    print(f"   - Tarefas completadas hoje: {completed_today}")
    print(f"   - Verificando se task_id {task_id} está em {completed_today}")
    
    if task_id in completed_today:
        print(f"   - ❌ TAREFA {task_id} JÁ FOI COMPLETADA HOJE!")
        return {
            "allowed": False,
            "reason": f"Este exercício (ID: {task_id}) já foi completado hoje. Tente novamente amanhã!",
            "can_repeat_tomorrow": True
        }
    
    # 🔥 **REGISTRA COMO COMPLETADA**
    completed_today.append(task_id)
    daily_completed_tasks[user_id][today] = completed_today
    print(f"   - ✅ TAREFA {task_id} REGISTRADA COMO COMPLETADA!")
    print(f"   - Nova lista de completadas: {completed_today}")
    
    return {
        "allowed": True,
        "reason": f"Exercício (ID: {task_id}) completado com sucesso!",
        "points_awarded": 15,
        "tasks_completed_today": len(completed_today),
        "remaining_tasks": 5 - len(completed_today)
    }

from app.models.schemas.task_schema import (
    TaskCreate, TaskUpdate, TaskOut, TaskCompletionCreate, 
    TaskCompletionOut, UserPointsOut, LeaderboardEntry, 
    PointsHistoryOut, GlobalChallengeOut, ChallengeParticipationCreate,
    ChallengeParticipationOut, PatientTaskList, TaskStats
)
# TODO: Implementar autenticação real
# SOBRESCREVENDO get_current_user do dependencies.py para evitar erro 422
# def get_current_user() -> dict:
#     return {"sub": 1, "role": "admin", "email": "test@test.com"}

def require_permission(permission):
    def decorator(func):
        return func
    return decorator

# Permissões temporárias
USER_MANAGE = "user:manage"
PROFESSIONAL_MANAGE = "professional:manage"
PATIENT_MANAGE = "patient:manage"
TASK_CREATE = "task:create"
TASK_READ = "task:read"
TASK_UPDATE = "task:update"
TASK_DELETE = "task:delete"

router = APIRouter(prefix="/tasks", tags=["tasks"])
task_service = TaskService()


# ============= ENDPOINTS DE TAREFAS =============

@router.post("")
def create_task(
    task_data: TaskCreate
):
    """Criar nova tarefa (profissional) - INTEGRADO COM patient_exercises_db"""
    
    # Debug: mostrar dados recebidos
    print(f"=== DEBUG TASK CREATE ===")
    print(f"Task data: {task_data}")
    print(f"Task data dict: {task_data.model_dump()}")
    print(f"========================")
    
    # 🔥 **ADICIONAR EXERCÍCIO AO patient_exercises_db**
    patient_id = task_data.patient_id
    new_exercise = {
        "id": 9999,  # ID sequencial simples
        "title": task_data.title,
        "description": task_data.description,
        "points_value": task_data.points_value,
        "frequency_per_week": task_data.frequency_per_week,
        "is_active": True,
        "created_at": task_data.start_date.isoformat() + "T00:00:00",
        "assigned_by": 1,  # Profissional fixo para teste
        "assigned_at": date.today().isoformat()
    }
    
    # 🔥 **ADICIONAR AO BANCO DE EXERCÍCIOS DO PACIENTE**
    if patient_id not in patient_exercises_db:
        patient_exercises_db[patient_id] = []
    
    patient_exercises_db[patient_id].append(new_exercise)
    
    print(f"🏋️ EXERCÍCIO CRIADO E ADICIONADO:")
    print(f"   - Paciente: {patient_id}")
    print(f"   - Exercício: {task_data.title}")
    print(f"   - Total de exercícios do paciente: {len(patient_exercises_db[patient_id])}")
    
    # Retorno mock para teste
    return {
        "id": 999,
        "professional_id": 1,
        "patient_id": task_data.patient_id,
        "title": task_data.title,
        "description": task_data.description,
        "points_value": task_data.points_value,
        "frequency_per_week": task_data.frequency_per_week,
        "start_date": task_data.start_date,
        "is_active": True,
        "created_at": "2024-01-01T00:00:00",
        "message": f"Exercício '{task_data.title}' criado e atribuído ao paciente {patient_id}!"
    }

@router.get("/patient-tasks")
def get_patient_tasks(current_user: UserOut = Depends(get_current_user)):
    """Obter tarefas do paciente - FILTRADO POR USUÁRIO"""
    patient_id = current_user.id
    
    print(f"🔍 BUSCANDO EXERCÍCIOS PARA PACIENTE {patient_id}")
    
    # 🔥 **OBTER EXERCÍCIOS ESPECÍFICOS DO PACIENTE**
    if patient_id in patient_exercises_db:
        exercises = patient_exercises_db[patient_id]
        print(f"   - Encontrados {len(exercises)} exercícios para paciente {patient_id}")
    else:
        # 🔥 **SE NÃO TIVER, CRIA EXERCÍCIOS PADRÃO**
        exercises = [
            {
                "id": 9999,
                "title": "Exercício básico",
                "description": "Exercício inicial para novo paciente",
                "points_value": 15,
                "frequency_per_week": 3,
                "is_active": True,
                "created_at": "2026-04-24T00:00:00"
            }
        ]
        patient_exercises_db[patient_id] = exercises
        print(f"   - Criados exercícios padrão para paciente {patient_id}")
    
    return {
        "success": True,
        "patient_id": patient_id,
        "total_exercises": len(exercises),
        "tasks": exercises
    }


@router.post("/exercises/assign-to-patient")
def assign_exercise_to_patient(
    exercise_data: dict, 
    current_user: UserOut = Depends(get_current_user)
):
    """Profissional atribui exercício específico ao paciente"""
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Apenas profissionais podem atribuir exercícios")
    
    patient_id = exercise_data.get("patient_id")
    exercise = {
        "id": exercise_data.get("id", 9999),
        "title": exercise_data.get("title", "Exercício personalizado"),
        "description": exercise_data.get("description", "Descrição personalizada"),
        "points_value": exercise_data.get("points_value", 15),
        "frequency_per_week": exercise_data.get("frequency_per_week", 3),
        "is_active": True,
        "created_at": date.today().isoformat() + "T00:00:00",
        "assigned_by": current_user.id,
        "assigned_at": date.today().isoformat()
    }
    
    # 🔥 **ADICIONAR EXERCÍCIO ESPECÍFICO AO PACIENTE**
    if patient_id not in patient_exercises_db:
        patient_exercises_db[patient_id] = []
    
    patient_exercises_db[patient_id].append(exercise)
    
    print(f"🏋️ EXERCÍCIO ATRIBUÍDO:")
    print(f"   - Profissional: {current_user.id}")
    print(f"   - Paciente: {patient_id}")
    print(f"   - Exercício: {exercise['title']}")
    print(f"   - ID: {exercise['id']}")
    
    return {
        "success": True,
        "message": f"Exercício '{exercise['title']}' atribuído ao paciente {patient_id}!",
        "exercise": exercise,
        "patient_id": patient_id,
        "assigned_by": current_user.id
    }

@router.get("/exercises/patient/{patient_id}")
def get_patient_exercises_for_professional(
    patient_id: int,
    current_user: UserOut = Depends(get_current_user)
):
    """Profissional visualiza exercícios de um paciente específico"""
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Apenas profissionais podem visualizar exercícios de pacientes")
    
    if patient_id not in patient_exercises_db:
        return {
            "success": True,
            "patient_id": patient_id,
            "total_exercises": 0,
            "tasks": [],
            "message": "Paciente não possui exercícios atribuídos"
        }
    
    exercises = patient_exercises_db[patient_id]
    
    return {
        "success": True,
        "patient_id": patient_id,
        "total_exercises": len(exercises),
        "tasks": exercises,
        "message": f"Paciente {patient_id} possui {len(exercises)} exercícios"
    }

@router.delete("/exercises/{exercise_id}")
def delete_exercise(
    exercise_id: int,
    current_user: UserOut = Depends(get_current_user)
):
    """Deletar exercício (apenas profissionais)"""
    
    # 🔥 **VERIFICAR PERMISSÃO**
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Apenas profissionais podem deletar exercícios")
    
    print(f"🗑️ SOLICITAÇÃO DE DELEÇÃO DE EXERCÍCIO:")
    print(f"   - Profissional: {current_user.id} ({current_user.role})")
    print(f"   - Exercise ID: {exercise_id}")
    
    # 🔥 **BUSCAR EXERCÍCIO EM TODOS OS PACIENTES**
    exercise_found = False
    deleted_from_patients = []
    
    for patient_id, exercises in patient_exercises_db.items():
        for i, exercise in enumerate(exercises):
            if exercise["id"] == exercise_id:
                # 🔥 **REMOVER EXERCÍCIO**
                patient_exercises_db[patient_id].pop(i)
                exercise_found = True
                deleted_from_patients.append(patient_id)
                print(f"   - Encontrado e removido do paciente {patient_id}")
                break
    
    if not exercise_found:
        raise HTTPException(status_code=404, detail=f"Exercício {exercise_id} não encontrado")
    
    print(f"✅ EXERCÍCIO DELETADO:")
    print(f"   - Exercise ID: {exercise_id}")
    print(f"   - Removido de: {len(deleted_from_patients)} paciente(s)")
    print(f"   - Pacientes afetados: {deleted_from_patients}")
    
    return {
        "success": True,
        "message": f"Exercício {exercise_id} deletado com sucesso!",
        "exercise_id": exercise_id,
        "deleted_from_patients": deleted_from_patients,
        "deleted_by": {
            "id": current_user.id,
            "role": current_user.role,
            "email": current_user.email
        }
    }

@router.get("/exercises/manage/{patient_id}")
def get_exercises_for_management(
    patient_id: int,
    current_user: UserOut = Depends(get_current_user)
):
    """Listar exercícios para gerenciamento (com IDs para deleção)"""
    
    if current_user.role not in ["professional", "doctor", "admin"]:
        raise HTTPException(status_code=403, detail="Apenas profissionais podem gerenciar exercícios")
    
    if patient_id not in patient_exercises_db:
        return {
            "success": True,
            "patient_id": patient_id,
            "total_exercises": 0,
            "exercises": [],
            "message": "Paciente não possui exercícios atribuídos"
        }
    
    exercises = patient_exercises_db[patient_id]
    
    # 🔥 **ADICIONAR INFORMAÇÕES DE GERENCIAMENTO**
    managed_exercises = []
    for exercise in exercises:
        managed_exercise = {
            "id": exercise["id"],
            "title": exercise["title"],
            "description": exercise["description"],
            "points_value": exercise["points_value"],
            "frequency_per_week": exercise["frequency_per_week"],
            "is_active": exercise["is_active"],
            "created_at": exercise["created_at"],
            "can_delete": True,  # Profissional pode deletar
            "assigned_by": exercise.get("assigned_by", "Sistema"),
            "assigned_at": exercise.get("assigned_at", "Desconhecido")
        }
        managed_exercises.append(managed_exercise)
    
    return {
        "success": True,
        "patient_id": patient_id,
        "total_exercises": len(managed_exercises),
        "exercises": managed_exercises,
        "message": f"Paciente {patient_id} possui {len(managed_exercises)} exercícios para gerenciamento"
    }

@router.get("/test")
def test_endpoint(current_user: UserOut = Depends(get_current_user)):
    """Endpoint de teste - RETORNANDO TAREFAS ESPECÍFICAS DO USUÁRIO"""
    patient_id = current_user.id
    
    print(f"🔍 ENDPOINT /TEST - BUSCANDO EXERCÍCIOS PARA PACIENTE {patient_id}")
    
    # 🔥 **OBTER EXERCÍCIOS ESPECÍFICOS DO PACIENTE**
    if patient_id in patient_exercises_db:
        exercises = patient_exercises_db[patient_id]
        print(f"   - Encontrados {len(exercises)} exercícios para paciente {patient_id}")
    else:
        # 🔥 **SE NÃO TIVER, CRIA EXERCÍCIOS PADRÃO**
        exercises = [
            {
                "id": 9999,
                "title": "Exercício básico",
                "description": "Exercício inicial para novo paciente",
                "points_value": 15,
                "frequency_per_week": 3,
                "is_active": True,
                "created_at": "2026-04-24T00:00:00"
            }
        ]
        patient_exercises_db[patient_id] = exercises
        print(f"   - Criados exercícios padrão para paciente {patient_id}")
    
    return {
        "message": "test working",
        "patient_id": patient_id,
        "total_exercises": len(exercises),
        "tasks": exercises
    }

@router.post("/simple")
def test_simple():
    """Endpoint POST simples sem parâmetros"""
    return {"message": "simple post working"}

@router.post("/complete-task")
def complete_task_with_control(task_data: dict = None, current_user: UserOut = Depends(get_current_user)):
    """Concluir tarefa COM CONTROLE DE REPETIÇÃO DIÁRIA"""
    # 🔥 **OBTER ID REAL DA TAREFA**
    if task_data and "task_id" in task_data:
        task_id = task_data["task_id"]
    else:
        # Para compatibilidade com frontend antigo
        task_id = 999  # ID padrão
    
    # 🔥 **VERIFICA SE PODE COMPLETAR HOJE**
    completion_check = can_complete_task_today(current_user.id, task_id)
    
    if not completion_check["allowed"]:
        return {
            "success": False,
            "message": completion_check["reason"],
            "can_repeat_tomorrow": completion_check.get("can_repeat_tomorrow", False),
            "task_id": task_id
        }
    
    # 🔥 **ADICIONA PONTOS SE PERMITIDO**
    points_per_task = completion_check["points_awarded"]
    add_points_to_user(current_user.id, points_per_task)
    updated_data = get_user_points_data(current_user.id)
    
    return {
        "success": True,
        "message": completion_check["reason"],
        "task_id": task_id,
        "patient_id": current_user.id,
        "completion_date": "2026-04-24",
        "points_awarded": points_per_task,
        "new_total_points": updated_data["total_points"],
        "tasks_completed": updated_data["tasks_completed"],
        "tasks_completed_today": completion_check["tasks_completed_today"],
        "remaining_tasks": completion_check["remaining_tasks"],
        "level_up": updated_data["level"] != "Nível 1",
        "current_level": updated_data["level"]
    }

@router.get("/user-points")
def get_user_points(current_user: UserOut = Depends(get_current_user)):
    """Obter pontos do usuário atual - USUÁRIO REAL DO LOGIN"""
    
    # 🔥 **PEGAR NOME REAL DO USUÁRIO DO TOKEN**
    # Extrair nome do email (parte antes do @)
    # Se o login for "chien@test.com" -> nome será "chien"
    # Se o login for "007@test.com" -> nome será "007"
    email_username = current_user.email.split("@")[0]
    
    # 🔥 **OBTER PONTOS REAIS DO SISTEMA**
    # Pega pontos acumulados do usuário (começa do 0 e soma com tarefas)
    user_data = get_user_points_data(current_user.id)
    
    return {
        "user_id": current_user.id,  # ID real do usuário logado
        "username": email_username,  # Nome real baseado no login
        "total_points": user_data["total_points"],  # 🔥 PONTOS REAIS ACUMULADOS
        "tasks_completed": user_data["tasks_completed"],  # 🔥 TAREFAS REAIS COMPLETADAS
        "current_streak": user_data["current_streak"],  # 🔥 SEQUÊNCIA REAL
        "weekly_points": user_data["weekly_points"],  # Pontos da semana
        "monthly_points": user_data["monthly_points"],  # Pontos do mês
        "level": user_data["level"],  # 🔥 NÍVEL REAL BASEADO EM PONTOS
        "next_level_points": user_data["next_level_points"],  # Próximo nível
        "badges": user_data["badges"]  # 🔥 BADGES REAIS CONQUISTADOS
    }

# REMOVIDO - ENDPOINT DUPLICADO


@router.post("/clear-test-data")
def clear_test_data(current_user: UserOut = Depends(get_current_user)):
    """Limpa dados de teste do usuário"""
    user_id = current_user.id
    today = date.today().isoformat()
    
    # 🔥 **LIMPAR PONTOS**
    if user_id in user_points_db:
        del user_points_db[user_id]
    
    # 🔥 **LIMPAR TAREFAS COMPLETADAS**
    if user_id in user_tasks_db:
        del user_tasks_db[user_id]
    
    # 🔥 **LIMPAR CONTROLE DE REPETIÇÃO - COMPLETO!**
    if user_id in daily_completed_tasks:
        del daily_completed_tasks[user_id]
    
    # 🔥 **FORÇAR LIMPEZA COMPLETA**
    # Garante que não reste nenhum registro
    daily_completed_tasks.clear()
    
    # 🔥 **LOG PARA DEBUG**
    print(f"🧹 DADOS LIMPOS para usuário {user_id}:")
    print(f"   - user_points_db: {user_id in user_points_db}")
    print(f"   - user_tasks_db: {user_id in user_tasks_db}")
    print(f"   - daily_completed_tasks: {user_id in daily_completed_tasks}")
    print(f"   - daily_completed_tasks total: {len(daily_completed_tasks)}")
    
    return {
        "success": True,
        "message": "Dados de teste limpos com sucesso!",
        "user_id": user_id,
        "today": today,
        "cleared_data": [
            "user_points",
            "user_tasks", 
            "daily_completed_tasks"
        ],
        "debug_info": {
            "daily_completed_tasks_size": len(daily_completed_tasks),
            "user_in_points_db": user_id in user_points_db,
            "user_in_tasks_db": user_id in user_tasks_db,
            "user_in_daily_tasks": user_id in daily_completed_tasks
        }
    }

@router.get("/progress/daily")
def get_daily_progress(current_user: UserOut = Depends(get_current_user)):
    """Obter progresso diário do usuário"""
    progress_data = get_daily_progress_percentage(current_user.id)
    
    print(f"📊 PROGRESSO DIÁRIO - Usuário {current_user.id}:")
    print(f"   - Completados hoje: {progress_data['completed_today']}")
    print(f"   - Total diário: {progress_data['total_daily_exercises']}")
    print(f"   - Porcentagem: {progress_data['progress_percentage']}%")
    print(f"   - Status: {progress_data['status_message']}")
    
    return {
        "success": True,
        "message": "Progresso diário carregado com sucesso!",
        "data": progress_data
    }

@router.get("/progress/detailed")
def get_detailed_progress(current_user: UserOut = Depends(get_current_user)):
    """Obter progresso detalhado com estatísticas"""
    daily_progress = get_daily_progress_percentage(current_user.id)
    user_points = get_user_points_data(current_user.id)
    
    return {
        "success": True,
        "message": "Progresso detalhado carregado!",
        "daily_progress": daily_progress,
        "user_stats": {
            "total_points": user_points["total_points"],
            "current_level": user_points["level"],
            "current_streak": user_points["current_streak"],
            "badges": user_points["badges"]
        },
        "weekly_summary": get_weekly_summary(current_user.id),
        "achievements": get_recent_achievements(current_user.id)
    }

@router.get("/leaderboard")
def get_leaderboard(current_user: UserOut = Depends(get_current_user)):
    """Obter ranking - USUÁRIO REAL + BOTS COMPETITIVOS"""
    
    # 🔥 **USUÁRIO REAL LOGADO EM 1º LUGAR**
    # Extrair nome do email (parte antes do @)
    # Se o login for "chien@test.com" -> nome será "chien"
    # Se o login for "007@test.com" -> nome será "007"
    email_username = current_user.email.split("@")[0]
    
    # 🔥 **USUÁRIO REAL COM PONTOS ACUMULADOS**
    user_data = get_user_points_data(current_user.id)
    real_user = {
        "user_id": current_user.id,  # ID real do usuário logado
        "username": email_username,  # Nome real baseado no login
        "total_points": user_data["total_points"],  # 🔥 PONTOS REAIS ACUMULADOS
        "tasks_completed": user_data["tasks_completed"],  # 🔥 TAREFAS REAIS COMPLETADAS
        "rank": 1,  # Sempre em 1º por enquanto
        "is_real_user": True  # Marcar como usuário real
    }
    
    # 🤖 **BOTS COMPETITIVOS (como no projeto test)**
    bots = [
        {
            "user_id": 999,
            "username": "Dr. Silva Bot",
            "total_points": 120,  # Um pouco menos que usuário real
            "tasks_completed": 5,
            "rank": 2,
            "is_real_user": False
        },
        {
            "user_id": 998,
            "username": "Ana Bot",
            "total_points": 95,
            "tasks_completed": 4,
            "rank": 3,
            "is_real_user": False
        },
        {
            "user_id": 997,
            "username": "Carlos Bot",
            "total_points": 80,
            "tasks_completed": 3,
            "rank": 4,
            "is_real_user": False
        },
        {
            "user_id": 996,
            "username": "Maria Bot",
            "total_points": 65,
            "tasks_completed": 2,
            "rank": 5,
            "is_real_user": False
        }
    ]
    
    # 🔥 **RETORNAR USUÁRIO REAL + BOTS**
    ranking = [real_user] + bots
    
    return ranking


@router.get("/patient/{patient_id}")
def get_patient_tasks(
    patient_id: int
):
    """Obter tarefas de um paciente - SEM DEPENDÊNCIAS"""
    # Mock de retorno para teste
    return {
        "patient_id": patient_id,
        "tasks": [
            {
                "id": 999,
                "title": "Olhar",
                "description": "bb",
                "points_value": 99,
                "frequency_per_week": 1,
                "is_active": True,
                "created_at": "2026-04-23T00:00:00"
            }
        ]
    }


# MOVIDA PARA O FIM DO ARQUIVO - ROTA COM PARÂMETRO DEPOIS DAS ESPECÍFICAS


@router.get("/professional/list", response_model=List[TaskOut])
def get_professional_tasks(
    current_user: dict = Depends(require_permission(TASK_READ)),
    db: Session = Depends(get_session)
):
    """Obter tarefas criadas pelo profissional"""
    professional_id = current_user.get("sub")
    return task_service.get_tasks_by_professional(professional_id, db)


@router.post("/{task_id}/complete")
def complete_task(
    task_id: int
):
    """Marcar tarefa como concluída - IMPLEMENTAÇÃO SIMPLIFICADA"""
    
    # Mock de resposta de sucesso
    return {
        "id": 1,
        "task_id": task_id,
        "patient_id": 3,
        "completion_date": "2026-04-24",
        "points_awarded": 15,
        "message": "Tarefa concluída com sucesso!"
    }


@router.get("/patient/{patient_id}/daily", response_model=List[TaskOut])
def get_daily_tasks(
    patient_id: int,
    current_user: dict = Depends(require_permission(TASK_READ)),
    db: Session = Depends(get_session)
):
    """Obter tarefas diárias que podem ser completadas hoje"""
    # Verificar permissão
    user_role = current_user.get("role", "").lower()
    user_id = current_user.get("sub")
    
    if user_role == "patient" and user_id != patient_id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Patients can only view their own tasks"
        )
    
    return task_service.get_daily_tasks_for_patient(patient_id, db)


# ============= ENDPOINTS DE PONTOS E RANKING =============

@router.get("/points/{user_id}", response_model=UserPointsOut)
def get_user_points(
    user_id: int,
    current_user: dict = Depends(require_permission(TASK_READ)),
    db: Session = Depends(get_session)
):
    """Obter pontos do usuário"""
    # Verificar se o usuário pode ver os pontos
    user_role = current_user.get("role", "").lower()
    current_user_id = current_user.get("sub")
    
    if user_role == "patient" and current_user_id != user_id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Patients can only view their own points"
        )
    
    points = task_service.get_user_points(user_id, db)
    if not points:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User points not found"
        )
    return points


@router.get("/points/{user_id}/history", response_model=List[PointsHistoryOut])
def get_points_history(
    user_id: int,
    limit: int = Query(default=50, ge=1, le=100),
    current_user: dict = Depends(require_permission(TASK_READ)),
    db: Session = Depends(get_session)
):
    """Obter histórico de pontos do usuário"""
    # Verificar permissão
    user_role = current_user.get("role", "").lower()
    current_user_id = current_user.get("sub")
    
    if user_role == "patient" and current_user_id != user_id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Patients can only view their own history"
        )
    
    return task_service.get_points_history(user_id, db, limit)


@router.post("/points/{user_id}/bonus")
def add_bonus_points(
    user_id: int,
    points: int,
    description: str,
    current_user: dict = Depends(require_permission(USER_MANAGE)),
    db: Session = Depends(get_session)
):
    """Adicionar pontos de bônus (admin)"""
    if points <= 0:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Points must be positive"
        )
    
    success = task_service.add_bonus_points(user_id, points, description, db)
    if not success:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to add bonus points"
        )
    return {"message": f"Added {points} bonus points to user {user_id}"}


# ============= ENDPOINTS DE DESAFIOS GLOBAIS =============

@router.get("/challenges", response_model=List[GlobalChallengeOut])
def get_global_challenges(
    active_only: bool = Query(default=True),
    current_user: dict = Depends(require_permission(TASK_READ)),
    db: Session = Depends(get_session)
):
    """Obter desafios globais"""
    return task_service.get_global_challenges(db, active_only)


@router.post("/challenges/join", response_model=ChallengeParticipationOut)
def join_challenge(
    participation_data: ChallengeParticipationCreate,
    current_user: dict = Depends(require_permission(TASK_READ)),
    db: Session = Depends(get_session)
):
    """Participar de um desafio global"""
    user_id = current_user.get("sub")
    participation = task_service.join_challenge(user_id, participation_data.challenge_id, db)
    if not participation:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Cannot join challenge (not found, inactive, or already joined)"
        )
    return participation


@router.get("/challenges/my", response_model=List[ChallengeParticipationOut])
def get_user_challenges(
    current_user: dict = Depends(require_permission(TASK_READ)),
    db: Session = Depends(get_session)
):
    """Obter desafios do usuário"""
    user_id = current_user.get("sub")
    return task_service.get_user_challenges(user_id, db)


# ============= ENDPOINTS DE ESTATÍSTICAS =============

@router.get("/stats", response_model=TaskStats)
def get_task_stats(
    patient_id: Optional[int] = Query(None),
    current_user: dict = Depends(require_permission(TASK_READ)),
    db: Session = Depends(get_session)
):
    """Obter estatísticas de tarefas"""
    user_role = current_user.get("role", "").lower()
    professional_id = None
    
    if user_role == "professional":
        professional_id = current_user.get("sub")
    elif user_role == "patient":
        patient_id = current_user.get("sub")
    elif user_role in ["admin", "doctor"]:
        # Admin pode ver estatísticas de qualquer paciente se especificado
        pass
    else:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Insufficient permissions"
        )
    
    return task_service.get_task_stats(db, professional_id, patient_id)


# ============= ENDPOINTS COM PARÂMETROS (POR ÚLTIMO) =============

@router.get("/patient/{patient_id}")
def get_patient_tasks(
    patient_id: int
):
    """Obter tarefas de um paciente - SEM DEPENDÊNCIAS"""
    # Mock de retorno para teste
    return {
        "patient_id": patient_id,
        "tasks": [
            {
                "id": 999,
                "title": "Olhar",
                "description": "bb",
                "points_value": 99,
                "frequency_per_week": 1,
                "is_active": True,
                "created_at": "2026-04-23T00:00:00"
            }
        ]
    }

# REMOVIDO - ESTA ROTA ESTAVA CAUSANDO CONFLITO COM /patient-tasks
