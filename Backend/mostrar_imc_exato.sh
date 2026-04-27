#!/bin/bash

echo "🏥 ÚLTIMOS IMCs SALVOS - NÚMEROS EXATOS"
echo "===================================="
echo ""

echo "📊 IMCs mais recentes:"
docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
  -e "SELECT 
    created_at,
    JSON_EXTRACT(value, '$.bmi') as imc_valor,
    JSON_EXTRACT(value, '$.category') as categoria,
    JSON_EXTRACT(value, '$.height') as altura,
    JSON_EXTRACT(value, '$.weight') as peso
    FROM health_tools 
    WHERE record_type = 'bmi' 
    ORDER BY created_at DESC 
    LIMIT 5;" 2>/dev/null | grep -v "Warning"

echo ""
echo "📈 ÚLTIMOS 3 IMCs FORMATADOS:"
docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
  -e "SELECT 
    CONCAT('📅 ', DATE_FORMAT(created_at, '%d/%m %H:%i:%s'), ' | 📊 IMC: ', JSON_EXTRACT(value, '$.bmi'), ' | 📏 Altura: ', JSON_EXTRACT(value, '$.height'), 'm | ⚖️ Peso: ', JSON_EXTRACT(value, '$.weight'), 'kg | 📈 ', JSON_EXTRACT(value, '$.category')) as resultado
    FROM health_tools 
    WHERE record_type = 'bmi' 
    ORDER BY created_at DESC 
    LIMIT 3;" 2>/dev/null | grep -v "Warning"

echo ""
echo "🎯 TOTAL DE IMCs SALVOS: $(docker compose exec mysql mysql -u smartuser -psmartpass smartsaude -e "SELECT COUNT(*) FROM health_tools WHERE record_type = 'bmi';" 2>/dev/null | grep -v "Warning")"
