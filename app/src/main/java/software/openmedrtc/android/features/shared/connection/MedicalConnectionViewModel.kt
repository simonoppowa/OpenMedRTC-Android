package software.openmedrtc.android.features.shared.connection

import kotlinx.coroutines.CoroutineScope
import okhttp3.Response
import org.webrtc.*
import software.openmedrtc.android.core.helper.JsonParser
import software.openmedrtc.android.features.shared.Patient
import software.openmedrtc.android.features.shared.User
import software.openmedrtc.android.features.shared.connection.DataMessage.Companion.MESSAGE_TYPE_ICE_CANDIDATE
import software.openmedrtc.android.features.shared.connection.DataMessage.Companion.MESSAGE_TYPE_SDP_ANSWER
import software.openmedrtc.android.features.shared.connection.sdp.GetSessionDescription
import software.openmedrtc.android.features.shared.connection.sdp.SdpType
import software.openmedrtc.android.features.shared.connection.sdp.SetSessionDescription
import timber.log.Timber

class MedicalConnectionViewModel(
    getPeerConnection: GetPeerConnection,
    getSessionDescription: GetSessionDescription,
    setSessionDescription: SetSessionDescription,
    peerConnectionFactory: PeerConnectionFactory,
    coroutineScope: CoroutineScope,
    private val getWebsocketConnection: GetWebsocketConnection,
    private val jsonParser: JsonParser
) : ConnectionViewModel(
    getPeerConnection,
    getSessionDescription,
    setSessionDescription,
    jsonParser,
    coroutineScope,
    peerConnectionFactory
) {

    fun initPatientConnection(patient: Patient) {
        getWebsocketConnection(patient)
    }

    private fun getWebsocketConnection(patient: Patient) {
        getWebsocketConnection(GetWebsocketConnection.Params()) {
            it.fold(::handleFailure) { websocket ->
                getPeerConnection(
                    websocket,
                    patient,
                    getPeerConnectionObserver(websocket, patient),
                    ::onGotWebsocketConnection
                )
            }
        }
    }

    private fun onGotWebsocketConnection(
        peerConnection: PeerConnection,
        websocketConnection: Websocket,
        user: User
    ) {
        handleWebsocketConnection(websocketConnection, peerConnection, user)
        createSessionDescription(
            peerConnection,
            websocketConnection,
            user,
            SdpType.OFFER,
            ::onCreateSessionDescriptionSuccess
        )
    }

    private fun onCreateSessionDescriptionSuccess(
        peerConnection: PeerConnection,
        websocket: Websocket,
        sessionDescription: SessionDescription,
        user: User
    ) {
        setLocalSessionDescription(
            peerConnection,
            websocket,
            sessionDescription,
            user,
            ::onSetLocalSessionDescriptionSuccess
        )
    }

    private fun onSetLocalSessionDescriptionSuccess(
        peerConnection: PeerConnection,
        websocketConnection: Websocket,
        sessionDescription: SessionDescription,
        user: User
    ) {
        sendSdpMessage(sessionDescription, user, peerConnection, websocketConnection, SdpType.OFFER)
    }

    private fun handleWebsocketConnection(
        websocketConnection: Websocket,
        peerConnection: PeerConnection,
        user: User
    ) {
        websocketConnection.addListener(object : Websocket.SocketListener {
            override fun onOpen(websocket: Websocket, response: Response) {}

            override fun onFailure(websocket: Websocket, t: Throwable, response: Response?) {}

            override fun onMessage(websocket: Websocket, text: String) {
                val dataMessage = jsonParser.parseDataMessage(text)

                when (dataMessage?.messageType) {
                    MESSAGE_TYPE_SDP_ANSWER -> {
                        Timber.d("Sdp answer received")

                        jsonParser.getSessionDescriptionFromDataMessage(dataMessage)
                            .fold(::handleFailure) { sessionDescription ->
                                setRemoteSessionDescription(
                                    peerConnection,
                                    websocket,
                                    sessionDescription,
                                    user
                                )
                            }
                    }
                    MESSAGE_TYPE_ICE_CANDIDATE -> {
                        Timber.d("Ice candidate received")

                        jsonParser.getIceCandidateFromDataMessage(dataMessage)
                            .fold(::handleFailure) { iceCandidate ->
                                setIceCandidate(iceCandidate, peerConnection)
                            }
                    }
                    else -> Timber.e("Wrong data message type")
                }

            }
        })
    }
}
