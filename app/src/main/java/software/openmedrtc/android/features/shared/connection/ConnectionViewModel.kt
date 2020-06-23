package software.openmedrtc.android.features.shared.connection

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.webrtc.*
import software.openmedrtc.android.core.di.USERNAME
import software.openmedrtc.android.core.helper.JsonParser
import software.openmedrtc.android.core.platform.BaseViewModel
import software.openmedrtc.android.features.shared.User
import software.openmedrtc.android.features.shared.connection.sdp.GetSessionDescription
import software.openmedrtc.android.features.shared.connection.sdp.SdpType
import software.openmedrtc.android.features.shared.connection.sdp.SessionType
import software.openmedrtc.android.features.shared.connection.sdp.SetSessionDescription
import timber.log.Timber

abstract class ConnectionViewModel(
    private val getWebsocketConnection: GetWebsocketConnection,
    private val getPeerConnection: GetPeerConnection,
    private val getSessionDescription: GetSessionDescription,
    private val setSessionDescription: SetSessionDescription,
    private val jsonParser: JsonParser,
    private val coroutineScope: CoroutineScope,
    val peerConnectionFactory: PeerConnectionFactory
) : BaseViewModel() {

    val remoteMediaStream: MutableLiveData<MediaStream> = MutableLiveData()
    val connectionReady: MutableLiveData<Boolean> = MutableLiveData()
    val peerConnection: MutableLiveData<PeerConnection> = MutableLiveData()

    abstract fun initConnection(user: User)

    open fun getWebsocketConnection(
        user: User,
        params: GetWebsocketConnection.Params,
        onSuccess: (Websocket, User) -> Unit
    ){
        return getWebsocketConnection(params) {
            it.fold(::handleFailure) { websocket ->
                onSuccess(websocket, user)
            }
        }
    }

    open fun getPeerConnection(
        websocket: Websocket,
        user: User,
        peerConnectionObserver: PeerConnectionObserver,
        onSuccess: (PeerConnection, Websocket, User) -> Unit
    ) = getPeerConnection(peerConnectionObserver)
    {
        it.fold(::handleFailure) { peerConnection ->
            // TODO BUG Should be postValue, but does not trigger observer
            coroutineScope.launch(Dispatchers.Main) {
                this@ConnectionViewModel.peerConnection.value = peerConnection
                onSuccess(peerConnection, websocket, user)
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

                jsonParser.createIceDataMessageJson(
                    USERNAME,
                    user.email,
                    p0,
                    DataMessage.MESSAGE_TYPE_ICE_CANDIDATE
                ).fold(::handleFailure) { dataMessageJson ->
                    websocket.sendMessage(dataMessageJson)
                }
            }

            override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
                super.onIceConnectionChange(p0)
                if(p0 == PeerConnection.IceConnectionState.CONNECTED) {
                    connectionReady.postValue(true)
                }
            }

            override fun onAddStream(p0: MediaStream?) {
                super.onAddStream(p0)
                remoteMediaStream.postValue(p0)
            }
        }

    open fun createSessionDescription(
        peerConnection: PeerConnection,
        websocketConnection: Websocket,
        user: User,
        sdpType: SdpType,
        onSuccess: (PeerConnection, Websocket, SessionDescription, User) -> Unit
    ) {
        getSessionDescription(
            GetSessionDescription.Params(sdpType, peerConnection)
        ) {
            it.fold(::handleFailure) { sessionDescription ->
                onSuccess(peerConnection, websocketConnection, sessionDescription, user)
            }
        }
    }

    open fun setLocalSessionDescription(
        peerConnection: PeerConnection,
        websocketConnection: Websocket,
        sessionDescription: SessionDescription,
        user: User,
        onSuccess: ((PeerConnection, Websocket, SessionDescription, User) -> Unit)? = null
    ) {
        setSessionDescription(
            SetSessionDescription.Params(
                SessionType.LOCAL,
                peerConnection,
                sessionDescription
            )
        ) {
            it.fold(::handleFailure) {
                if (onSuccess != null) {
                    onSuccess(
                        peerConnection,
                        websocketConnection,
                        sessionDescription,
                        user
                    )
                }
            }
        }
    }

    open fun setRemoteSessionDescription(
        peerConnection: PeerConnection,
        websocket: Websocket,
        sessionDescription: SessionDescription,
        user: User,
        onSuccess: ((PeerConnection, Websocket, SessionDescription, User) -> Unit)? = null
    ) {
        setSessionDescription(
            SetSessionDescription.Params(
                SessionType.REMOTE,
                peerConnection,
                sessionDescription
            )
        ) {
            it.fold(::handleFailure) {
                if(onSuccess != null) onSuccess(peerConnection, websocket, sessionDescription, user)
            }
        }
    }

    open fun setIceCandidate(iceCandidate: IceCandidate, peerConnection: PeerConnection) {
        if (peerConnection.addIceCandidate(iceCandidate)) {
            Timber.d("Ice candidate set")
        } else {
            Timber.e("Error while setting ice candidate")
        }
    }

    open fun sendSdpMessage(
        sessionDescription: SessionDescription,
        user: User,
        peerConnection: PeerConnection,
        websocketConnection: Websocket,
        sdpType: SdpType
    ) {
        val dataMessageType = if (sdpType == SdpType.OFFER) {
            DataMessage.MESSAGE_TYPE_SDP_OFFER
        } else {
            DataMessage.MESSAGE_TYPE_SDP_ANSWER
        }
        jsonParser.createSdpDataMessageJson(
            USERNAME,
            user.email,
            sessionDescription,
            dataMessageType
        ).fold(::handleFailure) { dataMessageJson ->
            websocketConnection.sendMessage(dataMessageJson)
        }
    }
}
