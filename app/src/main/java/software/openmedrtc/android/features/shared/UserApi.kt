package software.openmedrtc.android.features.shared

import retrofit2.Call
import retrofit2.http.GET

internal interface UserApi {
    companion object {
        private const val PATH_GET_MEDICALS = "/rest" // TODO change path
    }

    @GET(PATH_GET_MEDICALS)
    fun medicals(): Call<List<MedicalDTO>>
}
