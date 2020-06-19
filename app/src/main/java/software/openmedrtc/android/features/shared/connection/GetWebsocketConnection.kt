package software.openmedrtc.android.features.shared.connection

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import software.openmedrtc.android.core.functional.Either
import software.openmedrtc.android.core.functional.Failure
import software.openmedrtc.android.core.interactor.UseCase

class GetWebsocketConnection(
    private val websocketRepository: WebsocketRepository,
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher
) : UseCase<Websocket, GetWebsocketConnection.Params>(scope, dispatcher) {

    override suspend fun run(params: Params): Either<Failure, Websocket> =
        websocketRepository.websocket(params.medKey)

    data class Params(val medKey: String = "")
}
