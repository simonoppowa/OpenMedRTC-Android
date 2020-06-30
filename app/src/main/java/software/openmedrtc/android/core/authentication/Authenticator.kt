package software.openmedrtc.android.core.authentication

import okhttp3.*
import okhttp3.Authenticator
import software.openmedrtc.android.features.connection.entity.Medical
import software.openmedrtc.android.features.connection.entity.User
import java.io.IOException

class Authenticator {

    var okHttpClient: OkHttpClient? = null
    var loggedInUser: User? = null

    fun isMedical() = loggedInUser is Medical

    fun createClient(email: String, password: String) {
        okHttpClient = OkHttpClient.Builder()
            .authenticator(object : Authenticator {
                @Throws(IOException::class)
                override fun authenticate(route: Route?, response: Response): Request? {
                    if (response.request.header("Authorization") != null) {
                        return null // Give up, we've already attempted to authenticate.
                    }

                    println("Authenticating for response: $response")
                    println("Challenges: ${response.challenges()}")
                    val credential = Credentials.basic(email, password)
                    return response.request.newBuilder()
                        .header("Authorization", credential)
                        .build()
                }
            }).build()
    }
}
