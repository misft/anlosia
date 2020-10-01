import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anlosia.model.VacationResponse
import com.example.anlosia.util.Util
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class VacationViewModel : ViewModel() {
    val vacationResponse = MutableLiveData<VacationResponse>()
    val vacationRepo = VacationRepo()

    init {
        vacationResponse.value = null
    }

    fun getVacationResponse(): LiveData<VacationResponse> {
        return vacationResponse
    }

    fun postVacation(id_user: Int, id_company: Int, start_day: String, end_day: String, vacation_type: String, message: String, file: File) {
        val requestFile =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val fileBody = MultipartBody.Part.createFormData("attachment", file.name, requestFile)
        val id_user = MultipartBody.Part.createFormData("id_user", id_user.toString())
        val id_company = MultipartBody.Part.createFormData("id_company", id_company.toString())
        val start_day = MultipartBody.Part.createFormData("start_day", start_day)
        val end_day = MultipartBody.Part.createFormData("end_day", end_day)
        val vacation_type = MultipartBody.Part.createFormData("vacation_type", vacation_type)
        val message = MultipartBody.Part.createFormData("message", message)
        vacationRepo.postVacation(fileBody, id_user, id_company, start_day, end_day, vacation_type, message).enqueue(object: Callback<VacationResponse> {
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