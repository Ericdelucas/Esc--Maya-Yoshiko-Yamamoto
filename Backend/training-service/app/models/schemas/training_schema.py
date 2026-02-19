from pydantic import BaseModel, Field


class TrainingPlanCreate(BaseModel):
    patient_id: int = Field(ge=1)
    professional_id: int = Field(ge=1)
    title: str = Field(min_length=1, max_length=120)
    start_date_iso: str = Field(min_length=10, max_length=10)  # YYYY-MM-DD
    end_date_iso: str | None = None


class TrainingPlanOut(BaseModel):
    id: int
    patient_id: int
    professional_id: int
    title: str
    start_date_iso: str
    end_date_iso: str | None
    created_at_iso: str


class TrainingPlanItemCreate(BaseModel):
    exercise_id: int = Field(ge=1)
    sets: int = Field(ge=0, le=50)
    reps: int = Field(ge=0, le=500)
    frequency_per_week: int = Field(ge=0, le=14)
    notes: str | None = Field(default=None, max_length=512)


class TrainingLogCreate(BaseModel):
    patient_id: int = Field(ge=1)
    plan_id: int = Field(ge=1)
    exercise_id: int = Field(ge=1)
    perceived_effort: int | None = Field(default=None, ge=1, le=10)
    pain_level: int | None = Field(default=None, ge=0, le=10)
    notes: str | None = Field(default=None, max_length=512)


class TrainingLogOut(BaseModel):
    id: int
    patient_id: int
    plan_id: int
    exercise_id: int
    performed_at_iso: str
    perceived_effort: int | None
    pain_level: int | None
    notes: str | None
