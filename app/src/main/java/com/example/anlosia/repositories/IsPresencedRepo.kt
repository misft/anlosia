package com.example.anlosia.repositories

import com.example.anlosia.api.ApiClient
import com.example.anlosia.model.IsPresencedResponse
import okhttp3.RequestBody
import retrofit2.Call

class IsPresencedRepo {
    val apiClient = ApiClient()

    fun postIsPresenced(body: RequestBody): Call<IsPresencedResponse> {
        return apiClient.callApi().postIsPresenced(body)
    }
}