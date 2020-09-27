package com.example.anlosia.model

data class LoginResponseResultsData(
    val id: Int,
    val username: String,
    val email: String,
    val users: LoginResponseUsersData,
    val company: Int
)