package com.example.anlosia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anlosia.model.IsPresencedResponse
import com.example.anlosia.repositories.IsPresencedRepo
import com.example.anlosia.util.Util
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IsPresencedViewModel: ViewModel() {
    val isPresencedRepo = IsPresencedRepo()

    val isPresenced = MutableLiveData<IsPresencedResponse>()

    init {
        isPresenced.value = null
    }
    fun getIsPresenced(): LiveData<IsPresencedResponse> {
        return isPresenced
    }

    fun postIsPresenced(id_user: Int, date_presence: String) {
        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("id_user", id_user.toString())
            .addFormDataPart("date_presence", date_presence)
            .build()
        isPresencedRepo.postIsPresenced(body).enqueue(object: Callback<IsPresencedResponse> {
            override fun onFailure(call: Call<IsPresencedResponse>, t: Throwable) {
                Util.logD(t.toString())
            }

            override fun onResponse(
                call: Call<IsPresencedResponse>,
                response: Response<IsPresencedResponse>
            ) {
                isPresenced.value = response.body()
            }
        })
    }
}