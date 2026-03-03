package com.example.appparte1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ConsultationsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_consultations, container, false)
        
        val rvConsultations = view.findViewById<RecyclerView>(R.id.rv_consultations)
        rvConsultations.layoutManager = LinearLayoutManager(context)
        
        val dummyConsultations = listOf(
            Consultation(
                "Dra. Patrícia de Oliveira", 
                "Ortopedista Especialista em Coluna", 
                "14:00", 
                "quinta-feira, 12 de fevereiro", 
                "ORTOCITY | Lapa - Clínica Ortopédica",
                "A Dra. Patrícia possui mais de 10 anos de experiência no tratamento de dores lombares e reabilitação postural. Sua abordagem foca em exercícios preventivos e técnicas minimamente invasivas."
            ),
            Consultation(
                "Dra. Carolina Mendes", 
                "Fisioterapeuta Esportiva", 
                "09:30", 
                "segunda-feira, 16 de fevereiro", 
                "Clínica Dr. André Isidoro",
                "Especialista em recuperação acelerada de lesões em atletas de alto rendimento. Atua com terapias manuais e pilates clínico para fortalecimento do core."
            ),
            Consultation(
                "Dr. Felipe de Souza", 
                "Cardiologista e Nutrólogo", 
                "11:30", 
                "terça-feira, 17 de fevereiro", 
                "Clínica Pró Coração",
                "Focado em saúde integral e performance física. Realiza avaliações completas de risco cardíaco antes do início de novos protocolos de exercícios intensos."
            )
        )
        
        rvConsultations.adapter = ConsultationAdapter(dummyConsultations) { consultation ->
            // Abre a tela de detalhes da consulta ao clicar
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ConsultationDetailFragment.newInstance(consultation))
                .addToBackStack(null)
                .commit()
        }
        
        return view
    }
}