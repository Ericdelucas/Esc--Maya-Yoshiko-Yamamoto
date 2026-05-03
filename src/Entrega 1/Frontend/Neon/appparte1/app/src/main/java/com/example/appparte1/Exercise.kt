package com.example.appparte1

import java.io.Serializable

data class Exercise(
    val name: String,
    val duration: String,
    val description: String = "",
    val imageRes: Int = android.R.drawable.ic_menu_gallery
) : Serializable

data class Consultation(
    val doctorName: String,
    val specialty: String,
    val time: String,
    val date: String,
    val location: String,
    val description: String = "",
    val imageRes: Int = android.R.drawable.ic_menu_gallery
) : Serializable