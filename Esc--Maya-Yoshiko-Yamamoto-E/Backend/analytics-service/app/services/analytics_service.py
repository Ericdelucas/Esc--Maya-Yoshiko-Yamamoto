from sqlalchemy.orm import Session
from app.models.schemas.analytics_schema import GlobalEhrStatsOut, PatientEhrSummaryOut, PatientTrainingWeeklyOut, PatientTrainingRiskOut, GlobalTrainingAdherenceRow, GlobalTrainingRiskRow
from app.storage.database.analytics_repository import AnalyticsRepository


class AnalyticsService:
    def global_ehr_stats(self, db: Session) -> GlobalEhrStatsOut:
        data = AnalyticsRepository(db).global_ehr_stats()
        return GlobalEhrStatsOut(**data)

    def patient_ehr_summary(self, patient_id: int, db: Session) -> PatientEhrSummaryOut:
        data = AnalyticsRepository(db).patient_ehr_summary(patient_id)

        first_iso = data["first_record"].isoformat() if data["first_record"] else None
        last_iso = data["last_record"].isoformat() if data["last_record"] else None

        return PatientEhrSummaryOut(
            patient_id=int(data["patient_id"]),
            total_records=int(data["total_records"]),
            first_record_iso=first_iso,
            last_record_iso=last_iso,
        )

    def patient_training_weekly(self, patient_id: int, weeks: int, db: Session) -> list[PatientTrainingWeeklyOut]:
        data = AnalyticsRepository(db).patient_training_weekly(patient_id, weeks)
        out: list[PatientTrainingWeeklyOut] = []
        for r in data:
            out.append(
                PatientTrainingWeeklyOut(
                    patient_id=int(r["patient_id"]),
                    week_start_iso=r["week_start"].isoformat(),
                    executions=int(r["executions"]),
                    avg_effort=float(r["avg_effort"]) if r["avg_effort"] is not None else None,
                    avg_pain=float(r["avg_pain"]) if r["avg_pain"] is not None else None,
                )
            )
        return out

    def patient_training_risk_7d(self, patient_id: int, db: Session) -> PatientTrainingRiskOut:
        r = AnalyticsRepository(db).patient_training_risk_7d(patient_id)
        return PatientTrainingRiskOut(
            patient_id=int(r["patient_id"]),
            last_7d_executions=int(r["executions"]),
            last_7d_avg_effort=float(r["avg_effort"]) if r["avg_effort"] is not None else None,
            last_7d_avg_pain=float(r["avg_pain"]) if r["avg_pain"] is not None else None,
        )

    def global_training_adherence_7d(self, limit: int, db: Session) -> list[GlobalTrainingAdherenceRow]:
        rows = AnalyticsRepository(db).global_training_adherence_7d(limit)
        return [
            GlobalTrainingAdherenceRow(
                patient_id=int(r["patient_id"]),
                last_7d_executions=int(r["executions"]),
                last_7d_avg_effort=float(r["avg_effort"]) if r["avg_effort"] is not None else None,
                last_7d_avg_pain=float(r["avg_pain"]) if r["avg_pain"] is not None else None,
            )
            for r in rows
        ]

    def global_training_risk_7d(
        self,
        pain_threshold: float,
        effort_threshold: float,
        limit: int,
        db: Session,
    ) -> list[GlobalTrainingRiskRow]:
        rows = AnalyticsRepository(db).global_training_risk_7d(pain_threshold, effort_threshold, limit)

        out: list[GlobalTrainingRiskRow] = []
        for r in rows:
            avg_pain = float(r["avg_pain"]) if r["avg_pain"] is not None else None
            avg_effort = float(r["avg_effort"]) if r["avg_effort"] is not None else None

            reasons: list[str] = []
            if avg_pain is not None and avg_pain >= pain_threshold:
                reasons.append("high_pain")
            if avg_effort is not None and avg_effort >= effort_threshold:
                reasons.append("high_effort")

            out.append(
                GlobalTrainingRiskRow(
                    patient_id=int(r["patient_id"]),
                    last_7d_executions=int(r["executions"]),
                    last_7d_avg_effort=avg_effort,
                    last_7d_avg_pain=avg_pain,
                    risk_reason="|".join(reasons) if reasons else "unknown",
                )
            )
        return out
