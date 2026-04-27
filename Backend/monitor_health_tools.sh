#!/bin/bash

echo "🔍 MONITOR DE FERRAMENTAS DE SAÚDE - TEMPO REAL"
echo "=========================================="
echo ""

while true; do
    echo "📅 $(date '+%Y-%m-%d %H:%M:%S')"
    echo ""
    
    echo "🎯 ÚLTIMOS QUESTIONÁRIOS SALVOS:"
    echo "--------------------------------"
    docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
      -e "SELECT 
        CONCAT('Usuário ID: ', user_id, ' | Pontuação: ', total_score, '/', max_score, ' | Risco: ', risk_level, ' | ', DATE_FORMAT(created_at, '%d/%m %H:%i')) as resultado 
        FROM health_questionnaires 
        ORDER BY created_at DESC 
        LIMIT 3;" 2>/dev/null | grep -v "Warning" | grep -v "resultado"
    
    echo ""
    echo "📋 DETALHES DO ÚLTIMO REGISTRO:"
    echo "--------------------------------"
    docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
      -e "SELECT answers FROM health_questionnaires ORDER BY created_at DESC LIMIT 1;" 2>/dev/null | grep -v "Warning" | tail -1 | python3 -c "
import sys, json
try:
    data = json.loads(sys.stdin.read())
    print('Respostas:')
    for k, v in data.items():
        print(f'  • {k}: {v}')
except:
    pass
"
    
    echo ""
    echo "📊 ESTATÍSTICAS:"
    echo "--------------------------------"
    docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
      -e "SELECT 
        COUNT(*) as total_questionarios,
        COUNT(DISTINCT user_id) as usuarios_unicos,
        AVG(total_score) as pontuacao_media
        FROM health_questionnaires;" 2>/dev/null | grep -v "Warning" | grep -v "total_questionarios"
    
    echo ""
    echo "⏳ Aguardando novas respostas... (Ctrl+C para sair)"
    echo "================================================"
    sleep 5
    clear
done
