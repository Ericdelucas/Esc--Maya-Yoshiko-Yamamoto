# # **BACKEND COMPLETO - RELATÓRIOS DE PACIENTES**

## # **OBJETIVO**
Criar backend completo para relatórios de pacientes com abas profissionais, integrado ao sistema existente através do cardReports no ProfessionalMainActivity.

## # **ESTRUTURA DE DADOS - RELATÓRIOS DE PACIENTES**

### # **1. Modelo Principal: PatientReport**
```java
// Backend: patient_reports
public class PatientReport {
    private Integer id;
    private Integer patientId;
    private Integer professionalId;
    private Date reportDate;
    private String reportType; // "EVOLUTION", "ASSESSMENT", "DISCHARGE", "PROGRESS"
    private String title;
    private String content;
    private Map<String, Object> metadata; // Dados adicionais JSON
    
    // Seções do relatório
    private String clinicalEvolution;
    private String objectiveData;
    private String subjectiveData;
    private String treatmentPlan;
    private String recommendations;
    private String nextSteps;
    
    // Avaliações
    private Integer painScale;
    private String functionalStatus;
    private List<String> achievements;
    private List<String> limitations;
    
    // Metadados
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
}
```

### # **2. Modelo de Seção: ReportSection**
```java
// Backend: report_sections
public class ReportSection {
    private Integer id;
    private Integer reportId;
    private String sectionType; // "EVOLUTION", "ASSESSMENT", "TREATMENT", "GOALS"
    private String title;
    private String content;
    private Integer orderIndex;
    private Map<String, Object> sectionData;
}
```

### # **3. Modelo de Anexo: ReportAttachment**
```java
// Backend: report_attachments
public class ReportAttachment {
    private Integer id;
    private Integer reportId;
    private String attachmentType; // "IMAGE", "PDF", "VIDEO", "DOCUMENT"
    private String fileName;
    private String filePath;
    private String description;
    private Long fileSize;
    private Date uploadedAt;
}
```

## # **BACKEND - IMPLEMENTAÇÃO COMPLETA**

### # **1. SQL Schema - Tabelas Principais**
```sql
-- Tabela principal de relatórios
CREATE TABLE IF NOT EXISTS patient_reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    professional_id INT NOT NULL,
    report_date DATETIME NOT NULL,
    report_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    metadata JSON,
    
    -- Seções do relatório
    clinical_evolution TEXT,
    objective_data TEXT,
    subjective_data TEXT,
    treatment_plan TEXT,
    recommendations TEXT,
    next_steps TEXT,
    
    -- Avaliações
    pain_scale INT,
    functional_status VARCHAR(100),
    achievements JSON,
    limitations JSON,
    
    -- Metadados
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    
    INDEX idx_patient_id (patient_id),
    INDEX idx_professional_id (professional_id),
    INDEX idx_report_date (report_date),
    INDEX idx_report_type (report_type)
);

-- Tabela de seções de relatórios
CREATE TABLE IF NOT EXISTS report_sections (
    id INT AUTO_INCREMENT PRIMARY KEY,
    report_id INT NOT NULL,
    section_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    order_index INT DEFAULT 0,
    section_data JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (report_id) REFERENCES patient_reports(id) ON DELETE CASCADE,
    INDEX idx_report_id (report_id),
    INDEX idx_section_type (section_type)
);

-- Tabela de anexos de relatórios
CREATE TABLE IF NOT EXISTS report_attachments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    report_id INT NOT NULL,
    attachment_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500),
    description TEXT,
    file_size BIGINT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (report_id) REFERENCES patient_reports(id) ON DELETE CASCADE,
    INDEX idx_report_id (report_id),
    INDEX idx_attachment_type (attachment_type)
);

-- View para relatórios completos
CREATE VIEW patient_reports_complete AS
SELECT 
    pr.*,
    p.full_name as patient_name,
    p.email as patient_email,
    prof.full_name as professional_name,
    prof.email as professional_email,
    COUNT(rs.id) as section_count,
    COUNT(ra.id) as attachment_count
FROM patient_reports pr
LEFT JOIN users p ON pr.patient_id = p.id
LEFT JOIN users prof ON pr.professional_id = prof.id
LEFT JOIN report_sections rs ON pr.id = rs.report_id
LEFT JOIN report_attachments ra ON pr.id = ra.report_id
GROUP BY pr.id;
```

### # **2. ORM Model - PatientReportORM**
```python
# Backend/auth-service/app/models/orm/patient_report_orm.py
from sqlalchemy import Column, Integer, String, Text, DateTime, JSON, ForeignKey
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import DeclarativeBase

class Base(DeclarativeBase):
    pass

class PatientReportORM(Base):
    __tablename__ = "patient_reports"
    
    id = Column(Integer, primary_key=True)
    patient_id = Column(Integer, nullable=False)
    professional_id = Column(Integer, nullable=False)
    report_date = Column(DateTime, nullable=False)
    report_type = Column(String(50), nullable=False)
    title = Column(String(255), nullable=False)
    content = Column(Text)
    metadata = Column(JSON)
    
    # Seções
    clinical_evolution = Column(Text)
    objective_data = Column(Text)
    subjective_data = Column(Text)
    treatment_plan = Column(Text)
    recommendations = Column(Text)
    next_steps = Column(Text)
    
    # Avaliações
    pain_scale = Column(Integer)
    functional_status = Column(String(100))
    achievements = Column(JSON)
    limitations = Column(JSON)
    
    # Metadados
    created_at = Column(DateTime)
    updated_at = Column(DateTime)
    created_by = Column(String(255))
    
    # Relacionamentos
    sections = relationship("ReportSectionORM", back_populates="report", cascade="all, delete-orphan")
    attachments = relationship("ReportAttachmentORM", back_populates="report", cascade="all, delete-orphan")

class ReportSectionORM(Base):
    __tablename__ = "report_sections"
    
    id = Column(Integer, primary_key=True)
    report_id = Column(Integer, ForeignKey("patient_reports.id"), nullable=False)
    section_type = Column(String(50), nullable=False)
    title = Column(String(255), nullable=False)
    content = Column(Text)
    order_index = Column(Integer, default=0)
    section_data = Column(JSON)
    created_at = Column(DateTime)
    
    # Relacionamento
    report = relationship("PatientReportORM", back_populates="sections")

class ReportAttachmentORM(Base):
    __tablename__ = "report_attachments"
    
    id = Column(Integer, primary_key=True)
    report_id = Column(Integer, ForeignKey("patient_reports.id"), nullable=False)
    attachment_type = Column(String(50), nullable=False)
    file_name = Column(String(255), nullable=False)
    file_path = Column(String(500))
    description = Column(Text)
    file_size = Column(Integer)
    uploaded_at = Column(DateTime)
    
    # Relacionamento
    report = relationship("PatientReportORM", back_populates="attachments")
```

### # **3. Repository - PatientReportRepository**
```python
# Backend/auth-service/app/storage/database/patient_report_repository.py
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
    
    def findByDateRange(self, start_date: datetime, end_date: datetime, 
                       professional_id: int = None, patient_id: int = None) -> List[PatientReportORM]:
        query = self.db.query(PatientReportORM).filter(
            and_(
                PatientReportORM.report_date >= start_date,
                PatientReportORM.report_date <= end_date
            )
        )
        
        if professional_id:
            query = query.filter(PatientReportORM.professional_id == professional_id)
        
        if patient_id:
            query = query.filter(PatientReportORM.patient_id == patient_id)
        
        return query.order_by(desc(PatientReportORM.report_date)).all()
    
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
    
    def findAll(self, professional_id: int = None, limit: int = 100) -> List[PatientReportORM]:
        query = self.db.query(PatientReportORM)
        
        if professional_id:
            query = query.filter(PatientReportORM.professional_id == professional_id)
        
        return query.order_by(desc(PatientReportORM.report_date)).limit(limit).all()
    
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
```

### # **4. Pydantic Schemas**
```python
# Backend/auth-service/app/models/schemas/patient_report_schema.py
from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any
from datetime import datetime

class ReportSectionBase(BaseModel):
    section_type: str = Field(..., description="Tipo da seção")
    title: str = Field(..., description="Título da seção")
    content: Optional[str] = Field(None, description="Conteúdo da seção")
    order_index: int = Field(0, description="Ordem da seção")
    section_data: Optional[Dict[str, Any]] = Field(None, description="Dados adicionais")

class ReportSectionCreate(ReportSectionBase):
    pass

class ReportSectionResponse(ReportSectionBase):
    id: int
    created_at: datetime
    
    class Config:
        from_attributes = True

class ReportAttachmentBase(BaseModel):
    attachment_type: str = Field(..., description="Tipo do anexo")
    file_name: str = Field(..., description="Nome do arquivo")
    file_path: Optional[str] = Field(None, description="Caminho do arquivo")
    description: Optional[str] = Field(None, description="Descrição")
    file_size: Optional[int] = Field(None, description="Tamanho do arquivo")

class ReportAttachmentResponse(ReportAttachmentBase):
    id: int
    uploaded_at: datetime
    
    class Config:
        from_attributes = True

class PatientReportBase(BaseModel):
    patient_id: int = Field(..., description="ID do paciente")
    professional_id: int = Field(..., description="ID do profissional")
    report_date: datetime = Field(..., description="Data do relatório")
    report_type: str = Field(..., description="Tipo do relatório")
    title: str = Field(..., description="Título do relatório")
    content: Optional[str] = Field(None, description="Conteúdo principal")
    
    # Seções
    clinical_evolution: Optional[str] = Field(None, description="Evolução clínica")
    objective_data: Optional[str] = Field(None, description="Dados objetivos")
    subjective_data: Optional[str] = Field(None, description="Dados subjetivos")
    treatment_plan: Optional[str] = Field(None, description="Plano de tratamento")
    recommendations: Optional[str] = Field(None, description="Recomendações")
    next_steps: Optional[str] = Field(None, description="Próximos passos")
    
    # Avaliações
    pain_scale: Optional[int] = Field(None, ge=0, le=10, description="Escala de dor")
    functional_status: Optional[str] = Field(None, description="Status funcional")
    achievements: Optional[List[str]] = Field(default_factory=list, description="Conquistas")
    limitations: Optional[List[str]] = Field(default_factory=list, description="Limitações")
    metadata: Optional[Dict[str, Any]] = Field(None, description="Metadados")

class PatientReportCreate(PatientReportBase):
    sections: Optional[List[ReportSectionCreate]] = Field(default_factory=list)
    attachments: Optional[List[ReportAttachmentBase]] = Field(default_factory=list)

class PatientReportUpdate(BaseModel):
    title: Optional[str] = None
    content: Optional[str] = None
    clinical_evolution: Optional[str] = None
    objective_data: Optional[str] = None
    subjective_data: Optional[str] = None
    treatment_plan: Optional[str] = None
    recommendations: Optional[str] = None
    next_steps: Optional[str] = None
    pain_scale: Optional[int] = Field(None, ge=0, le=10)
    functional_status: Optional[str] = None
    achievements: Optional[List[str]] = None
    limitations: Optional[List[str]] = None
    metadata: Optional[Dict[str, Any]] = None

class PatientReportResponse(PatientReportBase):
    id: int
    created_at: datetime
    updated_at: datetime
    created_by: Optional[str] = None
    sections: Optional[List[ReportSectionResponse]] = []
    attachments: Optional[List[ReportAttachmentResponse]] = []
    
    class Config:
        from_attributes = True

class PatientReportList(BaseModel):
    reports: List[PatientReportResponse]
    total: int
    page: int
    per_page: int

class ReportStatistics(BaseModel):
    report_types: Dict[str, Dict[str, Any]]
    total_reports: int
    recent_reports: List[PatientReportResponse]
```

### # **5. API Router - PatientReportRouter**
```python
# Backend/auth-service/app/routers/patient_report_router.py
from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.orm import Session
from typing import List, Optional
from datetime import datetime, timedelta

from app.storage.database.patient_report_repository import PatientReportRepository
from app.models.schemas.patient_report_schema import (
    PatientReportCreate, PatientReportUpdate, PatientReportResponse,
    PatientReportList, ReportStatistics
)
from app.storage.database.db import get_db
from app.utils.auth import get_current_user

router = APIRouter(prefix="/reports", tags=["patient-reports"])

def get_repository(db: Session = Depends(get_db)):
    return PatientReportRepository(db)

@router.post("/", response_model=PatientReportResponse, status_code=status.HTTP_201_CREATED)
async def create_report(
    report: PatientReportCreate,
    current_user = Depends(get_current_user),
    repo: PatientReportRepository = Depends(get_repository)
):
    """Criar novo relatório de paciente"""
    # Criar relatório principal
    report_orm = PatientReportORM(
        patient_id=report.patient_id,
        professional_id=report.professional_id,
        report_date=report.report_date,
        report_type=report.report_type,
        title=report.title,
        content=report.content,
        clinical_evolution=report.clinical_evolution,
        objective_data=report.objective_data,
        subjective_data=report.subjective_data,
        treatment_plan=report.treatment_plan,
        recommendations=report.recommendations,
        next_steps=report.next_steps,
        pain_scale=report.pain_scale,
        functional_status=report.functional_status,
        achievements=report.achievements,
        limitations=report.limitations,
        metadata=report.metadata,
        created_by=current_user.email
    )
    
    # Salvar relatório
    saved_report = repo.save(report_orm)
    
    return PatientReportResponse.from_orm(saved_report)

@router.get("/", response_model=PatientReportList)
async def get_reports(
    professional_id: Optional[int] = Query(None),
    patient_id: Optional[int] = Query(None),
    report_type: Optional[str] = Query(None),
    page: int = Query(1, ge=1),
    per_page: int = Query(20, ge=1, le=100),
    current_user = Depends(get_current_user),
    repo: PatientReportRepository = Depends(get_repository)
):
    """Listar relatórios com filtros e paginação"""
    
    # Aplicar filtros
    if professional_id is None and current_user.role == "professional":
        professional_id = current_user.id
    
    # Buscar relatórios
    if professional_id and patient_id:
        reports = repo.findByPatientAndProfessional(patient_id, professional_id)
    elif professional_id:
        reports = repo.findByProfessionalId(professional_id, per_page)
    elif patient_id:
        reports = repo.findByPatientId(patient_id, per_page)
    elif report_type:
        reports = repo.findByType(report_type, professional_id)
    else:
        reports = repo.findAll(professional_id, per_page)
    
    # Paginação
    start = (page - 1) * per_page
    end = start + per_page
    paginated_reports = reports[start:end]
    
    return PatientReportList(
        reports=[PatientReportResponse.from_orm(r) for r in paginated_reports],
        total=len(reports),
        page=page,
        per_page=per_page
    )

@router.get("/patient/{patient_id}", response_model=List[PatientReportResponse])
async def get_patient_reports(
    patient_id: int,
    limit: int = Query(50, ge=1, le=100),
    current_user = Depends(get_current_user),
    repo: PatientReportRepository = Depends(get_repository)
):
    """Listar todos os relatórios de um paciente"""
    reports = repo.findByPatientId(patient_id, limit)
    return [PatientReportResponse.from_orm(r) for r in reports]

@router.get("/professional/{professional_id}", response_model=List[PatientReportResponse])
async def get_professional_reports(
    professional_id: int,
    limit: int = Query(100, ge=1, le=200),
    current_user = Depends(get_current_user),
    repo: PatientReportRepository = Depends(get_repository)
):
    """Listar todos os relatórios de um profissional"""
    reports = repo.findByProfessionalId(professional_id, limit)
    return [PatientReportResponse.from_orm(r) for r in reports]

@router.get("/{report_id}", response_model=PatientReportResponse)
async def get_report(
    report_id: int,
    current_user = Depends(get_current_user),
    repo: PatientReportRepository = Depends(get_repository)
):
    """Buscar relatório por ID"""
    report = repo.findById(report_id)
    if not report:
        raise HTTPException(status_code=404, detail="Relatório não encontrado")
    
    # Verificar permissões
    if (current_user.role == "professional" and report.professional_id != current_user.id) or \
       (current_user.role == "patient" and report.patient_id != current_user.id):
        raise HTTPException(status_code=403, detail="Sem permissão para acessar este relatório")
    
    return PatientReportResponse.from_orm(report)

@router.put("/{report_id}", response_model=PatientReportResponse)
async def update_report(
    report_id: int,
    report_update: PatientReportUpdate,
    current_user = Depends(get_current_user),
    repo: PatientReportRepository = Depends(get_repository)
):
    """Atualizar relatório existente"""
    report = repo.findById(report_id)
    if not report:
        raise HTTPException(status_code=404, detail="Relatório não encontrado")
    
    # Verificar permissões
    if current_user.role == "professional" and report.professional_id != current_user.id:
        raise HTTPException(status_code=403, detail="Sem permissão para editar este relatório")
    
    # Atualizar campos
    update_data = report_update.dict(exclude_unset=True)
    for field, value in update_data.items():
        setattr(report, field, value)
    
    report.updated_at = datetime.now()
    
    updated_report = repo.save(report)
    return PatientReportResponse.from_orm(updated_report)

@router.delete("/{report_id}")
async def delete_report(
    report_id: int,
    current_user = Depends(get_current_user),
    repo: PatientReportRepository = Depends(get_repository)
):
    """Excluir relatório"""
    report = repo.findById(report_id)
    if not report:
        raise HTTPException(status_code=404, detail="Relatório não encontrado")
    
    # Verificar permissões
    if current_user.role == "professional" and report.professional_id != current_user.id:
        raise HTTPException(status_code=403, detail="Sem permissão para excluir este relatório")
    
    success = repo.deleteById(report_id)
    if not success:
        raise HTTPException(status_code=500, detail="Erro ao excluir relatório")
    
    return {"message": "Relatório excluído com sucesso"}

@router.get("/search/patient", response_model=List[PatientReportResponse])
async def search_reports_by_patient(
    name: str = Query(..., min_length=2),
    current_user = Depends(get_current_user),
    repo: PatientReportRepository = Depends(get_repository)
):
    """Buscar relatórios por nome do paciente"""
    professional_id = current_user.id if current_user.role == "professional" else None
    reports = repo.searchReports(name, professional_id)
    return [PatientReportResponse.from_orm(r) for r in reports]

@router.get("/statistics", response_model=ReportStatistics)
async def get_report_statistics(
    current_user = Depends(get_current_user),
    repo: PatientReportRepository = Depends(get_repository)
):
    """Estatísticas dos relatórios do profissional"""
    professional_id = current_user.id if current_user.role == "professional" else None
    
    stats = repo.getReportStatistics(professional_id)
    recent_reports = repo.getRecentReports(professional_id) if professional_id else []
    
    return ReportStatistics(
        report_types=stats,
        total_reports=sum(s['count'] for s in stats.values()),
        recent_reports=[PatientReportResponse.from_orm(r) for r in recent_reports]
    )

@router.get("/date-range", response_model=List[PatientReportResponse])
async def get_reports_by_date_range(
    start_date: datetime = Query(...),
    end_date: datetime = Query(...),
    patient_id: Optional[int] = Query(None),
    current_user = Depends(get_current_user),
    repo: PatientReportRepository = Depends(get_repository)
):
    """Listar relatórios por período"""
    professional_id = current_user.id if current_user.role == "professional" else None
    reports = repo.findByDateRange(start_date, end_date, professional_id, patient_id)
    return [PatientReportResponse.from_orm(r) for r in reports]
```

### # **6. Integração no Main.py**
```python
# Backend/auth-service/main.py
# Adicionar import
from app.routers.patient_report_router import router as patient_report_router

# Adicionar no create_app()
app.include_router(patient_report_router, prefix="/api/v1")
```

## # **FRONTEND - IMPLEMENTAÇÃO NECESSÁRIA**

### # **1. Models Java**
```java
// PatientReport.java
// ReportSection.java  
// ReportAttachment.java
// ReportStatistics.java
```

### # **2. API Interface**
```java
// PatientReportApi.java
```

### # **3. Activities**
```java
// PatientReportsActivity.java - Principal com abas
// ReportListFragment.java - Lista de relatórios
// ReportDetailFragment.java - Detalhes do relatório
// CreateReportActivity.java - Criar novo relatório
// ReportStatisticsFragment.java - Estatísticas
```

### # **4. Layouts**
```java
// activity_patient_reports.xml - Com TabLayout
// fragment_report_list.xml - RecyclerView
// fragment_report_detail.xml - Detalhes
// fragment_report_statistics.xml - Gráficos
```

## # **INTEGRAÇÃO COM SISTEMA ATUAL**

### # **1. ProfessionalMainActivity**
```java
// CardReports já existe, só apontar para nova activity
cardReports.setOnClickListener(v -> {
    startActivity(new Intent(this, PatientReportsActivity.class));
});
```

### # **2. PatientsListActivity**
```java
// Adicionar opção de ver relatórios do paciente
// Botão "Ver Relatórios" em cada item da lista
```

## # **PRÓXIMOS PASSOS**

### # **Backend (IMEDIATO):**
1. # **Criar tabelas SQL**
2. # **Implementar ORM models**
3. # **Criar repository**
4. # **Implementar schemas**
5. # **Criar router**
6. # **Registrar no main.py**

### # **Frontend (PARA GEMINI):**
1. # **Models Java** - PatientReport, ReportSection, ReportAttachment
2. # **API Interface** - PatientReportApi com todos endpoints
3. # **Activities com abas** - PatientReportsActivity com TabLayout
4. # **Fragments** - Lista, detalhes, estatísticas
5. # **Layouts profissionais** - UI moderna e intuitiva

---

**Status Backend:** # **PRONTO PARA IMPLEMENTAR**
**Status Frontend:** # **PRECISA SER IMPLEMENTADO PELO GEMINI**
