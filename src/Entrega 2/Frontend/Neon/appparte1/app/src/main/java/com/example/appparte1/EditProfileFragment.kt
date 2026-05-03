package com.example.appparte1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class EditProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Atualizado para o novo nome do layout: fragment_profile_edit
        val view = inflater.inflate(R.layout.fragment_profile_edit, container, false)
        
        val btnBack = view.findViewById<ImageView>(R.id.btn_back_edit)
        val btnSave = view.findViewById<MaterialButton>(R.id.btn_save_profile)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnSave.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}