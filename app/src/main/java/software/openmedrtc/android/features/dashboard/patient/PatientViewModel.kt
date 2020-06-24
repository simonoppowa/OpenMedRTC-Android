package software.openmedrtc.android.features.dashboard.patient

import androidx.lifecycle.MutableLiveData
import software.openmedrtc.android.core.interactor.UseCase
import software.openmedrtc.android.core.platform.BaseViewModel
import software.openmedrtc.android.features.connection.entity.Medical
import software.openmedrtc.android.features.connection.rest.GetMedicals
import software.openmedrtc.android.features.connection.websocket.GetWebsocketConnection

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
