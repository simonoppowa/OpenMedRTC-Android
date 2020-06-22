package software.openmedrtc.android.features.shared.connection


import kotlinx.coroutines.CoroutineScope
import okhttp3.Response
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SessionDescription
import software.openmedrtc.android.core.helper.JsonParser
import software.openmedrtc.android.features.shared.Medical
import software.openmedrtc.android.features.shared.User
import software.openmedrtc.android.features.shared.connection.DataMessage.Companion.MESSAGE_TYPE_ICE_CANDIDATE
import software.openmedrtc.android.features.shared.connection.DataMessage.Companion.MESSAGE_TYPE_SDP_OFFER
import software.openmedrtc.android.features.shared.connection.sdp.GetSessionDescription
import software.openmedrtc.android.features.shared.connection.sdp.SdpType
import software.openmedrtc.android.features.shared.connection.sdp.SetSessionDescription
import timber.log.Timber

class PatientConnectionViewModel(
    getPeerConnection: GetPeerConnection,
    getSessionDescription: GetSessionDescription,
    setSessionDescription: SetSessionDescription,
    peerConnectionFactory: PeerConnectionFactory,
    coroutineScope: CoroutineScope,
    private val getWebsocketConnection: GetWebsocketConnection,
    private val jsonParser: JsonParser
) :
    ConnectionViewModel(
        getPeerConnection,
        getSessionDescription,
        setSessionDescription,
        jsonParser,
        coroutineScope,
        peerConnectionFactory
    ) {

    fun initMedicalConnection(medical: Medical) {
        getWebsocketConnection(medical)
    }

    private fun getWebsocketConnection(medical: Medical) {
        getWebsocketConnection(GetWebsocketConnection.Params(medKey = medical.email)) {
            it.fold(::handleFailure) { websocket ->
                getPeerConnection(
                    websocket,
                    medical,
                    getPeerConnectionObserver(websocket, medical),
                    ::onGetPeerConnectionSuccess
                )
            }
        }
    }

    private fun onGetPeerConnectionSuccess(
        peerConnection: PeerConnection,
        websocketConnection: Websocket,
        user: User
    ) {
        handleWebsocketConnection(websocketConnection, peerConnection, user)
    }

    private fun onSetRemoteSessionDescriptionSuccess(
        peerConnection: PeerConnection,
        websocket: Websocket,
        sessionDescription: SessionDescription,
        user: User
    ) {
        createSessionDescription(
            peerConnection,
            websocket,
            user,
            SdpType.ANSWER,
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
        websocket: Websocket,
        sessionDescription: SessionDescription,
        user: User
    ) {
        sendSdpMessage(sessionDescription, user, peerConnection, websocket, SdpType.ANSWER)
    }

    private fun handleWebsocketConnection(
        websocket: Websocket,
        peerConnection: PeerConnection,
        user: User
    ) {
        Timber.d("Got connection $websocket")

        websocket.addListener(object : Websocket.SocketListener {
            override fun onOpen(websocket: Websocket, response: Response) {}
            override fun onFailure(websocket: Websocket, t: Throwable, response: Response?) {}

            override fun onMessage(websocket: Websocket, text: String) {
                val dataMessage = jsonParser.parseDataMessage(text)
                when (dataMessage?.messageType) {
                    MESSAGE_TYPE_SDP_OFFER -> {
                        Timber.d("Sdp Offer received")

                        jsonParser.getSessionDescriptionFromDataMessage(dataMessage)
                            .fold(::handleFailure) { sessionDescription ->
                                setRemoteSessionDescription(
                                    peerConnection,
                                    websocket,
                                    sessionDescription,
                                    user,
                                    ::onSetRemoteSessionDescriptionSuccess
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
                    else -> {
                        Timber.e("Wrong data message type")
                    }
                }
            }
        })
    }

}
