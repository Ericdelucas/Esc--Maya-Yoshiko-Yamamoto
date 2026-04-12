package com.example.appparte1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class ExerciseDetailFragment : Fragment() {

    companion object {
        private const val ARG_EXERCISE = "exercise"

        fun newInstance(exercise: Exercise): ExerciseDetailFragment {
            val fragment = ExerciseDetailFragment()
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
        val view = inflater.inflate(R.layout.fragment_exercise_detail, container, false)
        
        val exercise = arguments?.getSerializable(ARG_EXERCISE) as? Exercise
        
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val tvNameDetail = view.findViewById<TextView>(R.id.tv_exercise_name_detail)
        val tvDesc = view.findViewById<TextView>(R.id.tv_description)
        val btnBack = view.findViewById<ImageView>(R.id.btn_back)
        val btnStart = view.findViewById<MaterialButton>(R.id.btn_start_exercise)

        exercise?.let {
            tvTitle.text = it.name
            tvNameDetail.text = it.name
            tvDesc.text = it.description
        }

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        
        btnStart.setOnClickListener {
            // Aqui abriria a tela do exercício ativo
            exercise?.let {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ExerciseActiveFragment.newInstance(it))
                    .addToBackStack(null)
                    .commit()
            }
        }

        return view
    }
}