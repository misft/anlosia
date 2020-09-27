package com.example.anlosia.ui.list.vacation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anlosia.model.ListVacationResponse
import com.example.anlosia.repositories.ListVacationRepo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListVacationViewModel : ViewModel() {
    val listVacationRepo = ListVacationRepo()

    val listVacationResponse = MutableLiveData<ListVacationResponse>()

    fun getListVacationResponse() : LiveData<ListVacationResponse> {
        return listVacationResponse
    }

    fun getListVacation(id: Int) {
        listVacationRepo.getListVacation(id).enqueue(object: Callback<ListVacationResponse> {
            override fun onFailure(call: Call<ListVacationResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(
                call: Call<ListVacationResponse>,
                response: Response<ListVacationResponse>
            ) {
                listVacationResponse.value = response.body()
            }
        })
    }
}