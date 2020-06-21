package software.openmedrtc.android.features.shared.connection


import okhttp3.Response
import org.webrtc.PeerConnection
import software.openmedrtc.android.core.helper.JsonParser
import software.openmedrtc.android.features.shared.Medical
import software.openmedrtc.android.features.shared.connection.DataMessage.Companion.MESSAGE_TYPE_ICE_CANDIDATE
import software.openmedrtc.android.features.shared.connection.DataMessage.Companion.MESSAGE_TYPE_SDP_OFFER
import software.openmedrtc.android.features.shared.connection.sdp.GetSessionDescription
import software.openmedrtc.android.features.shared.connection.sdp.SetSessionDescription
import timber.log.Timber

class PatientConnectionViewModel(
    getPeerConnection: GetPeerConnection,
    getSessionDescription: GetSessionDescription,
    setSessionDescription: SetSessionDescription,
    private val getWebsocketConnection: GetWebsocketConnection,
    private val jsonParser: JsonParser
) :
    ConnectionViewModel(
        getPeerConnection,
        getSessionDescription,
        setSessionDescription,
        jsonParser
    ) {

    fun initMedicalConnection(medical: Medical) {
        getWebsocketConnection(GetWebsocketConnection.Params(medKey = medical.email)) {
            it.fold(::handleFailure) { websocket ->
                getPeerConnection(
                    websocket,
                    medical,
                    getPeerConnectionObserver(websocket, medical),
                    ::handleWebsocketConnection
                )
            }
        }
    }

    private fun handleWebsocketConnection(websocket: Websocket, peerConnection: PeerConnection) {
        Timber.d("Got connection $websocket")

        websocket.addListener(object : Websocket.SocketListener {
            override fun onOpen(websocket: Websocket, response: Response) {}
            override fun onFailure(websocket: Websocket, t: Throwable, response: Response?) {}

            override fun onMessage(websocket: Websocket, text: String) {
                val dataMessage = jsonParser.parseDataMessage(text)
                when (dataMessage?.messageType) {
                    MESSAGE_TYPE_SDP_OFFER -> {
                        Timber.d("Sdp Offer received")

                        val sdpOffer = (jsonParser.parseSdpMessage(dataMessage.json)) ?: return
                        val sessionDescription =
                            jsonParser.parseSessionDescription(sdpOffer.sessionDescriptionString)
                                ?: return

                        setRemoteSessionDescription(peerConnection, sessionDescription)
                    }
                    MESSAGE_TYPE_ICE_CANDIDATE -> {
                        Timber.d("Ice candidate received")

                        val iceMessage = jsonParser.parseIceMessage(dataMessage.json) ?: return
                        val iceCandidate =
                            jsonParser.parseIceCandidate(iceMessage.iceCandidate) ?: return
                        setIceCandidate(iceCandidate, peerConnection)
                    }
                    else -> {
                        Timber.e("Wrong data message type")
                    }
                }
            }
        })
    }


}
