package software.openmedrtc.android.features.patient

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import software.openmedrtc.android.core.functional.Either
import software.openmedrtc.android.core.functional.Failure
import software.openmedrtc.android.core.interactor.UseCase
import software.openmedrtc.android.features.shared.Medical
import software.openmedrtc.android.features.shared.UserRepository

class GetMedicals(
    private val medicalsRepository: UserRepository.Network,
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher
) : UseCase<List<Medical>, UseCase.None>(scope, dispatcher) {

    override suspend fun run(params: None): Either<Failure, List<Medical>> =
        medicalsRepository.medicals()
}
