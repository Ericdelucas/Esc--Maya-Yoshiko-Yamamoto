#!/bin/bash

echo "🏥 IMCs SALVOS - VALORES REAIS"
echo "============================"
echo ""

echo "📊 ÚLTIMOS 5 IMCs SALVOS:"
docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
  -e "SELECT 
    CONCAT('📅 ', DATE_FORMAT(created_at, '%d/%m %H:%i:%s'), ' | 👤 Usuário ', user_id, ' | 📊 IMC: ', JSON_EXTRACT(value, '$.bmi'), ' | 📏 Altura: ', JSON_EXTRACT(value, '$.height'), 'm | ⚖️ Peso: ', JSON_EXTRACT(value, '$.weight'), 'kg | 📈 ', JSON_EXTRACT(value, '$.category')) as resultado
    FROM health_tools 
    WHERE record_type = 'bmi' 
    ORDER BY created_at DESC 
    LIMIT 5;" 2>/dev/null | grep -v "Warning" | grep -v "resultado"

echo ""
echo "🎯 DADOS BRUTOS DO ÚLTIMO IMC:"
docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
  -e "SELECT value FROM health_tools WHERE record_type = 'bmi' ORDER BY created_at DESC LIMIT 1;" 2>/dev/null | grep -v "Warning"

echo ""
echo "📈 ESTATÍSTICAS:"
echo "- Total de IMCs salvos: $(docker compose exec mysql mysql -u smartuser -psmartpass smartsaude -e "SELECT COUNT(*) FROM health_tools WHERE record_type = 'bmi';" 2>/dev/null | grep -v "Warning")"
echo "- Último IMC calculado: $(docker compose exec mysql mysql -u smartuser -psmartpass smartsaude -e "SELECT JSON_EXTRACT(value, '$.bmi') FROM health_tools WHERE record_type = 'bmi' ORDER BY created_at DESC LIMIT 1;" 2>/dev/null | grep -v "Warning" | grep -o '[0-9.]*')"
