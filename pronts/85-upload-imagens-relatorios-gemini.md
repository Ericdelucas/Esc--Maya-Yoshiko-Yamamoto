# # **UPLOAD DE MÚLTIPLAS IMAGENS EM RELATÓRIOS - GEMINI**

## # **FUNCIONALIDADE IMPLEMENTADA:**

### # **O que foi adicionado:**
- # **Upload de múltiplas imagens** (até 10 por vez)
- # **Suporte para documentos** (PDF, Word, Excel)
- # **Validação de tipos** e tamanho de arquivos
- # **Download e exclusão** de anexos
- # **Integração completa** com relatórios existentes

## # **ENDPOINTS DISPONÍVEIS:**

### # **1. Upload de Imagens:**
```bash
POST /reports/{report_id}/attachments
Content-Type: multipart/form-data

# # Parâmetros:
- files: List[UploadFile] (múltiplos arquivos)
- description: Optional[str] (descrição dos anexos)

# # Exemplo:
curl -X POST "http://localhost:8080/reports/123/attachments" \
  -F "files=@imagem1.jpg" \
  -F "files=@documento.pdf" \
  -F "description=Exames do paciente"
```

### # **2. Listar Anexos:**
```bash
GET /reports/{report_id}/attachments

# # Resposta:
{
  "attachments": [
    {
      "id": 1,
      "report_id": 123,
      "attachment_type": "image",
      "file_name": "exame_raio_x.jpg",
      "file_path": "uploads/reports/report_123_...",
      "file_size": 2048576,
      "description": "Exame de raio-x",
      "uploaded_at": "2026-04-22T12:00:00"
    }
  ],
  "total": 1
}
```

### # **3. Download de Anexo:**
```bash
GET /reports/{report_id}/attachments/{attachment_id}/download

# # Retorna o arquivo para download
```

### # **4. Excluir Anexo:**
```bash
DELETE /reports/{report_id}/attachments/{attachment_id}

# # Resposta:
{"message": "Anexo excluído com sucesso"}
```

### # **5. Relatório Completo com Anexos:**
```bash
GET /reports/{report_id}/with-attachments

# # Resposta:
{
  "id": 123,
  "patient_id": 456,
  "professional_id": 789,
  "title": "Relatório de Consulta",
  "content": "...",
  "attachments": [
    {
      "id": 1,
      "attachment_type": "image",
      "file_name": "exame.jpg",
      "file_size": 2048576,
      "description": "Exame do paciente"
    }
  ],
  "created_at": "2026-04-22T12:00:00"
}
```

## # **VALIDAÇÕES IMPLEMENTADAS:**

### # **Tipos de Arquivos Permitidos:**
#### # **Imagens:**
- # JPEG/JPG
- # PNG
- # GIF
- # WebP
- # BMP
- # TIFF

#### # **Documentos:**
- # PDF
- # Word (.doc, .docx)
- # Excel (.xls, .xlsx)
- # Texto (.txt)
- # CSV

### # **Limites:**
- # **Tamanho máximo:** 10MB por arquivo
- # **Quantidade máxima:** 10 arquivos por upload
- # **Nome único:** Gerado automaticamente com timestamp

## # **IMPLEMENTAÇÃO FRONTEND - GEMINI:**

### # **1. Upload de Múltiplas Imagens:**

```java
// ReportAttachmentsActivity.java
public class ReportAttachmentsActivity extends AppCompatActivity {
    
    private static final int PICK_IMAGES_REQUEST = 100;
    private List<Uri> selectedImages = new ArrayList<>();
    private RecyclerView recyclerView;
    private AttachmentsAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_attachments);
        
        // Configurar RecyclerView
        recyclerView = findViewById(R.id.attachments_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttachmentsAdapter(selectedImages, this::removeImage);
        recyclerView.setAdapter(adapter);
        
        // Botão de upload
        Button btnUpload = findViewById(R.id.btn_upload_images);
        btnUpload.setOnClickListener(v -> openImagePicker());
        
        // Botão salvar
        Button btnSave = findViewById(R.id.btn_save_attachments);
        btnSave.setOnClickListener(v -> uploadImages());
    }
    
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Selecione imagens"), PICK_IMAGES_REQUEST);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                // Múltiplas imagens
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    if (selectedImages.size() < 10) {
                        selectedImages.add(imageUri);
                    }
                }
            } else if (data.getData() != null) {
                // Única imagem
                Uri imageUri = data.getData();
                if (selectedImages.size() < 10) {
                    selectedImages.add(imageUri);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }
    
    private void uploadImages() {
        if (selectedImages.isEmpty()) {
            Toast.makeText(this, "Selecione pelo menos uma imagem", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int reportId = getIntent().getIntExtra("report_id", 0);
        if (reportId == 0) {
            Toast.makeText(this, "Relatório não encontrado", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Criar multipart request
        MultipartBody.Builder builder = new MultipartBody.Builder()
            .setType(MultipartBody.FORM);
        
        // Adicionar imagens
        for (int i = 0; i < selectedImages.size(); i++) {
            Uri imageUri = selectedImages.get(i);
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                byte[] imageBytes = getBytesFromInputStream(inputStream);
                
                RequestBody imageBody = RequestBody.create(
                    imageBytes, 
                    MediaType.parse(getContentResolver().getType(imageUri))
                );
                
                builder.addFormDataPart("files", "image_" + i + ".jpg", imageBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Adicionar descrição
        EditText edtDescription = findViewById(R.id.edt_description);
        String description = edtDescription.getText().toString();
        if (!description.isEmpty()) {
            builder.addFormDataPart("description", description);
        }
        
        MultipartBody requestBody = builder.build();
        
        // Fazer requisição
        Request request = new Request.Builder()
            .url("http://localhost:8080/reports/" + reportId + "/attachments")
            .post(requestBody)
            .addHeader("Authorization", "Bearer " + getToken())
            .build();
        
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(ReportAttachmentsActivity.this, 
                            "Imagens enviadas com sucesso!", Toast.LENGTH_LONG).show();
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ReportAttachmentsActivity.this, 
                            "Erro ao enviar imagens", Toast.LENGTH_SHORT).show();
                    });
                }
            }
            
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(ReportAttachmentsActivity.this, 
                        "Erro de conexão", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void removeImage(int position) {
        if (position < selectedImages.size()) {
            selectedImages.remove(position);
            adapter.notifyItemRemoved(position);
        }
    }
    
    private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        
        return byteBuffer.toByteArray();
    }
    
    private String getToken() {
        // Obter token do SharedPreferences
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        return prefs.getString("token", "");
    }
}
```

### # **2. Adapter para Imagens:**

```java
// AttachmentsAdapter.java
public class AttachmentsAdapter extends RecyclerView.Adapter<AttachmentsAdapter.ViewHolder> {
    
    private List<Uri> images;
    private OnImageRemoveListener removeListener;
    
    public interface OnImageRemoveListener {
        void onRemove(int position);
    }
    
    public AttachmentsAdapter(List<Uri> images, OnImageRemoveListener removeListener) {
        this.images = images;
        this.removeListener = removeListener;
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_attachment_image, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Uri imageUri = images.get(position);
        
        // Carregar imagem com Glide
        Glide.with(holder.itemView.getContext())
            .load(imageUri)
            .into(holder.imageView);
        
        // Remover imagem
        holder.btnRemove.setOnClickListener(v -> {
            removeListener.onRemove(position);
        });
    }
    
    @Override
    public int getItemCount() {
        return images.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton btnRemove;
        
        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_attachment);
            btnRemove = view.findViewById(R.id.btn_remove);
        }
    }
}
```

### # **3. Layout XML:**

```xml
<!-- activity_report_attachments.xml -->
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">
    
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Anexar Imagens"
        android:textSize="18sp"
        android:textStyle="bold"
        android:gravity="center"
        android:layout_marginBottom="16dp" />
    
    <Button
        android:id="@+id/btn_upload_images"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Selecionar Imagens"
        android:layout_marginBottom="16dp" />
    
    <EditText
        android:id="@+id/edt_description"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Descrição dos anexos (opcional)"
        android:layout_marginBottom="16dp" />
    
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Imagens selecionadas:"
        android:textStyle="bold"
        android:layout_marginBottom="8dp" />
    
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/attachments_recycler"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:layout_marginBottom="16dp" />
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">
        
        <Button
            android:id="@+id/btn_cancel"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Cancelar"
            android:layout_marginEnd="8dp" />
        
        <Button
            android:id="@+id/btn_save_attachments"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Salvar Anexos"
            android:layout_marginStart="8dp" />
        
    </LinearLayout>
    
</LinearLayout>
```

```xml
<!-- item_attachment_image.xml -->
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="120dp"
    android:layout_height="120dp"
    android:layout_margin="4dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="4dp">
    
    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        
        <ImageView
            android:id="@+id/img_attachment"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:scaleType="centerCrop" />
        
        <ImageButton
            android:id="@+id/btn_remove"
            android:layout_width="24dp"
            android:layout_height="24dp"
            android:layout_alignParentTop="true"
            android:layout_alignParentEnd="true"
            android:layout_margin="4dp"
            android:background="@android:drawable/ic_delete"
            android:backgroundTint="@android:color/holo_red_dark" />
        
    </RelativeLayout>
    
</androidx.cardview.widget.CardView>
```

## # **TESTES PARA VALIDAR:**

### # **1. Upload Bem-Sucedido:**
1. # Criar um relatório
2. # Selecionar múltiplas imagens
3. # Enviar para o endpoint
4. # Verificar se os arquivos foram salvos

### # **2. Validação de Tipos:**
1. # Tentar upload de arquivo não permitido (.exe)
2. # Deve retornar erro 400

### # **3. Limite de Tamanho:**
1. # Tentar upload de arquivo > 10MB
2. # Deve retornar erro 400

### # **4. Download de Anexo:**
1. # Fazer upload de uma imagem
2. # Fazer download pelo endpoint
3. # Verificar se o arquivo é o mesmo

## # **RESULTADO ESPERADO:**

### # **Funcionalidades:**
- # **Upload múltiplo:** Até 10 imagens simultâneas
- # **Visualização:** Grid com preview das imagens
- # **Remoção:** Excluir imagens antes de salvar
- # **Download:** Baixar anexos individuais
- # **Exclusão:** Remover anexos salvos

### # **Experiência do Usuário:**
- # **Interface intuitiva:** Seleção múltipla nativa
- # **Feedback visual:** Preview das imagens
- # **Validações:** Mensagens de erro claras
- # **Performance:** Upload eficiente e rápido

---

## # **IMPORTANTE PARA O GEMINI:**

**1. Implementar a Activity de upload com o código fornecido**
**2. Criar os layouts XML para a interface**
**3. Adicionar permissões no AndroidManifest.xml**
**4. Testar com diferentes tipos e tamanhos de arquivos**
**5. Implementar tratamento de erros e feedback ao usuário**

**A funcionalidade está 100% pronta no backend - só precisa implementar o frontend!**
