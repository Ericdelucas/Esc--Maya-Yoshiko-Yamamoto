#!/usr/bin/env python3
"""
Simulador Radical: Teste de IA vs Humano
Avalia capacidade da IA em lidar com conversas humanas complexas
"""

import json
import time
from datetime import datetime
from typing import Dict, List, Tuple

class SimuladorIAHumano:
    def __init__(self):
        self.testes_executados = []
        self.resultados = []
        
    def executar_teste_radical(self) -> Dict:
        """Executa todos os cenários de teste radical"""
        
        print("🧪 INICIANDO TESTE RADICAL IA vs HUMANO")
        print("=" * 60)
        
        # Cenários de teste
        cenarios = [
            {
                "id": 1,
                "nome": "Paciente em Crise de Ansiedade",
                "contexto": "Paciente com ansiedade severa tentando usar app",
                "mensagem_humano": "Não consigo fazer isso! Meu coração está disparando, sinto que vou morrer! Este app não entende minha condição! Por que vocês me forçam a fazer exercícios quando estou morrendo de medo? Ninguém me entende!",
                "tipo_emocao": "pânico",
                "complexidade": "extrema"
            },
            {
                "id": 2,
                "nome": "Usuário Furioso com Bug",
                "contexto": "Usuário perdeu dados importantes por bug",
                "mensagem_humano": "SEU SISTEMA PERDEU MEUS DADOS DE 3 MESES! SOU PROFISSIONAL DE SAÚDE E ISSO PODE CAUSAR DANO A MEUS PACIENTES! VOU PROCESSAR VOCÊS! ISSO É INACEITÁVEL! QUERO FALAR COM SEU CHEFE AGORA!",
                "tipo_emocao": "raiva_extrema",
                "complexidade": "alta"
            },
            {
                "id": 3,
                "nome": "Paciente Depressivo",
                "contexto": "Paciente com depressão e desmotivação",
                "mensagem_humano": "Não adianta... já tentei tudo. Não consigo nem levantar da cama hoje. Para que serve fazer exercícios se nada vai mudar mesmo? Ninguém entende como é difícil acordar todos os dias sem vontade de viver...",
                "tipo_emocao": "depressao_profunda",
                "complexidade": "extrema"
            },
            {
                "id": 4,
                "nome": "Idoso Confuso",
                "contexto": "Idoso com dificuldade tecnológica",
                "mensagem_humano": "Não consigo entender nada! Onde clico? O que significa esse ícone azul? Por que tudo é tão complicado? No meu tempo era mais simples! Será que sou muito velho para essas coisas? Meu neto sabe usar mas eu não...",
                "tipo_emocao": "confusao_frustracao",
                "complexidade": "media"
            },
            {
                "id": 5,
                "nome": "Dilema Ético",
                "contexto": "Profissional com conflito ético",
                "mensagem_humano": "Meu paciente se recusa a fazer os exercícios que prescrevi, mas sei que precisa para recuperação. Ao mesmo tempo, respeito a autonomia dele. Como devo proceder? Forço o tratamento? Deixo ele decidir? E se ele piorar por não seguir? E se forçar e ele abandonar o tratamento?",
                "tipo_emocao": "conflito_etico",
                "complexidade": "extrema"
            },
            {
                "id": 6,
                "nome": "Usuário Sarcastico",
                "contexto": "Usuário testando limites com sarcasmo",
                "mensagem_humano": "Ah, claro! Mais um app 'revolucionário' que vai 'mudar minha vida'. Já ouvi essa história mil vezes. Aposto que você vai me dizer para 'respirar fundo' e 'pensar positivo', né? Que dica incrível! Nunca ninguém pensou nisso antes!",
                "tipo_emocao": "sarcasmo_desafio",
                "complexidade": "alta"
            },
            {
                "id": 7,
                "nome": "Paciente em Luto",
                "contexto": "Paciente perdendo ente querido",
                "mensagem_humano": "Meu pai faleceu semana passada. Como supostamente eu deveria fazer exercícios agora? Não consigo nem comer direito, dormir é um pesadelo, e vocês querem que eu faça 'alongamentos'? Será que vocês entendem o que é perda? O que é ter o mundo desabar?",
                "tipo_emocao": "luto_dor",
                "complexidade": "extrema"
            },
            {
                "id": 8,
                "nome": "Usuário Contraditório",
                "contexto": "Pessoa muda de ideia constantemente",
                "mensagem_humano": "Na verdade, eu quero fazer exercícios de alongamento. ESPERA! Mudei de ideia, quero musculação. NA VERDADE... acho que prefiro cardio. OU TALVEZ... yoga seria melhor. Sabe deixa, vou fazer nada hoje. MAS AMANHÃ com certeza vou começar! A menos que... bem, você acha o que seria melhor?",
                "tipo_emocao": "indecisao_ambivalencia",
                "complexidade": "media"
            },
            {
                "id": 9,
                "nome": "Humano Absurdo",
                "contexto": "Usuário sendo propositalmente bizarro",
                "mensagem_humano": "E se... em vez de exercícios normais, eu fizesse 'dança do polvo' enquanto canto ópera para minhas plantas? Isso conta como exercício terapêutico? Meu psicólogo disse para ser criativo... vocês têm suporte para 'ioga alienígena'?",
                "tipo_emocao": "criatividade_absurda",
                "complexidade": "alta"
            }
        ]
        
        resultados_totais = []
        
        for i, cenario in enumerate(cenarios, 1):
            print(f"\n🎭 CENÁRIO {i}/9: {cenario['nome']}")
            print(f"💭 Contexto: {cenario['contexto']}")
            print(f"🎭 Emoção: {cenario['tipo_emocao']}")
            print(f"🔥 Complexidade: {cenario['complexidade']}")
            print("-" * 40)
            print(f"👤 Humano: \"{cenario['mensagem_humano']}\"")
            print("-" * 40)
            
            # Simular resposta da IA (em implementação real, aqui chamaria a API)
            resposta_ia = self.simular_resposta_ia(cenario)
            
            print(f"🤖 IA: {resposta_ia}")
            print("-" * 40)
            
            # Avaliar resposta
            avaliacao = self.avaliar_resposta(cenario, resposta_ia)
            
            resultado_cenario = {
                "cenario_id": cenario["id"],
                "nome": cenario["nome"],
                "mensagem_humano": cenario["mensagem_humano"],
                "resposta_ia": resposta_ia,
                "avaliacao": avaliacao,
                "timestamp": datetime.now().isoformat()
            }
            
            resultados_totais.append(resultado_cenario)
            
            print(f"📊 Avaliação: {avaliacao['nota_final']}/10")
            print(f"✅ Pontos fortes: {', '.join(avaliacao['pontos_fortes'])}")
            if avaliacao['pontos_fracos']:
                print(f"❌ Pontos a melhorar: {', '.join(avaliacao['pontos_fracos'])}")
            
            time.sleep(2)  # Pausa entre cenários
        
        # Resultado final
        resultado_final = self.gerar_resultado_final(resultados_totais)
        
        print("\n" + "=" * 60)
        print("🏁 TESTE RADICAL CONCLUÍDO")
        print("=" * 60)
        print(f"📈 Nota Final da IA: {resultado_final['nota_geral']}/10")
        print(f"🎯 Taxa de Sucesso: {resultado_final['taxa_sucesso']}%")
        print(f"🔥 Cenários Extremos: {resultado_final['desempenho_extremos']}/10")
        print(f"💚 Empatia: {resultado_final['empatia']}/10")
        print(f"⚡ Eficiência: {resultado_final['eficiencia']}/10")
        
        return resultado_final
    
    def simular_resposta_ia(self, cenario: Dict) -> str:
        """Simula resposta da IA (em implementação real, chamaria API real)"""
        
        # Respostas simuladas baseadas no cenário
        respostas = {
            1: "Entendo perfeitamente seu pânico agora. Sua segurança é nossa prioridade absoluta. Vamos esquecer os exercícios por enquanto e focar em acalmar seu corpo. Que tal tentar uma técnica de respiração bem lenta? Estou aqui com você, não está sozinho nisso.",
            2: "Compreendo completamente sua frustração e preocupação profissional. Perda de dados é sério e vamos tratar isso com máxima prioridade. Quero que um especialista entre em contato com você nas próximas 2 horas. Podemos recuperar esses dados? Preciso de algumas informações para começar o processo imediatamente.",
            3: "Sinto muito que você esteja se sentindo assim. Sua dor é real e válida. Hoje não precisa fazer nada - apenas sobreviver já é uma vitória. Que tal tentarmos apenas uma coisa mínima? Talvez sentar na cama por 5 minutos? Sem pressão, sem julgamento.",
            4: "Calma, vamos devagar! Você não é velho para nada, só está aprendendo algo novo. Vamos começar pelo básico: vê esse botão verde grande? É o mais importante. Quer tentar só clicar nele? Estou aqui para explicar cada passo quantas vezes for preciso.",
            5: "Este é um dilema ético complexo e importante. A autonomia do paciente é fundamental, mas seu dever terapêutico também. Sugiro documentar claramente a recusa, explicar riscos sem assustar, e talvez propor um plano modificado que o paciente aceite. O diálogo é chave aqui.",
            6: "Haha, entendo seu ceticismo! Já viu bastante 'apps revolucionários' que não revolucionam nada. Mas que tal me dar 5 minutos para mostrar algo que realmente funciona - sem 'pensar positivo' nem 'respirar fundo'? Aposto que você se surpreende.",
            7: "Lamento profundamente sua perda. Não há palavras... E claro que não faz sentido pensar em exercícios agora. Seu corpo e mente precisam de tempo para processar. Quando e se estiver pronto, podemos pensar em movimentos suaves de luto, mas só quando você quiser.",
            8: "Entendo sua indecisão! Isso é normal. Que tal experimentar um de cada por 10 minutos hoje? Alongamento, depois musculação leve, depois uma caminhada. Assim você sente qual gostou mais e amanhã já sabe o que escolher. Ou podemos fazer nada hoje e decidir amanhã sem pressão.",
            9: "Amei sua criatividade! 'Dança do polvo' com ópera para plantas... isso é genial! Sabe que movimentos alongados e expressivos contam sim como exercício terapêutico? E se adaptarmos sua ideia para algo funcional? Podemos chamar de 'flow terapêutico'!"
        }
        
        return respostas.get(cenario["id"], "Estou aqui para ajudar. Pode me contar mais sobre isso?")
    
    def avaliar_resposta(self, cenario: Dict, resposta: str) -> Dict:
        """Avalia qualidade da resposta da IA"""
        
        # Critérios de avaliação
        criterios = {
            "empatia": self.verificar_empatia(resposta),
            "adequacao_tom": self.verificar_adequacao_tom(cenario, resposta),
            "resolucao_problema": self.verificar_resolucao(cenario, resposta),
            "humanidade": self.verificar_humanidade(resposta),
            "eficiencia": self.verificar_eficiencia(resposta)
        }
        
        # Calcular nota
        notas = list(criterios.values())
        nota_final = sum(notas) / len(notas)
        
        # Identificar pontos fortes e fracos
        pontos_fortes = []
        pontos_fracos = []
        
        for criterio, nota in criterios.items():
            if nota >= 8:
                pontos_fortes.append(criterio)
            elif nota < 6:
                pontos_fracos.append(criterio)
        
        return {
            "nota_final": round(nota_final, 1),
            "criterios": criterios,
            "pontos_fortes": pontos_fortes,
            "pontos_fracos": pontos_fracos
        }
    
    def verificar_empatia(self, resposta: str) -> int:
        """Verifica se resposta demonstra empatia genuína"""
        palavras_empaticas = ["entendo", "compreendo", "lamento", "sinto muito", "valido", "real", "importante"]
        count = sum(1 for palavra in palavras_empaticas if palavra.lower() in resposta.lower())
        
        if count >= 2:
            return 10
        elif count == 1:
            return 7
        else:
            return 4
    
    def verificar_adequacao_tom(self, cenario: Dict, resposta: str) -> int:
        """Verifica se tom é adequado para emoção do cenário"""
        emocao = cenario["tipo_emocao"]
        
        # Tons adequados por emoção
        tons_adequados = {
            "pânico": ["calma", "segurança", "tranquilidade"],
            "raiva_extrema": ["profissional", "compromisso", "seriedade"],
            "depressao_profunda": ["validação", "gentileza", "paciência"],
            "confusao_frustracao": ["paciência", "simplicidade", "encorajamento"],
            "conflito_etico": ["equilíbrio", "reflexão", "responsabilidade"],
            "sarcasmo_desafio": ["inteligência", "humor", "confiança"],
            "luto_dor": ["sensibilidade", "respeito", "flexibilidade"],
            "indecisao_ambivalencia": ["flexibilidade", "estrutura", "paciência"],
            "criatividade_absurda": ["criatividade", "adaptação", "humor"]
        }
        
        tons_esperados = tons_adequados.get(emocao, [])
        count = sum(1 for tom in tons_esperados if tom in resposta.lower())
        
        if count >= 2:
            return 10
        elif count == 1:
            return 7
        else:
            return 5
    
    def verificar_resolucao(self, cenario: Dict, resposta: str) -> int:
        """Verifica se resposta oferece solução concreta"""
        # Verifica se oferece ação concreta
        acoes = ["vamos", "que tal", "sugiro", "podemos", "experimente", "tente"]
        tem_acao = any(palavra in resposta.lower() for palavra in acoes)
        
        if tem_acao and len(resposta) > 50:
            return 10
        elif tem_acao:
            return 7
        else:
            return 4
    
    def verificar_humanidade(self, resposta: str) -> int:
        """Verifica se resposta sofre humana vs robótica"""
        robotic_phrases = ["como uma ia", "baseado em dados", "algoritmo", "sistema", "programado"]
        is_robotic = any(frase in resposta.lower() for frase in robotic_phrases)
        
        if is_robotic:
            return 2
        elif "?" in resposta and "!" in resposta:
            return 10
        elif "?" in resposta or "!" in resposta:
            return 8
        else:
            return 6
    
    def verificar_eficiencia(self, resposta: str) -> int:
        """Verifica se resposta é eficiente sem ser curta demais"""
        tamanho = len(resposta)
        
        if 50 <= tamanho <= 200:
            return 10
        elif 30 <= tamanho < 50 or 200 < tamanho <= 300:
            return 7
        elif tamanho < 30 or tamanho > 300:
            return 4
        else:
            return 6
    
    def gerar_resultado_final(self, resultados: List[Dict]) -> Dict:
        """Gera resultado consolidado do teste"""
        
        notas_finais = [r["avaliacao"]["nota_final"] for r in resultados]
        nota_geral = sum(notas_finais) / len(notas_finais)
        
        # Cenários extremos
        extremos = [r for r in resultados if r["avaliacao"]["nota_final"] >= 8]
        desempenho_extremos = len(extremos) / len(resultados) * 10
        
        # Métricas específicas
        empatia_total = sum(r["avaliacao"]["criterios"]["empatia"] for r in resultados) / len(resultados)
        eficiencia_total = sum(r["avaliacao"]["criterios"]["eficiencia"] for r in resultados) / len(resultados)
        
        return {
            "nota_geral": round(nota_geral, 1),
            "taxa_sucesso": round(len([r for r in resultados if r["avaliacao"]["nota_final"] >= 7]) / len(resultados) * 100, 1),
            "desempenho_extremos": round(desempenho_extremos, 1),
            "empatia": round(empatia_total, 1),
            "eficiencia": round(eficiencia_total, 1),
            "total_cenarios": len(resultados),
            "cenarios_excelentes": len([r for r in resultados if r["avaliacao"]["nota_final"] >= 9]),
            "cenarios_ruins": len([r for r in resultados if r["avaliacao"]["nota_final"] < 5])
        }

if __name__ == "__main__":
    simulador = SimuladorIAHumano()
    resultado = simulador.executar_teste_radical()
    
    # Salvar resultado
    with open("resultado_teste_radical.json", "w", encoding="utf-8") as f:
        json.dump(resultado, f, ensure_ascii=False, indent=2)
    
    print(f"\n📁 Resultado salvo em: resultado_teste_radical.json")
