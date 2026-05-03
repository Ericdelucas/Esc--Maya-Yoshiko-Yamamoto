#!/usr/bin/env python3
"""
Script para testar endpoints /health de todos os serviços.
Execute: python test_health_endpoints.py
"""

import requests
import json
from typing import Dict, List

def test_health_endpoint(service_name: str, url: str) -> Dict[str, str]:
    """Testa um endpoint /health específico."""
    try:
        response = requests.get(url, timeout=5)
        if response.status_code == 200:
            data = response.json()
            if data.get("status") == "ok":
                return {
                    "service": service_name,
                    "status": "✅ PASS",
                    "url": url,
                    "response": data,
                    "error": None
                }
            else:
                return {
                    "service": service_name,
                    "status": "❌ FAIL",
                    "url": url,
                    "response": data,
                    "error": f"Resposta inválida: {data}"
                }
        else:
            return {
                "service": service_name,
                "status": "❌ FAIL",
                "url": url,
                "response": None,
                "error": f"HTTP {response.status_code}"
            }
    except requests.exceptions.ConnectionError:
        return {
            "service": service_name,
            "status": "❌ FAIL",
            "url": url,
            "response": None,
            "error": "Conexão recusada"
        }
    except requests.exceptions.Timeout:
        return {
            "service": service_name,
            "status": "❌ FAIL",
            "url": url,
            "response": None,
            "error": "Timeout"
        }
    except Exception as e:
        return {
            "service": service_name,
            "status": "❌ FAIL",
            "url": url,
            "response": None,
            "error": str(e)
        }

def main():
    """Testa todos os endpoints /health."""
    print("🔍 Testando endpoints /health dos serviços SmartSaúde AI")
    print("=" * 60)
    
    services = [
        ("auth-service", "http://localhost:8080/health"),
        ("exercise-service", "http://localhost:8081/health"),
        ("training-service", "http://localhost:8030/health"),
        ("ehr-service", "http://localhost:8060/health"),
        ("ai-service", "http://localhost:8090/health"),
    ]
    
    results = []
    passed = 0
    failed = 0
    
    for service_name, url in services:
        result = test_health_endpoint(service_name, url)
        results.append(result)
        
        if result["status"] == "✅ PASS":
            passed += 1
        else:
            failed += 1
        
        print(f"{result['status']} {result['service']}")
        print(f"   URL: {result['url']}")
        if result["error"]:
            print(f"   Erro: {result['error']}")
        if result["response"]:
            print(f"   Response: {result['response']}")
        print()
    
    # Resumo
    print("=" * 60)
    print("📊 RESUMO")
    print(f"   ✅ Passaram: {passed}")
    print(f"   ❌ Falharam: {failed}")
    print(f"   📈 Total: {passed + failed}")
    
    if failed == 0:
        print("\n🎉 Todos os serviços estão saudáveis!")
        print("✅ Stack está pronto para Swagger e Postman!")
    else:
        print(f"\n⚠️  {failed} serviço(s) precisam de atenção!")
        print("❌ Verifique os containers antes de prosseguir.")
    
    # Salvar resultados em JSON
    with open("health_test_results.json", "w") as f:
        json.dump(results, f, indent=2)
    print("\n📄 Resultados salvos em: health_test_results.json")

if __name__ == "__main__":
    main()
