package software.openmedrtc.android.features.connection.rest

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import software.openmedrtc.android.core.authentication.Authenticator
import software.openmedrtc.android.core.functional.Either
import software.openmedrtc.android.core.functional.Failure
import software.openmedrtc.android.core.interactor.UseCase
import software.openmedrtc.android.features.connection.entity.User

class AuthenticateUser(
    private val userRepository: UserRepository.Network,
    private val authenticator: Authenticator,
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher
) : UseCase<User, AuthenticateUser.Params>(scope, dispatcher) {
    override suspend fun run(params: Params): Either<Failure, User> {
        authenticator.createClient(params.email, params.password)
        return userRepository.authenticate()
    }

    data class Params(val email: String, val password: String)
}
