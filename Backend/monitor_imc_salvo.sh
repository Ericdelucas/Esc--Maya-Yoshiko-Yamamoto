#!/bin/bash

echo "🏥 MONITOR DE DADOS SALVOS - FERRAMENTAS DE SAÚDE"
echo "=============================================="
echo ""

while true; do
    echo "📅 $(date '+%Y-%m-%d %H:%M:%S')"
    echo ""
    
    echo "📊 ÚLTIMOS IMCs SALVOS:"
    echo "----------------------"
    docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
      -e "SELECT 
        CONCAT('📅 ', DATE_FORMAT(created_at, '%d/%m %H:%i'), ' | 👤 Usuário ', user_id, ' | 📊 IMC: ', JSON_EXTRACT(value, '$.bmi'), ' | 📈 ', JSON_EXTRACT(value, '$.category')) as resultado
        FROM health_tools 
        WHERE record_type = 'bmi' 
        ORDER BY created_at DESC 
        LIMIT 3;" 2>/dev/null | grep -v "Warning" | grep -v "resultado"
    
    echo ""
    echo "📋 ÚLTIMOS QUESTIONÁRIOS SALVOS:"
    echo "------------------------------"
    docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
      -e "SELECT 
        CONCAT('📅 ', DATE_FORMAT(created_at, '%d/%m %H:%i'), ' | 👤 Usuário ', user_id, ' | 🎯 Pontuação: ', total_score, '/', max_score, ' | ⚠️  Risco: ', risk_level) as resultado
        FROM health_questionnaires 
        ORDER BY created_at DESC 
        LIMIT 3;" 2>/dev/null | grep -v "Warning" | grep -v "resultado"
    
    echo ""
    echo "📈 RESPOSTAS DO ÚLTIMO QUESTIONÁRIO:"
    echo "----------------------------------"
    docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
      -e "SELECT answers FROM health_questionnaires ORDER BY created_at DESC LIMIT 1;" 2>/dev/null | grep -v "Warning" | tail -1 | python3 -c "
import sys, json
try:
    data = json.loads(sys.stdin.read())
    icons = {'symptoms': '🤒', 'allergies': '🌸', 'meds': '💊', 'chronic': '🏥', 'surgery': '🔪', 'habits': '🛌'}
    for k, v in data.items():
        icon = icons.get(k, '📝')
        print(f'  {icon} {k}: {v}')
except:
    pass
"
    
    echo ""
    echo "📊 ESTATÍSTICAS GERAIS:"
    echo "----------------------"
    docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
      -e "SELECT 
        CONCAT('📝 Questionários: ', COUNT(*)),
        CONCAT('📊 IMCs: ', (SELECT COUNT(*) FROM health_tools WHERE record_type = 'bmi')),
        CONCAT('👥 Usuários únicos: ', COUNT(DISTINCT user_id))
        FROM health_questionnaires;" 2>/dev/null | grep -v "Warning"
    
    echo ""
    echo "⏳ Aguardando novos dados... (Ctrl+C para sair)"
    echo "=============================================="
    sleep 3
    clear
done
