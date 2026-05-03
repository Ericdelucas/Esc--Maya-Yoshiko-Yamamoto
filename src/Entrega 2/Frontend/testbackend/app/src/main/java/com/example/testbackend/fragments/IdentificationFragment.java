package com.example.testbackend.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.testbackend.R;
import com.example.testbackend.models.PatientEvaluation;
import com.example.testbackend.viewmodels.PatientEvaluationViewModel;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class IdentificationFragment extends Fragment {

    private PatientEvaluationViewModel viewModel;
    private TextInputEditText etFullName, etCpf, etBirthDate, etPhone, etEmail, etAddress;
    private AutoCompleteTextView spinnerGender, spinnerMaritalStatus;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_evaluation_identification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PatientEvaluationViewModel.class);

        initializeViews(view);
        setupSpinners();
        setupDatePicker();
        setupTextWatchers();
    }

    private void initializeViews(View view) {
        etFullName = view.findViewById(R.id.etFullName);
        etCpf = view.findViewById(R.id.etCpf);
        etBirthDate = view.findViewById(R.id.etBirthDate);
        etPhone = view.findViewById(R.id.etPhone);
        etEmail = view.findViewById(R.id.etEmail);
        etAddress = view.findViewById(R.id.etAddress);
        spinnerGender = view.findViewById(R.id.spinnerGender);
        spinnerMaritalStatus = view.findViewById(R.id.spinnerMaritalStatus);
    }

    private void setupSpinners() {
        String[] genders = {"Masculino", "Feminino", "Outro", "Não informar"};
        String[] maritalStatuses = {"Solteiro(a)", "Casado(a)", "Divorciado(a)", "Viúvo(a)", "União Estável"};

        spinnerGender.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, genders));
        spinnerMaritalStatus.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, maritalStatuses));
        
        spinnerGender.setOnItemClickListener((parent, view, position, id) -> {
            PatientEvaluation eval = viewModel.getEvaluation().getValue();
            if (eval != null) {
                eval.setGender(genders[position]);
                viewModel.updateEvaluation(eval);
            }
        });
    }

    private void setupDatePicker() {
        etBirthDate.setOnClickListener(v -> {
            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                etBirthDate.setText(dateFormat.format(calendar.getTime()));
                
                PatientEvaluation eval = viewModel.getEvaluation().getValue();
                if (eval != null) {
                    eval.setBirthDate(calendar.getTime());
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

        etFullName.addTextChangedListener(commonWatcher);
        etCpf.addTextChangedListener(commonWatcher);
        etPhone.addTextChangedListener(commonWatcher);
        etEmail.addTextChangedListener(commonWatcher);
        etAddress.addTextChangedListener(commonWatcher);
    }

    private void updateViewModel() {
        PatientEvaluation eval = viewModel.getEvaluation().getValue();
        if (eval != null) {
            eval.setFullName(etFullName.getText().toString());
            eval.setCpf(etCpf.getText().toString());
            eval.setPhone(etPhone.getText().toString());
            eval.setEmail(etEmail.getText().toString());
            eval.setAddress(etAddress.getText().toString());
            viewModel.updateEvaluation(eval);
        }
    }
}
