        raise HTTPException(status_code=500, detail=f"Erro ao calcular IMC: {str(e)}")
             
@router.post("/calculate-bmi-test-query")
                         def calculate_bmi_test_query(