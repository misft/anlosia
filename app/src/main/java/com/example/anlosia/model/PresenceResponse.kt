package com.example.anlosia.model

data class PresenceResponse(
    val id: Int,
    val id_user: Int,
    val id_company: Int,
    val date_presence: String,
    val start_presence: String,
    val end_presence: String
)