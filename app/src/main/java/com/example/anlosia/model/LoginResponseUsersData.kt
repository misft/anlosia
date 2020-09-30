package com.example.anlosia.model

data class LoginResponseUsersData(
    val id: Int,
    val users__id_company: Int,
    val username: String,
    val password: String,
    val email: String,
    val users__name: String,
    val users__telp: String,
    val users__profile_pic: String,
    val users__id_company__location: String,
    val users__id_company__end_work: String,
    val users__id_company__start_work: String
)