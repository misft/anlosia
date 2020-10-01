package com.example.anlosia.ui.list.vacation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anlosia.model.ListVacationResponse
import com.example.anlosia.repositories.ListVacationRepo
import com.example.anlosia.util.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListVacationViewModel : ViewModel() {
    val listVacationRepo = ListVacationRepo()

    val listVacationResponse = MutableLiveData<ListVacationResponse>()

    init {
        listVacationResponse.value = null
    }

    fun getListVacationResponse() : LiveData<ListVacationResponse> {
        return listVacationResponse
    }

    fun getListVacation(id: Int) {
        listVacationRepo.getListVacation(id).enqueue(object: Callback<ListVacationResponse> {
            override fun onFailure(call: Call<ListVacationResponse>, t: Throwable) {
                Util.logD(t.toString())
            }

            override fun onResponse(
                call: Call<ListVacationResponse>,
                response: Response<ListVacationResponse>
            ) {
                Util.logD(response.body().toString())
                listVacationResponse.value = response.body()
            }
        })
    }
}