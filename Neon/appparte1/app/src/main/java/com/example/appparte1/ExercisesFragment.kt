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
            Exercise("Alongamento de quadril/Psoas", "8 minutos", "Deite-se com uma perna estendida e a outra dobrada, puxando o joelho em direção ao peito."),
            Exercise("Extensão Lombar", "6 minutos", "Deite-se de bruços, apoie as palmas das mãos no chão e estenda os cotovelos, elevando o tronco."),
            Exercise("Postura da Criança (Balasana)", "5 minutos", "Ajoelhe-se no chão, sente-se nos calcanhares e incline o corpo para a frente, estendendo os braços."),
            Exercise("Ponte (Bridge)", "7 minutos", "Deite-se de costas, dobre os joelhos e eleve o quadril em direção ao teto."),
            Exercise("Gato-Vaca (Cat-Cow)", "4 minutos", "Fique de quatro e alterne entre arquear e arredondar as costas."),
            Exercise("Fortalecimento de Escápulas", "10 minutos", "Mantenha a postura ereta e realize movimentos de retração das escápulas para fortalecer as costas."),
            Exercise("Mobilidade de Tornozelo", "5 minutos", "Movimente o tornozelo em círculos e para cima/baixo para melhorar a amplitude de movimento."),
            Exercise("Rotação de Tronco Sentado", "6 minutos", "Sentado com as costas retas, gire o tronco suavemente para os lados, mantendo o quadril fixo."),
            Exercise("Prancha Abdominal", "3 minutos", "Mantenha o corpo reto apoiado nos antebraços e pontas dos pés, ativando o core."),
            Exercise("Agachamento Terapêutico", "12 minutos", "Realize o movimento de sentar e levantar com apoio, focando no controle muscular."),
            Exercise("Alongamento de Isquiotibiais", "7 minutos", "Sentado ou deitado, estenda uma perna e alcance o pé para alongar a parte posterior da coxa."),
            Exercise("Respiração Diafragmática", "10 minutos", "Foque na respiração profunda expandindo o abdômen, relaxando a musculatura acessória.")
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