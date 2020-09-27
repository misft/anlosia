package com.example.anlosia.repositories

import com.example.anlosia.api.ApiClient
import com.example.anlosia.model.ListPresenceResponse
import retrofit2.Call

class ListPresenceRepo {
    val apiClient = ApiClient()

    fun getListPresence(id: Int): Call<ListPresenceResponse> {
        return apiClient.callApi().getListPresence(id)
    }
}