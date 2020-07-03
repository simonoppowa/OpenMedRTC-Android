package software.openmedrtc.android.features.connection.rest

import retrofit2.Call
import retrofit2.http.GET
import software.openmedrtc.android.features.connection.entity.MedicalDTO
import software.openmedrtc.android.features.connection.entity.UserDTO

internal interface UserApi {
    companion object {
        private const val PATH_GET_MEDICALS = "/rest" // TODO change path
        private const val PATH_AUTH_USER = "/auth"
    }

    @GET(PATH_GET_MEDICALS)
    fun medicals(): Call<List<MedicalDTO>>

    @GET(PATH_AUTH_USER)
    fun authenticate(): Call<UserDTO>
}
