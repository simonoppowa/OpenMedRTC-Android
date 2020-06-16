package software.openmedrtc.android.features.patient

import androidx.lifecycle.MutableLiveData
import software.openmedrtc.android.core.interactor.UseCase
import software.openmedrtc.android.core.platform.BaseViewModel
import software.openmedrtc.android.features.shared.Medical

class PatientViewModel(private val getMedicals: GetMedicals) : BaseViewModel() {

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
