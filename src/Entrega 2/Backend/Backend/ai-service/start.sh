#!/bin/bash

# Configurar Ollama para usar localhost
export OLLAMA_HOST=0.0.0.0:11434

# Iniciar Ollama em background
echo "🚀 Iniciando Ollama..."
ollama serve &

# Esperar Ollama iniciar
echo "⏳ Esperando Ollama iniciar..."
sleep 15

# Baixar modelo se não existir
echo "📥 Verificando modelo Llama3..."
if ! ollama list 2>/dev/null | grep -q "llama3:8b"; then
    echo "📥 Baixando modelo Llama3:8b..."
    ollama pull llama3:8b || echo "⚠️ Falha ao baixar modelo, continuando mesmo assim"
else
    echo "✅ Modelo Llama3:8b já disponível"
fi

# Iniciar aplicação FastAPI
echo "🚀 Iniciando aplicação FastAPI..."
exec python -m uvicorn main:app --host 0.0.0.0 --port 8090
