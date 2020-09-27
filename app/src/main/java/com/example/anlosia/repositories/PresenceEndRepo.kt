package com.example.anlosia.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.anlosia.api.ApiClient
import com.example.anlosia.model.PresenceResponse
import com.example.anlosia.util.Util
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class PresenceEndRepo {
    val apiClient: ApiClient = ApiClient()

    fun postPresenceEnd(
        id: Int,
        body: RequestBody
    ): Call<PresenceResponse> {
        return apiClient.callApi().postPresenceEnd(id, body)
    }
}