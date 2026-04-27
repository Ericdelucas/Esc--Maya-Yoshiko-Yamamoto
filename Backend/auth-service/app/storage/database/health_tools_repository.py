from datetime import datetime, timedelta
from sqlalchemy.orm import Session
from sqlalchemy import and_, desc
from app.models.orm.health_tools_orm import HealthToolsORM, HealthQuestionnaireORM
import json


class HealthToolsRepository:
    def __init__(self, db: Session) -> None:
        self._db = db

    def create_bmi_record(self, user_id: int, height: float, weight: float) -> dict:
        """Criar registro de IMC"""
        bmi = weight / (height ** 2)
        
        # Categorizar IMC
        if bmi < 18.5:
            category = "Abaixo do peso"
        elif bmi < 25:
            category = "Peso normal"
        elif bmi < 30:
            category = "Sobrepeso"
        elif bmi < 35:
            category = "Obesidade Grau I"
        elif bmi < 40:
            category = "Obesidade Grau II"
        else:
            category = "Obesidade Grau III"

        record = HealthToolsORM(
            user_id=user_id,
            record_type="bmi",
            value=json.dumps({
                "height": height,
                "weight": weight,
                "bmi": round(bmi, 2),
                "category": category
            }),
            record_date=datetime.utcnow()
        )
        
        self._db.add(record)
        self._db.commit()
        self._db.refresh(record)
        
        return {
            "id": record.id,
            "bmi": round(bmi, 2),
            "category": category,
            "created_at": record.created_at
        }

    def create_body_fat_record(self, user_id: int, height: float, weight: float, age: int, gender: str) -> dict:
        """Criar registro de gordura corporal"""
        # 🔥 CORREÇÃO: Converter altura de cm para metros se for > 3
        if height > 3:
            height = height / 100
        
        # 🔥 FÓRMULA CORRIGIDA - Fórmula de BMI simplificada (garantida)
        bmi = weight / (height ** 2)
        
        if gender.lower() == 'm':
            # Homens: (1.20 * BMI) - 16.2 + 0.23 * idade
            body_fat_percentage = (1.20 * bmi) - 16.2 + (0.23 * age)
        else:
            # Mulheres: (1.20 * BMI) - 5.4 + 0.23 * idade
            body_fat_percentage = (1.20 * bmi) - 5.4 + (0.23 * age)
        
        # Garantir que o percentual esteja em limites realistas
        body_fat_percentage = max(2.0, min(50.0, body_fat_percentage))
        
        # Categorizar
        if body_fat_percentage < 10:
            category = "Atleta"
        elif body_fat_percentage < 15:
            category = "Fitness"
        elif body_fat_percentage < 25:
            category = "Aceitável"
        elif body_fat_percentage < 32:
            category = "Elevado"
        else:
            category = "Excessivo"

        record = HealthToolsORM(
            user_id=user_id,
            record_type="body_fat",
            value=json.dumps({
                "height": height,
                "weight": weight,
                "age": age,
                "gender": gender,
                "body_fat_percentage": round(body_fat_percentage, 2),
                "category": category
            }),
            record_date=datetime.utcnow()
        )
        
        self._db.add(record)
        self._db.commit()
        self._db.refresh(record)
        
        return {
            "id": record.id,
            "body_fat_percentage": round(body_fat_percentage, 2),
            "category": category,
            "created_at": record.created_at
        }

    def create_questionnaire_record(self, user_id: int, answers: dict, db: Session) -> dict:
        """Criar registro de questionário de saúde"""
        
        # Calcular pontuação baseado nas respostas
        total_score = 0
        max_score = 0
        
        # Exemplo de cálculo de pontuação (personalizar conforme necessário)
        scoring_rules = {
            "fever_symptoms": {"yes": 15, "no": 0},
            "allergies": {"yes": 5, "no": 0},
            "medications": {"yes": 5, "no": 0},
            "chronic_diseases": {"yes": 10, "no": 0},
            "surgeries": {"yes": 8, "no": 0},
            "habits": {"excellent": 0, "regular": 5, "needs_improvement": 10},
            # Manter compatibilidade com perguntas antigas
            "smoking": {"yes": 10, "no": 0},
            "alcohol": {"daily": 5, "weekly": 2, "rarely": 1, "never": 0},
            "exercise": {"daily": 0, "weekly": 2, "rarely": 5, "never": 10},
            "sleep": {"7-8h": 0, "5-6h": 3, "<5h": 5, ">9h": 2},
            "stress": {"low": 0, "medium": 3, "high": 5, "very_high": 10}
        }
        
        for question, answer in answers.items():
            if question in scoring_rules and answer in scoring_rules[question]:
                total_score += scoring_rules[question][answer]
                max_score += max(scoring_rules[question].values())
        
        # Determinar nível de risco
        risk_percentage = (total_score / max_score) * 100 if max_score > 0 else 0
        
        if risk_percentage < 20:
            risk_level = "Baixo"
        elif risk_percentage < 40:
            risk_level = "Médio"
        elif risk_percentage < 60:
            risk_level = "Moderado"
        else:
            risk_level = "Alto"

        record = HealthQuestionnaireORM(
            user_id=user_id,
            questionnaire_date=datetime.utcnow(),
            total_score=total_score,
            max_score=max_score,
            risk_level=risk_level,
            answers=json.dumps(answers)
        )
        
        db.add(record)
        db.commit()
        db.refresh(record)
        
        return {
            "id": record.id,
            "total_score": total_score,
            "max_score": max_score,
            "risk_level": risk_level,
            "created_at": record.created_at
        }

    def get_user_history(self, user_id: int, limit: int = 50) -> list:
        """Buscar histórico completo do usuário"""
        records = (
            self._db.query(HealthToolsORM)
            .filter(HealthToolsORM.user_id == user_id)
            .order_by(desc(HealthToolsORM.created_at))
            .limit(limit)
            .all()
        )
        
        history = []
        for record in records:
            value_data = json.loads(record.value) if isinstance(record.value, str) else record.value
            
            history.append({
                "id": record.id,
                "record_type": record.record_type,
                "value": value_data,
                "record_date": record.record_date,
                "created_at": record.created_at
            })
        
        return history

    def get_latest_bmi(self, user_id: int) -> dict:
        """Buscar último registro de IMC"""
        record = (
            self._db.query(HealthToolsORM)
            .filter(and_(
                HealthToolsORM.user_id == user_id,
                HealthToolsORM.record_type == "bmi"
            ))
            .order_by(desc(HealthToolsORM.created_at))
            .first()
        )
        
        if record:
            value_data = json.loads(record.value) if isinstance(record.value, str) else record.value
            return {
                "id": record.id,
                **value_data,
                "created_at": record.created_at
            }
        return None

    def get_latest_body_fat(self, user_id: int) -> dict:
        """Buscar último registro de gordura corporal"""
        record = (
            self._db.query(HealthToolsORM)
            .filter(and_(
                HealthToolsORM.user_id == user_id,
                HealthToolsORM.record_type == "body_fat"
            ))
            .order_by(desc(HealthToolsORM.created_at))
            .first()
        )
        
        if record:
            value_data = json.loads(record.value) if isinstance(record.value, str) else record.value
            return {
                "id": record.id,
                **value_data,
                "created_at": record.created_at
            }
        return None

    def get_latest_questionnaire(self, user_id: int) -> dict:
        """Buscar último questionário respondido"""
        record = (
            self._db.query(HealthQuestionnaireORM)
            .filter(HealthQuestionnaireORM.user_id == user_id)
            .order_by(desc(HealthQuestionnaireORM.created_at))
            .first()
        )
        
        if record:
            answers_data = json.loads(record.answers) if isinstance(record.answers, str) else record.answers
            return {
                "id": record.id,
                "total_score": record.total_score,
                "max_score": record.max_score,
                "risk_level": record.risk_level,
                "answers": answers_data,
                "created_at": record.created_at
            }
        return None

    def get_health_summary(self, user_id: int) -> dict:
        """Buscar resumo completo das ferramentas de saúde"""
        latest_bmi = self.get_latest_bmi(user_id)
        latest_body_fat = self.get_latest_body_fat(user_id)
        latest_questionnaire = self.get_latest_questionnaire(user_id)
        
        # Contar total de registros
        total_records = (
            self._db.query(HealthToolsORM)
            .filter(HealthToolsORM.user_id == user_id)
            .count()
        )
        
        return {
            "latest_bmi": latest_bmi,
            "latest_body_fat": latest_body_fat,
            "latest_questionnaire": latest_questionnaire,
            "total_records": total_records,
            "last_updated": datetime.utcnow()
        }
