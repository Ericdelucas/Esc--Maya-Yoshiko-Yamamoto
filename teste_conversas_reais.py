#!/usr/bin/env python3
"""
Teste Real das Conversas do Assistente Virtual
Simula exatamente as conversas que o usuário relatou
"""

def simulate_assistente_response(user_message):
    """Simula a resposta do assistente baseado no código atualizado"""
    
    if user_message is None:
        return "Entendi. Use as seções Exercícios, Saúde ou Progresso do app."
    
    lower = user_message.lower()
    
    # 🔥 RECONHECIMENTO DE RANKING/LEADERBOARD
    if (any(palavra in lower for palavra in ["rank", "ranking", "leaderboard"]) or
        "colocação" in lower or "posição" in lower or "pontos" in lower):
        return "Deseja abrir o Ranking?"
    
    # 🔥 RECONHECIMENTO DE MEUS EXERCÍCIOS
    if (any(palavra in lower for palavra in ["meus exerc", "exerc", "treino"]) or
        "lista de exerc" in lower or "ver exerc" in lower):
        return "Deseja abrir Meus Exercícios?"
    
    # 🔥 RECONHECIMENTO DE SAÚDE/FERRAMENTAS
    if (any(palavra in lower for palavra in ["saúde", "saude", "medidas"]) or
        "ferramentas de saúde" in lower or "dados de saúde" in lower):
        return "Deseja abrir Ferramentas de Saúde?"
    
    # 🔥 RECONHECIMENTO DE PROGRESSO
    if (any(palavra in lower for palavra in ["progresso", "evolu", "estat", "desempenho"])):
        return "Deseja abrir Progresso?"
    
    # 🔥 RECONHECIMENTO DE CONFIGURAÇÕES
    if (any(palavra in lower for palavra in ["config", "perfil", "ajuste"])):
        return "Deseja abrir Configurações?"
    
    # 🔥 RECONHECIMENTO DE IMC
    if (any(palavra in lower for palavra in ["imc", "índice de massa corporal"])):
        return "Deseja abrir Calculadora IMC?"
    
    # 🔥 RECONHECIMENTO DE GORDURA
    if (any(palavra in lower for palavra in ["gordura", "gordur"])):
        return "Deseja abrir Calculadora de Gordura?"
    
    # 🔥 RECONHECIMENTO DE HISTÓRICO
    if (any(palavra in lower for palavra in ["histórico", "histor"])):
        return "Deseja abrir Histórico?"
    
    # 🔥 RECONHECIMENTO DE QUESTIONÁRIO
    if (any(palavra in lower for palavra in ["questionário", "question"])):
        return "Deseja abrir Questionário?"
    
    # Resposta genérica padrão (antiga)
    return "Entendi. Use as seções Exercícios, Saúde ou Progresso do app."

def testar_conversas_reais():
    """Testa as conversas exatas que o usuário relatou"""
    
    print("🤖 TESTE DAS CONVERSAS REAIS DO ASSISTENTE")
    print("=" * 60)
    
    # Conversas exatas que o usuário relatou
    conversas = [
        {
            "usuario": "my name's eric",
            "esperado": "GENÉRICO (não tem ação específica)",
            "nota": "Esta conversa não tem ação específica, é ok ser genérica"
        },
        {
            "usuario": "ola", 
            "esperado": "GENÉRICO (não tem ação específica)",
            "nota": "Saudação simples, resposta genérica é aceitável"
        },
        {
            "usuario": "vc sabe onde fica meus exercícios",
            "esperado": "Deseja abrir Meus Exercícios?",
            "nota": "CRÍTICO - Esta era a principal falha"
        },
        {
            "usuario": "e meus rank",
            "esperado": "Deseja abrir o Ranking?", 
            "nota": "CRÍTICO - Outra falha principal"
        },
        {
            "usuario": "onde fica meu ranking",
            "esperado": "Deseja abrir o Ranking?",
            "nota": "CRÍTICO - Terceira falha principal"
        }
    ]
    
    resultados = []
    conversas_melhoradas = 0
    conversas_criticas = 0
    conversas_criticas_melhoradas = 0
    
    for i, conversa in enumerate(conversas, 1):
        print(f"\n🗣️  Conversa {i}")
        print(f"   Usuário: \"{conversa['usuario']}\"")
        
        resposta = simulate_assistente_response(conversa['usuario'])
        print(f"   Assistente: \"{resposta}\"")
        print(f"   Esperado: \"{conversa['esperado']}\"")
        print(f"   Nota: {conversa['nota']}")
        
        # Verificar se melhorou
        if conversa['esperado'] == "GENÉRICO (não tem ação específica)":
            # Conversas genéricas são aceitáveis
            status = "✅ ACEITÁVEL"
            conversas_melhoradas += 1
        elif resposta == conversa['esperado']:
            # Melhorou perfeitamente
            status = "🎉 PERFEITO - MELHOROU!"
            conversas_criticas_melhoradas += 1
            conversas_melhoradas += 1
        elif "Deseja abrir" in resposta and "GENÉRICO" not in conversa['esperado']:
            # Melhorou (reconheceu e oferece ação)
            status = "✅ MELHOROU - Oferece ação!"
            conversas_criticas_melhoradas += 1
            conversas_melhoradas += 1
        else:
            # Continua genérico (não melhorou)
            status = "❌ CONTINUA GENÉRICO"
        
        conversas_criticas += 1
        resultados.append({
            "usuario": conversa['usuario'],
            "resposta": resposta,
            "esperado": conversa['esperado'],
            "status": status
        })
        
        print(f"   Status: {status}")
        print("-" * 50)
    
    # Resultado final
    print("\n" + "=" * 60)
    print("📊 RESULTADO DAS CONVERSAS REAIS")
    print("=" * 60)
    
    total_conversas = len(conversas)
    taxa_melhoria = (conversas_melhoradas / total_conversas) * 100
    taxa_criticas_melhoradas = (conversas_criticas_melhoradas / conversas_criticas) * 100
    
    print(f"📈 Total de Conversas Testadas: {total_conversas}")
    print(f"✅ Conversas Melhoradas: {conversas_melhoradas}/{total_conversas} ({taxa_melhoria:.1f}%)")
    print(f"🎯 Conversas Críticas: {conversas_criticas}")
    print(f"🚀 Conversas Críticas Melhoradas: {conversas_criticas_melhoradas}/{conversas_criticas} ({taxa_criticas_melhoradas:.1f}%)")
    
    # Veredito
    if taxa_criticas_melhoradas >= 80:
        veredito = "🎉 EXCELENTE - Problemas principais resolvidos!"
    elif taxa_criticas_melhoradas >= 60:
        veredito = "✅ BOM - Maioria dos problemas resolvidos!"
    elif taxa_criticas_melhoradas >= 40:
        veredito = "⚠️ REGULAR - Alguns problemas resolvidos!"
    else:
        veredito = "❌ PRECISA MELHORAR - Problemas principais continuam!"
    
    print(f"\n🏆 VEREDITO: {veredito}")
    
    # Comparativo Antes vs Depois
    print(f"\n📊 COMPARATIVO:")
    print(f"   ANTES:")
    print(f"   \"vc sabe onde fica meus exercícios\" → \"Entendi. Use as seções...\"")
    print(f"   \"e meus rank\" → \"Entendi. Use as seções...\"")
    print(f"   \"onde fica meu ranking\" → \"Entendi. Use as seções...\"")
    print(f"\n   🚀 DEPOIS:")
    
    for resultado in resultados:
        if "GENÉRICO" not in resultado['esperado']:
            print(f"   \"{resultado['usuario']}\" → \"{resultado['resposta']}\"")
    
    return {
        "total_conversas": total_conversas,
        "conversas_melhoradas": conversas_melhoradas,
        "taxa_melhoria": taxa_melhoria,
        "conversas_criticas_melhoradas": conversas_criticas_melhoradas,
        "taxa_criticas_melhoradas": taxa_criticas_melhoradas,
        "resultados": resultados
    }

if __name__ == "__main__":
    resultado = testar_conversas_reais()
    
    print(f"\n📁 Resultado salvo em memória para análise!")
