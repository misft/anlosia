package com.example.anlosia.ui.list.presence

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anlosia.model.ListPresenceResponse
import com.example.anlosia.model.ListVacationResponse
import com.example.anlosia.repositories.ListPresenceRepo
import com.example.anlosia.util.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListPresenceViewModel : ViewModel() {
    val listPresenceRepo = ListPresenceRepo()
    val listPresenceResponse = MutableLiveData<ListPresenceResponse>()

    init {
        listPresenceResponse.value = null
    }

    fun getListPresenceResponse() : LiveData<ListPresenceResponse> {
        return listPresenceResponse
    }

    fun getListPresence(id: Int) {
        listPresenceRepo.getListPresence(id).enqueue(object: Callback<ListPresenceResponse> {
            override fun onFailure(call: Call<ListPresenceResponse>, t: Throwable) {
                Util.logD(t.toString())
            }

            override fun onResponse(
                call: Call<ListPresenceResponse>,
                response: Response<ListPresenceResponse>
            ) {
                listPresenceResponse.value = response.body()
            }
        })
    }
}