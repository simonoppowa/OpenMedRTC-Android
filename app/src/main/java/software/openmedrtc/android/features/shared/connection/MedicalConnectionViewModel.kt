package software.openmedrtc.android.features.shared.connection

import okhttp3.Response
import org.webrtc.*
import software.openmedrtc.android.core.di.USERNAME
import software.openmedrtc.android.core.helper.JsonParser
import software.openmedrtc.android.core.platform.BaseViewModel
import software.openmedrtc.android.features.shared.Patient
import software.openmedrtc.android.features.shared.connection.DataMessage.Companion.MESSAGE_TYPE_ICE_CANDIDATE
import software.openmedrtc.android.features.shared.connection.DataMessage.Companion.MESSAGE_TYPE_SDP_ANSWER
import software.openmedrtc.android.features.shared.connection.DataMessage.Companion.MESSAGE_TYPE_SDP_OFFER
import software.openmedrtc.android.features.shared.connection.sdp.GetSessionDescription
import software.openmedrtc.android.features.shared.connection.sdp.SdpType
import software.openmedrtc.android.features.shared.connection.sdp.SessionType
import software.openmedrtc.android.features.shared.connection.sdp.SetSessionDescription
import timber.log.Timber

class MedicalConnectionViewModel(
    private val getPeerConnection: GetPeerConnection,
    private val getSessionDescription: GetSessionDescription,
    private val setSessionDescription: SetSessionDescription,
    private val getWebsocketConnection: GetWebsocketConnection,
    private val jsonParser: JsonParser
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
                // TODO get websocket
                //createSessionDescription(peerConnection, patient)
                getWebsocketConnection(peerConnection, patient)
            }
        }
    }

    private fun getWebsocketConnection(peerConnection: PeerConnection, patient: Patient) {
        getWebsocketConnection(GetWebsocketConnection.Params()) {
            it.fold(::handleFailure) {websocket ->
                createSessionDescription(peerConnection,websocket, patient)
            }
        }
    }

    private fun createSessionDescription(
        peerConnection: PeerConnection,
        websocketConnection: Websocket,
        patient: Patient
    ) {
        getSessionDescription(
            GetSessionDescription.Params(SdpType.OFFER, peerConnection)
        ) {
            it.fold(::handleFailure) { sessionDescription ->
                setLocalSessionDescription(
                    peerConnection,
                    websocketConnection,
                    sessionDescription,
                    patient
                )
            }
        }
    }

    private fun setLocalSessionDescription(
        peerConnection: PeerConnection,
        websocketConnection: Websocket,
        sessionDescription: SessionDescription,
        patient: Patient
    ) {
        setSessionDescription(
            SetSessionDescription.Params(
                SessionType.LOCAL,
                peerConnection,
                sessionDescription
            )
        ) {
            it.fold(::handleFailure) {
                sendSdpOffer(sessionDescription, patient, peerConnection, websocketConnection)
            }
        }
    }

    private fun sendSdpOffer(
        sessionDescription: SessionDescription,
        patient: Patient,
        peerConnection: PeerConnection,
        websocketConnection: Websocket
    ) {
        handleWebsocketConnection(websocketConnection, peerConnection)

        // TODO handle exception
        val sdpMessage = SdpMessage(
            USERNAME,
            patient.email,
            jsonParser.sessionDescriptionToJson(sessionDescription) ?: return
        )

        val dataMessage =
            DataMessage(MESSAGE_TYPE_SDP_OFFER, jsonParser.sdpMessageToJson(sdpMessage) ?: return)
        val dataMessageJson = jsonParser.dataMessageToJson(dataMessage) ?: return

        websocketConnection.sendMessage(dataMessageJson)
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

    private fun setRemoteSessionDescription(
        peerConnection: PeerConnection,
        sessionDescription: SessionDescription
    ) {
        setSessionDescription(
            SetSessionDescription.Params(
            SessionType.REMOTE,
                peerConnection,
                sessionDescription
        )) {
            it.fold(::handleFailure, { })
        }
    }

    private fun setIceCandidate(iceCandidate: IceCandidate, peerConnection: PeerConnection) {
        if(peerConnection.addIceCandidate(iceCandidate)) {
            Timber.d("Ice candidate set")
        } else {
            Timber.e("Error while setting ice candidate")
        }
    }
}
