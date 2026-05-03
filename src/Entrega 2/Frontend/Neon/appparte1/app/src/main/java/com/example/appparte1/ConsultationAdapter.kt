package com.example.appparte1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ConsultationAdapter(
    private val consultations: List<Consultation>,
    private val onItemClick: (Consultation) -> Unit
) : RecyclerView.Adapter<ConsultationAdapter.ConsultationViewHolder>() {

    class ConsultationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val doctorName: TextView = view.findViewById(R.id.tv_doctor_name)
        val specialty: TextView = view.findViewById(R.id.tv_specialty)
        val time: TextView = view.findViewById(R.id.tv_time)
        val location: TextView = view.findViewById(R.id.tv_location)
        val detailsLink: TextView = view.findViewById(R.id.tv_details_link)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_consultation_item, parent, false)
        return ConsultationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsultationViewHolder, position: Int) {
        val consultation = consultations[position]
        holder.doctorName.text = consultation.doctorName
        holder.specialty.text = consultation.specialty
        holder.time.text = consultation.time
        holder.location.text = "${consultation.location}\n${consultation.date}"
        
        // Clique tanto no card quanto no link "Ver detalhes"
        holder.itemView.setOnClickListener { onItemClick(consultation) }
        holder.detailsLink.setOnClickListener { onItemClick(consultation) }
    }

    override fun getItemCount() = consultations.size
}