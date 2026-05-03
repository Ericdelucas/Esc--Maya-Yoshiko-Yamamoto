package com.example.appparte1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class ConsultationDetailFragment : Fragment() {

    companion object {
        private const val ARG_CONSULTATION = "consultation"

        fun newInstance(consultation: Consultation): ConsultationDetailFragment {
            val fragment = ConsultationDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_CONSULTATION, consultation)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_consultation_detail, container, false)
        
        val consultation = arguments?.getSerializable(ARG_CONSULTATION) as? Consultation
        
        val tvName = view.findViewById<TextView>(R.id.tv_detail_doctor_name)
        val tvSpecialty = view.findViewById<TextView>(R.id.tv_detail_specialty)
        val tvDesc = view.findViewById<TextView>(R.id.tv_detail_description)
        val tvDateTime = view.findViewById<TextView>(R.id.tv_detail_date_time)
        val tvLocation = view.findViewById<TextView>(R.id.tv_detail_location)
        val btnBack = view.findViewById<ImageView>(R.id.btn_back_consultation_detail)

        consultation?.let {
            tvName.text = it.doctorName
            tvSpecialty.text = it.specialty
            tvDesc.text = it.description
            tvDateTime.text = "${it.date} às ${it.time}"
            tvLocation.text = it.location
        }

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}