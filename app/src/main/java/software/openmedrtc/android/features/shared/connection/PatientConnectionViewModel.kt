package software.openmedrtc.android.features.shared.connection

import software.openmedrtc.android.core.platform.BaseViewModel
import software.openmedrtc.android.features.shared.Medical

class PatientConnectionViewModel(private val getWebsocketConnection: GetWebsocketConnection) :
    BaseViewModel() {

    fun openWebsocketConnection(medical: Medical) {
        getWebsocketConnection(GetWebsocketConnection.Params(medKey = medical.email)) {
            it.fold(::handleFailure, ::handleWebsocketConnection)
        }
    }

    private fun handleWebsocketConnection(websocket: Websocket) {
        // TODO init peerConnection
    }
}
