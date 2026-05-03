from sqlalchemy import Column, Integer, String, DateTime, Text, DECIMAL, Boolean, Date, JSON
from sqlalchemy.sql import func
from ..storage.database import Base

class HealthMetric(Base):
    __tablename__ = "health_metrics"
    
    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, nullable=False, index=True)
    metric_type = Column(String(50), nullable=False, index=True)
    value = Column(DECIMAL(10, 2), nullable=False)
    unit = Column(String(20), nullable=False)
    classification = Column(String(50))
    measured_at = Column(DateTime, nullable=False)
    notes = Column(Text)
    created_at = Column(DateTime, server_default=func.now())
    updated_at = Column(DateTime, server_default=func.now(), onupdate=func.now())

class HealthQuestionnaire(Base):
    __tablename__ = "health_questionnaires"
    
    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, nullable=False, index=True)
    questionnaire_data = Column(JSON, nullable=False)
    version = Column(String(10), default="1.0")
    completed_at = Column(DateTime, nullable=False)
    created_at = Column(DateTime, server_default=func.now())
    updated_at = Column(DateTime, server_default=func.now(), onupdate=func.now())

class HealthGoal(Base):
    __tablename__ = "health_goals"
    
    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, nullable=False, index=True)
    goal_type = Column(String(50), nullable=False, index=True)
    target_value = Column(DECIMAL(10, 2), nullable=False)
    current_value = Column(DECIMAL(10, 2), default=0.0)
    target_date = Column(Date)
    is_active = Column(Boolean, default=True, index=True)
    achieved = Column(Boolean, default=False)
    notes = Column(Text)
    created_at = Column(DateTime, server_default=func.now())
    updated_at = Column(DateTime, server_default=func.now(), onupdate=func.now())

class HealthProgress(Base):
    __tablename__ = "health_progress"
    
    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, nullable=False, index=True)
    goal_id = Column(Integer, nullable=False, index=True)
    progress_value = Column(DECIMAL(10, 2), nullable=False)
    progress_date = Column(DateTime, nullable=False, index=True)
    notes = Column(Text)
    created_at = Column(DateTime, server_default=func.now())
