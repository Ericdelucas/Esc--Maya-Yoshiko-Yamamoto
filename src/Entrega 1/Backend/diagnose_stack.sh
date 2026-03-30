#!/bin/bash
# Script de diagnóstico do stack mínimo SmartSaúde AI
# Execute: chmod +x diagnose_stack.sh && ./diagnose_stack.sh

echo "🔍 DIAGNÓSTICO DO STACK MÍNIMO SMARTSAÚDE AI"
echo "============================================"

# 1. Verificar containers existentes
echo ""
echo "📦 1. Verificando containers existentes..."
docker ps -a

# 2. Subir o stack mínimo
echo ""
echo "🚀 2. Subindo stack mínimo..."
echo "APP_FERNET_KEYS=hPThbQLqXC3VrbwLDvKCFP1EGOTGAxEkhpsFZzFAne0= docker compose -f docker-compose.minimal.yml up --build"
echo ""
echo "Execute o comando acima em outro terminal e espere o resultado."
echo "Depois pressione ENTER para continuar..."
read

# 3. Verificar estado após build
echo ""
echo "📊 3. Verificando estado após build..."
docker ps -a

# 4. Verificar logs de containers com problemas
echo ""
echo "📋 4. Verificando logs de containers com problemas..."

services=("smartsaude-auth" "smartsaude-exercise" "smartsaude-training" "smartsaude-ehr" "smartsaude-ai" "smartsaude-mysql")

for service in "${services[@]}"; do
    echo ""
    echo "--- LOGS DO $service ---"
    docker logs "$service" --tail 20
done

# 5. Testar health endpoints
echo ""
echo "🏥 5. Testando endpoints /health..."
python test_health_endpoints.py

# 6. Verificar se Swagger está acessível
echo ""
echo "📚 6. Verificando Swagger endpoints..."
echo "Teste manualmente:"
echo "http://localhost:8080/docs"
echo "http://localhost:8081/docs"
echo "http://localhost:8030/docs"
echo "http://localhost:8060/docs"
echo "http://localhost:8090/docs"

echo ""
echo "✅ DIAGNÓSTICO CONCLUÍDO!"
echo "Copie os resultados acima para análise."
