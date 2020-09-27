import com.example.anlosia.api.ApiClient
import com.example.anlosia.model.VacationResponse
import okhttp3.RequestBody
import retrofit2.Call

class VacationRepo {
    val apiClient = ApiClient()

    fun postVacation(body: RequestBody): Call<VacationResponse> {
        return apiClient.callApi().postVacation(body)
    }
}