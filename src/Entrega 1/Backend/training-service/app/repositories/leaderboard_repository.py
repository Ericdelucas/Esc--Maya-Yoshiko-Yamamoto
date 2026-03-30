from sqlalchemy.orm import Session
from sqlalchemy import func, desc
from typing import List, Dict, Any

from app.models.orm.training_orm import PatientPointsORM
from app.storage.database.base_repository import SessionLocal


class LeaderboardRepository:
    
    def get_top_users(self, limit: int = 50) -> List[Dict[str, Any]]:
        """Retorna top usuários por pontos"""
        with SessionLocal() as session:
            # Join com users para obter nomes
            query = session.query(
                PatientPointsORM.patient_id,
                PatientPointsORM.total_points,
                func.row_number().over(order_by=desc(PatientPointsORM.total_points)).label('position')
            ).order_by(desc(PatientPointsORM.total_points)).limit(limit)
            
            results = []
            for row in query.all():
                # Simplificado - na implementação real, buscaria nome da tabela users
                name = f"Paciente {row.patient_id}"  # Placeholder
                results.append({
                    "position": row.position,
                    "name": name,
                    "points": row.total_points
                })
            
            return results
    
    def get_user_ranking(self, patient_id: int) -> Dict[str, Any]:
        """Retorna posição específica do usuário"""
        with SessionLocal() as session:
            # Calcular posição do usuário
            user_points = session.query(PatientPointsORM).filter(
                PatientPointsORM.patient_id == patient_id
            ).first()
            
            if not user_points:
                return {"position": None, "points": 0}
            
            # Contar usuários com mais pontos
            higher_ranked = session.query(func.count(PatientPointsORM.patient_id)).filter(
                PatientPointsORM.total_points > user_points.total_points
            ).scalar()
            
            position = higher_ranked + 1
            
            return {
                "position": position,
                "points": user_points.total_points
            }
    
    def get_leaderboard_with_user(self, patient_id: int, limit: int = 50) -> List[Dict[str, Any]]:
        """Retorna leaderboard destacando usuário atual"""
        top_users = self.get_top_users(limit)
        user_ranking = self.get_user_ranking(patient_id)
        
        # Marcar usuário atual
        for user in top_users:
            user["is_current_user"] = (user.get("patient_id") == patient_id)
        
        # Se usuário não está no top, adicionar no final
        if user_ranking["position"] and user_ranking["position"] > limit:
            name = f"Paciente {patient_id}"
            top_users.append({
                "position": user_ranking["position"],
                "name": name,
                "points": user_ranking["points"],
                "is_current_user": True
            })
        
        return top_users
    
    def get_total_users_count(self) -> int:
        """Retorna total de usuários no ranking"""
        with SessionLocal() as session:
            return session.query(func.count(PatientPointsORM.patient_id)).scalar()
