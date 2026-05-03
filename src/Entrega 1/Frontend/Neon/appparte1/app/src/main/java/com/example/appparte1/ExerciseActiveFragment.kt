package com.example.appparte1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExerciseActiveFragment : Fragment() {

    companion object {
        private const val ARG_EXERCISE = "exercise"

        fun newInstance(exercise: Exercise): ExerciseActiveFragment {
            val fragment = ExerciseActiveFragment()
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
        val view = inflater.inflate(R.layout.fragment_exercise_active, container, false)
        
        val exercise = arguments?.getSerializable(ARG_EXERCISE) as? Exercise
        
        val btnBack = view.findViewById<ImageView>(R.id.btn_back_active)
        val fabPause = view.findViewById<FloatingActionButton>(R.id.fab_pause)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        fabPause.setOnClickListener {
            // No futuro, isso pausaria o cronômetro. 
            // Por enquanto, vamos simular que o exercício acabou e ir para o resumo.
            exercise?.let {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ExerciseSummaryFragment.newInstance(it))
                    .addToBackStack(null)
                    .commit()
            }
        }

        return view
    }
}