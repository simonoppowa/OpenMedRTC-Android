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

    private fun handleMedicals(medicals: List<Medical>) {
        this.medicals.value = medicals
    }
}
