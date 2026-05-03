package com.example.testbackend.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.testbackend.R;
import com.example.testbackend.models.PatientEvaluation;
import com.example.testbackend.viewmodels.PatientEvaluationViewModel;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class AdministrativeFragment extends Fragment {

    private PatientEvaluationViewModel viewModel;
    private TextInputEditText etFirstContactDate, etProfession, etHealthPlan, etPatientOrigin, 
                             etSessionFee, etAppointmentTime, etFrequency, etMedications;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_evaluation_administrative, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PatientEvaluationViewModel.class);

        initializeViews(view);
        setupDatePicker();
        setupTextWatchers();
    }

    private void initializeViews(View view) {
        etFirstContactDate = view.findViewById(R.id.etFirstContactDate);
        etProfession = view.findViewById(R.id.etProfession);
        etHealthPlan = view.findViewById(R.id.etHealthPlan);
        etPatientOrigin = view.findViewById(R.id.etPatientOrigin);
        etSessionFee = view.findViewById(R.id.etSessionFee);
        etAppointmentTime = view.findViewById(R.id.etAppointmentTime);
        etFrequency = view.findViewById(R.id.etFrequency);
        etMedications = view.findViewById(R.id.etMedications);
    }

    private void setupDatePicker() {
        etFirstContactDate.setOnClickListener(v -> {
            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                etFirstContactDate.setText(dateFormat.format(calendar.getTime()));
                
                PatientEvaluation eval = viewModel.getEvaluation().getValue();
                if (eval != null) {
                    eval.setFirstContactDate(calendar.getTime());
                    viewModel.updateEvaluation(eval);
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void setupTextWatchers() {
        TextWatcher commonWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateViewModel();
            }
        };

        etProfession.addTextChangedListener(commonWatcher);
        etHealthPlan.addTextChangedListener(commonWatcher);
        etPatientOrigin.addTextChangedListener(commonWatcher);
        etSessionFee.addTextChangedListener(commonWatcher);
        etAppointmentTime.addTextChangedListener(commonWatcher);
        etFrequency.addTextChangedListener(commonWatcher);
        etMedications.addTextChangedListener(commonWatcher);
    }

    private void updateViewModel() {
        PatientEvaluation eval = viewModel.getEvaluation().getValue();
        if (eval != null) {
            eval.setProfession(etProfession.getText().toString());
            eval.setHealthPlan(etHealthPlan.getText().toString());
            eval.setPatientOrigin(etPatientOrigin.getText().toString());
            
            try {
                eval.setSessionFee(Double.parseDouble(etSessionFee.getText().toString()));
            } catch (NumberFormatException ignored) {}

            eval.setAppointmentTime(etAppointmentTime.getText().toString());
            eval.setFrequency(etFrequency.getText().toString());
            
            String meds = etMedications.getText().toString();
            if (!meds.isEmpty()) {
                eval.setMedications(Arrays.asList(meds.split("\\s*,\\s*")));
            }
            
            viewModel.updateEvaluation(eval);
        }
    }
}
