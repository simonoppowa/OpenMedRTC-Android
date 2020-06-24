package software.openmedrtc.android.features.connection.rest

import retrofit2.Call
import retrofit2.http.GET
import software.openmedrtc.android.features.connection.entity.MedicalDTO

internal interface UserApi {
    companion object {
        private const val PATH_GET_MEDICALS = "/rest" // TODO change path
    }

    @GET(PATH_GET_MEDICALS)
    fun medicals(): Call<List<MedicalDTO>>
}
