package com.example.testbackend.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.testbackend.fragments.IdentificationFragment;
import com.example.testbackend.fragments.AdministrativeFragment;
import com.example.testbackend.fragments.PlaceholderEvaluationFragment;

public class EvaluationPagerAdapter extends FragmentStateAdapter {

    private final String[] tabTitles = {
        "Identificação", "Administrativo", "Queixa", "Dor", 
        "Clínico", "Exames", "Físico", "Análise", "Tratamento"
    };

    public EvaluationPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new IdentificationFragment();
            case 1: return new AdministrativeFragment();
            default: return PlaceholderEvaluationFragment.newInstance(tabTitles[position]);
        }
    }

    @Override
    public int getItemCount() {
        return tabTitles.length;
    }

    public String getTabTitle(int position) {
        return tabTitles[position];
    }
}
