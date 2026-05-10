from fastapi import APIRouter, HTTPException, status
from app.schemas.chat import ChatRequest, ChatResponse
from app.services.chat_service import chat_service
from app.services.ollama_client import ollama_client

router = APIRouter()


@router.post("/chat", response_model=ChatResponse)
async def chat(request: ChatRequest) -> ChatResponse:
    """
    Endpoint principal do assistente SmartSaúde
    
    Processa mensagens do usuário com:
    - Memória de sessão contextual
    - Integração com Ollama/Llama 3
    - Análise de intenção e ações estruturadas
    - Navegação inteligente no app
    
    Args:
        request: Requisição com mensagem e contexto
        
    Returns:
        ChatResponse com resposta, intenção e ação opcional
    """
    try:
        return chat_service.process_message(request)
    except Exception as e:
        # Fallback inteligente quando Ollama não está disponível
        message = request.message.lower()
        
        # Respostas detalhadas baseadas no conhecimento completo do SmartSaúde
        if "olá" in message or "oi" in message or "bom dia" in message or "boa tarde" in message or "boa noite" in message:
            reply = """Olá! Sou o assistente SmartSaúde! 🏥‍⚕️

Estou aqui para ajudar você a usar TODAS as funcionalidades do app:

🏃‍♂️ **Exercícios e Treinos:** Acesse Início → Exercícios para treinos com IA em tempo real
🏥 **Saúde e Métricas:** Vá em Início → Saúde e Ferramentas para IMC, gordura corporal e questionários
📊 **Progresso e Gamificação:** Confira seu ranking e desafios em Início → Dashboard/Desafios
👨‍⚕️ **Profissionais:** Gerencie pacientes e planos de tratamento

Em que posso ajudar você hoje?"""
            intent = "greeting"
            
        elif "ajuda" in message or "help" in message or "como usar" in message or "como funciona" in message:
            reply = """🎯 **GUIA COMPLETO DO SMARTSAÚDE:**

**🏃‍♂️ EXERCÍCIOS E IA:**
• Início → Exercícios → Escolher exercício → Iniciar Treino IA
• Análise de movimento em tempo real via câmera
• Feedback de áudio durante exercícios

**🏥 SAÚDE E FERRAMENTAS:**
• Início → Saúde e Ferramentas → IMC/Gordura Corporal
• Questionário de saúde completo
• Histórico detalhado de evolução

**📊 GAMIFICAÇÃO:**
• Início → Dashboard de Progresso (seus stats)
• Início → Ranking (posição entre usuários)
• Início → Desafios (desafios semanais/mensais)

**👨‍⚕️ PROFISSIONAIS:**
• Lista de pacientes e gestão completa
• Criação de planos de exercícios
• Agendamento e relatórios

**💬 COMUNICAÇÃO:**
• Início → Chat (fale com seu profissional/paciente)
• Calendário integrado
• Sistema de notificações

O que você gostaria de explorar primeiro?"""
            intent = "help"
            
        elif "exercício" in message or "exercicio" in message or "treino" in message or "exercicios" in message:
            reply = """🏋‍♂️ **EXERCÍCIOS NO SMARTSAÚDE:**

**Acesso:** Início → Exercícios

**O que você encontrará:**
• 📚 Biblioteca completa com vídeos instrutivos
• 🤖 Treinos com IA que analisam seus movimentos em tempo real
• 🎧 Feedback de áudio durante execução
• 📈 Progresso individual por exercício
• ✅ Sistema de conclusão com pontos

**Para treinar com IA:**
1. Escolha um exercício na lista
2. Toque em "Iniciar Treino IA" 
3. Permita acesso à câmera
4. Siga as instruções e receba feedback em tempo real

**Tip:** Use o ranking e desafios para se motivar! Início → Ranking/Desafios

Quer ajuda com algum exercício específico?"""
            intent = "navigation"
            action = {"screen": "ExerciseListActivity", "params": {}}
            
        elif "saúde" in message or "medic" in message or "imc" in message or "gordura" in message or "peso" in message or "altura" in message:
            reply = """🏥 **CENTRAL DE SAÚDE E FERRAMENTAS:**

**Acesso:** Início → Saúde e Ferramentas

**📏 MÉTRICAS DISPONÍVEIS:**
• **IMC:** Índice de Massa Corporal com classificação automática
• **Gordura Corporal:** Cálculo preciso de percentual de gordura
• **Questionário:** Avaliação completa de saúde
• **Histórico:** Evolução temporal de todas as métricas

**📊 VISUALIZAÇÃO:**
• Gráficos interativos de evolução
• Comparação histórica
• Alertas e recomendações personalizadas

**🔍 COMO USAR:**
1. IMC: Informe peso e altura para obter classificação
2. Gordura: Use medidas específicas para cálculo preciso
3. Questionário: Responda para avaliação completa
4. Histórico: Acompanhe sua evolução ao longo do tempo

Todos os dados ficam salvos e sincronizados! Qual métrica quer calcular agora?"""
            intent = "navigation"
            action = {"screen": "HealthHubActivity", "params": {}}
            
        elif "ranking" in message or "pontos" in message or "desafio" in message or "conquista" in message or "nível" in message:
            reply = """🏆 **GAMIFICAÇÃO E PROGRESSO:**

**📊 DASHBOARD DE PROGRESSO:**
Início → Dashboard de Progresso
• Estatísticas detalhadas de treinos
• Evolução de IMC e métricas
• Conquistas e badges desbloqueados

**🥇 RANKING COMPETITIVO:**
Início → Ranking
• Sua posição entre todos os usuários
• Pontuação total e nível atual
• Líderes do mês/semana

**🎯 DESAFIOS SEMANAIS/MENSAIS:**
Início → Desafios
• Desafios temáticos com recompensas
• Pontos extras e bônus
• Compartilhamento social

**⭐ SISTEMA DE PONTOS:**
• Complete exercícios para ganhar pontos
• Participe de desafios para bônus
• Suba de nível e desbloqueie conquistas
• Compare-se com outros usuários

**Dica:** O sistema de gamificação ajuda a manter a motivação! Quer ver seu ranking atual?"""
            intent = "navigation"
            action = {"screen": "LeaderboardActivity", "params": {}}
            
        elif "profissional" in message or "paciente" in message or "médico" in message or "doctor" in message:
            reply = """👨‍⚕️ **PARA PROFISSIONAIS DE SAÚDE:**

**🏥 TELA PROFISSIONAL:**
Após login, você acessa a área profissional com:

**👥 GESTÃO DE PACIENTES:**
• Lista completa de pacientes
• Prontuários detalhados
• Histórico de tratamentos

**📋 PLANOS DE TRATAMENTO:**
• Crie exercícios personalizados
• Atribua planos aos pacientes
• Acompanhe evolução individual

**📅 AGENDAMENTO:**
• Calendário integrado de consultas
• Lembretes automáticos
• Sincronização com pacientes

**📊 RELATÓRIOS:**
• Geração de relatórios detalhados
• Análise de progresso do paciente
• Exportação de dados

**💬 COMUNICAÇÃO:**
• Chat direto com pacientes
• Compartilhamento seguro de arquivos
• Notificações importantes

**Para acessar:** Faça login como profissional e use a área dedicada!

Precisa de ajuda com alguma função específica?"""
            intent = "navigation"
            
        elif "chat" in message or "mensagem" in message or "comunicação" in message or "falar" in message:
            reply = """💬 **COMUNICAÇÃO NO SMARTSAÚDE:**

**📱 CHAT INTEGRADO:**
**Acesso:** Início → Chat

**🎯 FUNCIONALIDADES:**
• Conversa direta paciente ↔ profissional
• Mensagens em tempo real
• Histórico completo de conversas
• Notificações de novas mensagens

**📎 COMPARTILHAMENTO:**
• Envio de arquivos seguros
• Compartilhamento de relatórios
• Fotos e vídeos de exercícios
• Documentos médicos

**🔒 SEGURANÇA:**
• Comunicação criptografada
• Apenas autorizados podem conversar
• Backup automático de mensagens

**📅 INTEGRAÇÃO:**
• Agendamento via chat
• Lembretes automáticos
• Confirmação de consultas

**Dica:** Use o chat para tirar dúvidas sobre exercícios, agendar consultas ou compartilhar seu progresso!

Quer iniciar uma conversa agora?"""
            intent = "navigation"
            action = {"screen": "ChatActivity", "params": {}}
            
        elif "problema" in message or "erro" in message or "ajuda" in message or "não entendi" in message or "dúvida" in message:
            reply = """🤔 **ENTENDI SUA DÚVIDA! VAMOS RESOLVER:**

**🎯 O que você precisa saber?**

**📱 NAVEGAÇÃO BÁSICA:**
• Início → Todas as funcionalidades principais
• Use o menu inferior para acesso rápido
• Swipe para descobrir mais opções

**🏋‍♂️ EXERCÍCIOS:**
• Início → Exercícios → Escolher → Treinar
• Use a câmera para feedback da IA
• Acompanhe pontos e progresso

**🏥 SAÚDE:**
• Início → Saúde e Ferramentas
• Calcule IMC, gordura corporal
• Responda questionários de saúde

**📊 PROGRESSO:**
• Início → Dashboard/Desafios/Ranking
• Veja sua evolução
• Compare-se com outros usuários

**💬 SUPORTE:**
• Use o chat para falar com profissionais
• Eu estou aqui para ajudar 24/7
• Descreva seu problema específico

**Qual área está te dando problema? Posso ajudar passo a passo!**"""
            intent = "help"
            
        else:
            reply = """🎯 **COMO POSSO AJUDAR MELHOR?**

**🏋‍♂️ EXERCÍCIOS E TREINOS:**
Diga "quero treinar" ou "meus exercícios"

**🏥 SAÚDE E MÉTRICAS:**
Pergunte "como calcular IMC" ou "avaliação de saúde"

**📊 PROGRESSO E RANKING:**
Fale "meu progresso" ou "ranking de usuários"

**👨‍⚕️ SUPORTE PROFISSIONAL:**
Peça "ajuda profissional" ou "gerenciar pacientes"

**💬 COMUNICAÇÃO:**
Diga "quero falar com profissional" ou "enviar mensagem"

**🎮 DESAFIOS:**
Pergunte "quais desafios" ou "como ganhar pontos"

**❓ DÚVIDAS ESPECÍFICAS:**
Seja específico! Ex: "como fazer agachamento com IA?"

O que você gostaria de fazer agora?"""
            intent = "info"
        
        return ChatResponse(
            reply=reply,
            intent=intent,
            action=action if 'action' in locals() else None,
            confidence=0.8
        )


@router.get("/chat/status")
async def chat_status():
    """
    Verifica status do serviço de chat
    
    Returns:
        Status do serviço e fallback
    """
    try:
        ollama_status = ollama_client.check_connection()
        return {
            "chat_service": "online",
            "ollama": ollama_status,
            "mode": "ai_enabled"
        }
    except Exception as e:
        return {
            "chat_service": "online",
            "ollama": {"status": "offline", "error": str(e)},
            "mode": "fallback_enabled"
        }


@router.delete("/chat/session/{session_id}")
async def clear_session(session_id: str):
    """
    Limpa memória de uma sessão específica
    
    Args:
        session_id: ID da sessão para limpar
        
    Returns:
        Status da operação
    """
    try:
        success = chat_service.clear_session(session_id)
        return {
            "session_id": session_id,
            "cleared": success,
            "message": "Sessão limpa com sucesso" if success else "Sessão não encontrada"
        }
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Erro ao limpar sessão: {str(e)}"
        )
