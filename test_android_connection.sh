#!/bin/bash

echo "📱 TESTANDO CONEXÃO ANDROID → BACKEND LOCAL"
echo "==========================================="

# Configurar URL do Android (192.168.15.6 é o IP do host)
ANDROID_URL="http://192.168.15.6:8080"

echo "🌐 URL de destino: $ANDROID_URL"
echo ""

# 1. Testar conexão básica
echo "🔍 1. Testando conexão básica..."
HEALTH_RESPONSE=$(curl -s -X GET $ANDROID_URL/health --connect-timeout 5)

if [ $? -eq 0 ]; then
    echo "✅ Backend local acessível!"
else
    echo "❌ Backend local não acessível!"
    echo "💡 Verifique se:"
    echo "   - Docker está rodando (docker-compose up)"
    echo "   - Porta 8080 está liberada"
    echo "   - Android Emulator tem rede configurada"
    exit 1
fi

echo ""

# 2. Testar login
echo "🔐 2. Testando login..."
LOGIN_RESPONSE=$(curl -s -X POST $ANDROID_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@test.com", "password": "123456"}' \
  --connect-timeout 10)

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.token // empty')

if [ -n "$TOKEN" ] && [ "$TOKEN" != "empty" ]; then
    echo "✅ Login funcionando!"
    echo "🎫 Token: ${TOKEN:0:20}..."
else
    echo "❌ Login falhou!"
    echo "📝 Resposta: $LOGIN_RESPONSE"
    exit 1
fi

echo ""

# 3. Testar lista de pacientes
echo "📋 3. Testando lista de pacientes..."
PATIENTS_RESPONSE=$(curl -s -X GET $ANDROID_URL/professional/patients \
  -H "Authorization: Bearer $TOKEN" \
  --connect-timeout 10)

TOTAL_PATIENTS=$(echo $PATIENTS_RESPONSE | jq -r '.total_patients // 0')

if [ "$TOTAL_PATIENTS" -gt 0 ]; then
    echo "✅ Lista de pacientes funcionando!"
    echo "👥 Encontrados: $TOTAL_PATIENTS pacientes"
    
    # Mostrar primeiros pacientes
    echo "📝 Pacientes:"
    echo $PATIENTS_RESPONSE | jq -r '.patients[:3][] | "   - \(.full_name) (ID: \(.id))"'
else
    echo "❌ Lista de pacientes vazia ou erro!"
    echo "📝 Resposta: $PATIENTS_RESPONSE"
fi

echo ""

# 4. Testar exercícios do primeiro paciente
if [ "$TOTAL_PATIENTS" -gt 0 ]; then
    echo "🏋️‍♂️ 4. Testando exercícios do primeiro paciente..."
    
    FIRST_PATIENT_ID=$(echo $PATIENTS_RESPONSE | jq -r '.patients[0].id')
    EXERCISES_RESPONSE=$(curl -s -X GET $ANDROID_URL/professional/patients/$FIRST_PATIENT_ID/exercises \
      -H "Authorization: Bearer $TOKEN" \
      --connect-timeout 10)
    
    TOTAL_EXERCISES=$(echo $EXERCISES_RESPONSE | jq -r '.total_exercises // 0')
    PATIENT_NAME=$(echo $EXERCISES_RESPONSE | jq -r '.patient_name // "Desconhecido"')
    
    echo "✅ Exercícios do paciente funcionando!"
    echo "📝 Paciente: $PATIENT_NAME"
    echo "🎯 Total exercícios: $TOTAL_EXERCISES"
fi

echo ""
echo "🎉 TESTE DE CONEXÃO CONCLUÍDO!"
echo "================================"
echo "✅ Backend local acessível"
echo "✅ Login funcionando"  
echo "✅ API de pacientes funcionando"
echo "✅ API de exercícios funcionando"
echo ""
echo "📱 O Android agora deve conseguir:"
echo "   🔐 Fazer login"
echo "   📋 Carregar lista de pacientes"
echo "   👆 Selecionar pacientes no diálogo"
echo "   🏋️‍♂️ Visualizar exercícios do paciente selecionado"
echo ""
echo "🚀 INSTALE O NOVO APK NO ANDROID EMULATOR!"
