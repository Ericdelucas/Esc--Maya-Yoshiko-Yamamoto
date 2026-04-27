#!/bin/bash

echo "🧪 TESTAR CÁLCULO DE IMC"
echo "======================"
echo ""

# Testar com os dados do erro
echo "📝 Enviando dados: altura=1.9m, peso=36.0kg"
response=$(curl -s -X POST "http://localhost:8080/health-tools/calculate-bmi-test" \
  -H "Content-Type: application/json" \
  -d '{
    "height": 1.9,
    "weight": 36.0
  }')

echo "📋 Resposta JSON:"
echo "$response" | jq .

echo ""
echo "🎯 Análise do resultado:"
echo "$response" | jq -r '
  if .success then
    "✅ Sucesso: " + .message + "\n" +
    "📊 IMC: " + (.data.bmi | tostring) + "\n" +
    "📈 Categoria: " + .data.category + "\n" +
    "📅 Salvo em: " + .data.created_at
  else
    "❌ Erro: " + (.detail // "Erro desconhecido")
  end
'

echo ""
echo "💾 Ver no banco:"
echo "IMCs salvos:"
docker compose exec mysql mysql -u smartuser -psmartpass smartsaude \
  -e "SELECT 
    CONCAT('📅 ', DATE_FORMAT(created_at, '%d/%m %H:%i'), ' | 👤 Usuário ', user_id, ' | 📊 IMC: ', JSON_EXTRACT(value, '$.bmi'), ' | 📈 ', JSON_EXTRACT(value, '$.category')) as resultado
    FROM health_tools 
    WHERE record_type = 'bmi' 
    ORDER BY created_at DESC 
    LIMIT 3;" 2>/dev/null | grep -v "Warning" | grep -v "resultado"
