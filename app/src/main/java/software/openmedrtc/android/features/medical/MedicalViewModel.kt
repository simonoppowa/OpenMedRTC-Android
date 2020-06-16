package software.openmedrtc.android.features.medical

import androidx.lifecycle.MutableLiveData
import software.openmedrtc.android.core.platform.BaseViewModel
import software.openmedrtc.android.features.shared.Patient

class MedicalViewModel: BaseViewModel() {

    var patients: MutableLiveData<List<Patient>> = MutableLiveData()

    fun loadPatients() {
        // TODO
    }

}
