package software.openmedrtc.android

import android.os.Bundle
import software.openmedrtc.android.core.platform.BaseActivity
import software.openmedrtc.android.features.patient.PatientMainFragment

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO
        openFragment(PatientMainFragment.newInstance(), R.id.main_fragment_container)
    }
}
