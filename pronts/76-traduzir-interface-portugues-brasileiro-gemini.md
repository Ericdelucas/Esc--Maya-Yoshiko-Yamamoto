# # **TRADUZIR E ADAPTAR INTERFACE PARA PORTUGUÊS BRASILEIRO - PARA O GEMINI**

## # **OBJETIVO:**
Traduzir toda a interface do aplicativo para português brasileiro, usando terminologia médica profissional e termos que fazem sentido para o contexto de saúde no Brasil.

## # **TRADUÇÕES E ADAPTAÇÕES EXIGIDAS:**

### # **1. STRINGS PRINCIPAIS - strings.xml**
```xml
<!-- # SUBSTITUIR ou ADICIONAR em res/values/strings.xml: -->

<!-- # Títulos e Navegação -->
<string name="app_name">SmartSaúde</string>
<string name="relatorios_pacientes">Relatórios de Pacientes</string>
<string name="criar_relatorio">Novo Relatório</string>
<string name="detalhes_relatorio">Detalhes do Relatório</string>
<string name="editar_relatorio">Editar Relatório</string>
<string name="lista_relatorios">Meus Relatórios</string>
<string name="estatisticas">Estatísticas</string>

<!-- # Campos do Relatório -->
<string name="titulo_relatorio">Título do Relatório</string>
<string name="tipo_relatorio">Tipo de Relatório</string>
<string name="data_relatorio">Data do Relatório</string>
<string name="conteudo_principal">Conteúdo Principal</string>
<string name="evolucao_clinica">Evolução Clínica</string>
<string name="dados_objetivos">Dados Objetivos</string>
<string name="dados_subjetivos">Dados Subjetivos</string>
<string name="plano_tratamento">Plano de Tratamento</string>
<string name="recomendacoes">Recomendações</string>
<string name="proximos_passos">Próximos Passos</string>
<string name="escala_dor">Escala de Dor</string>
<string name="status_funcional">Status Funcional</string>

<!-- # Tipos de Relatório (Português Brasileiro) -->
<string name="evolucao">Evolução</string>
<string name="avaliacao">Avaliação</string>
<string name="alta">Alta</string>
<string name="progresso">Progresso</string>
<string name="anamnese">Anamnese</string>
<string name="reavaliacao">Reavaliação</string>

<!-- # Status Funcional (Terminologia Brasileira) -->
<string name="excelente">Excelente</string>
<string name="bom">Bom</string>
<string name="regular">Regular</string>
<string name="ruim">Ruim</string>
<string name="critico">Crítico</string>

<!-- # Botões e Ações -->
<string name="salvar">Salvar</string>
<string name="cancelar">Cancelar</string>
<string name="excluir">Excluir</string>
<string name="editar">Editar</string>
<string name="voltar">Voltar</string>
<string name="confirmar">Confirmar</string>
<string name="continuar">Continuar</string>

<!-- # Mensagens e Feedback -->
<string name="relatorio_criado_sucesso">Relatório criado com sucesso!</string>
<string name="relatorio_atualizado_sucesso">Relatório atualizado com sucesso!</string>
<string name="relatorio_excluido_sucesso">Relatório excluído com sucesso!</string>
<string name="erro_criar_relatorio">Erro ao criar relatório</string>
<string name="erro_atualizar_relatorio">Erro ao atualizar relatório</string>
<string name="erro_excluir_relatorio">Erro ao excluir relatório</string>
<string name="erro_conexao">Erro de conexão</string>
<string name="erro_carregar">Erro ao carregar dados</string>

<!-- # Diálogos e Confirmações -->
<string name="confirmar_excluir">Tem certeza que deseja excluir este relatório?</string>
<string name="confirmar_excluir_titulo">Excluir Relatório</string>
<string name="confirmar_excluir_mensagem">Esta ação não poderá ser desfeita. Deseja continuar?</string>
<string name="sim">Sim</string>
<string name="nao">Não</string>

<!-- # Validações -->
<string name="campo_obrigatorio">Campo obrigatório</string>
<string name="titulo_obrigatorio">Título é obrigatório</string>
<string name="paciente_obrigatorio">Selecione um paciente</string>
<string name="tipo_obrigatorio">Selecione o tipo de relatório</string>
<string name="data_obrigatoria">Data é obrigatória</string>

<!-- # Informações do Paciente -->
<string name="paciente">Paciente</string>
<string name="paciente_id">ID do Paciente</string>
<string name="profissional">Profissional</string>
<string name="profissional_id">ID do Profissional</string>
<string name="criado_em">Criado em</string>
<string name="atualizado_em">Atualizado em</string>
<string name="relatorio_numero">Relatório #%d</string>

<!-- # Estatísticas e Dashboard -->
<string name="total_relatorios">Total de Relatórios</string>
<string name="relatorios_mes">Relatórios este mês</string>
<string name="media_dor">Média de Dor</string>
<string name="relatorios_recentes">Relatórios Recentes</string>
<string name="nenhum_relatorio">Nenhum relatório encontrado</string>
<string name="sem_relatorios">Você ainda não tem relatórios</string>

<!-- # Termos Médicos (Português Brasileiro) -->
<string name="avaliacao_dor">Avaliação da Dor</string>
<string name="intensidade_dor">Intensidade da Dor</string>
<string name="escala_eva">Escala EVA (0-10)</string>
<string name="funcionalidade">Funcionalidade</string>
<string name="mobilidade">Mobilidade</string>
<string name="forca_muscular">Força Muscular</string>
<string name="amplitude_movimento">Amplitude de Movimento</string>
<string name="limitacoes">Limitações</string>
<string name="conquistas">Conquistas</string>
<string name="evolucao_paciente">Evolução do Paciente</string>

<!-- # Estados Vazios -->
<string name="estado_vazio_titulo">Nenhum relatório encontrado</string>
<string name="estado_vazio_mensagem">Comece criando seu primeiro relatório de paciente</string>
<string name="estado_vazio_botao">Criar Relatório</string>
<string name="carregando">Carregando...</string>
<string name="aguarde">Aguarde...</string>

<!-- # Erros Específicos -->
<string name="erro_validacao">Erro de validação: %s</string>
<string name="erro_servidor">Erro no servidor. Tente novamente.</string>
<string name="erro_rede">Sem conexão com a internet</string>
<string name="erro_timeout">Tempo esgotado. Verifique sua conexão.</string>
<string name="erro_desconhecido">Ocorreu um erro inesperado</string>
</string>

<!-- # Arrays para Spinners -->
<string-array name="tipos_relatorio_array">
    <item>Selecione o tipo...</item>
    <item>Evolução</item>
    <item>Avaliação</item>
    <item>Alta</item>
    <item>Progresso</item>
    <item>Anamnese</item>
    <item>Reavaliação</item>
</string-array>

<string-array name="status_funcional_array">
    <item>Selecione...</item>
    <item>Excelente</item>
    <item>Bom</item>
    <item>Regular</item>
    <item>Ruim</item>
    <item>Critico</item>
</string-array>

<string-array name="escala_dor_array">
    <item>0 - Sem dor</item>
    <item>1 - Dor mínima</item>
    <item>2 - Dor leve</item>
    <item>3 - Dor moderada</item>
    <item>4 - Dor moderada</item>
    <item>5 - Dor moderada</item>
    <item>6 - Dor forte</item>
    <item>7 - Dor forte</item>
    <item>8 - Dor muito forte</item>
    <item>9 - Dor muito forte</item>
    <item>10 - Dor insuportável</item>
</string-array>
```

### # **2. ADAPTAÇÕES NOS LAYOUTS**

#### # **2.1 item_report.xml - Terminologia Médica**
```xml
<!-- # SUBSTITUIR textos em item_report.xml: -->

<TextView
    android:id="@+id/tvType"
    android:text="Evolução"  <!-- # Em vez de "EVOLUTION" -->
    android:text="@string/evolucao"  <!-- # Usar string resource -->
    android:background="@drawable/rounded_background_evolution"  <!-- # Cores diferentes por tipo -->
    android:textColor="@color/white"/>

<TextView
    android:id="@+id/tvPatient"
    android:text="Paciente: João Silva"  <!-- # Em vez de "Paciente ID: 1" -->
    android:drawableStart="@drawable/ic_person"  <!-- # Ícone mais apropriado -->
    android:drawablePadding="4dp"/>

<TextView
    android:id="@+id/tvPainScale"
    android:text="Dor: 4/10"  <!-- # Em vez de "Dor: 4/10" -->
    android:text="@string/avaliacao_dor_formatada"  <!-- # String com placeholder -->
    android:drawableStart="@drawable/ic_pain_scale"  <!-- # Ícone de dor -->
    android:drawablePadding="4dp"/>

<TextView
    android:id="@+id/tvActionText"
    android:text="Ver detalhes"  <!-- # Em vez de "Ver detalhes" -->
    android:text="@string/ver_detalhes"/>
```

#### # **2.2 activity_create_report.xml - Labels Brasileiros**
```xml
<!-- # ATUALIZAR hints e labels: -->

<com.google.android.material.textfield.TextInputLayout
    android:hint="@string/titulo_relatorio"
    android:label="@string/titulo_relatorio">

<com.google.android.material.textfield.TextInputLayout
    android:hint="@string/conteudo_principal"
    android:label="@string/conteudo_principal">

<com.google.android.material.textfield.TextInputLayout
    android:hint="@string/evolucao_clinica"
    android:label="@string/evolucao_clinica">

<com.google.android.material.textfield.TextInputLayout
    android:hint="@string/dados_objetivos"
    android:label="@string/dados_objetivos">

<com.google.android.material.textfield.TextInputLayout
    android:hint="@string/dados_subjetivos"
    android:label="@string/dados_subjetivos">

<com.google.android.material.textfield.TextInputLayout
    android:hint="@string/plano_tratamento"
    android:label="@string/plano_tratamento">

<com.google.android.material.textfield.TextInputLayout
    android:hint="@string/recomendacoes"
    android:label="@string/recomendacoes">

<com.google.android.material.textfield.TextInputLayout
    android:hint="@string/proximos_passos"
    android:label="@string/proximos_passos">

<!-- # Escala de Dor com terminologia brasileira -->
<TextView
    android:text="@string/escala_eva"
    android:textAppearance="@style/TextAppearance.Material3.BodyLarge"
    android:textStyle="bold"/>

<!-- # Status Funcional -->
<TextView
    android:text="@string/status_funcional"
    android:textAppearance="@style/TextAppearance.Material3.BodyLarge"
    android:textStyle="bold"/>
```

### # **3. ADAPTAÇÕES NO CÓDIGO JAVA**

#### # **3.1 CreateReportActivity.java - Mensagens Brasileiras**
```java
// # SUBSTITUIR mensagens de erro e sucesso:

// # Validações:
if (editTitle.getText().toString().trim().isEmpty()) {
    editTitle.setError(getString(R.string.titulo_obrigatorio));
    return;
}

// # Feedback de sucesso:
Toast.makeText(this, getString(R.string.relatorio_criado_sucesso), Toast.LENGTH_SHORT).show();

// # Tratamento de erros específicos:
if (response.code() == 422) {
    String errorBody = response.errorBody().string();
    errorMessage = getString(R.string.erro_validacao, errorBody);
} else if (response.code() == 403) {
    errorMessage = "Sem permissão para criar relatório";
} else {
    errorMessage = getString(R.string.erro_criar_relatorio);
}

// # Erro de conexão:
@Override
public void onFailure(Call<PatientReport> call, Throwable t) {
    String errorMessage = getString(R.string.erro_conexao) + ": " + t.getMessage();
    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
}
```

#### # **3.2 ReportListFragment.java - Diálogos Brasileiros**
```java
// # Diálogo de exclusão com português brasileiro:
private void onReportLongClick(PatientReport report) {
    new AlertDialog.Builder(getContext())
        .setTitle(getString(R.string.confirmar_excluir_titulo))
        .setMessage(getString(R.string.confirmar_excluir_mensagem) + "\n\n\"" + report.getTitle() + "\"")
        .setPositiveButton(getString(R.string.sim), (dialog, which) -> deleteReport(report))
        .setNegativeButton(getString(R.string.nao), null)
        .show();
}

// # Mensagens de feedback:
Toast.makeText(getContext(), getString(R.string.relatorio_excluido_sucesso), Toast.LENGTH_SHORT).show();
Toast.makeText(getContext(), getString(R.string.erro_excluir_relatorio), Toast.LENGTH_SHORT).show();
```

#### # **3.3 ReportDetailActivity.java - Interface Brasileira**
```java
// # Labels e informações:
tvReportId.setText(getString(R.string.relatorio_numero, currentReport.getId()));
tvPatientId.setText(getString(R.string.paciente) + ": " + currentReport.getPatientId());
tvCreatedAt.setText(getString(R.string.criado_em) + ": " + formatDateTime(currentReport.getCreatedAt()));
tvUpdatedAt.setText(getString(R.string.atualizado_em) + ": " + formatDateTime(currentReport.getUpdatedAt()));

// # Mensagens:
Toast.makeText(this, getString(R.string.relatorio_atualizado_sucesso), Toast.LENGTH_SHORT).show();
Toast.makeText(this, getString(R.string.erro_atualizar_relatorio), Toast.LENGTH_SHORT).show();

// # Validação:
if (editTitle.getText().toString().trim().isEmpty()) {
    editTitle.setError(getString(R.string.titulo_obrigatorio));
    return;
}
```

### # **4. CORES E ESTILOS POR TIPO DE RELATÓRIO**

#### # **4.1 Criar Cores Diferenciadas**
```xml
<!-- # EM res/values/colors.xml: -->

<!-- # Cores para tipos de relatório -->
<color name="evolution_primary">#2196F3</color>  <!-- # Azul para Evolução -->
<color name="avaliacao_primary">#4CAF50</color>  <!-- # Verde para Avaliação -->
<color name="alta_primary">#FF9800</color>       <!-- # Laranja para Alta -->
<color name="progresso_primary">#9C27B0</color>   <!-- # Roxo para Progresso -->
<color name="anamnese_primary">#607D8B</color>     <!-- # Azul cinza para Anamnese -->

<!-- # Cores para status funcional -->
<color name="excelente">#4CAF50</color>           <!-- # Verde -->
<color name="bom">#8BC34A</color>                 <!-- # Verde claro -->
<color name="regular">#FFC107</color>             <!-- # Amarelo -->
<color name="ruim">#FF5722</color>               <!-- # Laranja -->
<color name="critico">#F44336</color>             <!-- # Vermelho -->
```

#### # **4.2 Drawables para Tipos de Relatório**
```xml
<!-- # CRIAR res/drawable/rounded_background_evolution.xml: -->
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@color/evolution_primary"/>
    <corners android:radius="12dp"/>
</shape>

<!-- # CRIAR res/drawable/rounded_background_avaliacao.xml: -->
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@color/avaliacao_primary"/>
    <corners android:radius="12dp"/>
</shape>

<!-- # ... outros drawables para cada tipo -->
```

#### # **4.3 Adapter com Cores Dinâmicas**
```java
// # EM ReportAdapter.java - método para obter cor por tipo:
private int getColorForReportType(String reportType) {
    Context context = holder.itemView.getContext();
    switch (reportType) {
        case "Evolução":
            return ContextCompat.getColor(context, R.color.evolution_primary);
        case "Avaliação":
            return ContextCompat.getColor(context, R.color.avaliacao_primary);
        case "Alta":
            return ContextCompat.getColor(context, R.color.alta_primary);
        case "Progresso":
            return ContextCompat.getColor(context, R.color.progresso_primary);
        case "Anamnese":
            return ContextCompat.getColor(context, R.color.anamnese_primary);
        default:
            return ContextCompat.getColor(context, R.color.primary);
    }
}

// # Aplicar cor no onBindViewHolder:
holder.tvType.setTextColor(getColorForReportType(report.getReportType()));
```

### # **5. FORMATAÇÃO DE DATAS E HORAS BRASILEIRAS**

#### # **5.1 Formato Brasileiro**
```java
// # ADICIONAR método utilitário para formatação brasileira:
private String formatDateTimeBrazilian(String dateTime) {
    if (dateTime == null) return "Não informado";
    
    try {
        // # Parse do formato ISO
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Date date = isoFormat.parse(dateTime);
        
        // # Formato brasileiro
        SimpleDateFormat brazilianFormat = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault());
        return brazilianFormat.format(date);
    } catch (Exception e) {
        return dateTime; // # Fallback
    }
}

// # Usar em todos os lugares que exibem data:
tvCreatedAt.setText(getString(R.string.criado_em) + ": " + formatDateTimeBrazilian(currentReport.getCreatedAt()));
tvUpdatedAt.setText(getString(R.string.atualizado_em) + ": " + formatDateTimeBrazilian(currentReport.getUpdatedAt()));
```

### # **6. ÍCONES APROPRIADOS**

#### # **6.1 Ícones Médicos Brasileiros**
```xml
<!-- # EM item_report.xml - usar ícones mais apropriados: -->

<TextView
    android:id="@+id/tvDate"
    android:drawableStart="@drawable/ic_calendar"  <!-- # Calendário -->
    android:drawablePadding="4dp"/>

<TextView
    android:id="@+id/tvPatient"
    android:drawableStart="@drawable/ic_person"  <!-- # Pessoa -->
    android:drawablePadding="4dp"/>

<TextView
    android:id="@+id/tvPainScale"
    android:drawableStart="@drawable/ic_heart"    <!-- # Coração/Dor -->
    android:drawablePadding="4dp"/>
```

### # **7. TERMINOLOGIA MÉDICA BRASILEIRA**

#### # **7.1 Sugestões de Termos Médicos**
```java
// # Mapeamento de termos para português brasileiro:
private Map<String, String> getMedicalTermsMapping() {
    Map<String, String> mapping = new HashMap<>();
    mapping.put("EVOLUTION", "Evolução");
    mapping.put("ASSESSMENT", "Avaliação");
    mapping.put("DISCHARGE", "Alta");
    mapping.put("PROGRESS", "Progresso");
    mapping.put("ANAMNESIS", "Anamnese");
    mapping.put("REASSESSMENT", "Reavaliação");
    
    mapping.put("EXCELLENT", "Excelente");
    mapping.put("GOOD", "Bom");
    mapping.put("FAIR", "Regular");
    mapping.put("POOR", "Ruim");
    mapping.put("CRITICAL", "Crítico");
    
    return mapping;
}

// # Usar para traduzir termos da API:
private String translateMedicalTerm(String englishTerm) {
    Map<String, String> mapping = getMedicalTermsMapping();
    return mapping.getOrDefault(englishTerm.toUpperCase(), englishTerm);
}
```

### # **8. TESTES DE TRADUÇÃO**

#### # **8.1 Verificar Todas as Telas**
```java
// # CHECKLIST DE TRADUÇÃO:

// # 1. Tela Principal:
// # - "Relatórios de Pacientes" em vez de "Patient Reports"
// # - "Estatísticas" em vez de "Statistics"
// # - "Nenhum relatório encontrado" em vez de "No reports found"

// # 2. Tela de Criar Relatório:
// # - "Novo Relatório" em vez de "Create Report"
// # - "Evolução Clínica" em vez de "Clinical Evolution"
// # - "Plano de Tratamento" em vez de "Treatment Plan"
// # - "Escala EVA (0-10)" em vez de "Pain Scale (0-10)"

// # 3. Tela de Detalhes:
// # - "Detalhes do Relatório" em vez de "Report Details"
// # - "Criado em" em vez de "Created at"
// # - "Atualizado em" em vez de "Updated at"

// # 4. Diálogos:
// # - "Tem certeza que deseja excluir?" em vez de "Are you sure?"
// # - "Sim" / "Não" em vez de "Yes" / "No"

// # 5. Mensagens de Erro:
// # - "Erro de conexão" em vez de "Connection error"
// # - "Campo obrigatório" em vez de "Field required"
```

## # **IMPLEMENTAÇÃO PASSO A PASSO:**

### # **PASSO 1: Criar strings.xml brasileiro**
1. # **Copiar** todas as strings acima para `res/values/strings.xml`
2. # **Verificar** se não há strings duplicadas
3. # **Testar** compilação

### # **PASSO 2: Atualizar layouts**
1. # **Substituir** textos hardcoded por `@string/`
2. # **Atualizar** hints e labels para português
3. # **Adicionar** ícones apropriados

### # **PASSO 3: Adaptar código Java**
1. # **Substituir** strings hardcoded por `getString(R.string.*)`
2. # **Implementar** formatação de data brasileira
3. # **Adicionar** tradução de termos médicos

### # **PASSO 4: Implementar cores e estilos**
1. # **Criar** cores diferenciadas por tipo
2. # **Criar** drawables para badges
3. # **Implementar** lógica de cores dinâmicas

### # **PASSO 5: Testar tudo**
1. # **Abrir** todas as telas e verificar textos
2. # **Testar** criação, edição e exclusão
3. # **Verificar** mensagens de erro e sucesso
4. # **Validar** formatação de datas

## # **RESULTADO ESPERADO:**

### # **Interface 100% Brasileira:**
- # **Títulos:** "Relatórios de Pacientes", "Novo Relatório"
- # **Campos:** "Evolução Clínica", "Plano de Tratamento"
- # **Mensagens:** "Relatório criado com sucesso!"
- # **Datas:** "21/04/2026 às 15:30"
- # **Diálogos:** "Tem certeza que deseja excluir?"

### # **Terminologia Médica Apropriada:**
- # **Escala EVA** em vez de "Pain Scale"
- # **Status Funcional** em vez de "Functional Status"
- # **Alta** em vez de "Discharge"
- # **Anamnese** em vez de "Anamnesis"

### # **Cores e Ícones Profissionais:**
- # **Azul** para Evolução
- # **Verde** para Avaliação
- # **Laranja** para Alta
- # **Ícones** médicos apropriados

---

## # **IMPORTANTE PARA O GEMINI:**

**Implemente todas as traduções e adaptações acima para tornar o aplicativo 100% brasileiro e profissional. Use terminologia médica adequada e formatação brasileira de datas e números.**

**O objetivo é que o aplicativo pareça ter sido desenvolvido no Brasil para profissionais de saúde brasileiros.**
