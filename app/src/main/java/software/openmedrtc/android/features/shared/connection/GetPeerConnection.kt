package software.openmedrtc.android.features.shared.connection

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import software.openmedrtc.android.core.functional.Either
import software.openmedrtc.android.core.functional.Failure
import software.openmedrtc.android.core.interactor.UseCase

class GetPeerConnection(
    private val peerConnectionFactory: PeerConnectionFactory,
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher
) : UseCase<PeerConnection, PeerConnectionObserver>(scope, dispatcher) {

    // TODO
    private val iceServers = listOf(
        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
            .createIceServer()
    )

    override suspend fun run(params: PeerConnectionObserver): Either<Failure, PeerConnection> {
        val peerConnection = createPeerConnection(params)

        return if (peerConnection != null) {
            Either.Right(peerConnection)
        } else {
            Either.Left(Failure.PeerConnectionFailure)
        }
    }

    private fun createPeerConnection(
        peerConnectionObserver: PeerConnectionObserver
    ): PeerConnection? {
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers).apply {
            tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
            bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
            rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
            continualGatheringPolicy =
                PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
            keyType = PeerConnection.KeyType.ECDSA // ECDSA encryption.
        }

        return peerConnectionFactory.createPeerConnection(
            rtcConfig,
            peerConnectionObserver
        )
    }

}
