import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anlosia.model.VacationResponse
import com.example.anlosia.util.Util
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Multipart

class VacationViewModel : ViewModel() {
    val vacationResponse = MutableLiveData<VacationResponse>()
    val vacationRepo = VacationRepo()

    init {
        vacationResponse.value = null
    }

    fun getVacationResponse(): LiveData<VacationResponse> {
        return vacationResponse
    }

    fun postVacation(id_user: Int, id_company: Int, start_day: String, end_day: String, vacation_type: String, message: String) {
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("id_user", id_user.toString())
            .addFormDataPart("id_company", id_company.toString())
            .addFormDataPart("start_day", start_day)
            .addFormDataPart("end_day", end_day)
            .addFormDataPart("vacation_type", vacation_type)
            .addFormDataPart("message", message)
            .build()

        vacationRepo.postVacation(body).enqueue(object: Callback<VacationResponse> {
            override fun onFailure(call: Call<VacationResponse>, t: Throwable) {
                Util.logD(t.toString())
            }

            override fun onResponse(
                call: Call<VacationResponse>,
                response: Response<VacationResponse>
            ) {
                vacationResponse.value = response.body()
            }
        })
    }
}