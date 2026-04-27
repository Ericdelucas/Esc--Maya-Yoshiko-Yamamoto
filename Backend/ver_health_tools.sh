#!/bin/bash

echo "🏥 FERRAMENTAS DE SAÚDE - DADOS SALVOS"
echo "====================================="
echo ""

echo "📋 ÚLTIMOS 5 QUESTIONÁRIOS:"
docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
  -e "SELECT 
    CONCAT('📅 ', DATE_FORMAT(created_at, '%d/%m %H:%i'), ' | 👤 Usuário ', user_id, ' | 🎯 Pontuação: ', total_score, '/', max_score, ' | ⚠️  Risco: ', risk_level) as resultado
    FROM health_questionnaires 
    ORDER BY created_at DESC 
    LIMIT 5;" 2>/dev/null | grep -v "Warning"

echo ""
echo "📊 RESPOSTAS DO ÚLTIMO REGISTRO:"
docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
  -e "SELECT answers FROM health_questionnaires ORDER BY created_at DESC LIMIT 1;" 2>/dev/null | grep -v "Warning" | tail -1 | python3 -c "
import sys, json
try:
    data = json.loads(sys.stdin.read())
    for k, v in data.items():
        icons = {'symptoms': '🤒', 'allergies': '🌸', 'meds': '💊', 'chronic': '🏥', 'surgery': '🔪', 'habits': '🛌'}
        icon = icons.get(k, '📝')
        print(f'  {icon} {k}: {v}')
except:
    pass
"

echo ""
echo "📈 ESTATÍSTICAS GERAIS:"
docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
  -e "SELECT 
    CONCAT('📝 Total: ', COUNT(*)),
    CONCAT('👥 Usuários: ', COUNT(DISTINCT user_id)),
    CONCAT('📊 Média: ', ROUND(AVG(total_score), 1))
    FROM health_questionnaires;" 2>/dev/null | grep -v "Warning"

echo ""
