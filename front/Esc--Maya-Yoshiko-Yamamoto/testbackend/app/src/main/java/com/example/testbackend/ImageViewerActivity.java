package com.example.testbackend;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.testbackend.models.ReportAttachment;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.PatientReportApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageViewerActivity extends AppCompatActivity {
    private static final String TAG = "ImageViewerActivity";
    private static final int REQUEST_WRITE_STORAGE = 100;
    
    private ImageView imageView;
    private TextView tvFileName;
    private TextView tvImageInfo;
    private ProgressBar progressBar;
    private Button btnBack;
    private Button btnShare;
    private Button btnDownload;
    
    private PatientReportApi api;
    private ReportAttachment currentAttachment;
    private int reportId;
    private int attachmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        
        // Obter dados da Intent
        reportId = getIntent().getIntExtra("report_id", -1);
        attachmentId = getIntent().getIntExtra("attachment_id", -1);
        String fileName = getIntent().getStringExtra("file_name");
        
        if (reportId == -1 || attachmentId == -1) {
            Toast.makeText(this, "Dados inválidos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        api = ApiClient.getAuthClient().create(PatientReportApi.class);
        
        setupViews(fileName);
        loadImage();
    }
    
    private void setupViews(String fileName) {
        imageView = findViewById(R.id.imageView);
        tvFileName = findViewById(R.id.tvFileName);
        tvImageInfo = findViewById(R.id.tvImageInfo);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        btnDownload = findViewById(R.id.btnDownload);
        
        tvFileName.setText(fileName != null ? fileName : "Imagem");
        
        btnBack.setOnClickListener(v -> finish());
        btnShare.setOnClickListener(v -> shareImage());
        btnDownload.setOnClickListener(v -> downloadImage());
    }
    
    private void loadImage() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        Log.d(TAG, "📥 Baixando imagem: report_id=" + reportId + ", attachment_id=" + attachmentId);
        
        api.downloadAttachment(reportId, attachmentId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Ler bytes da imagem
                        byte[] imageBytes = response.body().bytes();
                        
                        // Converter para Bitmap
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        
                        if (bitmap != null) {
                            // Exibir imagem
                            imageView.setImageBitmap(bitmap);
                            
                            // Atualizar informações
                            String info = "Tamanho: " + formatFileSize(imageBytes.length) + 
                                        "\nResolução: " + bitmap.getWidth() + "x" + bitmap.getHeight();
                            tvImageInfo.setText(info);
                            
                            // Habilitar botões
                            btnShare.setEnabled(true);
                            btnDownload.setEnabled(true);
                            
                            // Salvar referência para compartilhamento/download
                            currentAttachment = new ReportAttachment();
                            currentAttachment.setId(attachmentId);
                            currentAttachment.setReportId(reportId);
                            currentAttachment.setFileBytes(imageBytes);
                            
                            Log.d(TAG, "✅ Imagem carregada com sucesso");
                        } else {
                            Log.e(TAG, "❌ Falha ao decodificar imagem");
                            showError("Não foi possível carregar a imagem");
                        }
                        
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Erro ao processar imagem", e);
                        showError("Erro ao processar imagem: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "❌ Erro no download: " + response.code());
                    showError("Erro ao baixar imagem: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Log.e(TAG, "❌ Falha na conexão", t);
                showError("Falha na conexão: " + t.getMessage());
            }
        });
    }
    
    private void shareImage() {
        if (currentAttachment == null || currentAttachment.getFileBytes() == null) {
            Toast.makeText(this, "Imagem não carregada", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            Log.d(TAG, "📤 Iniciando compartilhamento da imagem");
            
            // Salvar imagem temporária para compartilhamento
            File tempFile = new File(getCacheDir(), "temp_share_image_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(currentAttachment.getFileBytes());
            fos.close();
            
            Log.d(TAG, "✅ Imagem salva temporariamente: " + tempFile.getAbsolutePath());
            
            // Criar Intent de compartilhamento
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            
            // Usar URI compatível
            android.net.Uri imageUri = android.net.Uri.fromFile(tempFile);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Imagem do relatório #" + reportId + " - SmartSaude");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            // Verificar se há apps para compartilhar
            if (shareIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(shareIntent, "Compartilhar imagem via"));
                Log.d(TAG, "✅ Compartilhamento iniciado com sucesso");
            } else {
                Toast.makeText(this, "Nenhum app encontrado para compartilhamento", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "⚠️ Nenhum app de compartilhamento encontrado");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Erro ao compartilhar imagem", e);
            Toast.makeText(this, "Erro ao compartilhar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void downloadImage() {
        if (currentAttachment == null || currentAttachment.getFileBytes() == null) {
            Toast.makeText(this, "Imagem não carregada", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Verificar permissão de escrita para Android 6.0+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) 
                != PackageManager.PERMISSION_GRANTED) {
                // Solicitar permissão
                ActivityCompat.requestPermissions(
                    this, 
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 
                    REQUEST_WRITE_STORAGE
                );
                return;
            }
        }
        
        // Se já tem permissão, prosseguir com o download
        performDownload();
    }
    
    private void performDownload() {
        try {
            Log.d(TAG, "💾 Iniciando download da imagem");
            
            // Verificar e criar diretório Downloads/SmartSaude
            File downloadsDir = new File(android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_DOWNLOADS), "SmartSaude");
            
            if (!downloadsDir.exists()) {
                boolean created = downloadsDir.mkdirs();
                Log.d(TAG, "📁 Diretório Downloads/SmartSaude criado: " + created);
            }
            
            // Nome único para o arquivo
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = "relatorio_" + reportId + "_img_" + attachmentId + "_" + timestamp + ".jpg";
            File imageFile = new File(downloadsDir, fileName);
            
            Log.d(TAG, "💾 Salvando imagem em: " + imageFile.getAbsolutePath());
            
            // Salvar arquivo
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(currentAttachment.getFileBytes());
            fos.flush();
            fos.close();
            
            // Verificar se o arquivo foi salvo corretamente
            if (imageFile.exists() && imageFile.length() > 0) {
                Log.d(TAG, "✅ Imagem salva com sucesso: " + imageFile.length() + " bytes");
                
                // Notificar o sistema sobre o novo arquivo
                android.media.MediaScannerConnection.scanFile(
                    this, 
                    new String[]{imageFile.getAbsolutePath()}, 
                    null,
                    (path, uri) -> {
                        Log.d(TAG, "📱 Arquivo escaneado: " + path);
                    }
                );
                
                // Mensagem de sucesso com opção de abrir
                String successMsg = "Imagem salva em Downloads/SmartSaude\n" + fileName;
                Toast.makeText(this, successMsg, Toast.LENGTH_LONG).show();
                
                // Opcional: abrir a pasta Downloads
                openDownloadsFolder();
                
            } else {
                Log.e(TAG, "❌ Falha ao salvar o arquivo");
                Toast.makeText(this, "Falha ao salvar a imagem", Toast.LENGTH_SHORT).show();
            }
            
        } catch (SecurityException e) {
            Log.e(TAG, "❌ Erro de permissão ao salvar", e);
            Toast.makeText(this, "Sem permissão para salvar arquivos", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "❌ Erro ao salvar imagem", e);
            Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void openDownloadsFolder() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setType(android.provider.DocumentsContract.Document.MIME_TYPE_DIR);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            // Tentar abrir a pasta Downloads
            File downloadsDir = new File(android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_DOWNLOADS), "SmartSaude");
            
            if (downloadsDir.exists()) {
                android.net.Uri downloadsUri = android.net.Uri.fromFile(downloadsDir);
                intent.setDataAndType(downloadsUri, "resource/folder");
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.w(TAG, "⚠️ Não foi possível abrir a pasta Downloads", e);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida, prosseguir com o download
                Log.d(TAG, "✅ Permissão de escrita concedida");
                performDownload();
            } else {
                // Permissão negada
                Log.w(TAG, "❌ Permissão de escrita negada");
                Toast.makeText(this, "Permissão necessária para salvar imagens", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void showError(String message) {
        tvImageInfo.setText("Erro: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        
        // Desabilitar botões de ação
        btnShare.setEnabled(false);
        btnDownload.setEnabled(false);
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
