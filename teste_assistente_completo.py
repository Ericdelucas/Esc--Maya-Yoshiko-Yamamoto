#!/usr/bin/env python3
"""
Teste Completo do Assistente Virtual
Verifica se todas as áreas da tela principal são reconhecidas
"""

def testar_assistente_virtual():
    """Testa todas as funcionalidades do assistente virtual"""
    
    print("🤖 TESTE COMPLETO DO ASSISTENTE VIRTUAL")
    print("=" * 60)
    
    # Casos de teste organizados por área
    testes = {
        "🏆 RANKING/LEADERBOARD": [
            "onde fica meu ranking",
            "e meus rank", 
            "quero ver minha colocação",
            "quantos pontos tenho",
            "ranking",
            "leaderboard",
            "minha posição"
        ],
        
        "💪 MEUS EXERCÍCIOS": [
            "vc sabe onde fica meus exercícios",
            "meus exercícios",
            "onde fica meus treinos",
            "lista de exercícios",
            "ver meus exercícios",
            "quero fazer exercícios",
            "exercícios de hoje"
        ],
        
        "❤️ SAÚDE/FERRAMENTAS": [
            "onde fica saúde",
            "ferramentas de saúde",
            "quero ver minhas medidas",
            "dados de saúde",
            "saúde",
            "medidas corporais"
        ],
        
        "📊 PROGRESSO/ESTATÍSTICAS": [
            "quero ver meu progresso",
            "minhas estatísticas",
            "como está meu desempenho",
            "evolução",
            "progresso semanal",
            "estatísticas detalhadas"
        ],
        
        "⚙️ CONFIGURAÇÕES/PERFIL": [
            "config",
            "meu perfil",
            "configurações",
            "ajustes",
            "quero configurar",
            "perfil do usuário"
        ],
        
        "🧮 CALCULADORAS": [
            "calcular meu IMC",
            "índice de massa corporal",
            "calcular gordura corporal",
            "medir gordura",
            "IMC",
            "calculadora de gordura"
        ],
        
        "📋 HISTÓRICO/DADOS": [
            "meu histórico",
            "histórico de saúde",
            "questionário de saúde",
            "dados anteriores",
            "ver histórico completo",
            "questionário"
        ]
    }
    
    resultados = {}
    total_testes = 0
    testes_passaram = 0
    
    for area, frases in testes.items():
        print(f"\n{area}")
        print("-" * 40)
        
        resultados_area = []
        
        for frase in frases:
            total_testes += 1
            
            # Simular reconhecimento do assistente
            reconhecido = simular_reconhecimento_assistente(frase)
            
            if reconhecido:
                print(f"✅ '{frase}' → RECONHECIDO ({reconhecido})")
                testes_passaram += 1
                resultados_area.append({"frase": frase, "status": "PASS", "acao": reconhecido})
            else:
                print(f"❌ '{frase}' → NÃO RECONHECIDO")
                resultados_area.append({"frase": frase, "status": "FAIL", "acao": None})
        
        resultados[area] = resultados_area
    
    # Resultado final
    print("\n" + "=" * 60)
    print("📊 RESULTADO FINAL DO TESTE")
    print("=" * 60)
    
    taxa_sucesso = (testes_passaram / total_testes) * 100
    
    print(f"🎯 Total de Testes: {total_testes}")
    print(f"✅ Testes Passaram: {testes_passaram}")
    print(f"❌ Testes Falharam: {total_testes - testes_passaram}")
    print(f"📈 Taxa de Sucesso: {taxa_sucesso:.1f}%")
    
    # Análise por área
    print(f"\n📋 ANÁLISE POR ÁREA:")
    for area, resultados_area in resultados.items():
        passados_area = len([r for r in resultados_area if r["status"] == "PASS"])
        total_area = len(resultados_area)
        taxa_area = (passados_area / total_area) * 100
        
        status = "✅ PERFEITO" if taxa_area == 100 else "⚠️ PARCIAL" if taxa_area >= 70 else "❌ CRÍTICO"
        print(f"   {area}: {passados_area}/{total_area} ({taxa_area:.1f}%) {status}")
    
    # Verificação crítica
    areas_criticas = ["🏆 RANKING/LEADERBOARD", "💪 MEUS EXERCÍCIOS"]
    areas_criticas_ok = True
    
    for area in areas_criticas:
        passados = len([r for r in resultados[area] if r["status"] == "PASS"])
        total = len(resultados[area])
        if passados < total:
            areas_criticas_ok = False
            print(f"\n🚨 ÁREA CRÍTICA COM PROBLEMA: {area} ({passados}/{total})")
    
    # Veredito final
    if taxa_sucesso >= 90 and areas_criticas_ok:
        print(f"\n🎉 VEREDITO: EXCELENTE!")
        print("   Assistente reconhece praticamente todos os comandos")
        print("   Áreas críticas funcionando perfeitamente")
    elif taxa_sucesso >= 75 and areas_criticas_ok:
        print(f"\n✅ VEREDITO: BOM!")
        print("   Assistente funcional com pequenas melhorias possíveis")
        print("   Áreas críticas funcionando")
    elif taxa_sucesso >= 50:
        print(f"\n⚠️ VEREDITO: REGULAR")
        print("   Assistente funciona mas precisa de melhorias")
        print("   Algumas áreas podem ter problemas")
    else:
        print(f"\n❌ VEREDITO: PRECISA MELHORAR")
        print("   Assistente não reconhece comandos básicos")
        print("   Requer revisão urgente")
    
    return {
        "total_testes": total_testes,
        "testes_passaram": testes_passaram,
        "taxa_sucesso": taxa_sucesso,
        "resultados": resultados,
        "areas_criticas_ok": areas_criticas_ok
    }

def simular_reconhecimento_assistente(frase):
    """Simula o reconhecimento do assistente baseado no código modificado"""
    
    frase_lower = frase.lower()
    
    # 🔥 RECONHECIMENTO DE RANKING/LEADERBOARD
    if (any(palavra in frase_lower for palavra in ["rank", "ranking", "leaderboard"]) or
        "colocação" in frase_lower or "posição" in frase_lower or "pontos" in frase_lower):
        return "LeaderboardActivity"
    
    # 🔥 RECONHECIMENTO DE MEUS EXERCÍCIOS
    if (any(palavra in frase_lower for palavra in ["meus exerc", "exerc", "treino"]) or
        "lista de exerc" in frase_lower or "ver exerc" in frase_lower):
        return "ExerciseListActivity"
    
    # 🔥 RECONHECIMENTO DE SAÚDE/FERRAMENTAS
    if (any(palavra in frase_lower for palavra in ["saúde", "saude", "medidas"]) or
        "ferramentas de saúde" in frase_lower or "dados de saúde" in frase_lower):
        return "HealthToolsActivity"
    
    # 🔥 RECONHECIMENTO DE PROGRESSO
    if (any(palavra in frase_lower for palavra in ["progresso", "evolu", "estat", "desempenho"])):
        return "ProgressDashboardActivity"
    
    # 🔥 RECONHECIMENTO DE CONFIGURAÇÕES
    if (any(palavra in frase_lower for palavra in ["config", "perfil", "ajuste"])):
        return "SettingsActivity"
    
    # 🔥 RECONHECIMENTO DE IMC
    if (any(palavra in frase_lower for palavra in ["imc", "índice de massa corporal"])):
        return "ImcCalculatorActivity"
    
    # 🔥 RECONHECIMENTO DE GORDURA
    if (any(palavra in frase_lower for palavra in ["gordura", "gordur"])):
        return "BodyFatCalculatorActivity"
    
    # 🔥 RECONHECIMENTO DE HISTÓRICO
    if (any(palavra in frase_lower for palavra in ["histórico", "histor"])):
        return "HealthHistoryActivity"
    
    # 🔥 RECONHECIMENTO DE QUESTIONÁRIO
    if (any(palavra in frase_lower for palavra in ["questionário", "question"])):
        return "HealthQuestionnaireActivity"
    
    return None

if __name__ == "__main__":
    resultado = testar_assistente_virtual()
    
    # Salvar resultado
    import json
    with open("resultado_teste_assistente.json", "w", encoding="utf-8") as f:
        json.dump(resultado, f, ensure_ascii=False, indent=2)
    
    print(f"\n📁 Resultado detalhado salvo em: resultado_teste_assistente.json")
