package com.example.anlosia.model

data class LoginResponse(val api_status: Int, val api_message: String, val data: Array<LoginResponseUsersData>?)