#!/bin/bash

echo "📋 RESPOSTA JSON DO QUESTIONÁRIO:"
echo "=============================="

curl -s -X POST "http://localhost:8080/health-tools/save-questionnaire-test" \
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
  }' | jq .

echo ""
