package com.example.anlosia.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anlosia.model.LoginResponse
import com.example.anlosia.model.LoginResponseUsersData
import com.example.anlosia.repositories.LoginRepo
import com.example.anlosia.util.Util
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Multipart

class HomeViewModel : ViewModel() {
    val loginRepo = LoginRepo()

    var usersData = MutableLiveData<LoginResponse>()

    init {
        usersData.value = null
    }

    fun getUsersData() : LiveData<LoginResponse> {
        return usersData
    }

    fun updateUsersData(username: String, password: String) {
        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("username", username)
            .addFormDataPart("password", password)
            .build()

        loginRepo.postLogin(body).enqueue(object: Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                usersData.value = response.body()
            }
        })
    }
}