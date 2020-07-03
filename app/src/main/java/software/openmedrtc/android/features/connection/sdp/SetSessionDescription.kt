package software.openmedrtc.android.features.connection.sdp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.webrtc.PeerConnection
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import software.openmedrtc.android.core.functional.Either
import software.openmedrtc.android.core.functional.Failure
import software.openmedrtc.android.core.interactor.UseCase
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SetSessionDescription(
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher
) : UseCase<UseCase.None, SetSessionDescription.Params>(scope, dispatcher) {

    override suspend fun run(params: Params): Either<Failure, None> =
        setSessionDescription(params.type, params.peerConnection, params.sessionDescription)

    private suspend fun setSessionDescription(
        sessionType: SessionType,
        peerConnection: PeerConnection,
        sessionDescription: SessionDescription
    ): Either<Failure, None> {
        val wasSuccess = suspendCoroutine<Boolean> { cont ->
            val sdpObserver = object : SdpObserver {
                override fun onSetSuccess() {
                    Timber.d("Set $sessionType SessionDescription")
                    cont.resume(true)
                }

                override fun onSetFailure(p0: String?) {
                    Timber.e("Failed to set $sessionType SessionsDescription: $p0")
                    cont.resume(false)
                }

                override fun onCreateSuccess(p0: SessionDescription?) {}
                override fun onCreateFailure(p0: String?) {}

            }
            when (sessionType) {
                SessionType.LOCAL ->
                    peerConnection.setLocalDescription(sdpObserver, sessionDescription)
                SessionType.REMOTE ->
                    peerConnection.setRemoteDescription(sdpObserver, sessionDescription)
            }
        }

        return if (wasSuccess) {
            Either.Right(None())
        } else {
            Either.Left(Failure.SdpFailure)
        }
    }

    class Params(
        val type: SessionType,
        val peerConnection: PeerConnection,
        val sessionDescription: SessionDescription
    )
}
