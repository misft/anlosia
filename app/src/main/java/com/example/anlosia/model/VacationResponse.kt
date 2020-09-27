package com.example.anlosia.model

data class VacationResponse(
    val id_user: Int,
    val id_company: Int,
    val start_day: String,
    val end_day: String,
    val vacation_type: String,
    val message: String,
    val vacation_status: String
)

