import com.example.anlosia.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("login/")
    fun postLogin(@Body body: RequestBody): Call<LoginResponse>

    @POST("presence/")
    fun postPresenceStart(@Body body: RequestBody): Call<PresenceResponse>

    @PUT("presence/{id}/")
    fun postPresenceEnd(
        @Path("id") id: Int,
        @Body body: RequestBody
    ): Call<PresenceResponse>

    @Multipart
    @POST("upload/")
    fun postUploadFile(
        @Part body: MultipartBody.Part
    ): Call<UploadResponse>

    @GET("facerecognition/")
    fun postFaceRecognition(): Call<FaceRecognitionResponse>

    @POST("location/")
    fun postRecordLocation(@Body body: RequestBody): Call<RecordLocationResponse>

    @Multipart
    @POST("vacation/")
    fun postVacation(
        @Part fileBody: MultipartBody.Part,
        @Part id_user: MultipartBody.Part,
        @Part id_company: MultipartBody.Part,
        @Part start_day: MultipartBody.Part,
        @Part end_day: MultipartBody.Part,
        @Part vacation_type: MultipartBody.Part,
        @Part message: MultipartBody.Part
    ): Call<VacationResponse>

    @GET("vacation/")
    fun getListVacation(@Query("id_user") id: Int): Call<ListVacationResponse>

    @GET("presence/")
    fun getListPresence(@Query("id_user") id: Int): Call<ListPresenceResponse>

    @POST("p/")
    fun postIsPresenced(@Body body: RequestBody) : Call<IsPresencedResponse>

}