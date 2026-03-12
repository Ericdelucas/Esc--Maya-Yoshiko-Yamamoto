#!/usr/bin/env python3
"""
Script para iniciar todos os serviços do backend
"""
import subprocess
import sys
import time
import os
from pathlib import Path

def start_service(service_name, port, main_file="main.py"):
    """Inicia um serviço em background"""
    service_path = Path(service_name)
    if not service_path.exists():
        print(f"Servico {service_name} nao encontrado")
        return None
    
    print(f"Iniciando {service_name} na porta {port}...")
    
    try:
        # Inicia o processo em background
        process = subprocess.Popen([
            sys.executable, main_file
        ], 
        cwd=service_path,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
        )
        
        time.sleep(2)  # Espera um pouco para o serviço iniciar
        
        if process.poll() is None:
            print(f"{service_name} iniciado com PID {process.pid}")
            return process
        else:
            stdout, stderr = process.communicate()
            print(f"{service_name} falhou ao iniciar:")
            print(f"   STDOUT: {stdout}")
            print(f"   STDERR: {stderr}")
            return None
            
    except Exception as e:
        print(f"Erro ao iniciar {service_name}: {e}")
        return None

def main():
    """Função principal"""
    print("Iniciando todos os servicos do backend...")
    print("Pressione Ctrl+C para parar todos os servicos\n")
    
    # Lista de serviços para iniciar
    services = [
        ("auth-service", 8085),
        ("notification-service", 8070),
        ("ehr-service", 8061),
        ("analytics-service", 8050),
        ("exercise-service", 8082),
        ("training-service", 8030),
        ("ai-service", 8090)
    ]
    
    processes = []
    
    try:
        # Inicia cada serviço
        for service_name, port in services:
            process = start_service(service_name, port)
            if process:
                processes.append((service_name, port, process))
            
        print(f"\n{len(processes)} servicos iniciados com sucesso!")
        print("\nServicos rodando:")
        for service_name, port, _ in processes:
            print(f"   • {service_name}: http://localhost:{port}")
        
        print("\nEndpoints disponíveis:")
        print("   • Auth: http://localhost:8085/docs")
        print("   • Notifications: http://localhost:8070/docs")
        print("   • EHR: http://localhost:8061/docs")
        print("   • Analytics: http://localhost:8050/docs")
        print("   • Exercise: http://localhost:8082/docs")
        print("   • Training: http://localhost:8030/docs")
        print("   • AI: http://localhost:8090/docs")
        
        print("\nMantendo servicos ativos... (Ctrl+C para parar)")
        
        # Mantém os processos rodando
        while True:
            time.sleep(1)
            
            # Verifica se algum processo morreu
            for i, (service_name, port, process) in enumerate(processes):
                if process.poll() is not None:
                    print(f"AVISO: {service_name} parou inesperadamente!")
                    
    except KeyboardInterrupt:
        print("\n\nParando todos os servicos...")
        for service_name, _, process in processes:
            try:
                process.terminate()
                print(f"{service_name} parado")
            except:
                pass
        print("Todos os servicos foram parados")
    
    except Exception as e:
        print(f"\nErro inesperado: {e}")
        # Limpa processos
        for _, _, process in processes:
            try:
                process.terminate()
            except:
                pass

if __name__ == "__main__":
    main()
