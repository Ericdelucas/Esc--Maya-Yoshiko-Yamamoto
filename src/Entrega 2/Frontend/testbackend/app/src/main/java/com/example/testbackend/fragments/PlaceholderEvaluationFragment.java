package com.example.testbackend.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.testbackend.R;

public class PlaceholderEvaluationFragment extends Fragment {

    private static final String ARG_TITLE = "arg_title";

    public static PlaceholderEvaluationFragment newInstance(String title) {
        PlaceholderEvaluationFragment fragment = new PlaceholderEvaluationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(android.R.layout.simple_list_item_1, container, false);
        TextView textView = view.findViewById(android.R.id.text1);
        
        if (getArguments() != null) {
            textView.setText("Formulário: " + getArguments().getString(ARG_TITLE) + "\n(Campos em desenvolvimento)");
            textView.setPadding(50, 50, 50, 50);
        }
        
        return view;
    }
}
