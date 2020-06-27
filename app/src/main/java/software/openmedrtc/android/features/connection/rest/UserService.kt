package software.openmedrtc.android.features.connection.rest

import retrofit2.Call
import retrofit2.Retrofit
import software.openmedrtc.android.features.connection.entity.MedicalDTO
import software.openmedrtc.android.features.connection.entity.UserDTO

class UserService(private val retrofit: Retrofit) :
    UserApi {
    private val userApi by lazy { retrofit.create(UserApi::class.java) }

    override fun medicals(): Call<List<MedicalDTO>> = userApi.medicals()
    override fun authenticate(): Call<UserDTO> = userApi.authenticate()

}
