from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.core.dependencies import get_session
from app.models.schemas.user_schema import UserOut
from app.core.dependencies import get_current_user
import requests

router = APIRouter(prefix="/notifications")

@router.get("/pending-simple")
def get_pending_notifications_simple(
    current_user: UserOut = Depends(get_current_user),
    db: Session = Depends(get_session)
):
    """Endpoint simples para frontend buscar notificações"""
    
    try:
        # Buscar notificações direto do banco
        from app.storage.database.db import get_db as get_db_local
        
        # Conectar ao banco de notificações
        import mysql.connector
        from mysql.connector import Error
        
        connection = None
        notifications = []
        
        try:
            connection = mysql.connector.connect(
                host='mysql',
                database='smartsaude',
                user='smartuser',
                password='smartpass'
            )
            
            if connection.is_connected():
                cursor = connection.cursor(dictionary=True)
                query = """
                SELECT id, title, message, created_at, status 
                FROM notifications 
                WHERE user_id = %s AND status = 'queued' 
                ORDER BY created_at DESC 
                LIMIT 5
                """
                cursor.execute(query, (current_user.id,))
                notifications = cursor.fetchall()
                
                # Marcar como lidas
                update_query = "UPDATE notifications SET status = 'sent' WHERE user_id = %s AND status = 'queued'"
                cursor.execute(update_query, (current_user.id,))
                connection.commit()
                
        except Error as e:
            print(f"❌ Erro ao buscar notificações: {e}")
            
        finally:
            if connection and connection.is_connected():
                cursor.close()
                connection.close()
        
        return {
            "success": True,
            "notifications": notifications,
            "count": len(notifications)
        }
        
    except Exception as e:
        print(f"❌ Erro geral: {e}")
        return {
            "success": False,
            "notifications": [],
            "count": 0
        }
