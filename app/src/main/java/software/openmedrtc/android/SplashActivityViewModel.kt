package software.openmedrtc.android

import androidx.lifecycle.MutableLiveData
import software.openmedrtc.android.core.authentication.Authenticator
import software.openmedrtc.android.core.functional.Failure
import software.openmedrtc.android.core.platform.BaseViewModel
import software.openmedrtc.android.features.connection.rest.AuthenticateUser

class SplashActivityViewModel(
    private val authenticator: Authenticator,
    private val authenticateUser: AuthenticateUser
): BaseViewModel() {

    val userAuthenticated: MutableLiveData<Boolean> = MutableLiveData()

    fun checkUserCredentialsSaved(): Boolean {
        val email = authenticator.getSavedEmail()
        val password = authenticator.getSavedPassword()

        return email != null && password != null
    }

    fun authenticateUser() {
        val email = authenticator.getSavedEmail()
        val password = authenticator.getSavedPassword()
        if (email != null && password != null) {
            authenticateUser(AuthenticateUser.Params(email, password)) {
                it.fold(::handleFailure) { user ->
                    authenticator.initLoggedInUser(user, password)
                    userAuthenticated.postValue(true)
                }
            }
        } else {
            failure.value = Failure.AuthFailure
        }
    }

}
