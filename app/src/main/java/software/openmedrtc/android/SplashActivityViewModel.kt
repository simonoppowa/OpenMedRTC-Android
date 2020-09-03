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
        val id = authenticator.getSavedId()
        val password = authenticator.getSavedPassword()

        return id != null && password != null
    }

    fun authenticateUser() {
        val id = authenticator.getSavedId()
        val password = authenticator.getSavedPassword()
        if (id != null && password != null) {
            authenticateUser(AuthenticateUser.Params(id, password)) {
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
