package software.openmedrtc.android.core.platform

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import software.openmedrtc.android.core.functional.Failure

open class BaseViewModel: ViewModel() {

    var failure: MutableLiveData<Failure> = MutableLiveData()

    protected fun handleFailure(failure: Failure) {
        this.failure.value = failure
    }

}
