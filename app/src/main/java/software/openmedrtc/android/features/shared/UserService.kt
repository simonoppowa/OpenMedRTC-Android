package software.openmedrtc.android.features.shared

import retrofit2.Call
import retrofit2.Retrofit

class UserService(private val retrofit: Retrofit) : UserApi {
    private val userApi by lazy { retrofit.create(UserApi::class.java) }

    override fun medicals(): Call<List<MedicalDTO>> = userApi.medicals()
}
