package com.example.appparte1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExercisesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exercises, container, false)
        
        val rvExercises = view.findViewById<RecyclerView>(R.id.rv_exercises)
        rvExercises.layoutManager = LinearLayoutManager(context)
        
        val dummyExercises = listOf(
            Exercise(
                "Alongamento de quadril/Psoas", 
                "8 minutos", 
                "Deite-se com uma perna estendida e a outra dobrada, puxando o joelho em direção ao peito.",
                R.drawable.ex_1
            ),
            Exercise(
                "Extensão Lombar", 
                "6 minutos", 
                "Deite-se de bruços, apoie as palmas das mãos no chão e estenda os cotovelos, elevando o tronco.",
                R.drawable.ex_2
            ),
            Exercise(
                "Postura da Criança (Balasana)", 
                "5 minutos", 
                "Ajoelhe-se no chão, sente-se nos calcanhares e incline o corpo para a frente, estendendo os braços.",
                R.drawable.ex_3
            ),
            Exercise(
                "Ponte (Bridge)", 
                "7 minutos", 
                "Deite-se de costas, dobre os joelhos e eleve o quadril em direção ao teto.",
                R.drawable.ex_1 // Reutilizando ex_1 para exemplo
            ),
            Exercise(
                "Gato-Vaca (Cat-Cow)", 
                "4 minutos", 
                "Fique de quatro e alterne entre arquear e arredondar as costas.",
                R.drawable.ex_2 // Reutilizando ex_2 para exemplo
            ),
            Exercise(
                "Fortalecimento de Escápulas", 
                "10 minutos", 
                "Mantenha a postura ereta e realize movimentos de retração das escápulas para fortalecer as costas.",
                R.drawable.ex_3 // Reutilizando ex_3 para exemplo
            )
        )
        
        rvExercises.adapter = ExerciseAdapter(dummyExercises) { exercise ->
            // Abre a tela de detalhes do exercício ao clicar
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ExerciseDetailFragment.newInstance(exercise))
                .addToBackStack(null)
                .commit()
        }
        
        return view
    }
}