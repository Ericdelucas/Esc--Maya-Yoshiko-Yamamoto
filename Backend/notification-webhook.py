#!/usr/bin/env python3
import requests
import json
import time

def send_direct_notification():
    """Enviar notificação diretamente sem frontend"""
    
    # Criar notificação no backend
    notification_data = {
        "user_id": 1,
        "channel": "push", 
        "title": "📱 NOTIFICAÇÃO DIRETA!",
        "message": "Enviada diretamente para você sem precisar de frontend!",
        "schedule_at_iso": None
    }
    
    try:
        # Enviar para notification-service
        response = requests.post(
            "http://localhost:8070/notifications",
            json=notification_data,
            timeout=5
        )
        
        if response.status_code == 200:
            result = response.json()
            print(f"✅ Notificação criada: {result}")
            
            # Mostrar detalhes
            print(f"📱 Título: {notification_data['title']}")
            print(f"📄 Mensagem: {notification_data['message']}")
            print(f"🆔 ID: {result.get('notification_id', 'N/A')}")
            print(f"📊 Status: {result.get('status', 'N/A')}")
            
            return True
        else:
            print(f"❌ Erro: {response.status_code} - {response.text}")
            return False
            
    except Exception as e:
        print(f"❌ Erro ao enviar notificação: {e}")
        return False

if __name__ == "__main__":
    print("🚀 Enviando notificação direta para seu celular...")
    print("=" * 50)
    
    success = send_direct_notification()
    
    print("=" * 50)
    if success:
        print("✅ Notificação enviada com sucesso!")
        print("📱 Verifique seu celular em alguns segundos!")
        print("🔔 Se não aparecer, o app Android precisa buscar as notificações.")
    else:
        print("❌ Falha ao enviar notificação.")
    
    print("\n📋 Notificações disponíveis:")
    print("🔍 Verifique: http://localhost:8080/notifications/pending-simple")
