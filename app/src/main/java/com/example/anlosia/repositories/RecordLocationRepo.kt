package com.example.anlosia.repositories

import androidx.lifecycle.MutableLiveData
import com.example.anlosia.api.ApiClient
import com.example.anlosia.model.Location
import com.example.anlosia.model.RecordLocationResponse
import com.example.anlosia.util.Util
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RecordLocationRepo {
    val apiClient : ApiClient = ApiClient()

    val recordLocation = MutableLiveData<RecordLocationResponse>()

    fun postLocation(
        id: Int,
        id_company: Int,
        location: Location
    ): MutableLiveData<RecordLocationResponse> {
        val body: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("user_id", id.toString())
            .addFormDataPart("id_company", id_company.toString())
            .addFormDataPart("location", location.toString())
            .build()
        apiClient.callApi().postRecordLocation(body).enqueue(object : Callback<RecordLocationResponse> {
            override fun onFailure(call: Call<RecordLocationResponse>, t: Throwable) {
                Util.logE(t.toString())
            }

            override fun onResponse(
                call: Call<RecordLocationResponse>,
                response: Response<RecordLocationResponse>
            ) {
                recordLocation.value = response.body()
            }
        })

        return recordLocation
    }
}
