#!/usr/bin/env python3
"""
Script para testar se a IA está respondendo com o conhecimento atualizado do SmartSaúde
"""

import requests
import json
import time

# Configuração
BASE_URL = "http://localhost:8090"  # AI Service
CHAT_ENDPOINT = f"{BASE_URL}/chat"

# Test cases para verificar se a IA conhece as funcionalidades atualizadas
test_cases = [
    {
        "name": "Teste Básico - Saudação",
        "message": "Olá, sou novo aqui. Como funciona o app?",
        "expected_keywords": ["início", "principal", "funcionalidades", "exercícios", "saúde"]
    },
    {
        "name": "Teste Navegação - IMC",
        "message": "Como calculo meu IMC?",
        "expected_keywords": ["início", "saúde", "ferramentas", "imc", "calculadora"]
    },
    {
        "name": "Teste IA - Treinos com Câmera",
        "message": "Quero treinar com IA e câmera",
        "expected_keywords": ["exercícios", "ia", "câmera", "movimento", "tempo real"]
    },
    {
        "name": "Teste Gamificação - Ranking",
        "message": "Como vejo meu ranking?",
        "expected_keywords": ["ranking", "placar", "início", "pontos", "conquistas"]
    },
    {
        "name": "Teste Profissional - Gestão",
        "message": "Sou profissional de saúde, como gerencio meus pacientes?",
        "expected_keywords": ["profissional", "pacientes", "lista", "gerenciar", "planos"]
    },
    {
        "name": "Teste Desafios",
        "message": "Onde encontro os desafios do app?",
        "expected_keywords": ["desafios", "início", "semanais", "mensais", "pontos"]
    },
    {
        "name": "Teste Chat",
        "message": "Como falo com meu profissional?",
        "expected_keywords": ["chat", "mensagens", "comunicação", "início"]
    },
    {
        "name": "Teste Completo - Funcionalidades",
        "message": "Quais são todas as funcionalidades principais do SmartSaúde?",
        "expected_keywords": ["exercícios", "saúde", "gamificação", "profissional", "ia", "gestão"]
    }
]

def test_ai_response():
    """Testa as respostas da IA com os casos de teste"""
    print("🤖 TESTANDO IA ATUALIZADA DO SMARTSAÚDE")
    print("=" * 60)
    
    results = []
    
    for i, test_case in enumerate(test_cases, 1):
        print(f"\n📝 Teste {i}: {test_case['name']}")
        print(f"💬 Mensagem: {test_case['message']}")
        print("-" * 40)
        
        try:
            # Prepara a requisição
            payload = {
                "message": test_case['message'],
                "session_id": f"test_session_{int(time.time())}",
                "context": {
                    "user_type": "patient",  # Testar como paciente
                    "locale": "pt"
                }
            }
            
            # Envia a requisição
            response = requests.post(CHAT_ENDPOINT, json=payload, timeout=30)
            
            if response.status_code == 200:
                data = response.json()
                reply = data.get('reply', '')
                intent = data.get('intent', '')
                action = data.get('action', {})
                
                print(f"🤖 Resposta da IA: {reply[:200]}...")
                print(f"🎯 Intenção detectada: {intent}")
                if action:
                    print(f"⚡ Ação sugerida: {action}")
                
                # Verifica se as palavras-chave esperadas estão na resposta
                reply_lower = reply.lower()
                found_keywords = [kw for kw in test_case['expected_keywords'] if kw in reply_lower]
                
                success = len(found_keywords) >= len(test_case['expected_keywords']) * 0.6  # 60% das keywords
                
                results.append({
                    'test': test_case['name'],
                    'success': success,
                    'keywords_found': found_keywords,
                    'keywords_expected': test_case['expected_keywords'],
                    'response_preview': reply[:100]
                })
                
                if success:
                    print("✅ TESTE PASSOU - Palavras-chave encontradas:", found_keywords)
                else:
                    print("❌ TESTE FALHOU - Palavras-chave encontradas:", found_keywords)
                    print(f"   Esperadas: {test_case['expected_keywords']}")
                
            else:
                print(f"❌ Erro HTTP: {response.status_code}")
                print(f"   Resposta: {response.text}")
                results.append({
                    'test': test_case['name'],
                    'success': False,
                    'error': f"HTTP {response.status_code}",
                    'response_preview': response.text[:100]
                })
                
        except requests.exceptions.ConnectionError:
            print("❌ Erro de conexão - Serviço de IA não está rodando")
            print("   Execute: docker compose -f docker-compose.minimal.yml up ai-service")
            results.append({
                'test': test_case['name'],
                'success': False,
                'error': 'Connection refused',
                'response_preview': ''
            })
            break
            
        except Exception as e:
            print(f"❌ Erro inesperado: {str(e)}")
            results.append({
                'test': test_case['name'],
                'success': False,
                'error': str(e),
                'response_preview': ''
            })
    
    # Resumo final
    print("\n" + "=" * 60)
    print("📊 RESUMO DOS TESTES")
    print("=" * 60)
    
    passed = sum(1 for r in results if r.get('success', False))
    total = len(results)
    
    for result in results:
        status = "✅ PASSOU" if result.get('success', False) else "❌ FALHOU"
        print(f"{status} - {result['test']}")
        if not result.get('success', False) and 'error' in result:
            print(f"    Erro: {result['error']}")
    
    print(f"\n🎯 RESULTADO: {passed}/{total} testes passaram ({passed/total*100:.1f}%)")
    
    if passed == total:
        print("🎉 PARABÉNS! A IA está respondendo com conhecimento atualizado!")
    elif passed >= total * 0.7:
        print("👍 BOM! A IA está respondendo bem, mas pode melhorar.")
    else:
        print("⚠️ ATENÇÃO! A IA precisa de mais ajustes.")
    
    # Salva resultados detalhados
    with open('ai_test_results.json', 'w', encoding='utf-8') as f:
        json.dump(results, f, ensure_ascii=False, indent=2)
    
    print("\n📄 Resultados detalhados salvos em: ai_test_results.json")

if __name__ == "__main__":
    test_ai_response()
