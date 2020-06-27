package software.openmedrtc.android.features.connection.rest

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import software.openmedrtc.android.core.functional.Either
import software.openmedrtc.android.core.functional.Failure
import software.openmedrtc.android.core.interactor.UseCase
import software.openmedrtc.android.features.connection.entity.User

class AuthenticateUser(
    private val userRepository: UserRepository.Network,
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher
) : UseCase<User, UseCase.None>(scope, dispatcher) {
    override suspend fun run(params: None): Either<Failure, User> = userRepository.authenticate()
}
