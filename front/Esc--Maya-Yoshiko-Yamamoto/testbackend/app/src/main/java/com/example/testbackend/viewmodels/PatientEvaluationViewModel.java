package com.example.testbackend.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.testbackend.models.PatientEvaluation;

public class PatientEvaluationViewModel extends ViewModel {
    private final MutableLiveData<PatientEvaluation> evaluation = new MutableLiveData<>(new PatientEvaluation());

    public LiveData<PatientEvaluation> getEvaluation() {
        return evaluation;
    }

    public void updateEvaluation(PatientEvaluation updatedEvaluation) {
        evaluation.setValue(updatedEvaluation);
    }
}
