package com.example.anlosia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anlosia.model.PresenceResponse
import com.example.anlosia.repositories.PresenceEndRepo
import com.example.anlosia.util.Util
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Multipart

class PresenceEndViewModel : ViewModel() {
    private val presenceEndRepo = PresenceEndRepo()

    var presenceEnd = MutableLiveData<PresenceResponse>()

    init {
        presenceEnd.value = null
    }

    fun getPresence() : LiveData<PresenceResponse> {
        return presenceEnd
    }

    fun postPresence(id_presence: Int, id_user: Int, id_company: Int, date_presence: String, end_presence: String) {
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("id_user", id_user.toString())
            .addFormDataPart("id_company", id_company.toString())
            .addFormDataPart("date_presence", date_presence)
            .addFormDataPart("end_presence", end_presence)
            .build()
        presenceEndRepo.postPresenceEnd(id_presence, body).enqueue(object: Callback<PresenceResponse> {
            override fun onFailure(call: Call<PresenceResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(
                call: Call<PresenceResponse>,
                response: Response<PresenceResponse>
            ) {
                Util.logD(response.message())
                presenceEnd.value = response.body()
            }
        })
    }
}