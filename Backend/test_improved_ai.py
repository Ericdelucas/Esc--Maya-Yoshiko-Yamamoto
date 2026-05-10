#!/usr/bin/env python3
"""
Script para testar as melhorias da IA do SmartSaúde
"""

import requests
import json
import time

# Configuração
BASE_URL = "http://localhost:8090"  # AI Service
CHAT_ENDPOINT = f"{BASE_URL}/chat"

# Test cases para verificar as melhorias da IA
test_cases = [
    {
        "name": "Saudação Detalhada",
        "message": "oi",
        "expected_keywords": ["smartsaúde", "exercícios", "saúde", "ferramentas", "progresso", "profissionais"],
        "min_length": 100
    },
    {
        "name": "Pedido de Ajuda Completo",
        "message": "ajuda",
        "expected_keywords": ["exercícios", "ia", "saúde", "gamificação", "profissionais", "chat"],
        "min_length": 200
    },
    {
        "name": "Explicação de Exercícios",
        "message": "como funcionam os exercícios com IA?",
        "expected_keywords": ["câmera", "tempo real", "feedback", "vídeos", "pontos"],
        "min_length": 150
    },
    {
        "name": "Ferramentas de Saúde",
        "message": "quero calcular meu IMC",
        "expected_keywords": ["imc", "saúde e ferramentas", "peso", "altura", "classificação"],
        "min_length": 120
    },
    {
        "name": "Sistema de Gamificação",
        "message": "como vejo meu ranking?",
        "expected_keywords": ["ranking", "pontos", "dashboard", "desafios", "nível"],
        "min_length": 120
    },
    {
        "name": "Suporte Profissional",
        "message": "sou profissional de saúde",
        "expected_keywords": ["pacientes", "planos", "relatórios", "chat", "agenda"],
        "min_length": 150
    },
    {
        "name": "Sistema de Chat",
        "message": "como falo com meu profissional?",
        "expected_keywords": ["chat", "comunicação", "mensagens", "arquivos", "seguro"],
        "min_length": 120
    },
    {
        "name": "Resolução de Problemas",
        "message": "estou com um problema não entendi",
        "expected_keywords": ["resolver", "passo a passo", "navegação", "específico", "ajudar"],
        "min_length": 150
    },
    {
        "name": "Mensagem Genérica",
        "message": "quero saber mais",
        "expected_keywords": ["exercícios", "saúde", "progresso", "profissional", "desafios"],
        "min_length": 100
    }
]

def test_improved_ai():
    """Testa as respostas melhoradas da IA"""
    print("🤖 TESTANDO IA MELHORADA DO SMARTSAÚDE")
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
                "session_id": f"test_session_{int(time.time())}_{i}",
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
                
                print(f"🤖 Resposta da IA:")
                print(f"   {reply[:200]}...")
                if len(reply) > 200:
                    print(f"   ...({len(reply)} caracteres totais)")
                print(f"🎯 Intenção detectada: {intent}")
                if action:
                    print(f"⚡ Ação sugerida: {action}")
                
                # Verifica se as palavras-chave esperadas estão na resposta
                reply_lower = reply.lower()
                found_keywords = [kw for kw in test_case['expected_keywords'] if kw in reply_lower]
                
                # Verifica comprimento mínimo
                length_ok = len(reply) >= test_case['min_length']
                
                # Verifica qualidade da resposta
                has_emojis = any(char in reply for char in ['🏋', '🏥', '📊', '👨', '💬', '🎯', '🏆', '⭐'])
                has_structure = any(marker in reply for marker in ['**', '•', '1.', '2.', '3.', '4.'])
                
                success = (
                    len(found_keywords) >= len(test_case['expected_keywords']) * 0.4 and  # 40% das keywords
                    length_ok and
                    (has_emojis or has_structure)  # Tem estrutura visual
                )
                
                results.append({
                    'test': test_case['name'],
                    'success': success,
                    'keywords_found': found_keywords,
                    'keywords_expected': test_case['expected_keywords'],
                    'keywords_match_rate': len(found_keywords) / len(test_case['expected_keywords']),
                    'length': len(reply),
                    'length_ok': length_ok,
                    'has_emojis': has_emojis,
                    'has_structure': has_structure,
                    'response_preview': reply[:150]
                })
                
                if success:
                    print("✅ TESTE PASSOU")
                    print(f"   📏 Comprimento: {len(reply)} caracteres (mín: {test_case['min_length']})")
                    print(f"   🔍 Keywords: {found_keywords} ({len(found_keywords)}/{len(test_case['expected_keywords'])})")
                    print(f"   🎨 Formatação: {'Emojis' if has_emojis else ''}{'Estrutura' if has_structure else ''}")
                else:
                    print("❌ TESTE FALHOU")
                    print(f"   📏 Comprimento: {len(reply)} caracteres (mín: {test_case['min_length']}) - {'✅' if length_ok else '❌'}")
                    print(f"   🔍 Keywords: {found_keywords} ({len(found_keywords)}/{len(test_case['expected_keywords'])})")
                    print(f"   🎨 Formatação: {'Emojis' if has_emojis else '❌'}{'Estrutura' if has_structure else '❌'}")
                
            else:
                print(f"❌ Erro HTTP: {response.status_code}")
                print(f"   Resposta: {response.text}")
                results.append({
                    'test': test_case['name'],
                    'success': False,
                    'error': f"HTTP {response.status_code}",
                    'response_preview': response.text[:150]
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
    print("📊 RESUMO DOS TESTES - IA MELHORADA")
    print("=" * 60)
    
    passed = sum(1 for r in results if r.get('success', False))
    total = len(results)
    
    # Análise detalhada
    avg_keywords_match = sum(r.get('keywords_match_rate', 0) for r in results) / total if total > 0 else 0
    avg_length = sum(r.get('length', 0) for r in results) / total if total > 0 else 0
    with_emojis = sum(1 for r in results if r.get('has_emojis', False))
    with_structure = sum(1 for r in results if r.get('has_structure', False))
    
    print(f"🎯 RESULTADO GERAL: {passed}/{total} testes passaram ({passed/total*100:.1f}%)")
    print(f"📈 Taxa média de keywords: {avg_keywords_match*100:.1f}%")
    print(f"📏 Comprimento médio das respostas: {avg_length:.0f} caracteres")
    print(f"🎨 Respostas com emojis: {with_emojis}/{total} ({with_emojis/total*100:.1f}%)")
    print(f"📋 Respostas com estrutura: {with_structure}/{total} ({with_structure/total*100:.1f}%)")
    
    print("\n📋 DETALHES POR TESTE:")
    for result in results:
        status = "✅ PASSOU" if result.get('success', False) else "❌ FALHOU"
        print(f"{status} - {result['test']}")
        if not result.get('success', False) and 'error' in result:
            print(f"    Erro: {result['error']}")
        elif result.get('success', False):
            print(f"    Keywords: {result.get('keywords_match_rate', 0)*100:.0f}% | "
                  f"Tamanho: {result.get('length', 0)} | "
                  f"Formatação: {'🎨' if result.get('has_emojis') or result.get('has_structure') else '❌'}")
    
    # Salva resultados detalhados
    with open('ai_improvement_test_results.json', 'w', encoding='utf-8') as f:
        json.dump(results, f, ensure_ascii=False, indent=2)
    
    print(f"\n📄 Resultados detalhados salvos em: ai_improvement_test_results.json")
    
    # Verificação final de qualidade
    if passed >= total * 0.8:
        print("\n🎉 EXCELENTE! A IA está respondendo com alta qualidade!")
    elif passed >= total * 0.6:
        print("\n👍 BOM! A IA melhorou bastante, mas ainda pode evoluir.")
    else:
        print("\n⚠️ PRECISA MELHORAR! A IA ainda precisa de ajustes.")

if __name__ == "__main__":
    test_improved_ai()
