package software.openmedrtc.android.features.shared.connection

import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import software.openmedrtc.android.core.di.USERNAME
import software.openmedrtc.android.core.helper.JsonParser
import software.openmedrtc.android.core.platform.BaseViewModel
import software.openmedrtc.android.features.shared.Medical
import timber.log.Timber

class PatientConnectionViewModel(
    private val getWebsocketConnection: GetWebsocketConnection,
    private val getPeerConnection: GetPeerConnection,
    private val jsonParser: JsonParser
) :
    BaseViewModel() {

    fun openWebsocketConnection(medical: Medical) {
        getWebsocketConnection(GetWebsocketConnection.Params(medKey = medical.email)) {
            it.fold(::handleFailure) { websocket ->
                getPeerConnection(websocket, medical)
            }
        }
    }

    private fun handleWebsocketConnection(websocket: Websocket, medical: Medical) {
        // TODO 
    }

    private fun getPeerConnection(websocket: Websocket, medical: Medical) {
        val peerConnectionObserver = handlePeerConnectionChange(websocket, medical)
        getPeerConnection(peerConnectionObserver) {
            it.fold(::handleFailure) { peerConnection ->
                // TODO create sessiondescription
            }
        }
    }

    // TODO duplicate code
    private fun handlePeerConnectionChange(
        websocket: Websocket,
        medical: Medical
    ): PeerConnectionObserver = object : PeerConnectionObserver() {
        override fun onIceCandidate(p0: IceCandidate?) {
            if(p0 == null) return
            super.onIceCandidate(p0)

            val iceJson = jsonParser.iceCandidateToJson(p0) ?: return
            val iceMessage = IceMessage(USERNAME, medical.email, iceJson)
            val iceMessageJson = jsonParser.iceMessageToJson(iceMessage) ?: return

            val dataMessage = DataMessage(DataMessage.MESSAGE_TYPE_ICE_CANDIDATE, iceMessageJson)

            val dataMessageJson = jsonParser.dataMessageToJson(dataMessage) ?: return
            websocket.sendMessage(dataMessageJson)
        }

        override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
            super.onIceConnectionChange(p0)
        }

        override fun onAddStream(p0: MediaStream?) {
            super.onAddStream(p0)
        }
    }
}
