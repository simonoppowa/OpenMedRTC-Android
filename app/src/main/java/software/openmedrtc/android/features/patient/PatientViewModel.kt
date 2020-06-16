package software.openmedrtc.android.features.patient

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import software.openmedrtc.android.core.functional.Failure
import software.openmedrtc.android.core.interactor.UseCase
import software.openmedrtc.android.features.shared.Medical

class PatientViewModel(private val getMedicals: GetMedicals) : ViewModel() {

    var failure: MutableLiveData<Failure> = MutableLiveData()
    var medicals: MutableLiveData<List<Medical>> = MutableLiveData()

    // TODO fetch medicals
    fun loadMedicals() {
        getMedicals(UseCase.None()) {
            it.fold(::handleFailure, ::handleMedicals)
        }
    }

    private fun handleMedicals(medicals: List<Medical>) {
        this.medicals.value = medicals
    }

    private fun handleFailure(failure: Failure) {
        this.failure.value = failure
    }

}
