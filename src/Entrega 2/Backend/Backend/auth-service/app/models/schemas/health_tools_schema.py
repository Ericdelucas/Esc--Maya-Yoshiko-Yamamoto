from pydantic import BaseModel, Field
from datetime import datetime
from typing import Optional, List

class BMICalculation(BaseModel):
    user_id: int
    height: float = Field(gt=0, description="Altura em metros")
    weight: float = Field(gt=0, description="Peso em kg")
    bmi: float = Field(description="IMC calculado")
    category: str = Field(description="Categoria do IMC")
    created_at: datetime

class BodyFatCalculation(BaseModel):
    user_id: int
    height: float = Field(gt=0, description="Altura em metros")
    weight: float = Field(gt=0, description="Peso em kg")
    age: int = Field(gt=0, description="Idade")
    gender: str = Field(description="Gênero (M/F)")
    body_fat_percentage: float = Field(description="Percentual de gordura corporal")
    category: str = Field(description="Categoria da gordura corporal")
    created_at: datetime

class HealthQuestionnaire(BaseModel):
    user_id: int
    question_id: int
    question_text: str
    answer: str
    question_type: str = Field(description="Tipo de questão: multiple_choice, text, scale")
    created_at: datetime

class HealthQuestionnaireResponse(BaseModel):
    id: int
    user_id: int
    questionnaire_date: datetime
    total_score: int
    max_score: int
    risk_level: str = Field(description="Baixo, Médio, Alto")
    answers: List[HealthQuestionnaire]
    created_at: datetime

class HealthHistory(BaseModel):
    id: int
    user_id: int
    record_type: str = Field(description="bmi, body_fat, questionnaire, blood_pressure, etc")
    value: str = Field(description="Valor em formato JSON")
    record_date: datetime
    created_at: datetime

class HealthToolsSummary(BaseModel):
    latest_bmi: Optional[BMICalculation] = None
    latest_body_fat: Optional[BodyFatCalculation] = None
    latest_questionnaire: Optional[HealthQuestionnaireResponse] = None
    total_records: int
    last_updated: datetime
