import requests
import json
from typing import Dict, List, Optional, Any
from app.core.config import settings


class OllamaClient:
    def __init__(self, base_url: str = None, model: str = "qwen:0.5b-chat"):
        """
        Cliente para integração com Ollama
        
        Args:
            base_url: URL do Ollama (default: 172.17.0.1:11434)
            model: Modelo a ser usado (default: qwen:0.5b-chat)
        """
        self.base_url = base_url or getattr(settings, 'OLLAMA_BASE_URL', 'http://172.17.0.1:11434')
        self.model = model
        self.timeout = 30
    
    def chat(self, messages: List[Dict[str, str]], 
            system_prompt: str = None, 
            temperature: float = 0.7,
            max_tokens: int = 500) -> Dict[str, Any]:
        """
        Envia requisição de chat para Ollama
        
        Args:
            messages: Lista de mensagens no formato [{"role": "user|assistant", "content": "..."}]
            system_prompt: Prompt de sistema opcional
            temperature: Temperatura para geração (0.0-1.0)
            max_tokens: Máximo de tokens na resposta
            
        Returns:
            Dict com resposta do modelo
        """
        
        # Prepara payload para Ollama
        payload = {
            "model": self.model,
            "messages": messages,
            "stream": False,
            "options": {
                "temperature": temperature,
                "num_predict": max_tokens
            }
        }
        
        # Adiciona system prompt se fornecido
        if system_prompt:
            payload["system"] = system_prompt
        
        try:
            response = requests.post(
                f"{self.base_url}/api/chat",
                json=payload,
                timeout=self.timeout,
                headers={"Content-Type": "application/json"}
            )
            
            response.raise_for_status()
            
            result = response.json()
            
            # Extrai resposta do modelo
            if "message" in result and "content" in result["message"]:
                return {
                    "success": True,
                    "content": result["message"]["content"],
                    "model": self.model,
                    "usage": result.get("usage", {})
                }
            else:
                return {
                    "success": False,
                    "error": "Resposta inválida do Ollama",
                    "raw_response": result
                }
                
        except requests.exceptions.ConnectionError:
            return {
                "success": False,
                "error": f"Não foi possível conectar ao Ollama em {self.base_url}",
                "suggestion": "Verifique se o Ollama está rodando e o modelo está disponível"
            }
            
        except requests.exceptions.Timeout:
            return {
                "success": False,
                "error": "Timeout na requisição ao Ollama",
                "suggestion": "Tente novamente ou reduza o tamanho da requisição"
            }
            
        except requests.exceptions.RequestException as e:
            return {
                "success": False,
                "error": f"Erro na requisição: {str(e)}",
                "suggestion": "Verifique conexão e configurações do Ollama"
            }
            
        except Exception as e:
            return {
                "success": False,
                "error": f"Erro inesperado: {str(e)}",
                "suggestion": "Verifique logs para mais detalhes"
            }
    
    def check_connection(self) -> Dict[str, Any]:
        """
        Verifica se Ollama está acessível e o modelo disponível
        
        Returns:
            Dict com status da conexão
        """
        try:
            # Verifica se Ollama está online
            response = requests.get(f"{self.base_url}/api/tags", timeout=5)
            response.raise_for_status()
            
            models = response.json().get("models", [])
            model_names = [model["name"] for model in models]
            
            # Verifica se modelo específico está disponível
            model_available = any(self.model in name for name in model_names)
            
            return {
                "connected": True,
                "model_available": model_available,
                "available_models": model_names,
                "target_model": self.model
            }
            
        except requests.exceptions.ConnectionError:
            return {
                "connected": False,
                "error": f"Ollama não está acessível em {self.base_url}",
                "suggestion": "Inicie o Ollama: ollama serve"
            }
            
        except Exception as e:
            return {
                "connected": False,
                "error": f"Erro ao verificar Ollama: {str(e)}",
                "suggestion": "Verifique instalação do Ollama"
            }
    
    def list_models(self) -> List[str]:
        """
        Lista modelos disponíveis no Ollama
        
        Returns:
            Lista de nomes de modelos
        """
        try:
            response = requests.get(f"{self.base_url}/api/tags", timeout=5)
            response.raise_for_status()
            
            models = response.json().get("models", [])
            return [model["name"] for model in models]
            
        except Exception:
            return []
    
    def pull_model(self, model_name: str) -> Dict[str, Any]:
        """
        Baixa modelo do Ollama
        
        Args:
            model_name: Nome do modelo para baixar
            
        Returns:
            Dict com status do download
        """
        try:
            response = requests.post(
                f"{self.base_url}/api/pull",
                json={"name": model_name},
                timeout=300  # 5 minutos para download
            )
            response.raise_for_status()
            
            return {
                "success": True,
                "message": f"Modelo {model_name} baixado com sucesso"
            }
            
        except Exception as e:
            return {
                "success": False,
                "error": f"Erro ao baixar modelo {model_name}: {str(e)}"
            }


# Instância global do cliente
ollama_client = OllamaClient()
