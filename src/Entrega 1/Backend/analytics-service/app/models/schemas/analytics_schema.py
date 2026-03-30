from pydantic import BaseModel


class PatientEhrSummaryOut(BaseModel):
    patient_id: int
    total_records: int
    first_record_iso: str | None
    last_record_iso: str | None


class GlobalEhrStatsOut(BaseModel):
    total_records: int
    total_patients: int
    total_professionals: int


class PatientTrainingWeeklyOut(BaseModel):
    patient_id: int
    week_start_iso: str
    executions: int
    avg_effort: float | None
    avg_pain: float | None


class PatientTrainingRiskOut(BaseModel):
    patient_id: int
    last_7d_executions: int
    last_7d_avg_effort: float | None
    last_7d_avg_pain: float | None


class GlobalTrainingAdherenceRow(BaseModel):
    patient_id: int
    last_7d_executions: int
    last_7d_avg_effort: float | None
    last_7d_avg_pain: float | None


class GlobalTrainingRiskRow(BaseModel):
    patient_id: int
    last_7d_executions: int
    last_7d_avg_effort: float | None
    last_7d_avg_pain: float | None
    risk_reason: str
