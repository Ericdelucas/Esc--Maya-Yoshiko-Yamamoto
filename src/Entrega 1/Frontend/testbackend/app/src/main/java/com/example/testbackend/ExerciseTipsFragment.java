package com.example.testbackend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragmento para exibir dicas profissionais sobre o exercício.
 * Uso de Fragments conforme requisito.
 */
public class ExerciseTipsFragment extends Fragment {

    private static final String ARG_TIP = "exercise_tip";

    public static ExerciseTipsFragment newInstance(String tip) {
        ExerciseTipsFragment fragment = new ExerciseTipsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TIP, tip);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tips, container, false);
        TextView tvTipText = view.findViewById(R.id.tvTipText);
        
        if (getArguments() != null) {
            String tip = getArguments().getString(ARG_TIP);
            tvTipText.setText(tip);
        }
        
        return view;
    }
}