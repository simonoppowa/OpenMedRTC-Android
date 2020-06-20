package software.openmedrtc.android.features.shared.connection.sdp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import software.openmedrtc.android.core.functional.Either
import software.openmedrtc.android.core.functional.Failure
import software.openmedrtc.android.core.interactor.UseCase

class GetSessionDescription(
    private val sessionDescriptionRepository: SessionDescriptionRepository,
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher
) : UseCase<SessionDescription, GetSessionDescription.Params>(scope, dispatcher) {

    override suspend fun run(params: Params): Either<Failure, SessionDescription> =
        sessionDescriptionRepository.sessionDescription(params.sdpType, params.peerConnection)


    data class Params(val sdpType: SdpType, val peerConnection: PeerConnection)
}
