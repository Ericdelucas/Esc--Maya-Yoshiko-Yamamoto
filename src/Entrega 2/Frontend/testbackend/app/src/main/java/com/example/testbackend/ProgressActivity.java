package com.example.testbackend;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Esta tela foi desativada pois o layout activity_progress.xml foi removido.
 * O progresso agora é exibido diretamente na MainActivity.
 * Este arquivo pode ser deletado manualmente.
 */
public class ProgressActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Activity desativada - não chama setContentView para evitar erros de compilação
        finish();
    }
}
