from sqlalchemy.orm import Session
from sqlalchemy import and_, or_, desc, func
from datetime import datetime, timedelta
from typing import List, Optional
from app.models.orm.patient_report_orm import PatientReportORM, ReportSectionORM, ReportAttachmentORM

class PatientReportRepository:
    
    def __init__(self, db: Session):
        self.db = db
    
    def save(self, report: PatientReportORM) -> PatientReportORM:
        self.db.add(report)
        self.db.commit()
        self.db.refresh(report)
        return report
    
    def findById(self, report_id: int) -> Optional[PatientReportORM]:
        return self.db.query(PatientReportORM).filter(
            PatientReportORM.id == report_id
        ).first()
    
    def findByPatientId(self, patient_id: int, limit: int = 50) -> List[PatientReportORM]:
        return self.db.query(PatientReportORM).filter(
            PatientReportORM.patient_id == patient_id
        ).order_by(desc(PatientReportORM.report_date)).limit(limit).all()
    
    def findByProfessionalId(self, professional_id: int, limit: int = 100) -> List[PatientReportORM]:
        return self.db.query(PatientReportORM).filter(
            PatientReportORM.professional_id == professional_id
        ).order_by(desc(PatientReportORM.report_date)).limit(limit).all()
    
    def findByPatientAndProfessional(self, patient_id: int, professional_id: int) -> List[PatientReportORM]:
        return self.db.query(PatientReportORM).filter(
            and_(
                PatientReportORM.patient_id == patient_id,
                PatientReportORM.professional_id == professional_id
            )
        ).order_by(desc(PatientReportORM.report_date)).all()
    
    def findByType(self, report_type: str, professional_id: int = None) -> List[PatientReportORM]:
        query = self.db.query(PatientReportORM).filter(
            PatientReportORM.report_type == report_type
        )
        
        if professional_id:
            query = query.filter(PatientReportORM.professional_id == professional_id)
        
        return query.order_by(desc(PatientReportORM.report_date)).all()
    
    def findAll(self, professional_id: int = None, limit: int = 100) -> List[PatientReportORM]:
        query = self.db.query(PatientReportORM)
        
        if professional_id:
            query = query.filter(PatientReportORM.professional_id == professional_id)
        
        return query.order_by(desc(PatientReportORM.report_date)).limit(limit).all()
    
    def searchReports(self, search_term: str, professional_id: int = None) -> List[PatientReportORM]:
        query = self.db.query(PatientReportORM).filter(
            or_(
                PatientReportORM.title.ilike(f"%{search_term}%"),
                PatientReportORM.content.ilike(f"%{search_term}%"),
                PatientReportORM.clinical_evolution.ilike(f"%{search_term}%"),
                PatientReportORM.treatment_plan.ilike(f"%{search_term}%")
            )
        )
        
        if professional_id:
            query = query.filter(PatientReportORM.professional_id == professional_id)
        
        return query.order_by(desc(PatientReportORM.report_date)).all()
    
    def deleteById(self, report_id: int) -> bool:
        report = self.findById(report_id)
        if report:
            self.db.delete(report)
            self.db.commit()
            return True
        return False
    
    def getReportStatistics(self, professional_id: int = None) -> dict:
        query = self.db.query(
            PatientReportORM.report_type,
            func.count(PatientReportORM.id).label('count'),
            func.avg(PatientReportORM.pain_scale).label('avg_pain')
        ).group_by(PatientReportORM.report_type)
        
        if professional_id:
            query = query.filter(PatientReportORM.professional_id == professional_id)
        
        results = query.all()
        
        stats = {}
        for result in results:
            stats[result.report_type] = {
                'count': result.count,
                'avg_pain': float(result.avg_pain) if result.avg_pain else 0
            }
        
        return stats
    
    def getRecentReports(self, professional_id: int, days: int = 30) -> List[PatientReportORM]:
        start_date = datetime.now() - timedelta(days=days)
        return self.db.query(PatientReportORM).filter(
            and_(
                PatientReportORM.professional_id == professional_id,
                PatientReportORM.report_date >= start_date
            )
        ).order_by(desc(PatientReportORM.report_date)).all()
    
    # # Métodos para gerenciar anexos
    def save_attachment(self, attachment: ReportAttachmentORM) -> ReportAttachmentORM:
        """Salvar anexo de relatório"""
        self.db.add(attachment)
        self.db.commit()
        self.db.refresh(attachment)
        return attachment
    
    def find_attachments_by_report(self, report_id: int) -> List[ReportAttachmentORM]:
        """Buscar todos os anexos de um relatório"""
        return self.db.query(ReportAttachmentORM).filter(
            ReportAttachmentORM.report_id == report_id
        ).order_by(desc(ReportAttachmentORM.uploaded_at)).all()
    
    def find_attachment_by_id(self, attachment_id: int) -> Optional[ReportAttachmentORM]:
        """Buscar anexo por ID"""
        return self.db.query(ReportAttachmentORM).filter(
            ReportAttachmentORM.id == attachment_id
        ).first()
    
    def delete_attachment(self, attachment_id: int) -> bool:
        """Excluir anexo"""
        attachment = self.find_attachment_by_id(attachment_id)
        if attachment:
            self.db.delete(attachment)
            self.db.commit()
            return True
        return False
    
    def delete_attachments_by_report(self, report_id: int) -> bool:
        """Excluir todos os anexos de um relatório"""
        attachments = self.find_attachments_by_report(report_id)
        for attachment in attachments:
            self.db.delete(attachment)
        self.db.commit()
        return True
