from datetime import date
from typing import List, Dict

from app.repositories.challenges_repository import ChallengesRepository
from app.models.schemas.challenges_schema import ChallengeResponse


class ChallengesService:
    
    def __init__(self):
        self.challenges_repo = ChallengesRepository()
    
    def get_patient_challenges(self, patient_id: int) -> List[ChallengeResponse]:
        """Retorna desafios disponíveis com progresso do paciente"""
        challenges_data = self.challenges_repo.get_patient_challenges(patient_id)
        
        return [
            ChallengeResponse(
                id=challenge["id"],
                title=challenge["title"],
                description=challenge["description"],
                reward_points=challenge["reward_points"],
                start_date=challenge["start_date"],
                end_date=challenge["end_date"],
                joined=challenge["joined"],
                progress=challenge["progress"],
                target_sessions=challenge["target_sessions"],
                status=challenge["status"]
            )
            for challenge in challenges_data
        ]
    
    def join_challenge(self, patient_id: int, challenge_id: int) -> Dict:
        """Paciente entra em um desafio"""
        try:
            patient_challenge = self.challenges_repo.join_challenge(patient_id, challenge_id)
            
            return {
                "message": "Successfully joined challenge",
                "challenge_id": challenge_id,
                "joined": True,
                "progress": patient_challenge.progress_sessions
            }
        except Exception as e:
            return {
                "message": f"Error joining challenge: {str(e)}",
                "joined": False
            }
    
    def update_challenge_progress(self, patient_id: int, sessions_completed: int = 1) -> List[Dict]:
        """Atualiza progresso em todos os desafios ativos do paciente"""
        # Buscar desafios ativos do paciente
        challenges_data = self.challenges_repo.get_patient_challenges(patient_id)
        
        updated_challenges = []
        for challenge in challenges_data:
            if challenge["joined"] and challenge["status"] == "in_progress":
                try:
                    patient_challenge = self.challenges_repo.update_challenge_progress(
                        patient_id, challenge["id"], sessions_completed
                    )
                    
                    # Verificar se completou e calcular pontos
                    points_awarded = 0
                    if patient_challenge.completed:
                        points_awarded = challenge["reward_points"]
                        # Aqui poderia chamar ProgressService para adicionar pontos
                    
                    updated_challenges.append({
                        "challenge_id": challenge["id"],
                        "title": challenge["title"],
                        "progress": patient_challenge.progress_sessions,
                        "target": challenge["target_sessions"],
                        "completed": patient_challenge.completed,
                        "points_awarded": points_awarded
                    })
                    
                except Exception as e:
                    # Log error mas continua com outros desafios
                    print(f"Error updating challenge {challenge['id']}: {e}")
        
        return updated_challenges
    
    def get_available_challenges(self) -> List[Dict]:
        """Retorna desafios disponíveis (sem progresso específico)"""
        active_challenges = self.challenges_repo.get_active_challenges()
        
        return [
            {
                "id": challenge.id,
                "title": challenge.title,
                "description": challenge.description,
                "reward_points": challenge.reward_points,
                "target_sessions": challenge.target_sessions,
                "start_date": challenge.start_date,
                "end_date": challenge.end_date
            }
            for challenge in active_challenges
        ]
