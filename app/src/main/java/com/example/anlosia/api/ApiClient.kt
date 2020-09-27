package com.example.anlosia.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ApiService

class ApiClient {
    private val URL = "https://anlosia.xyz/api/"

    fun apiClient(): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    fun callApi(): ApiService {
        return apiClient().create(ApiService::class.java)
    }
}