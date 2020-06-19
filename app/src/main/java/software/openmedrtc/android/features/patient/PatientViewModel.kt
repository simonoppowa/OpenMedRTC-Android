package software.openmedrtc.android.features.patient

import androidx.lifecycle.MutableLiveData
import software.openmedrtc.android.core.interactor.UseCase
import software.openmedrtc.android.core.platform.BaseViewModel
import software.openmedrtc.android.features.shared.Medical
import software.openmedrtc.android.features.shared.connection.GetWebsocketConnection
import software.openmedrtc.android.features.shared.connection.Websocket

class PatientViewModel(
    private val getMedicals: GetMedicals,
    private val getWebsocketConnection: GetWebsocketConnection
) : BaseViewModel() {

    var medicals: MutableLiveData<List<Medical>> = MutableLiveData()

    fun loadMedicals() {
        getMedicals(UseCase.None()) {
            it.fold(::handleFailure, ::handleMedicals)
        }
    }

    fun openWebsocketConnection(medical: Medical) {
        getWebsocketConnection(GetWebsocketConnection.Params(medKey = medical.email)) {
            it.fold(::handleFailure, ::handleWebsocketConnection)
        }
    }

    private fun handleWebsocketConnection(websocket: Websocket) {
        // TODO init peerConnection
    }

    private fun handleMedicals(medicals: List<Medical>) {
        this.medicals.value = medicals
    }
}
