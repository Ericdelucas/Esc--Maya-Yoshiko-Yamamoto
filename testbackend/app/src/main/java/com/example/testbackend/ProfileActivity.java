package com.example.testbackend;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.testbackend.models.FileUploadResponse;
import com.example.testbackend.models.UserProfileResponse;
import com.example.testbackend.network.ApiClient;
import com.example.testbackend.network.AuthApi;
import com.example.testbackend.utils.Constants;
import com.example.testbackend.utils.ImageUtils;
import com.example.testbackend.utils.LocaleHelper;
import com.example.testbackend.utils.TokenManager;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "PROFILE_DEBUG";
    private TextView tvProfileInitial, tvProfileName, tvProfileEmail, tvProfileRole;
    private ImageView ivProfilePhoto;
    private MaterialButton btnChangePhoto, btnChangePassword, btnProfileLogout;
    private TokenManager tokenManager;

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    uploadProfilePhoto(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tokenManager = new TokenManager(this);

        setupToolbar();
        initViews();
        loadUserProfile();
        setupListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        tvProfileInitial = findViewById(R.id.tvProfileInitial);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileRole = findViewById(R.id.tvProfileRole);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnProfileLogout = findViewById(R.id.btnProfileLogout);
    }

    private void loadUserProfile() {
        if (!tokenManager.isLoggedIn()) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AuthApi authApi = ApiClient.getAuthClient().create(AuthApi.class);
        authApi.getProfile(tokenManager.getAuthToken()).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    Log.e(TAG, "Erro ao carregar perfil: " + response.code());
                    loadUserDataLocally();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Log.e(TAG, "Falha na requisição de perfil", t);
                loadUserDataLocally();
            }
        });
    }

    private void loadUserDataLocally() {
        String email = tokenManager.getUserEmail();
        String role = tokenManager.getUserRole();

        tvProfileEmail.setText(email.isEmpty() ? "usuario@email.com" : email);
        tvProfileRole.setText(role.toUpperCase());
        
        if (!email.isEmpty()) {
            String name = email.split("@")[0];
            tvProfileName.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
            tvProfileInitial.setText(email.substring(0, 1).toUpperCase());
        }
    }

    private void updateUI(UserProfileResponse profile) {
        tvProfileName.setText(profile.getFullName() != null && !profile.getFullName().isEmpty() ? 
                profile.getFullName() : profile.getEmail().split("@")[0]);
        tvProfileEmail.setText(profile.getEmail());
        tvProfileRole.setText(profile.getRole().toUpperCase());
        tvProfileInitial.setText(profile.getEmail().substring(0, 1).toUpperCase());

        if (profile.getProfilePhotoUrl() != null && !profile.getProfilePhotoUrl().isEmpty()) {
            // Removendo a barra final da URL base se existir e concatenando com a URL da foto
            String baseUrl = Constants.AUTH_BASE_URL;
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            String fullImageUrl = baseUrl + profile.getProfilePhotoUrl();
            Log.d(TAG, "Carregando imagem: " + fullImageUrl);
            
            Picasso.get()
                .load(fullImageUrl)
                .into(ivProfilePhoto, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        ivProfilePhoto.setVisibility(View.VISIBLE);
                        tvProfileInitial.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Erro Picasso: " + e.getMessage());
                        ivProfilePhoto.setVisibility(View.GONE);
                        tvProfileInitial.setVisibility(View.VISIBLE);
                    }
                });
        } else {
            ivProfilePhoto.setVisibility(View.GONE);
            tvProfileInitial.setVisibility(View.VISIBLE);
        }
    }

    private void setupListeners() {
        btnChangePhoto.setOnClickListener(v -> galleryLauncher.launch("image/*"));
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
        });
        btnProfileLogout.setOnClickListener(v -> logout());
    }

    private void uploadProfilePhoto(Uri uri) {
        btnChangePhoto.setEnabled(false);
        btnChangePhoto.setText("Enviando...");

        byte[] imageBytes = ImageUtils.getImageBytes(this, uri);
        if (imageBytes == null) {
            Toast.makeText(this, "Erro ao processar imagem", Toast.LENGTH_SHORT).show();
            btnChangePhoto.setEnabled(true);
            btnChangePhoto.setText("Alterar foto de perfil");
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "profile.jpg", requestFile);

        AuthApi authApi = ApiClient.getAuthClient().create(AuthApi.class);
        authApi.uploadProfilePhoto(tokenManager.getAuthToken(), body).enqueue(new Callback<FileUploadResponse>() {
            @Override
            public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                btnChangePhoto.setEnabled(true);
                btnChangePhoto.setText("Alterar foto de perfil");
                
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Foto atualizada!", Toast.LENGTH_SHORT).show();
                    loadUserProfile();
                } else {
                    Log.e(TAG, "Erro upload code: " + response.code());
                    Toast.makeText(ProfileActivity.this, "Erro no upload: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                btnChangePhoto.setEnabled(true);
                btnChangePhoto.setText("Alterar foto de perfil");
                Log.e(TAG, "Falha upload", t);
                Toast.makeText(ProfileActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        tokenManager.clearToken();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}