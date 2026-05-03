from sqlalchemy.orm import Session
from sqlalchemy import text


class AnalyticsRepository:
    def __init__(self, db: Session) -> None:
        self._db = db

    def global_ehr_stats(self) -> dict:
        q = text("""
            SELECT
              COUNT(*) AS total_records,
              COUNT(DISTINCT patient_id) AS total_patients,
              COUNT(DISTINCT professional_id) AS total_professionals
            FROM medical_records
        """)
        row = self._db.execute(q).mappings().first()
        return dict(row) if row else {"total_records": 0, "total_patients": 0, "total_professionals": 0}

    def patient_ehr_summary(self, patient_id: int) -> dict:
        q = text("""
            SELECT
              patient_id,
              COUNT(*) AS total_records,
              MIN(created_at) AS first_record,
              MAX(created_at) AS last_record
            FROM medical_records
            WHERE patient_id = :patient_id
            GROUP BY patient_id
        """)
        row = self._db.execute(q, {"patient_id": patient_id}).mappings().first()
        if not row:
            return {"patient_id": patient_id, "total_records": 0, "first_record": None, "last_record": None}
        return dict(row)

    def patient_training_weekly(self, patient_id: int, weeks: int = 8) -> list[dict]:
        q = text("""
            SELECT
              patient_id,
              DATE_SUB(DATE(performed_at), INTERVAL WEEKDAY(performed_at) DAY) AS week_start,
              COUNT(*) AS executions,
              AVG(perceived_effort) AS avg_effort,
              AVG(pain_level) AS avg_pain
            FROM training_logs
            WHERE patient_id = :patient_id
              AND performed_at >= DATE_SUB(UTC_TIMESTAMP(), INTERVAL :weeks WEEK)
            GROUP BY patient_id, week_start
            ORDER BY week_start DESC
        """)
        rows = self._db.execute(q, {"patient_id": patient_id, "weeks": weeks}).mappings().all()
        return [dict(r) for r in rows]

    def patient_training_risk_7d(self, patient_id: int) -> dict:
        q = text("""
            SELECT
              patient_id,
              COUNT(*) AS executions,
              AVG(perceived_effort) AS avg_effort,
              AVG(pain_level) AS avg_pain
            FROM training_logs
            WHERE patient_id = :patient_id
              AND performed_at >= DATE_SUB(UTC_TIMESTAMP(), INTERVAL 7 DAY)
            GROUP BY patient_id
        """)
        row = self._db.execute(q, {"patient_id": patient_id}).mappings().first()
        if not row:
            return {"patient_id": patient_id, "executions": 0, "avg_effort": None, "avg_pain": None}
        return dict(row)

    def global_training_adherence_7d(self, limit: int = 20) -> list[dict]:
        q = text("""
            SELECT
              patient_id,
              COUNT(*) AS executions,
              AVG(perceived_effort) AS avg_effort,
              AVG(pain_level) AS avg_pain
            FROM training_logs
            WHERE performed_at >= DATE_SUB(UTC_TIMESTAMP(), INTERVAL 7 DAY)
            GROUP BY patient_id
            ORDER BY executions DESC
            LIMIT :limit
        """)
        rows = self._db.execute(q, {"limit": limit}).mappings().all()
        return [dict(r) for r in rows]

    def global_training_risk_7d(self, pain_threshold: float, effort_threshold: float, limit: int = 50) -> list[dict]:
        q = text("""
            SELECT
              patient_id,
              COUNT(*) AS executions,
              AVG(perceived_effort) AS avg_effort,
              AVG(pain_level) AS avg_pain
            FROM training_logs
            WHERE performed_at >= DATE_SUB(UTC_TIMESTAMP(), INTERVAL 7 DAY)
            GROUP BY patient_id
            HAVING (avg_pain IS NOT NULL AND avg_pain >= :pain_threshold)
                OR (avg_effort IS NOT NULL AND avg_effort >= :effort_threshold)
            ORDER BY avg_pain DESC, avg_effort DESC
            LIMIT :limit
        """)
        rows = self._db.execute(
            q,
            {"pain_threshold": pain_threshold, "effort_threshold": effort_threshold, "limit": limit},
        ).mappings().all()
        return [dict(r) for r in rows]
