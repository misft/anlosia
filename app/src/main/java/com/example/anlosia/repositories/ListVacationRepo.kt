package com.example.anlosia.repositories

import com.example.anlosia.api.ApiClient
import com.example.anlosia.model.ListVacationResponse
import retrofit2.Call

class ListVacationRepo {
    val apiClient = ApiClient()

    fun getListVacation(id: Int): Call<ListVacationResponse> {
        return apiClient.callApi().getListVacation(id)
    }
}