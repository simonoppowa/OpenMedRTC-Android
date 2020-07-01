package software.openmedrtc.android.features.login

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import software.openmedrtc.android.core.authentication.Authenticator
import software.openmedrtc.android.core.di.EMAIL_SPE_KEY
import software.openmedrtc.android.core.di.PASSWORD_SPE_KEY
import software.openmedrtc.android.core.platform.BaseViewModel
import software.openmedrtc.android.features.connection.rest.AuthenticateUser

class LoginViewModel(
    private val authenticateUser: AuthenticateUser,
    private val authenticator: Authenticator,
    private val sharedPreferences: SharedPreferences
): BaseViewModel() {

    val userAuthenticated: MutableLiveData<Boolean> = MutableLiveData()

    fun authUser(email: String, password: String) {
        authenticateUser(AuthenticateUser.Params(email, password)) {
            it.fold(::handleFailure) { user ->
                authenticator.loggedInUser.value = user
                saveCredentials(user.email, password)
                userAuthenticated.postValue(true)
            }
        }
    }

    private fun saveCredentials(email: String, password: String) {
        // TODO encrypt credentials
        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString(EMAIL_SPE_KEY, email)
        sharedPreferencesEditor.putString(PASSWORD_SPE_KEY, password)
        sharedPreferencesEditor.apply()
    }

}
