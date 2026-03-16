import json
import re
from typing import Dict, Optional, Tuple
from app.schemas.chat import ChatRequest, ChatResponse, ChatAction
from app.services.memory_service import memory_service
from app.services.ollama_client import ollama_client
from app.prompts.smartsaude_assistant_prompt import SMARTSAUDE_SYSTEM_PROMPT, APP_CONTEXT_INFO


class ChatService:
    def __init__(self):
        self.system_prompt = SMARTSAUDE_SYSTEM_PROMPT
        self.context_info = APP_CONTEXT_INFO
    
    def detect_navigation_intent(self, message: str, locale: str = "pt") -> Optional[Dict]:
        """
        Classificador de intenção de navegação por palavras-chave (bilíngue)
        
        Args:
            message: Mensagem do usuário
            locale: Idioma do usuário (pt/en)
            
        Returns:
            Dict com resposta estruturada ou None
        """
        text = (message or "").lower()
        
        # IMC
        if "imc" in text or "indice de massa corporal" in text or "índice de massa corporal" in text or "bmi" in text or "body mass index" in text:
            if locale == "en":
                return {
                    "reply": "To access BMI calculation, go to: Home → Health & Tools → BMI.",
                    "intent": "navigation",
                    "action": {
                        "type": "open_screen",
                        "target": "imc_calculator",
                        "label": "Open BMI"
                    }
                }
            return {
                "reply": "Para acessar o cálculo de IMC, siga: Início → Saúde e Ferramentas → IMC.",
                "intent": "navigation",
                "action": {
                    "type": "open_screen",
                    "target": "imc_calculator",
                    "label": "Abrir IMC"
                }
            }
        
        # Gordura Corporal
        if ("gordura" in text or "fat" in text) and ("corporal" in text or "body" in text):
            if locale == "en":
                return {
                    "reply": "To access body fat calculation, go to: Home → Health & Tools → Body Fat.",
                    "intent": "navigation",
                    "action": {
                        "type": "open_screen",
                        "target": "body_fat_calculator",
                        "label": "Open Body Fat"
                    }
                }
            return {
                "reply": "Para acessar o cálculo de gordura corporal, siga: Início → Saúde e Ferramentas → Gordura Corporal.",
                "intent": "navigation",
                "action": {
                    "type": "open_screen",
                    "target": "body_fat_calculator",
                    "label": "Abrir Gordura Corporal"
                }
            }
        
        # Histórico
        if "histórico" in text or "historico" in text or "meus dados" in text or "history" in text or "my data" in text:
            if locale == "en":
                return {
                    "reply": "To access your health history, go to: Home → Health & Tools → Health History.",
                    "intent": "navigation",
                    "action": {
                        "type": "open_screen",
                        "target": "health_history",
                        "label": "Open History"
                    }
                }
            return {
                "reply": "Para acessar seu histórico de saúde, siga: Início → Saúde e Ferramentas → Histórico de Saúde.",
                "intent": "navigation",
                "action": {
                    "type": "open_screen",
                    "target": "health_history",
                    "label": "Abrir Histórico"
                }
            }
        
        # Questionário
        if "questionário" in text or "questionario" in text or "triagem" in text or "avaliação" in text or "questionnaire" in text or "screening" in text or "assessment" in text:
            if locale == "en":
                return {
                    "reply": "To access the health questionnaire, go to: Home → Health & Tools → Questionnaire.",
                    "intent": "navigation",
                    "action": {
                        "type": "open_screen",
                        "target": "health_questionnaire",
                        "label": "Open Questionnaire"
                    }
                }
            return {
                "reply": "Para acessar o questionário de saúde, siga: Início → Saúde e Ferramentas → Questionário.",
                "intent": "navigation",
                "action": {
                    "type": "open_screen",
                    "target": "health_questionnaire",
                    "label": "Abrir Questionário"
                }
            }
        
        # Progresso
        if "progresso" in text or "evolução" in text or "evolucao" in text or "estatísticas" in text or "progress" in text or "statistics" in text:
            if locale == "en":
                return {
                    "reply": "To access your progress, go to: Home → Progress Dashboard.",
                    "intent": "navigation",
                    "action": {
                        "type": "open_screen",
                        "target": "progress_dashboard",
                        "label": "Open Progress"
                    }
                }
            return {
                "reply": "Para acessar seu progresso, siga: Início → Dashboard de Progresso.",
                "intent": "navigation",
                "action": {
                    "type": "open_screen",
                    "target": "progress_dashboard",
                    "label": "Abrir Progresso"
                }
            }
        
        # Exercícios
        if "exerc" in text or "treino" in text or "atividade" in text or "exercise" in text or "workout" in text or "activity" in text:
            if locale == "en":
                return {
                    "reply": "To access your exercises, go to: Home → My Exercises.",
                    "intent": "navigation",
                    "action": {
                        "type": "open_screen",
                        "target": "exercise_list",
                        "label": "Open Exercises"
                    }
                }
            return {
                "reply": "Para acessar seus exercícios, siga: Início → Meus Exercícios.",
                "intent": "navigation",
                "action": {
                    "type": "open_screen",
                    "target": "exercise_list",
                    "label": "Abrir Exercícios"
                }
            }
        
        # Configurações
        if "config" in text or "idioma" in text or "tema" in text or "perfil" in text or "settings" in text or "language" in text or "theme" in text or "profile" in text:
            if locale == "en":
                return {
                    "reply": "To access settings, go to: Home → Settings icon.",
                    "intent": "navigation",
                    "action": {
                        "type": "open_screen",
                        "target": "settings",
                        "label": "Open Settings"
                    }
                }
            return {
                "reply": "Para acessar as configurações, siga: Início → ícone de configurações.",
                "intent": "navigation",
                "action": {
                    "type": "open_screen",
                    "target": "settings",
                    "label": "Abrir Configurações"
                }
            }
        
        return None
    
    def process_message(self, request: ChatRequest) -> ChatResponse:
        """
        Processa mensagem do usuário e retorna resposta do assistente
        
        Args:
            request: Requisição de chat com mensagem e contexto
            
        Returns:
            ChatResponse com resposta, intenção e ação opcional
        """
        
        # 1. Tentar detectar navegação por palavras-chave primeiro
        navigation_result = self.detect_navigation_intent(request.message, request.locale or "pt")
        
        if navigation_result:
            # 2. Se for navegação, usar resposta estruturada
            memory_service.add_user_message(request.session_id, request.message)
            memory_service.add_assistant_message(
                session_id=request.session_id,
                message=navigation_result["reply"],
                intent=navigation_result["intent"],
                target=navigation_result["action"]["target"]
            )
            
            return ChatResponse(
                reply=navigation_result["reply"],
                intent=navigation_result["intent"],
                action=ChatAction(**navigation_result["action"]),
                memory_updated=True
            )
        
        # 3. Se não for navegação, usar o LLM
        return self._process_with_llm(request)
    
    def _process_with_llm(self, request: ChatRequest) -> ChatResponse:
        """
        Processa mensagem usando o LLM quando não é navegação direta
        """
        # Adiciona mensagem do usuário à memória
        memory_service.add_user_message(request.session_id, request.message)
        
        # Obtém contexto da sessão
        session_context = memory_service.get_session_context(request.session_id)
        
        # Prepara mensagens para Ollama com instrução de idioma
        messages = self._prepare_messages(request, session_context)
        
        # Adiciona instrução de idioma ao prompt
        locale = request.locale or "pt"
        if locale == "en":
            language_instruction = "Always answer in English."
        else:
            language_instruction = "Sempre responda em português."
        
        # Envia para Ollama
        ollama_response = ollama_client.chat(
            messages=messages,
            system_prompt=f"{self.system_prompt}\n\n{language_instruction}",
            temperature=0.7,
            max_tokens=500
        )
        
        if not ollama_response["success"]:
            # Fallback em caso de erro
            return self._create_fallback_response(ollama_response.get("error", "Erro desconhecido"))
        
        # Processa resposta do modelo
        assistant_reply = ollama_response["content"].strip()
        
        # Analisa intenção e extrai ação
        intent, action = self._analyze_intent_and_action(assistant_reply)
        
        # Adiciona resposta à memória
        memory_service.add_assistant_message(
            request.session_id, 
            assistant_reply, 
            intent, 
            action.target if action else None
        )
        
        return ChatResponse(
            reply=assistant_reply,
            intent=intent,
            action=action,
            memory_updated=True
        )
    
    def _prepare_messages(self, request: ChatRequest, session_context: Dict) -> list:
        """
        Prepara lista de mensagens para enviar ao Ollama
        
        Args:
            request: Requisição atual
            session_context: Contexto da sessão
            
        Returns:
            Lista de mensagens formatada
        """
        
        # Obtém histórico da conversa
        history = memory_service.get_conversation_history(request.session_id, limit=8)
        
        # Adiciona contexto da tela atual se disponível
        context_message = ""
        if request.screen_context:
            context_message = f"\nContexto atual: Usuário está na tela {request.screen_context}."
        
        # Adiciona contexto adicional se for primeira mensagem
        if len(history) <= 2:  # Apenas user + assistant
            context_message += f"\n\n{self.context_info}"
        
        # Prepara mensagem atual com contexto
        current_message = request.message
        if context_message:
            current_message = f"{context_message}\n\nPergunta: {request.message}"
        
        # Constrói lista de mensagens
        messages = []
        
        # Adiciona histórico (exceto a última mensagem do usuário que será adicionada separadamente)
        for msg in history[:-1]:  # Exclui última msg user que será a atual
            # Verifica se msg é dict ou ChatMessage
            if isinstance(msg, dict):
                # Já é dict (formato para Ollama)
                messages.append(msg)
            else:
                # É ChatMessage, converte para dict
                messages.append({
                    "role": msg.role,
                    "content": msg.content
                })
        
        # Adiciona mensagem atual
        messages.append({
            "role": "user",
            "content": current_message
        })
        
        return messages
    
    def _analyze_intent_and_action(self, reply: str) -> Tuple[str, Optional[ChatAction]]:
        """
        Analisa a resposta para extrair intenção e ação estruturada
        
        Args:
            reply: Resposta do assistente
            
        Returns:
            Tuple com (intent, action)
        """
        
        # Padrões para detectar intenção de navegação
        navigation_patterns = [
            r"vá até[:\s]*([^→\n]+)",
            r"acesse[:\s]*([^→\n]+)",
            r"para acessar[:\s]*([^→\n]+)",
            r"siga[:\s]*([^→\n]+)",
            r"navegue[:\s]*([^→\n]+)"
        ]
        
        # Padrão para detectar caminhos de navegação
        path_pattern = r"([^→\n]+)\s*→\s*([^→\n]+)(?:\s*→\s*([^→\n]+))?"
        
        # Verifica se é uma resposta de navegação
        for pattern in navigation_patterns:
            if re.search(pattern, reply, re.IGNORECASE):
                intent = "navigation"
                
                # Tenta extrair caminho específico
                path_match = re.search(path_pattern, reply)
                if path_match:
                    # Extrai telas do caminho
                    screens = [s.strip() for s in path_match.groups() if s]
                    
                    # Mapeia nomes de tela para IDs
                    target_screen = self._map_screen_to_id(screens[-1])  # Última tela do caminho
                    
                    if target_screen:
                        action = ChatAction(
                            type="open_screen",
                            target=target_screen,
                            label=f"Abrir {screens[-1]}"
                        )
                        return intent, action
                
                return intent, None
        
        # Outras intenções
        if any(keyword in reply.lower() for keyword in ["como", "qual", "onde", "funciona"]):
            intent = "info"
        elif any(keyword in reply.lower() for keyword in ["ajuda", "suporte", "dúvida"]):
            intent = "help"
        else:
            intent = "other"
        
        return intent, None
    
    def _map_screen_to_id(self, screen_name: str) -> Optional[str]:
        """
        Mapeia nome da tela para ID usado no frontend
        
        Args:
            screen_name: Nome da tela extraído da resposta
            
        Returns:
            ID da tela ou None se não encontrar
        """
        
        screen_mapping = {
            "login": "login",
            "início": "main",
            "principal": "main",
            "exercícios": "exercise_list",
            "lista de exercícios": "exercise_list",
            "detalhes do exercício": "exercise_detail",
            "treino com ia": "ia_workout",
            "central de saúde": "health_hub",
            "saúde e ferramentas": "health_hub",
            "calculadora de imc": "imc_calculator",
            "imc": "imc_calculator",
            "calculadora de gordura corporal": "body_fat_calculator",
            "gordura corporal": "body_fat_calculator",
            "questionário de saúde": "health_questionnaire",
            "questionário": "health_questionnaire",
            "histórico de saúde": "health_history",
            "histórico": "health_history",
            "dashboard de progresso": "progress_dashboard",
            "progresso": "progress_dashboard",
            "configurações": "settings",
            "assistente": "assistant",
            "ia": "assistant"
        }
        
        # Normaliza o nome da tela
        normalized_name = screen_name.lower().strip()
        
        return screen_mapping.get(normalized_name)
    
    def _create_fallback_response(self, error_message: str) -> ChatResponse:
        """
        Cria resposta de fallback quando Ollama não está disponível
        
        Args:
            error_message: Mensagem de erro
            
        Returns:
            ChatResponse de fallback
        """
        
        fallback_reply = (
            f"Desculpe, estou com dificuldades técnicas no momento. "
            f"Erro: {error_message}\n\n"
            f"Enquanto isso, posso ajudar com navegação básica:\n\n"
            f"• Para exercícios: Início → Exercícios\n"
            f"• Para saúde: Início → Saúde e Ferramentas\n"
            f"• Para progresso: Início → Dashboard de Progresso\n\n"
            f"Tente novamente em alguns instantes."
        )
        
        return ChatResponse(
            reply=fallback_reply,
            intent="help",
            action=None,
            memory_updated=False
        )
    
    def clear_session(self, session_id: str) -> bool:
        """
        Limpa sessão específica
        
        Args:
            session_id: ID da sessão para limpar
            
        Returns:
            True se limpou com sucesso
        """
        return memory_service.clear_session(session_id)
    
    def get_session_stats(self, session_id: str) -> Dict:
        """
        Obtém estatísticas da sessão
        
        Args:
            session_id: ID da sessão
            
        Returns:
            Dict com estatísticas
        """
        session = memory_service.get_session(session_id)
        context = memory_service.get_session_context(session_id)
        
        return {
            "session_id": session_id,
            "message_count": len(session.messages),
            "last_intent": context.get("last_intent"),
            "last_target": context.get("last_target"),
            "messages": [
                {"role": msg.role, "content": msg.content[:100] + "..." if len(msg.content) > 100 else msg.content}
                for msg in session.messages
            ]
        }


# Instância global do serviço
chat_service = ChatService()
