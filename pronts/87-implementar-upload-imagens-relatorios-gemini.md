# # **IMPLEMENTAR UPLOAD DE IMAGENS EM RELATÓRIOS - GEMINI**

## # **PROBLEMA IDENTIFICADO:**

### # **O que falta implementar:**
- # **Botão de upload** na tela de detalhes do relatório
- # **Interface para selecionar** múltiplas imagens
- # **API endpoints** para anexos
- # **Models** para anexos
- # **Adapter** para mostrar anexos existentes

## # **ESTRUTURA ATUAL DO FRONTEND:**

### # **Arquivos existentes:**
- # **ReportDetailActivity.java** - Tela de detalhes (sem upload)
- # **activity_report_detail.xml** - Layout (sem seção de anexos)
- # **PatientReportApi.java** - API (sem endpoints de anexos)
- # **PatientReport.java** - Model (sem lista de anexos)

## # **IMPLEMENTAÇÃO PASSO A PASSO:**

### # **PASSO 1: Criar Models para Anexos**

#### # **Criar arquivo: ReportAttachment.java**
```java
// Caminho: app/src/main/java/com/example/testbackend/models/ReportAttachment.java
package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;

public class ReportAttachment {
    @SerializedName("id")
    private int id;
    
    @SerializedName("report_id")
    private int reportId;
    
    @SerializedName("attachment_type")
    private String attachmentType;
    
    @SerializedName("file_name")
    private String fileName;
    
    @SerializedName("file_path")
    private String filePath;
    
    @SerializedName("file_size")
    private Integer fileSize;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("uploaded_at")
    private String uploadedAt;
    
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }
    
    public String getAttachmentType() { return attachmentType; }
    public void setAttachmentType(String attachmentType) { this.attachmentType = attachmentType; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public Integer getFileSize() { return fileSize; }
    public void setFileSize(Integer fileSize) { this.fileSize = fileSize; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(String uploadedAt) { this.uploadedAt = uploadedAt; }
}
```

#### # **Criar arquivo: ReportAttachmentList.java**
```java
// Caminho: app/src/main/java/com/example/testbackend/models/ReportAttachmentList.java
package com.example.testbackend.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReportAttachmentList {
    @SerializedName("attachments")
    private List<ReportAttachment> attachments;
    
    @SerializedName("total")
    private int total;
    
    public List<ReportAttachment> getAttachments() { return attachments; }
    public void setAttachments(List<ReportAttachment> attachments) { this.attachments = attachments; }
    
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
}
```

#### # **Atualizar PatientReport.java**
```java
// Adicionar no arquivo PatientReport.java existente:
import java.util.List;

public class PatientReport {
    // ... campos existentes ...
    
    @SerializedName("attachments")
    private List<ReportAttachment> attachments;
    
    public List<ReportAttachment> getAttachments() { return attachments; }
    public void setAttachments(List<ReportAttachment> attachments) { this.attachments = attachments; }
}
```

### # **PASSO 2: Atualizar API**

#### # **Atualizar PatientReportApi.java**
```java
// Adicionar estes métodos na interface PatientReportApi.java:

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.Streaming;

// ... imports existentes ...

public interface PatientReportApi {
    // ... métodos existentes ...
    
    // # ENDPOINTS DE ANEXOS
    @Multipart
    @POST("reports/{reportId}/attachments")
    Call<List<ReportAttachment>> uploadAttachments(
        @Path("reportId") int reportId,
        @Part List<MultipartBody.Part> files,
        @Part MultipartBody.Part description
    );
    
    @GET("reports/{reportId}/attachments")
    Call<ReportAttachmentList> getReportAttachments(@Path("reportId") int reportId);
    
    @Streaming
    @GET("reports/{reportId}/attachments/{attachmentId}/download")
    Call<ResponseBody> downloadAttachment(
        @Path("reportId") int reportId,
        @Path("attachmentId") int attachmentId
    );
    
    @DELETE("reports/{reportId}/attachments/{attachmentId}")
    Call<Void> deleteAttachment(
        @Path("reportId") int reportId,
        @Path("attachmentId") int attachmentId
    );
    
    @GET("reports/{reportId}/with-attachments")
    Call<PatientReport> getReportWithAttachments(@Path("reportId") int reportId);
}
```

### # **PASSO 3: Atualizar Layout**

#### # **Adicionar seção de anexos no activity_report_detail.xml**
```xml
<!-- Adicionar antes do final do LinearLayout principal (antes dos botões) -->

<!-- # SEÇÃO DE ANEXOS -->
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="16dp"
    app:cardCornerRadius="12dp"
    app:cardElevation="4dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical">

            <TextView
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="Anexos"
                android:textAppearance="@style/TextAppearance.Material3.HeadlineSmall"
                android:textStyle="bold"/>

            <Button
                android:id="@+id/btnAddAttachment"
                style="@style/Widget.Material3.Button.TextButton"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Adicionar Imagens"/>

        </LinearLayout>

        <TextView
            android:id="@+id/tvNoAttachments"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:text="Nenhum anexo adicionado"
            android:textAppearance="@style/TextAppearance.Material3.BodyMedium"
            android:textColor="?android:attr/textColorSecondary"
            android:visibility="gone"/>

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/recyclerAttachments"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:nestedScrollingEnabled="false"/>

    </LinearLayout>

</com.google.android.material.card.MaterialCardView>
```

### # **PASSO 4: Criar Adapter para Anexos**

#### # **Criar arquivo: ReportAttachmentAdapter.java**
```java
// Caminho: app/src/main/java/com/example/testbackend/adapters/ReportAttachmentAdapter.java
package com.example.testbackend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.testbackend.R;
import com.example.testbackend.models.ReportAttachment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportAttachmentAdapter extends RecyclerView.Adapter<ReportAttachmentAdapter.ViewHolder> {
    
    private List<ReportAttachment> attachments;
    private Context context;
    private OnAttachmentClickListener listener;
    
    public interface OnAttachmentClickListener {
        void onViewClick(ReportAttachment attachment);
        void onDeleteClick(ReportAttachment attachment);
    }
    
    public ReportAttachmentAdapter(Context context, List<ReportAttachment> attachments, OnAttachmentClickListener listener) {
        this.context = context;
        this.attachments = attachments;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report_attachment, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReportAttachment attachment = attachments.get(position);
        
        // Nome do arquivo
        holder.tvFileName.setText(attachment.getFileName());
        
        // Tamanho do arquivo
        if (attachment.getFileSize() != null) {
            String size = formatFileSize(attachment.getFileSize());
            holder.tvFileSize.setText(size);
        }
        
        // Data de upload
        holder.tvUploadDate.setText(formatDate(attachment.getUploadedAt()));
        
        // Descrição
        if (attachment.getDescription() != null && !attachment.getDescription().isEmpty()) {
            holder.tvDescription.setText(attachment.getDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }
        
        // Preview da imagem (se for imagem)
        if ("image".equals(attachment.getAttachmentType())) {
            holder.imgPreview.setVisibility(View.VISIBLE);
            // Carregar preview da imagem
            String imageUrl = "http://localhost:8080/reports/" + attachment.getReportId() + 
                             "/attachments/" + attachment.getId() + "/download";
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_error)
                .into(holder.imgPreview);
        } else {
            holder.imgPreview.setVisibility(View.GONE);
        }
        
        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewClick(attachment);
            }
        });
        
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(attachment);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return attachments.size();
    }
    
    public void updateAttachments(List<ReportAttachment> newAttachments) {
        this.attachments = newAttachments;
        notifyDataSetChanged();
    }
    
    private String formatFileSize(int bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateString;
        }
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPreview;
        TextView tvFileName;
        TextView tvFileSize;
        TextView tvUploadDate;
        TextView tvDescription;
        ImageButton btnDelete;
        
        ViewHolder(View view) {
            super(view);
            imgPreview = view.findViewById(R.id.imgPreview);
            tvFileName = view.findViewById(R.id.tvFileName);
            tvFileSize = view.findViewById(R.id.tvFileSize);
            tvUploadDate = view.findViewById(R.id.tvUploadDate);
            tvDescription = view.findViewById(R.id.tvDescription);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }
}
```

#### # **Criar layout item_report_attachment.xml**
```xml
<!-- Caminho: app/src/main/res/layout/item_report_attachment.xml -->
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="4dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="2dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="12dp">

        <ImageView
            android:id="@+id/imgPreview"
            android:layout_width="60dp"
            android:layout_height="60dp"
            android:layout_marginEnd="12dp"
            android:scaleType="centerCrop"
            android:src="@drawable/ic_image_placeholder"/>

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical">

            <TextView
                android:id="@+id/tvFileName"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="nome_do_arquivo.jpg"
                android:textAppearance="@style/TextAppearance.Material3.BodyMedium"
                android:textStyle="bold"/>

            <TextView
                android:id="@+id/tvFileSize"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="2.5 MB"
                android:textAppearance="@style/TextAppearance.Material3.BodySmall"
                android:textColor="?android:attr/textColorSecondary"/>

            <TextView
                android:id="@+id/tvUploadDate"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="22/04/2026 12:30"
                android:textAppearance="@style/TextAppearance.Material3.BodySmall"
                android:textColor="?android:attr/textColorSecondary"/>

            <TextView
                android:id="@+id/tvDescription"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="4dp"
                android:text="Descrição do anexo"
                android:textAppearance="@style/TextAppearance.Material3.BodySmall"
                android:visibility="gone"/>

        </LinearLayout>

        <ImageButton
            android:id="@+id/btnDelete"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:background="?attr/selectableItemBackgroundBorderless"
            android:src="@drawable/ic_delete"
            android:contentDescription="Excluir anexo"/>

    </LinearLayout>

</com.google.android.material.card.MaterialCardView>
```

### # **PASSO 5: Atualizar ReportDetailActivity**

#### # **Adicionar funcionalidade de upload no ReportDetailActivity.java**
```java
// Adicionar estes imports e campos na classe ReportDetailActivity.java:

import android.content.Intent;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testbackend.adapters.ReportAttachmentAdapter;
import com.example.testbackend.models.ReportAttachment;
import com.example.testbackend.models.ReportAttachmentList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

// Adicionar estes campos na classe:
private RecyclerView recyclerAttachments;
private TextView tvNoAttachments;
private Button btnAddAttachment;
private ReportAttachmentAdapter attachmentAdapter;
private List<ReportAttachment> attachments = new ArrayList<>();
private ActivityResultLauncher<Intent> imagePickerLauncher;

// Adicionar no método setupViews():
recyclerAttachments = findViewById(R.id.recyclerAttachments);
tvNoAttachments = findViewById(R.id.tvNoAttachments);
btnAddAttachment = findViewById(R.id.btnAddAttachment);

// Setup RecyclerView
recyclerAttachments.setLayoutManager(new LinearLayoutManager(this));
attachmentAdapter = new ReportAttachmentAdapter(this, attachments, 
    new ReportAttachmentAdapter.OnAttachmentClickListener() {
        @Override
        public void onViewClick(ReportAttachment attachment) {
            // Abrir/visualizar anexo
            openAttachment(attachment);
        }
        
        @Override
        public void onDeleteClick(ReportAttachment attachment) {
            // Confirmar exclusão
            deleteAttachment(attachment);
        }
    });
recyclerAttachments.setAdapter(attachmentAdapter);

// Setup image picker
imagePickerLauncher = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Intent data = result.getData();
            List<Uri> selectedImages = new ArrayList<>();
            
            if (data.getClipData() != null) {
                // Múltiplas imagens
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    selectedImages.add(data.getClipData().getItemAt(i).getUri());
                }
            } else if (data.getData() != null) {
                // Única imagem
                selectedImages.add(data.getData());
            }
            
            uploadImages(selectedImages);
        }
    }
);

btnAddAttachment.setOnClickListener(v -> openImagePicker());

// Modificar o método loadReport() para carregar anexos:
private void loadReport() {
    progressBar.setVisibility(View.VISIBLE);
    
    // Carregar relatório com anexos
    api.getReportWithAttachments(reportId).enqueue(new Callback<PatientReport>() {
        @Override
        public void onResponse(Call<PatientReport> call, Response<PatientReport> response) {
            progressBar.setVisibility(View.GONE);
            
            if (response.isSuccessful() && response.body() != null) {
                currentReport = response.body();
                populateFields();
                loadAttachments(); // Carregar anexos
            } else {
                // Tratamento de erro existente...
            }
        }
        
        @Override
        public void onFailure(Call<PatientReport> call, Throwable t) {
            // Tratamento de falha existente...
        }
    });
}

// Adicionar estes métodos na classe:
private void loadAttachments() {
    api.getReportAttachments(reportId).enqueue(new Callback<ReportAttachmentList>() {
        @Override
        public void onResponse(Call<ReportAttachmentList> call, Response<ReportAttachmentList> response) {
            if (response.isSuccessful() && response.body() != null) {
                attachments.clear();
                attachments.addAll(response.body().getAttachments());
                attachmentAdapter.updateAttachments(attachments);
                
                // Atualizar UI
                if (attachments.isEmpty()) {
                    tvNoAttachments.setVisibility(View.VISIBLE);
                    recyclerAttachments.setVisibility(View.GONE);
                } else {
                    tvNoAttachments.setVisibility(View.GONE);
                    recyclerAttachments.setVisibility(View.VISIBLE);
                }
            }
        }
        
        @Override
        public void onFailure(Call<ReportAttachmentList> call, Throwable t) {
            // Erro ao carregar anexos - mostrar mensagem se necessário
        }
    });
}

private void openImagePicker() {
    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("image/*");
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
    imagePickerLauncher.launch(Intent.createChooser(intent, "Selecione imagens"));
}

private void uploadImages(List<Uri> imageUris) {
    if (imageUris.isEmpty()) return;
    
    progressBar.setVisibility(View.VISIBLE);
    
    // Criar multipart body
    List<MultipartBody.Part> fileParts = new ArrayList<>();
    
    for (int i = 0; i < imageUris.size(); i++) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUris.get(i));
            File tempFile = new File(getCacheDir(), "temp_image_" + i + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            inputStream.close();
            outputStream.close();
            
            RequestBody requestFile = RequestBody.create(tempFile, MediaType.parse("image/*"));
            MultipartBody.Part part = MultipartBody.Part.createFormData("files", "image_" + i + ".jpg", requestFile);
            fileParts.add(part);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Adicionar descrição (opcional)
    RequestBody description = RequestBody.create("", MediaType.parse("text/plain"));
    
    // Fazer upload
    api.uploadAttachments(reportId, fileParts, description).enqueue(new Callback<List<ReportAttachment>>() {
        @Override
        public void onResponse(Call<List<ReportAttachment>> call, retrofit2.Response<List<ReportAttachment>> response) {
            progressBar.setVisibility(View.GONE);
            
            if (response.isSuccessful() && response.body() != null) {
                Toast.makeText(ReportDetailActivity.this, "Imagens enviadas com sucesso!", Toast.LENGTH_SHORT).show();
                loadAttachments(); // Recarregar lista
            } else {
                Toast.makeText(ReportDetailActivity.this, "Erro ao enviar imagens", Toast.LENGTH_SHORT).show();
            }
        }
        
        @Override
        public void onFailure(Call<List<ReportAttachment>> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ReportDetailActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
        }
    });
}

private void openAttachment(ReportAttachment attachment) {
    // Implementar visualização/download do anexo
    String downloadUrl = "http://localhost:8080/reports/" + attachment.getReportId() + 
                       "/attachments/" + attachment.getId() + "/download";
    
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
    startActivity(intent);
}

private void deleteAttachment(ReportAttachment attachment) {
    new androidx.appcompat.app.AlertDialog.Builder(this)
        .setTitle("Excluir Anexo")
        .setMessage("Deseja realmente excluir '" + attachment.getFileName() + "'?")
        .setPositiveButton("Excluir", (dialog, which) -> {
            progressBar.setVisibility(View.VISIBLE);
            
            api.deleteAttachment(reportId, attachment.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                    progressBar.setVisibility(View.GONE);
                    
                    if (response.isSuccessful()) {
                        Toast.makeText(ReportDetailActivity.this, "Anexo excluído", Toast.LENGTH_SHORT).show();
                        loadAttachments(); // Recarregar lista
                    } else {
                        Toast.makeText(ReportDetailActivity.this, "Erro ao excluir anexo", Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ReportDetailActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                }
            });
        })
        .setNegativeButton("Cancelar", null)
        .show();
}
```

### # **PASSO 6: Adicionar Recursos**

#### # **Adicionar drawables em res/drawable:**
```xml
<!-- ic_image_placeholder.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path android:fillColor="#757575" android:pathData="M21,19V5c0,-1.1 -0.9,-2 -2,-2H5c-1.1,0 -2,0.9 -2,2v14c0,1.1 0.9,2 2,2h14c1.1,0 2,-0.9 2,-2zM8.5,13.5l2.5,3.01L14.5,12l4.5,6H5l3.5,-4.5z"/>
</vector>

<!-- ic_image_error.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path android:fillColor="#F44336" android:pathData="M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10,-4.48 10,-10S17.52,2 12,2zM13,17h-2v-6h2v6zM13,9h-2L11,7h2v2z"/>
</vector>

<!-- ic_delete.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path android:fillColor="#F44336" android:pathData="M6,19c0,1.1 0.9,2 2,2h8c1.1,0 2,-0.9 2,-2V7H6v12zM19,4h-3.5l-1,-1h-5l-1,1H5v2h14V4z"/>
</vector>
```

### # **PASSO 7: Adicionar Permissões**

#### # **Adicionar em AndroidManifest.xml:**
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.INTERNET" />
```

## # **TESTES PARA VALIDAR:**

### # **1. Upload de Imagens:**
1. # Abrir relatório existente
2. # Clicar em "Adicionar Imagens"
3. # Selecionar múltiplas imagens
4. # Verificar upload e preview

### # **2. Visualização de Anexos:**
1. # Verificar lista de anexos
2. # Clicar em anexo para visualizar
3. # Verificar informações (nome, tamanho, data)

### # **3. Exclusão de Anexos:**
1. # Clicar no ícone de deletar
2. # Confirmar exclusão
3. # Verificar remoção da lista

## # **RESULTADO ESPERADO:**

### # **Funcionalidades:**
- # **Upload múltiplo:** Selecionar várias imagens de uma vez
- # **Preview visual:** Miniaturas das imagens
- # **Informações detalhadas:** Nome, tamanho, data, descrição
- # **Ações completas:** Visualizar, baixar, excluir
- # **Interface intuitiva:** Integrada à tela de relatórios

---

## # **IMPORTANTE PARA O GEMINI:**

**1. Implementar todos os passos na ordem correta**
**2. Testar cada funcionalidade individualmente**
**3. Verificar se o backend está rodando antes de testar**
**4. Ajustar URLs da API se necessário (localhost:8080)**
**5. Implementar tratamento de erros e feedback ao usuário**

**O backend já está 100% pronto - só precisa implementar o frontend seguindo estes passos detalhados!**
