package com.example.testbackend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

public class HealthQuestionnaireActivity extends AppCompatActivity {

    private RadioGroup rgMeds, rgInjuries, rgJointPain, rgHeart, rgRestriction;
    private MaterialButton btnSave;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_questionnaire);

        prefs = getSharedPreferences("health_prefs", MODE_PRIVATE);

        setupToolbar();
        initViews();
        loadAnswers();

        btnSave.setOnClickListener(v -> saveAnswers());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        rgMeds = findViewById(R.id.rgMeds);
        rgInjuries = findViewById(R.id.rgInjuries);
        rgJointPain = findViewById(R.id.rgJointPain);
        rgHeart = findViewById(R.id.rgHeart);
        rgRestriction = findViewById(R.id.rgRestriction);
        btnSave = findViewById(R.id.btnSave);
    }

    private void loadAnswers() {
        checkRadioGroup(rgMeds, "meds");
        checkRadioGroup(rgInjuries, "injuries");
        checkRadioGroup(rgJointPain, "joint_pain");
        checkRadioGroup(rgHeart, "heart");
        checkRadioGroup(rgRestriction, "restriction");
    }

    private void checkRadioGroup(RadioGroup rg, String key) {
        boolean answer = prefs.getBoolean(key, false);
        boolean exists = prefs.contains(key);
        
        if (exists) {
            if (answer) {
                rg.check(rg.getChildAt(0).getId()); // Yes is usually first
            } else {
                rg.check(rg.getChildAt(1).getId()); // No is usually second
            }
        }
    }

    private void saveAnswers() {
        SharedPreferences.Editor editor = prefs.edit();
        
        editor.putBoolean("meds", rgMeds.getCheckedRadioButtonId() == R.id.rbMedsYes);
        editor.putBoolean("injuries", rgInjuries.getCheckedRadioButtonId() == R.id.rbInjuriesYes);
        editor.putBoolean("joint_pain", rgJointPain.getCheckedRadioButtonId() == R.id.rbJointPainYes);
        editor.putBoolean("heart", rgHeart.getCheckedRadioButtonId() == R.id.rbHeartYes);
        editor.putBoolean("restriction", rgRestriction.getCheckedRadioButtonId() == R.id.rbRestrictionYes);
        
        editor.apply();
        Toast.makeText(this, "Questionário salvo com sucesso!", Toast.LENGTH_SHORT).show();
        finish();
    }
}