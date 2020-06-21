package software.openmedrtc.android.features.shared.connection

import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import software.openmedrtc.android.core.di.USERNAME
import software.openmedrtc.android.core.helper.JsonParser
import software.openmedrtc.android.core.platform.BaseViewModel
import software.openmedrtc.android.features.shared.Patient
import software.openmedrtc.android.features.shared.User
import software.openmedrtc.android.features.shared.connection.sdp.GetSessionDescription
import software.openmedrtc.android.features.shared.connection.sdp.SdpType
import software.openmedrtc.android.features.shared.connection.sdp.SessionType
import software.openmedrtc.android.features.shared.connection.sdp.SetSessionDescription
import timber.log.Timber

abstract class ConnectionViewModel(
    private val getPeerConnection: GetPeerConnection,
    private val getSessionDescription: GetSessionDescription,
    private val setSessionDescription: SetSessionDescription,
    private val jsonParser: JsonParser
) : BaseViewModel() {

    open fun getPeerConnection(
        websocket: Websocket,
        user: User,
        peerConnectionObserver: PeerConnectionObserver,
        handleWebSocketConnection: (Websocket, PeerConnection) -> Unit
    ) {
        getPeerConnection(peerConnectionObserver) {
            it.fold(::handleFailure) { peerConnection ->
                handleWebSocketConnection(websocket, peerConnection)
                createSessionDescription(peerConnection, websocket, user)
            }
        }

    }

    open fun getPeerConnectionObserver(
        websocket: Websocket,
        user: User
    ): PeerConnectionObserver =
        object : PeerConnectionObserver() {
            override fun onIceCandidate(p0: IceCandidate?) {
                if (p0 == null) return
                super.onIceCandidate(p0)

                val iceJson = jsonParser.iceCandidateToJson(p0) ?: return
                val iceMessage = IceMessage(USERNAME, user.email, iceJson)
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

    open fun createSessionDescription(
        peerConnection: PeerConnection,
        websocketConnection: Websocket,
        user: User
    ) {
        getSessionDescription(
            GetSessionDescription.Params(SdpType.OFFER, peerConnection)
        ) {
            it.fold(::handleFailure) { sessionDescription ->
                setLocalSessionDescription(
                    peerConnection,
                    websocketConnection,
                    sessionDescription,
                    user
                )
            }
        }
    }

    open fun setLocalSessionDescription(
        peerConnection: PeerConnection,
        websocketConnection: Websocket,
        sessionDescription: SessionDescription,
        user: User
    ) {
        setSessionDescription(
            SetSessionDescription.Params(
                SessionType.LOCAL,
                peerConnection,
                sessionDescription
            )
        ) {
            it.fold(::handleFailure) {
                sendSdpOffer(sessionDescription, user, peerConnection, websocketConnection)
            }
        }
    }

    open fun setRemoteSessionDescription(
        peerConnection: PeerConnection,
        sessionDescription: SessionDescription
    ) {
        setSessionDescription(
            SetSessionDescription.Params(
                SessionType.REMOTE,
                peerConnection,
                sessionDescription
            )
        ) {
            it.fold(::handleFailure, { })
        }
    }

    open fun setIceCandidate(iceCandidate: IceCandidate, peerConnection: PeerConnection) {
        if (peerConnection.addIceCandidate(iceCandidate)) {
            Timber.d("Ice candidate set")
        } else {
            Timber.e("Error while setting ice candidate")
        }
    }

    open fun sendSdpOffer(
        sessionDescription: SessionDescription,
        user: User,
        peerConnection: PeerConnection,
        websocketConnection: Websocket
    ) {
        // TODO handle exception
        val sdpMessage = SdpMessage(
            USERNAME,
            user.email,
            jsonParser.sessionDescriptionToJson(sessionDescription) ?: return
        )

        val dataMessage =
            DataMessage(
                DataMessage.MESSAGE_TYPE_SDP_OFFER,
                jsonParser.sdpMessageToJson(sdpMessage) ?: return
            )
        val dataMessageJson = jsonParser.dataMessageToJson(dataMessage) ?: return

        websocketConnection.sendMessage(dataMessageJson)
    }
}
