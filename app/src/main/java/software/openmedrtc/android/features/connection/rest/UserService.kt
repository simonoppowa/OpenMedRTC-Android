package software.openmedrtc.android.features.connection.rest

import retrofit2.Call
import retrofit2.Retrofit
import software.openmedrtc.android.core.authentication.Authenticator
import software.openmedrtc.android.features.connection.entity.MedicalDTO
import software.openmedrtc.android.features.connection.entity.UserDTO
import java.lang.NullPointerException

class UserService(
    private val retrofitBuilder: Retrofit.Builder,
    private val authenticator: Authenticator
) : UserApi {

    override fun medicals(): Call<List<MedicalDTO>> = initApi().medicals()
    override fun authenticate(): Call<UserDTO> = initApi().authenticate()


    private fun initApi(): UserApi {
        val okHttpClient =
            authenticator.okHttpClient ?: throw NullPointerException("OkHttpClient null")
        return retrofitBuilder.client(okHttpClient).build().create(UserApi::class.java)
    }
}
