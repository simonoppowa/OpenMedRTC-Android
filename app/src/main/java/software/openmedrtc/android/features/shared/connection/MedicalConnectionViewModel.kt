package software.openmedrtc.android.features.shared.connection

import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import software.openmedrtc.android.core.platform.BaseViewModel
import software.openmedrtc.android.features.shared.Patient
import software.openmedrtc.android.features.shared.connection.sdp.GetSessionDescription
import software.openmedrtc.android.features.shared.connection.sdp.SdpType

class MedicalConnectionViewModel(
    private val getPeerConnection: GetPeerConnection,
    private val getSessionDescription: GetSessionDescription
) : BaseViewModel() {

    fun initPatientPeerConnection(patient: Patient) {

        val peerConnectionObserver = object : PeerConnectionObserver() {
            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)
            }

            override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
                super.onIceConnectionChange(p0)
            }

            override fun onAddStream(p0: MediaStream?) {
                super.onAddStream(p0)
            }
        }


        getPeerConnection(peerConnectionObserver) {
            it.fold(::handleFailure) { peerConnection ->
                createSessionDescription(peerConnection, patient)
            }
        }

    }

    private fun createSessionDescription(
        peerConnection: PeerConnection,
        patient: Patient
    ) {
        getSessionDescription(
            GetSessionDescription.Params(SdpType.OFFER, peerConnection)
        ) {
            it.fold(::handleFailure) { sessionDescription ->
                // TODO set session description
            }
        }
    }
}
