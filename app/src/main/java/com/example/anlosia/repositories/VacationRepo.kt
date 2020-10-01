import com.example.anlosia.api.ApiClient
import com.example.anlosia.model.VacationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

class VacationRepo {
    val apiClient = ApiClient()

    fun postVacation(
        fileBody: MultipartBody.Part,
        id_user: MultipartBody.Part,
        id_company: MultipartBody.Part,
        start_day: MultipartBody.Part,
        end_day: MultipartBody.Part,
        vacation_type: MultipartBody.Part,
        message: MultipartBody.Part
        ): Call<VacationResponse> {
        return apiClient.callApi().postVacation(fileBody, id_user, id_company, start_day, end_day, vacation_type, message)
    }
}