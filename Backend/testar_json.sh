#!/bin/bash

echo "🧪 TESTAR QUESTIONÁRIO COM RESPOSTA JSON"
echo "======================================"
echo ""

# Testar com diferentes respostas
echo "📝 Enviando questionário..."
response=$(curl -s -X POST "http://localhost:8080/health-tools/save-questionnaire-test" \
  -H "Content-Type: application/json" \
  -d '{
    "answers": [
      {"question_id": "symptoms", "answer": "no"},
      {"question_id": "allergies", "answer": "no"},
      {"question_id": "meds", "answer": "no"},
      {"question_id": "chronic", "answer": "no"},
      {"question_id": "surgery", "answer": "no"},
      {"question_id": "habits", "answer": "excellent"}
    ]
  }')

echo "📋 Resposta JSON:"
echo "$response" | jq .

echo ""
echo "🎯 Análise da resposta:"
echo "$response" | jq -r '
  if .success then
    "✅ Sucesso: " + .message + "\n" +
    "📊 Pontuação: " + (.data.total_score | tostring) + "/" + (.data.max_score | tostring) + "\n" +
    "⚠️  Risco: " + .data.risk_level + "\n" +
    "📅 Salvo em: " + .data.created_at
  else
    "❌ Erro: " + (.detail // "Erro desconhecido")
  end
'

echo ""
echo "💾 Ver no banco:"
./ver_health_tools.sh | tail -10
