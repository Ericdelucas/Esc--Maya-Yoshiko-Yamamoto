from typing import List, Dict

from app.repositories.leaderboard_repository import LeaderboardRepository
from app.models.schemas.leaderboard_schema import LeaderboardResponse, LeaderboardEntry


class LeaderboardService:
    
    def __init__(self):
        self.leaderboard_repo = LeaderboardRepository()
    
    def get_leaderboard(self, patient_id: int, limit: int = 50) -> LeaderboardResponse:
        """Retorna ranking completo com destaque para usuário atual"""
        # Buscar leaderboard com usuário destacado
        rankings_data = self.leaderboard_repo.get_leaderboard_with_user(patient_id, limit)
        
        # Converter para schema
        rankings = []
        for user_data in rankings_data:
            rankings.append(LeaderboardEntry(
                position=user_data["position"],
                name=user_data["name"],
                points=user_data["points"],
                is_current_user=user_data.get("is_current_user", False)
            ))
        
        # Buscar total de usuários
        total_users = self.leaderboard_repo.get_total_users_count()
        
        return LeaderboardResponse(
            rankings=rankings,
            total_users=total_users
        )
    
    def get_user_position(self, patient_id: int) -> Dict:
        """Retorna posição específica do usuário"""
        return self.leaderboard_repo.get_user_ranking(patient_id)
    
    def get_top_users(self, limit: int = 10) -> List[LeaderboardEntry]:
        """Retorna apenas top usuários (para widgets)"""
        top_data = self.leaderboard_repo.get_top_users(limit)
        
        return [
            LeaderboardEntry(
                position=user["position"],
                name=user["name"],
                points=user["points"]
            )
            for user in top_data
        ]
