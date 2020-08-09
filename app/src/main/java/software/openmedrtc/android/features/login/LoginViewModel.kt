package software.openmedrtc.android.features.login

import androidx.lifecycle.MutableLiveData
import software.openmedrtc.android.core.authentication.Authenticator
import software.openmedrtc.android.core.platform.BaseViewModel
import software.openmedrtc.android.features.connection.rest.AuthenticateUser

class LoginViewModel(
    private val authenticateUser: AuthenticateUser,
    private val authenticator: Authenticator
): BaseViewModel() {

    val userAuthenticated: MutableLiveData<Boolean> = MutableLiveData()

    fun authUser(id: String, password: String) {
        authenticateUser(AuthenticateUser.Params(id, password)) {
            it.fold(::handleFailure) { user ->
                authenticator.initLoggedInUser(user, password)
                userAuthenticated.postValue(true)
            }
        }
    }

}
