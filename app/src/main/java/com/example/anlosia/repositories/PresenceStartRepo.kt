package com.example.anlosia.repositories

import com.example.anlosia.api.ApiClient
import com.example.anlosia.model.FaceRecognitionResponse
import com.example.anlosia.model.PresenceResponse
import com.example.anlosia.model.UploadResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File


class PresenceStartRepo() {
    val apiClient: ApiClient = ApiClient()

    fun postUploadFile(file: File): Call<UploadResponse> {
        val requestFile =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body =
            MultipartBody.Part.createFormData("image", file.name, requestFile)

        return apiClient.callApi().postUploadFile(body)
    }

    fun postFaceRecognition(): Call<FaceRecognitionResponse> {
        return apiClient.callApi().postFaceRecognition()
    }

    fun postPresenceStart(body: RequestBody) : Call<PresenceResponse> {
        return apiClient.callApi().postPresenceStart(body)
    }
}