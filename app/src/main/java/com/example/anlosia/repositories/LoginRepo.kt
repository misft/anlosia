package com.example.anlosia.repositories

import android.os.AsyncTask
import android.util.Log
import com.example.anlosia.api.ApiClient
import com.example.anlosia.model.Location
import com.example.anlosia.model.LoginResponse
import com.example.anlosia.util.Util
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.util.*

class LoginRepo {
    val apiClient: ApiClient = ApiClient()

    fun postLogin(body: RequestBody): Call<LoginResponse> {
        return apiClient.callApi().postLogin(body)
    }
}
