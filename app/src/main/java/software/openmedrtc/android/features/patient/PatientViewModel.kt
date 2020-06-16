package software.openmedrtc.android.features.patient

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import software.openmedrtc.android.core.functional.Failure
import software.openmedrtc.android.features.shared.Medical
import java.util.*

class PatientViewModel() : ViewModel() {

    var failure: MutableLiveData<Failure> = MutableLiveData()
    var medicals: MutableLiveData<List<Medical>> = MutableLiveData()

    // TODO fetch medicals
    fun loadMedicals() {
        
    }

}
