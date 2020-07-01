package software.openmedrtc.android.core.authentication

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import okhttp3.*
import okhttp3.Authenticator
import software.openmedrtc.android.core.di.EMAIL_SPE_KEY
import software.openmedrtc.android.core.di.PASSWORD_SPE_KEY
import software.openmedrtc.android.features.connection.entity.Medical
import software.openmedrtc.android.features.connection.entity.User
import java.io.IOException


class Authenticator(private val sharedPreferences: SharedPreferences) {

    var okHttpClient: OkHttpClient? = null
    var loggedInUser: MutableLiveData<User> = MutableLiveData()

    fun isMedical() = loggedInUser.value is Medical

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

    fun initLoggedInUser(user: User, password: String) {
        loggedInUser.value = user
        saveUserCredentials(user.email, password)
    }

    private fun saveUserCredentials(email: String, password: String) {
        // TODO encrypt credentials
        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString(EMAIL_SPE_KEY, email)
        sharedPreferencesEditor.putString(PASSWORD_SPE_KEY, password)
        sharedPreferencesEditor.apply()
    }

    fun getSavedEmail(): String? =
        sharedPreferences.getString(EMAIL_SPE_KEY, null)

    fun getSavedPassword(): String? =
        sharedPreferences.getString(PASSWORD_SPE_KEY, null)

}
