package software.openmedrtc.android.features.shared.connection.sdp

import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import software.openmedrtc.android.core.functional.Either
import software.openmedrtc.android.core.functional.Failure
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface SessionDescriptionRepository {

    suspend fun sessionDescription(
        sdpType: SdpType,
        peerConnection: PeerConnection
    ): Either<Failure, SessionDescription>


    class SessionDescriptionRepositoryImpl : SessionDescriptionRepository {

        private val sdpConstraints: MediaConstraints = MediaConstraints().apply {
            mandatory.add(
                MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
            )
            mandatory.add(
                MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")

            )
        }

        override suspend fun sessionDescription(
            sdpType: SdpType,
            peerConnection: PeerConnection
        ): Either<Failure, SessionDescription> {

            val sessionDescription = suspendCoroutine<SessionDescription?> { cont ->

                val sdpObserver = object : SdpObserver {
                    override fun onSetSuccess() {}
                    override fun onSetFailure(p0: String?) {}

                    override fun onCreateSuccess(p0: SessionDescription?) {
                        Timber.d("Created SessionDescription")
                        cont.resume(p0)
                    }

                    override fun onCreateFailure(p0: String?) {
                        Timber.e("Failure while creating session description: %s", p0)
                        cont.resume(null)
                    }
                }

                if (sdpType == SdpType.OFFER) {
                    peerConnection.createOffer(sdpObserver, sdpConstraints)
                } else {
                    peerConnection.createAnswer(sdpObserver, sdpConstraints)
                }
            }

            return if (sessionDescription != null) {
                Either.Right(sessionDescription)
            } else {
                Either.Left(Failure.SdpFailure)
            }
        }

    }
}
