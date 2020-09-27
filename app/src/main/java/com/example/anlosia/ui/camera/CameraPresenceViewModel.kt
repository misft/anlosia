package com.example.anlosia.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anlosia.model.FaceRecognitionResponse
import com.example.anlosia.model.PresenceResponse
import com.example.anlosia.model.UploadResponse
import com.example.anlosia.repositories.PresenceStartRepo
import com.example.anlosia.util.Util
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class CameraPresenceViewModel : ViewModel() {
    val presenceStartRepo = PresenceStartRepo()

    val uploadPhotoResponse = MutableLiveData<UploadResponse>()
    fun getUploadPhotoResponse() : LiveData<UploadResponse> {
        return uploadPhotoResponse
    }
    val faceRecognitionResponse = MutableLiveData<FaceRecognitionResponse>()

    fun getFaceRecognitionResponse() : LiveData<FaceRecognitionResponse> {
        return faceRecognitionResponse
    }

    fun postUploadFile(file: File) {
        presenceStartRepo.postUploadFile(file).enqueue(object: Callback<UploadResponse> {
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Util.logD(t.toString())
            }

            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                uploadPhotoResponse.value = response.body()
                postFaceRecognition()
            }
        })
    }

    fun postFaceRecognition() {
        presenceStartRepo.postFaceRecognition().enqueue(object: Callback<FaceRecognitionResponse> {
            override fun onFailure(call: Call<FaceRecognitionResponse>, t: Throwable) {
                Util.logD(t.toString())
            }

            override fun onResponse(
                call: Call<FaceRecognitionResponse>,
                response: Response<FaceRecognitionResponse>
            ) {
                Util.logD(response.body().toString())
                faceRecognitionResponse.value = response.body()
            }
        })
    }

    val presenceStart = MutableLiveData<PresenceResponse>()

    fun getPresenceStart() : LiveData<PresenceResponse> {
        return presenceStart
    }

    fun postPresenceStart(id_user: Int, id_company: Int, date_presence: String?, start_presence: String?) {
        val body: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("id_user", id_user.toString())
            .addFormDataPart("id_company", id_company.toString())
            .addFormDataPart("date_presence", date_presence)
            .addFormDataPart("start_presence", start_presence)
            .build()
        presenceStartRepo.postPresenceStart(body).enqueue(object: Callback<PresenceResponse> {
            override fun onFailure(call: Call<PresenceResponse>, t: Throwable) {
                Util.logD(t.toString())
            }

            override fun onResponse(
                call: Call<PresenceResponse>,
                response: Response<PresenceResponse>
            ) {
                Util.logD(response.body().toString())
                presenceStart.value = response.body()
            }
        })
    }
}
