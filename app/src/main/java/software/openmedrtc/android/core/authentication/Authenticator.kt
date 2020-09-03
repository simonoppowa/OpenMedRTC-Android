package software.openmedrtc.android.core.authentication

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import okhttp3.*
import okhttp3.Authenticator
import software.openmedrtc.android.core.di.ID_SPE_KEY
import software.openmedrtc.android.core.di.PASSWORD_SPE_KEY
import software.openmedrtc.android.features.connection.entity.Medical
import software.openmedrtc.android.features.connection.entity.Patient
import software.openmedrtc.android.features.connection.entity.User
import java.io.IOException


class Authenticator(private val sharedPreferences: SharedPreferences) {

    var okHttpClient: OkHttpClient? = null
    var loggedInUser: MutableLiveData<User> = MutableLiveData()

    fun isMedical() = loggedInUser.value is Medical
    fun isPatient() = loggedInUser.value is Patient

    fun createClient(id: String, password: String) {
        okHttpClient = OkHttpClient.Builder()
            .authenticator(object : Authenticator {
                @Throws(IOException::class)
                override fun authenticate(route: Route?, response: Response): Request? {
                    if (response.request.header("Authorization") != null) {
                        return null // Give up, we've already attempted to authenticate.
                    }

                    println("Authenticating for response: $response")
                    println("Challenges: ${response.challenges()}")
                    val credential = Credentials.basic(id, password)
                    return response.request.newBuilder()
                        .header("Authorization", credential)
                        .build()
                }
            }).build()
    }

    fun initLoggedInUser(user: User, password: String) {
        loggedInUser.value = user
        saveUserCredentials(user.id, password)
    }

    private fun saveUserCredentials(id: String, password: String) {
        // TODO encrypt credentials
        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString(ID_SPE_KEY, id)
        sharedPreferencesEditor.putString(PASSWORD_SPE_KEY, password)
        sharedPreferencesEditor.apply()
    }

    fun getSavedId(): String? =
        sharedPreferences.getString(ID_SPE_KEY, null)

    fun getSavedPassword(): String? =
        sharedPreferences.getString(PASSWORD_SPE_KEY, null)

}
