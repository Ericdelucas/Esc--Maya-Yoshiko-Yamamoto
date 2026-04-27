#!/bin/bash

echo "🏥 IMC SALVO - NÚMEROS NO TERMINAL"
echo "================================="
echo ""

echo "🎯 ÚLTIMO IMC SALVO:"
docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
  -e "SELECT value FROM health_tools WHERE record_type = 'bmi' ORDER BY created_at DESC LIMIT 1;" | tail -1 | sed 's/^"//' | sed 's/"$//' | sed 's/\\//g' | python3 -c "
import sys, json
data = sys.stdin.read().strip()
try:
    parsed = json.loads(data)
    print('📊 IMC:', parsed['bmi'])
    print('📏 Altura:', parsed['height'], 'm')
    print('⚖️ Peso:', parsed['weight'], 'kg')
    print('📈 Categoria:', parsed['category'])
    print('')
    print('🎯 CÁLCULO: {:.1f} / ({:.2f}²) = {:.2f}'.format(parsed['weight'], parsed['height'], parsed['bmi']))
except Exception as e:
    print('Erro ao processar dados')
"

echo ""
echo "📊 ÚLTIMOS 3 IMCs:"
docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
  -e "SELECT 
    CONCAT('📅 ', DATE_FORMAT(created_at, '%d/%m %H:%i'), ' | 📊 IMC: ', 
           REPLACE(REPLACE(JSON_EXTRACT(value, '$.bmi'), '\"', ''), '\\\\', ''), 
           ' | 📏 ', REPLACE(REPLACE(JSON_EXTRACT(value, '$.height'), '\"', ''), '\\\\', ''), 'm | ⚖️ ', 
           REPLACE(REPLACE(JSON_EXTRACT(value, '$.weight'), '\"', ''), '\\\\', ''), 'kg') as resultado
    FROM health_tools 
    WHERE record_type = 'bmi' 
    ORDER BY created_at DESC 
    LIMIT 3;" 2>/dev/null | grep -v "Warning" | grep -v "resultado"

echo ""
echo "📈 TOTAL DE IMCs SALVOS: $(docker compose exec mysql mysql -u smartuser -psmartpass smartsaude -e "SELECT COUNT(*) FROM health_tools WHERE record_type = 'bmi';" 2>/dev/null | grep -v "Warning" | grep -o '[0-9]*')"
