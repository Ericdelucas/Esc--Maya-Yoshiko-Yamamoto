package com.example.appparte1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class ExerciseSummaryFragment : Fragment() {

    companion object {
        private const val ARG_EXERCISE = "exercise"

        fun newInstance(exercise: Exercise): ExerciseSummaryFragment {
            val fragment = ExerciseSummaryFragment()
            val args = Bundle()
            args.putSerializable(ARG_EXERCISE, exercise)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exercise_summary, container, false)
        
        val btnClose = view.findViewById<ImageView>(R.id.btn_close_summary)
        val btnSave = view.findViewById<MaterialButton>(R.id.btn_save_summary)

        btnClose.setOnClickListener {
            // Volta para a lista de exercícios limpando a pilha de telas do exercício
            parentFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ExercisesFragment())
                .commit()
        }

        btnSave.setOnClickListener {
            // Volta para a home após salvar
            parentFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        return view
    }
}