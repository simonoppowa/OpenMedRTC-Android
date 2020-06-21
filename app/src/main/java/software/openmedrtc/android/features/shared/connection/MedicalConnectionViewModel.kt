package software.openmedrtc.android.features.shared.connection

import okhttp3.Response
import org.webrtc.*
import software.openmedrtc.android.core.di.USERNAME
import software.openmedrtc.android.core.helper.JsonParser
import software.openmedrtc.android.features.shared.Patient
import software.openmedrtc.android.features.shared.connection.DataMessage.Companion.MESSAGE_TYPE_ICE_CANDIDATE
import software.openmedrtc.android.features.shared.connection.DataMessage.Companion.MESSAGE_TYPE_SDP_ANSWER
import software.openmedrtc.android.features.shared.connection.sdp.GetSessionDescription
import software.openmedrtc.android.features.shared.connection.sdp.SetSessionDescription
import timber.log.Timber

class MedicalConnectionViewModel(
    getPeerConnection: GetPeerConnection,
    getSessionDescription: GetSessionDescription,
    setSessionDescription: SetSessionDescription,
    private val getWebsocketConnection: GetWebsocketConnection,
    private val jsonParser: JsonParser
) : ConnectionViewModel(
    getPeerConnection,
    getSessionDescription,
    setSessionDescription,
    jsonParser
) {

    fun initPatientConnection(patient: Patient) {
        getWebsocketConnection(GetWebsocketConnection.Params()) {
            it.fold(::handleFailure) { websocket ->
                getPeerConnection(
                    websocket,
                    patient,
                    getPeerConnectionObserver(websocket, patient),
                    ::handleWebsocketConnection
                )
            }
        }
    }

    private fun handleWebsocketConnection(
        websocketConnection: Websocket,
        peerConnection: PeerConnection
    ) {
        websocketConnection.addListener(object : Websocket.SocketListener {
            override fun onOpen(websocket: Websocket, response: Response) {}

            override fun onFailure(websocket: Websocket, t: Throwable, response: Response?) {}

            override fun onMessage(websocket: Websocket, text: String) {
                val dataMessage = jsonParser.parseDataMessage(text)

                when (dataMessage?.messageType) {
                    MESSAGE_TYPE_SDP_ANSWER -> {
                        Timber.d("Sdp answer received")
                        val sdpMessage = jsonParser.parseSdpMessage(dataMessage.json) ?: return

                        val sessionDescription =
                            jsonParser.parseSessionDescription(sdpMessage.sessionDescriptionString)
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
                    else -> Timber.e("Wrong data message type")
                }

            }

        })
    }
}
