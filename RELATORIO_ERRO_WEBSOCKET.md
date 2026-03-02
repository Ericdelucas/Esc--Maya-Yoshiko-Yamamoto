# 🚨 RELATÓRIO DE ERRO: PROBLEMA PERSISTENTE

## **Status: FALHA CRÍTICA**

### **O que foi testado:**
- ✅ Probe WebSocket: `ws://localhost:8090/ai/ws/probe` - FUNCIONA
- ❌ Pose WebSocket: `ws://localhost:8090/ai/pose/ws` - FALHA (403)
- ❌ Teste direto: `ws://localhost:8090/test-ws` - FALHA (403)

### **Problema identificado:**
Apenas endpoints definidos em routers funcionam. Endpoints definidos diretamente no app falham.

### **Causa provável:**
Router registration order ou middleware interference.

### **Próximo passo:**
Verificar ordem de inclusão dos routers e possíveis conflitos de prefixo.

## **Conclusão:**
Problema mais complexo que dependências OpenCV. Issue está na arquitetura de registro de rotas WebSocket.
