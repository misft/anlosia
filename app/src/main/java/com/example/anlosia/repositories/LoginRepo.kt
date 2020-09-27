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

class LoginRepo : AsyncTask<String, Void, LoginResponse>() {
    val apiClient: ApiClient = ApiClient()

    override fun doInBackground(vararg params: String): LoginResponse? {
        val body : RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("username", params[0])
            .addFormDataPart("password", params[0])
            .build()
        val call: Response<LoginResponse> = apiClient.callApi().postLogin(body).execute()
        val response: LoginResponse? = call.body()
        return response
    }
}
