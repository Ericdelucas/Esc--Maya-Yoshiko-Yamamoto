from fastapi import APIRouter, HTTPException, Request, Response
import httpx
import os
import json

router = APIRouter(prefix="/ai", tags=["ai"])

AI_SERVICE_URL = os.getenv("AI_SERVICE_URL", "http://ai-service:8090")


def get_professionals_context():
    """Retorna informações sobre profissionais para contexto da IA"""
    # Mock de dados dos profissionais (em produção, viria do banco)
    professionals_mock = [
        {
            "id": 1,
            "name": "Dr. João Silva",
            "email": "joao.silva@saude.com",
            "role": "doctor",
            "specialty": "Médico",
            "experience": "10 anos",
            "focus_areas": ["Ortopedia", "Reabilitação Física"]
        },
        {
            "id": 2,
            "name": "Dra. Maria Santos",
            "email": "maria.santos@saude.com", 
            "role": "professional",
            "specialty": "Fisioterapeuta",
            "experience": "8 anos",
            "focus_areas": ["Fisioterapia Respiratória", "Reabilitação Motora"]
        },
        {
            "id": 3,
            "name": "Carlos Oliveira",
            "email": "carlos.oliveira@saude.com",
            "role": "professional", 
            "specialty": "Fisioterapeuta",
            "experience": "5 anos",
            "focus_areas": ["Fisioterapia Esportiva", "Prevenção de Lesões"]
        }
    ]
    
    return professionals_mock


@router.post("/chat")
async def proxy_ai_chat(request: Request):
    try:
        payload = await request.json()
        
        # 🔥 **ADICIONAR INFORMAÇÕES SOBRE PROFISSIONAIS**
        professionals_info = get_professionals_context()
        
        # 🔥 **ENRIQUECER O PAYLOAD COM CONTEXTO DO SISTEMA**
        enriched_payload = {
            **payload,
            "system_context": {
                "professionals": professionals_info,
                "total_professionals": len(professionals_info),
                "available_specialties": ["Fisioterapeuta", "Médico", "Administrador"],
                "system_features": {
                    "exercise_management": "Profissionais podem criar e atribuir exercícios específicos para pacientes",
                    "patient_progress": "Sistema monitora progresso diário dos exercícios",
                    "points_system": "Pacientes ganham pontos ao completar exercícios",
                    "ai_assistant": "Assistente IA disponível 24/7 para ajuda"
                }
            },
            "professional_guidance": {
                "instruction": "Se o usuário perguntar sobre profissionais, use as informações fornecidas abaixo para dar respostas específicas e detalhadas. Não dê respostas genéricas sobre navegação quando tiver informações específicas dos profissionais.",
                "professionals_summary": "Temos 3 profissionais disponíveis: Dr. João Silva (Médico - Ortopedia), Dra. Maria Santos (Fisioterapeuta - Respiratória), Carlos Oliveira (Fisioterapeuta - Esportiva)",
                "contact_info": "Os pacientes podem entrar em contato através do app ou agendar consultas",
                "specialties_available": ["Médico", "Fisioterapeuta"]
            }
        }
        
        print(f"🔍 ENVIANDO PARA AI SERVICE:")
        print(f"   - URL: {AI_SERVICE_URL}/ai/chat")
        print(f"   - Original Payload: {payload}")
        print(f"   - Enriched Payload: {json.dumps(enriched_payload, indent=2)}")

        import time
        start_time = time.time()
        
        async with httpx.AsyncClient(timeout=60.0) as client:
            response = await client.post(
                f"{AI_SERVICE_URL}/ai/chat",
                json=enriched_payload
            )
            
        end_time = time.time()
        duration = end_time - start_time

        print(f"✅ RESPOSTA DO AI SERVICE:")
        print(f"   - Status Code: {response.status_code}")
        print(f"   - Content Length: {len(response.content) if response.content else 0}")
        print(f"   - Duration: {duration:.2f}s")
        print(f"   - Response: {response.text[:200] if response.text else 'Empty'}...")

        return Response(
            content=response.content,
            status_code=response.status_code,
            media_type="application/json"
        )

    except httpx.TimeoutException as e:
        print(f"🚨 TIMEOUT NO AI PROXY: {e}")
        print(f"   - AI_SERVICE_URL: {AI_SERVICE_URL}")
        print(f"   - Payload: {payload}")
        raise HTTPException(status_code=504, detail=f"AI Service Timeout: {str(e)}")
    except httpx.ConnectError as e:
        print(f"🚨 ERRO DE CONEXÃO NO AI PROXY: {e}")
        print(f"   - AI_SERVICE_URL: {AI_SERVICE_URL}")
        raise HTTPException(status_code=503, detail=f"AI Service Unavailable: {str(e)}")
    except httpx.RequestError as e:
        print(f"🚨 ERRO DE REQUEST NO AI PROXY: {e}")
        print(f"   - AI_SERVICE_URL: {AI_SERVICE_URL}")
        print(f"   - Payload: {payload}")
        raise HTTPException(status_code=502, detail=f"AI Service Error: {str(e)}")
    except Exception as e:
        print(f"🚨 ERRO INESPERADO NO AI PROXY: {e}")
        print(f"   - Type: {type(e).__name__}")
        raise HTTPException(status_code=500, detail=f"Unexpected Error: {str(e)}")
